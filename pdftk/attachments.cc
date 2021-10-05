/* -*- Mode: C++; tab-width: 2; c-basic-offset: 2 -*- */
/*
	PDFtk, the PDF Toolkit
	Copyright (c) 2003, 2004, 2010 Steward and Lee, LLC


	This program is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.


	Visit: www.pdftk.com for pdftk information and articles
	Permalink: http://www.pdflabs.com/tools/pdftk-the-pdf-toolkit/

	Please email Sid Steward with questions or bug reports.
	Include "pdftk" in the subject line to ensure successful delivery:
	sid.steward at pdflabs dot com

*/

// Tell C++ compiler to use Java-style exceptions.
#pragma GCC java_exceptions

#include <gcj/cni.h>

#include <iostream>
#include <fstream>
#include <string>
#include <map>
#include <vector>
#include <set>
#include <algorithm>

#include <java/lang/System.h>
#include <java/lang/Throwable.h>
#include <java/lang/String.h>
#include <java/io/IOException.h>
#include <java/io/PrintStream.h>
#include <java/io/FileOutputStream.h>
#include <java/util/Set.h>
#include <java/util/Vector.h>
#include <java/util/ArrayList.h>
#include <java/util/Iterator.h>
#include <java/util/HashMap.h>

#include <java/util/ArrayList.h>

#include "pdftk/com/lowagie/text/Document.h"
#include "pdftk/com/lowagie/text/Rectangle.h"
#include "pdftk/com/lowagie/text/pdf/PdfName.h"
#include "pdftk/com/lowagie/text/pdf/PdfString.h"
#include "pdftk/com/lowagie/text/pdf/PdfNumber.h"
#include "pdftk/com/lowagie/text/pdf/PdfArray.h"
#include "pdftk/com/lowagie/text/pdf/PdfDictionary.h"
#include "pdftk/com/lowagie/text/pdf/PdfOutline.h"
#include "pdftk/com/lowagie/text/pdf/PdfCopy.h"
#include "pdftk/com/lowagie/text/pdf/PdfReader.h"
#include "pdftk/com/lowagie/text/pdf/PdfImportedPage.h"
#include "pdftk/com/lowagie/text/pdf/PdfWriter.h"
#include "pdftk/com/lowagie/text/pdf/PdfStamperImp.h"
#include "pdftk/com/lowagie/text/pdf/PdfNameTree.h"
#include "pdftk/com/lowagie/text/pdf/FdfReader.h"
#include "pdftk/com/lowagie/text/pdf/AcroFields.h"
#include "pdftk/com/lowagie/text/pdf/PdfIndirectReference.h"
#include "pdftk/com/lowagie/text/pdf/PdfIndirectObject.h"
#include "pdftk/com/lowagie/text/pdf/PdfFileSpecification.h"

#include "pdftk/com/lowagie/text/pdf/PdfAnnotation.h"
#include "pdftk/com/lowagie/text/pdf/PRStream.h"
#include "pdftk/com/lowagie/text/pdf/BaseFont.h"
#include "pdftk/com/lowagie/text/pdf/PdfEncodings.h"

#include <gcj/array.h>

using namespace std;

namespace java {
	using namespace java::lang;
	using namespace java::io;
	using namespace java::util;
}

namespace itext {
	using namespace pdftk::com::lowagie::text;
	using namespace pdftk::com::lowagie::text::pdf;
}

#include "pdftk.h"
#include "attachments.h"

static string
	drop_path( string ss )
{
	const char path_delim= PATH_DELIM; // given at compile-time
	string::size_type loc= 0;
	if( (loc=ss.rfind( path_delim ))!= string::npos && loc!= ss.length()- 1 ) {
		return string( ss, loc+ 1 );
	}
	return ss;
}

void 
TK_Session::attach_files
( itext::PdfReader* input_reader_p,
	itext::PdfWriter* writer_p )
{
	if( !m_input_attach_file_filename.empty() ) {

		if( m_input_attach_file_pagenum== -1 ) { // our signal to prompt the user for a pagenum
			cout << "Please enter the page number you want to attach these files to." << endl;
			cout << "   The first page is 1.  The final page is \"end\"." << endl;
			cout << "   To attach files at the document level, just press Enter." << endl;
			char buff[64];
			cin.getline( buff, 64 );
			if( buff[0]== 0 ) { // attach to document
				m_input_attach_file_pagenum= 0;
			}
			if( strcmp(buff, "end")== 0 ) { // the final page
				m_input_attach_file_pagenum= input_reader_p->getNumberOfPages();
			}
			else {
				m_input_attach_file_pagenum= 0;
				for( int ii= 0; buff[ii] && isdigit(buff[ii]); ++ii ) {
					m_input_attach_file_pagenum= m_input_attach_file_pagenum* 10+ buff[ii]- '0';
				}
			}
		}
		else if( m_input_attach_file_pagenum== -2 ) { // the final page ("end")
			m_input_attach_file_pagenum= input_reader_p->getNumberOfPages();
		}

		if( m_input_attach_file_pagenum ) { // attach to a page using annotations
			static int trans= 27;
			static int margin= 18;

			if( 0< m_input_attach_file_pagenum &&
					m_input_attach_file_pagenum<= input_reader_p->getNumberOfPages() ) {

				itext::PdfDictionary* page_p= input_reader_p->getPageN( m_input_attach_file_pagenum );
				if( page_p && page_p->isDictionary() ) {

					itext::Rectangle* crop_box_p= 
						input_reader_p->getCropBox( m_input_attach_file_pagenum );
					float corner_top= crop_box_p->top()- margin;
					float corner_left= crop_box_p->left()+ margin;

					itext::PdfArray* annots_p= (itext::PdfArray*)
						input_reader_p->getPdfObject( page_p->get( itext::PdfName::ANNOTS ) );
					bool annots_new_b= false;
					if( !annots_p ) { // create Annots array
						annots_p= new itext::PdfArray();
						annots_new_b= true;
					}
					else { // grab corner_top and corner_left from the bottom right of the newest annot
						java::ArrayList* annots_array_p= annots_p->getArrayList();
						for( jint ii= 0; ii< annots_array_p->size(); ++ii ) {
							itext::PdfDictionary* annot_p= (itext::PdfDictionary*)
								input_reader_p->getPdfObject( (itext::PdfObject*)annots_array_p->get( ii ) );
							if( annot_p && annot_p->isDictionary() ) {
								itext::PdfArray* annot_bbox_p= (itext::PdfArray*)
									input_reader_p->getPdfObject( annot_p->get( itext::PdfName::RECT ) );
								if( annot_bbox_p && annot_bbox_p->isArray() ) {
									java::ArrayList* bbox_array_p= annot_bbox_p->getArrayList();
									if( bbox_array_p->size()== 4 ) {
										corner_top= ((itext::PdfNumber*)bbox_array_p->get( 1 ))->floatValue();
										corner_left= ((itext::PdfNumber*)bbox_array_p->get( 2 ))->floatValue();
									}
								}
							}
						}
					}
					if( annots_p && annots_p->isArray() ) {
						for( vector< string >::iterator vit= m_input_attach_file_filename.begin();
								 vit!= m_input_attach_file_filename.end(); ++vit )
							{
								if( *vit== "PROMPT" ) {
									prompt_for_filename( "Please enter a filename for attachment:", *vit );
								}

								string filename= drop_path(*vit);

								// wrap our location over page bounds, if needed
								if( crop_box_p->right() < corner_left+ trans ) {
									corner_left= crop_box_p->left()+ margin;
								}
								if( corner_top- trans< crop_box_p->bottom() ) {
									corner_top= crop_box_p->top()- margin;
								}

								itext::Rectangle* annot_bbox_p= 
									new itext::Rectangle( corner_left,
																				corner_top- trans,
																				corner_left+ trans,
																				corner_top );
																		
								itext::PdfAnnotation* annot_p=
									itext::PdfAnnotation::createFileAttachment
									( writer_p,
										annot_bbox_p,
										JvNewStringUTF( filename.c_str() ), // contents
										0,
										JvNewStringUTF( vit->c_str() ), // the file path
										JvNewStringUTF( filename.c_str() ) ); // display name

								itext::PdfIndirectReference* ref_p=
									writer_p->addToBody( annot_p )->getIndirectReference();

								annots_p->add( ref_p );

								// advance the location of our annotation
								corner_left+= trans;
								corner_top-= trans;
							}
						if( annots_new_b ) { // add new Annots array to page dict
							itext::PdfIndirectReference* ref_p=
								writer_p->addToBody( annots_p )->getIndirectReference();
							page_p->put( itext::PdfName::ANNOTS, ref_p );							
						}
					}
				}
				else { // error
					cerr << "Internal Error: unable to get page dictionary" << endl;
				}
			}
			else { // error
				cerr << "Error: page number " << (int)m_input_attach_file_pagenum;
				cerr << " is not present in the input PDF." << endl;
			}
		}
		else { // attach to document using the EmbeddedFiles name tree
			itext::PdfDictionary* catalog_p= input_reader_p->catalog; // to top, Root dict
			if( catalog_p && catalog_p->isDictionary() ) {

				// the Names dict
				itext::PdfDictionary* names_p= (itext::PdfDictionary*)
					input_reader_p->getPdfObject( catalog_p->get( itext::PdfName::NAMES ) );
				bool names_new_b= false;
				if( !names_p ) { // create Names dict
					names_p= new itext::PdfDictionary();
					names_new_b= true;
				}
				if( names_p && names_p->isDictionary() ) {

					// the EmbeddedFiles name tree (ref. 1.5, sec. 3.8.5), which is a dict at top
					itext::PdfDictionary* emb_files_tree_p= (itext::PdfDictionary*)
						input_reader_p->getPdfObject( names_p->get( itext::PdfName::EMBEDDEDFILES ) );
					java::HashMap* emb_files_map_p= 0;
					bool emb_files_tree_new_b= false;
					if( emb_files_tree_p ) { // read current name tree of attachments into a map
						emb_files_map_p= itext::PdfNameTree::readTree( emb_files_tree_p );
					}
					else { // create material
						emb_files_map_p= new java::HashMap();
						emb_files_tree_new_b= true;
					}

					////
					// add matter to name tree

					for( vector< string >::iterator vit= m_input_attach_file_filename.begin();
							 vit!= m_input_attach_file_filename.end(); ++vit )
						{
							if( *vit== "PROMPT" ) {
								prompt_for_filename( "Please enter a filename for attachment:", *vit );
							}

							string filename= drop_path(*vit);

							itext::PdfFileSpecification* filespec_p= 0;
							try {
								// create the file spec. from file
								filespec_p= 
									itext::PdfFileSpecification::fileEmbedded
									( writer_p,
										JvNewStringUTF( vit->c_str() ), // the file path
										JvNewStringUTF( filename.c_str() ), // the display name
										0 );
							}
							catch( java::io::IOException* ioe_p ) { // file open error
								cerr << "Error: Failed to open attachment file: " << endl;
								cerr << "   " << *vit << endl;
								cerr << "   Skipping this file." << endl;
								continue;
							}

							// add file spec. to PDF via indirect ref.
							itext::PdfIndirectReference* ref_p=
								writer_p->addToBody( filespec_p )->getIndirectReference();

							// contruct a name, if necessary, to prevent possible key collision on the name tree
							java::String* key_p= 
								JvNewStringUTF( vit->c_str() );
							{
								int counter= 1;
								while( emb_files_map_p->containsKey( key_p ) ) { // append a unique suffix
									char buff[256];
									sprintf( buff, "-%d", counter++ );
									key_p= 
										JvNewStringUTF( ( *vit + buff ).c_str() );
								}
							}

							// add file spec. to map
							emb_files_map_p->put( key_p, ref_p );
						}

					if( !emb_files_map_p->isEmpty() ) {
						// create a name tree from map
						itext::PdfDictionary* emb_files_tree_new_p=
							itext::PdfNameTree::writeTree( emb_files_map_p, writer_p );

						if( emb_files_tree_new_b && emb_files_tree_new_p ) {
							// adding new material
							itext::PdfIndirectReference* ref_p=
								writer_p->addToBody( emb_files_tree_new_p )->getIndirectReference();
							names_p->put( itext::PdfName::EMBEDDEDFILES, ref_p );
						}
						else if( emb_files_tree_p && emb_files_tree_new_p ) {
							// supplementing old material
							emb_files_tree_p->merge( emb_files_tree_new_p );
						}
						else { // error
							cerr << "Internal Error: no valid EmbeddedFiles tree to add to PDF." << endl;
						}

						if( names_new_b ) {
							// perform addToBody only after packing new names_p into names_p;
							// use the resulting ref. to pack our new Names dict. into the catalog (Root)
							itext::PdfIndirectReference* ref_p=
								writer_p->addToBody( names_p )->getIndirectReference();
							catalog_p->put( itext::PdfName::NAMES, ref_p );
						}
					}
				}
				else { // error
					cerr << "Internal Error: couldn't read or create PDF Names dictionary." << endl;
				}
			}
			else { // error
				cerr << "Internal Error: couldn't read input PDF Root dictionary." << endl;
				cerr << "   File attachment failed; no new files attached to output." << endl;
			}
		}
	}
}

static string
normalize_pathname( string output_pathname )
{
	const char path_delim= PATH_DELIM; // given at compile-time
	if( output_pathname== "PROMPT" ) {
		prompt_for_filename( "Please enter the directory where you want attachments unpacked:",
												 output_pathname );
	}
	if( output_pathname.rfind( path_delim )== output_pathname.length()- 1 ) {
		return output_pathname;
	}
	else{ // add delim to end
		return output_pathname+ (char)PATH_DELIM;
	}
}

static void
unpack_file( itext::PdfReader* input_reader_p,
						 itext::PdfDictionary* filespec_p,
						 string output_pathname,
						 bool ask_about_warnings_b )
{
	if( filespec_p && filespec_p->isDictionary() ) {

		itext::PdfName* type_p= (itext::PdfName*)
			input_reader_p->getPdfObject( filespec_p->get( itext::PdfName::TYPE ) );
		if( type_p && type_p->isName() && 
				( type_p->compareTo( itext::PdfName::FILESPEC )== 0 ||
					type_p->compareTo( itext::PdfName::F )== 0 ) )
			{
				itext::PdfDictionary* ef_p= (itext::PdfDictionary*)
					input_reader_p->getPdfObject( filespec_p->get( itext::PdfName::EF ) );
				if( ef_p && ef_p->isDictionary() ) {

					// UF introduced in PDF 1.7
					itext::PdfString* fn_p= (itext::PdfString*)
						input_reader_p->getPdfObject( filespec_p->get( itext::PdfName::UF ) );
					if( !fn_p ) { // try the F key
						fn_p= (itext::PdfString*)
						input_reader_p->getPdfObject( filespec_p->get( itext::PdfName::F ) );
					}

					if( fn_p && fn_p->isString() ) {

						// patch by Johann Felix Soden <johfel@gmx.de>
						// patch tweaked by Sid Steward:
						// toString() doesn't ensure conversion from internal encoding (e.g., Y+diaeresis)
						jstring fn_str = fn_p->toUnicodeString();
						//jstring fn_str= JvNewStringUTF( "hello" ); // debug
						int fn_buff_len = JvGetStringUTFLength( fn_str );
						char* fn_buff= (char*)malloc( fn_buff_len* sizeof(char) ); // fn_buff not a C string, not NULL terminated
						JvGetStringUTFRegion( fn_str, 0, fn_str->length(), fn_buff );
						string fn= drop_path( string( fn_buff, fn_buff_len ) );
						free( fn_buff );

						// did the user supply a path?
						if( !output_pathname.empty() ) { // prepend it
							fn= output_pathname+ fn; // output_pathname has been normalized, already
						}
											
						// assuming that F key is used to store the data, and not DOS, Mac, or Unix
						itext::PdfStream* f_p= (itext::PdfStream*)
							input_reader_p->getPdfObject( ef_p->get( itext::PdfName::F ) );
						if( f_p && f_p->isStream() ) {
											
							jbyteArray byte_arr_p= input_reader_p->getStreamBytes( (itext::PRStream*)f_p );
							const jbyte* bytes_p= elements(byte_arr_p);
							jsize num_bytes= byte_arr_p->length;

							if( ask_about_warnings_b ) {
								// test for existing file by this name
								bool output_exists_b= false;
								{
									FILE* fp= fopen( fn.c_str(), "rb" );
									if( fp ) {
										output_exists_b= true;
										fclose( fp );
									}
								}
								if( output_exists_b ) {
									cout << "Warning: the file: " << fn << " already exists.  Overwrite? (y/n)" << endl;
									char buff[64];
									cin.getline( buff, 64 );
									if( buff[0]!= 'y' && buff[0]!= 'Y' ) {
										cout << "   Skipping: " << fn << endl;
										return; // <--- return
									}
								}
							}
							ofstream ofs( fn.c_str(), ios_base::binary | ios_base::out );
							if( ofs ) {
								ofs.write( (const char*)bytes_p, num_bytes );
								ofs.close();
							}
							else { // error
								cerr << "Error: unable to create the file:" << endl;
								cerr << "   " << fn << endl;
								cerr << "   Skipping." << endl;
							}
						}
					}
				}
			}
	}
}

void 
TK_Session::unpack_files
( itext::PdfReader* input_reader_p )
{
	// output pathname; PROMPT if necessary
	string output_pathname= normalize_pathname( m_output_filename );

	{ // unpack document attachments
		itext::PdfDictionary* catalog_p= input_reader_p->catalog; // to top, Root dict
		if( catalog_p && catalog_p->isDictionary() ) {

			// the Names dict
			itext::PdfDictionary* names_p= (itext::PdfDictionary*)
				input_reader_p->getPdfObject( catalog_p->get( itext::PdfName::NAMES ) );
			if( names_p && names_p->isDictionary() ) {

				// the EmbeddedFiles name tree (ref. 1.5, sec. 3.8.5), which is a dict at top
				itext::PdfDictionary* emb_files_tree_p= (itext::PdfDictionary*)
					input_reader_p->getPdfObject( names_p->get( itext::PdfName::EMBEDDEDFILES ) );
				java::HashMap* emb_files_map_p= 0;
				if( emb_files_tree_p && emb_files_tree_p->isDictionary() ) { 
					// read current name tree of attachments into a map
					emb_files_map_p= itext::PdfNameTree::readTree( emb_files_tree_p );

					for( java::Iterator* jit= emb_files_map_p->keySet()->iterator(); jit->hasNext(); ) {
						java::String* key_p= (java::String*)jit->next();
						itext::PdfDictionary* filespec_p= (itext::PdfDictionary*)
							input_reader_p->getPdfObject( (itext::PdfObject*)(emb_files_map_p->get(key_p)) );
						if( filespec_p && filespec_p->isDictionary() ) {

							unpack_file( input_reader_p,
													 filespec_p,
													 output_pathname,
													 m_ask_about_warnings_b );
						}
					}
				}
			}
		}
	}

	{ // unpack page attachments
		jint num_pages= input_reader_p->getNumberOfPages();
		for( jint ii= 1; ii<= num_pages; ++ii ) { // 1-based page ref.s

				itext::PdfDictionary* page_p= input_reader_p->getPageN( ii );
				if( page_p && page_p->isDictionary() ) {

					itext::PdfArray* annots_p= (itext::PdfArray*)
						input_reader_p->getPdfObject( page_p->get( itext::PdfName::ANNOTS ) );
					if( annots_p && annots_p->isArray() ) {

						java::ArrayList* annots_array_p= annots_p->getArrayList();
						for( jint jj= 0; jj< annots_array_p->size(); ++jj ) {
							itext::PdfDictionary* annot_p= (itext::PdfDictionary*)
								input_reader_p->getPdfObject( (itext::PdfObject*)annots_array_p->get( jj ) );
							if( annot_p && annot_p->isDictionary() ) {

								itext::PdfName* subtype_p= (itext::PdfName*)
									input_reader_p->getPdfObject( annot_p->get( itext::PdfName::SUBTYPE ) );
								if( subtype_p && subtype_p->isName() && 
										subtype_p->equals(itext::PdfName::FILEATTACHMENT) ) {
									
									itext::PdfDictionary* filespec_p= (itext::PdfDictionary*)
										input_reader_p->getPdfObject( annot_p->get( itext::PdfName::FS ) );
									if( filespec_p && filespec_p->isDictionary() ) {
										
										unpack_file( input_reader_p,
																 filespec_p,
																 output_pathname,
																 m_ask_about_warnings_b );
									}
								}
							}
						}
					}
				}
			
		}
	}
}
