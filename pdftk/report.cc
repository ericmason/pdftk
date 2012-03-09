/* -*- Mode: C++; tab-width: 2; c-basic-offset: 2 -*- */
/*
	pdftk, the PDF Toolkit
	Copyright (c) 2003, 2004, 2010 Sid Steward


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
#include <sstream>
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

#include "com/lowagie/text/Document.h"
#include "com/lowagie/text/Rectangle.h"
#include "com/lowagie/text/pdf/PdfObject.h"
#include "com/lowagie/text/pdf/PdfName.h"
#include "com/lowagie/text/pdf/PdfString.h"
#include "com/lowagie/text/pdf/PdfNumber.h"
#include "com/lowagie/text/pdf/PdfArray.h"
#include "com/lowagie/text/pdf/PdfDictionary.h"
#include "com/lowagie/text/pdf/PdfOutline.h"
#include "com/lowagie/text/pdf/PdfCopy.h"
#include "com/lowagie/text/pdf/PdfReader.h"
#include "com/lowagie/text/pdf/PdfImportedPage.h"
#include "com/lowagie/text/pdf/PdfWriter.h"
#include "com/lowagie/text/pdf/PdfStamperImp.h"
#include "com/lowagie/text/pdf/PdfEncryptor.h"
#include "com/lowagie/text/pdf/PdfNameTree.h"
#include "com/lowagie/text/pdf/FdfReader.h"
#include "com/lowagie/text/pdf/AcroFields.h"
#include "com/lowagie/text/pdf/PdfIndirectReference.h"
#include "com/lowagie/text/pdf/PdfIndirectObject.h"
#include "com/lowagie/text/pdf/PdfFileSpecification.h"

#include "com/lowagie/text/pdf/PRStream.h"

using namespace std;

namespace java {
	using namespace java::lang;
	using namespace java::io;
	using namespace java::util;
}

namespace itext {
	using namespace com::lowagie::text;
	using namespace com::lowagie::text::pdf;
}

#include "pdftk.h"
#include "report.h"

static void 
OutputXmlString( ostream& ofs,
								 java::lang::String* jss_p )
{
	if( jss_p ) {
		for( jint ii= 0; ii< jss_p->length(); ++ii ) {
			jchar wc= jss_p->charAt(ii);
			if( 0x20<= wc && wc<= 0x7e ) {
				switch( wc ) {
				case '<' :
					ofs << "&lt;";
					break;
				case '>':
					ofs << "&gt;";
					break;
				case '&':
					ofs << "&amp;";
					break;
				case '"':
					ofs << "&quot;";
					break;
				default:
					ofs << (char)wc;
					break;
				}
			}
			else { // HTML/MXL numerical entity
				ofs << "&#" << (unsigned int)wc << ";";
			}
		}
	}
}

static void 
OutputUtf8String( ostream& ofs,
									java::lang::String* jss_p )
{
	if( jss_p ) {
		int fn_buff_len = JvGetStringUTFLength( jss_p );
		char* fn_buff= (char*)malloc( fn_buff_len* sizeof(char) ); // fn_buff not a C string, not NULL terminated
		JvGetStringUTFRegion( jss_p, 0, jss_p->length(), fn_buff );

		for( int ii= 0; ii< fn_buff_len; ++ii ) {
			ofs << fn_buff[ii];
		}

		free( fn_buff );
	}
}

static void
OutputPdfString( ostream& ofs,
								 itext::PdfString* pdfss_p,
								 bool utf8_b )
{
	if( pdfss_p && pdfss_p->isString() ) {
		java::lang::String* jss_p= pdfss_p->toUnicodeString();
		if( utf8_b ) {
			OutputUtf8String( ofs, jss_p );
		}
		else {
			OutputXmlString( ofs, jss_p );
		}
	}
}

static void
OutputPdfName( ostream& ofs,
							 itext::PdfName* pdfnn_p )
{
	if( pdfnn_p && pdfnn_p->isName() ) {
		java::String* jnn_p= new java::String( pdfnn_p->getBytes() );
		jnn_p= itext::PdfName::decodeName( jnn_p );
		OutputXmlString( ofs, jnn_p );
	}
}

static void
OutputID( ostream& ofs,
					java::lang::String* id_p )
{
	ofs << hex;
	for( jint ii= 0; ii< id_p->length(); ++ii ) {
		unsigned int jc= (unsigned int)id_p->charAt(ii);
		ofs << jc;
	}
	ofs << dec;
}

static int
GetPageNumber( itext::PdfDictionary* dict_p,
							 itext::PdfReader* reader_p )
// take a PdfPage dictionary and return its page location in the document;
// recurse our way up the pages tree, counting pages as we go;
// dict_p may be a page or a page tree object;
// return value is zero-based;
{
	if( dict_p && dict_p->contains( itext::PdfName::PARENT ) ) {
		jint sum_pages= 0;

		itext::PdfDictionary* parent_p= (itext::PdfDictionary*)
			reader_p->getPdfObject( dict_p->get( itext::PdfName::PARENT ) );
		if( parent_p && parent_p->isDictionary() ) {
			// a parent is a page tree object and will have Kids

			itext::PdfArray* parent_kids_p= (itext::PdfArray*)
				reader_p->getPdfObject( parent_p->get( itext::PdfName::KIDS ) );
			if( parent_kids_p && parent_kids_p->isArray() ) {
				// Kids may be Pages or Page Tree Nodes

				// iterate over *dict_p's parent's kids until we run into *dict_p
				java::ArrayList* kids_p= parent_kids_p->getArrayList();
				if( kids_p ) {
					for( jint kids_ii= 0; kids_ii< kids_p->size(); ++kids_ii ) {

						itext::PdfDictionary* kid_p= (itext::PdfDictionary*)
							reader_p->getPdfObject( (itext::PdfDictionary*)(kids_p->get(kids_ii)) );
						if( kid_p && kid_p->isDictionary() ) {

							if( kid_p== dict_p ) {
								// counting kids is complete; recurse

								return sum_pages+ GetPageNumber( parent_p, reader_p ); // <--- return
							}
							else {
								// is kid a page, or is kid a page tree object? add count to sum;
								// PdfDictionary::isPage() and PdfDictionary::isPages()
								// are not reliable, here

								itext::PdfName* kid_type_p= (itext::PdfName*)
									reader_p->getPdfObject( kid_p->get( itext::PdfName::TYPE ) );
								if( kid_type_p && kid_type_p->isName() ) {

									if( kid_type_p->equals( itext::PdfName::PAGE ) ) {
										// *kid_p is a Page

										sum_pages+= 1;
									}
									else if( kid_type_p->equals( itext::PdfName::PAGES ) ) {
										// *kid_p is a Page Tree Node

										itext::PdfNumber* count_p= (itext::PdfNumber*)
											reader_p->getPdfObject( kid_p->get( itext::PdfName::COUNT ) );
										if( count_p && count_p->isNumber() ) {

											sum_pages+= count_p->intValue();

										}
										else { // error
											cerr << "Internal Error: invalid count in GetPageNumber" << endl;
										}
									}
									else { // error
										cerr << "Internal Error: unexpected kid type in GetPageNumber" << endl;
									}
								}
								else { // error
									cerr << "Internal Error: invalid kid_type_p in GetPageNumber" << endl;
								}
							}
						}
						else { // error
							cerr << "Internal Error: invalid kid_p in GetPageNumber" << endl;
						}
					} // done iterating over kids

				}
				else { // error
					cerr << "Internal Error: invalid kids_p in GetPageNumber" << endl;
				}
			}
			else { // error
				cerr << "Internal Error: invalid kids array GetPageNumber" << endl;
			}
		}
		else { // error
			cerr << "Internal Error: invalid parent in GetPageNumber" << endl;
		}
	}
	else {
		// *dict_p has no parent; end recursion
		return 0;
	}

	// error: should have recursed
	cerr << "Internal Error: recursion case skipped in GetPageNumber" << endl;

	return 0;
}

static void
ReportOutlines( ostream& ofs, 
								itext::PdfDictionary* outline_p,
								int level,
								itext::PdfReader* reader_p,
								bool utf8_b )
{
	// the title; HTML-compatible
	ofs << "BookmarkTitle: ";
	itext::PdfString* title_p= (itext::PdfString*)
		reader_p->getPdfObject( outline_p->get( itext::PdfName::TITLE ) );
	if( title_p && title_p->isString() ) {

		OutputPdfString( ofs, title_p, utf8_b );
		
		ofs << endl;
	}
	else { // error
		ofs << "[ERROR: TITLE NOT FOUND]" << endl;
	}

	// the level; 1-based to jive with HTML heading level concept
	ofs << "BookmarkLevel: " << level+ 1 << endl;

	// page number, 1-based; 
	// a zero value indicates no page destination or an error
	ofs << "BookmarkPageNumber: ";
	{
		bool fail_b= false;

		// the destination object may take be in a couple different places
		// and may take a couple, different forms

		itext::PdfObject* destination_p= 0; {
			if( outline_p->contains( itext::PdfName::DEST ) ) {
				destination_p=
					reader_p->getPdfObject( outline_p->get( itext::PdfName::DEST ) );
			}
			else if( outline_p->contains( itext::PdfName::A ) ) {

				itext::PdfDictionary* action_p= (itext::PdfDictionary*)
					reader_p->getPdfObject( outline_p->get( itext::PdfName::A ) );
				if( action_p && action_p->isDictionary() ) {

					// TODO: confirm action subtype of GoTo
					itext::PdfName* s_p= (itext::PdfName*)
						reader_p->getPdfObject( action_p->get( itext::PdfName::S ) );
					if( s_p && s_p->isName() ) {

						if( s_p->equals( itext::PdfName::GOTO ) ) {
							destination_p=
								reader_p->getPdfObject( action_p->get( itext::PdfName::D ) );
						}
						else { // immediate action is not a link in this document;
							// not an error

							fail_b= true;
						}
					}
					else { // error
						fail_b= true;
					}
				}
				else { // error
					fail_b= true;
				}
			}
			else { // unexpected
				fail_b= true;
			}
		}

		// destination is an array
		if( destination_p && destination_p->isArray() ) {

			java::ArrayList* array_list_p= ((itext::PdfArray*)destination_p)->getArrayList();
			if( array_list_p && !array_list_p->isEmpty() ) {

				itext::PdfDictionary* page_p= (itext::PdfDictionary*)
					reader_p->getPdfObject( (itext::PdfObject*)(array_list_p->get(0)) );

				if( page_p && page_p->isDictionary() ) {
					ofs << GetPageNumber(page_p, reader_p)+ 1 << endl;
				}
				else { // error
					fail_b= true;
				}
			}
			else { // error
				fail_b= true;
			}
		} // TODO: named destinations handling
		else { // error
			fail_b= true;
		}

		if( fail_b ) { // output our 'null page reference' code
			ofs << 0 << endl;
		}
	}

	// recurse into any children
	if( outline_p->contains( itext::PdfName::FIRST ) ) {

		itext::PdfDictionary* child_p= (itext::PdfDictionary*)
			reader_p->getPdfObject( outline_p->get( itext::PdfName::FIRST ) );
		if( child_p && child_p->isDictionary() ) {

			ReportOutlines( ofs, child_p, level+ 1, reader_p, utf8_b );
		}
	}

	// recurse into next sibling
	if( outline_p->contains( itext::PdfName::NEXT ) ) {

		itext::PdfDictionary* sibling_p= (itext::PdfDictionary*)
			reader_p->getPdfObject( outline_p->get( itext::PdfName::NEXT ) );
		if( sibling_p && sibling_p->isDictionary() ) {

			ReportOutlines( ofs, sibling_p, level, reader_p, utf8_b );
		}
	}
}

static void
ReportInfo( ostream& ofs,
						itext::PdfDictionary* info_p,
						bool utf8_b )
{
	if( info_p && info_p->isDictionary() ) {
		java::Set* keys_p= info_p->getKeys();

		// iterate over Info keys
		for( java::Iterator* it= keys_p->iterator(); it->hasNext(); ) {

			itext::PdfName* key_p= (itext::PdfName*)it->next();
			int key_len= JvGetArrayLength( key_p->getBytes() )- 1; // minus one for init. slash

			itext::PdfObject* value_p= (itext::PdfObject*)info_p->get( key_p );

			// don't output empty keys or values
			if( 0< key_len &&
					value_p->isString() && 
					0< ((itext::PdfString*)value_p)->toString()->length() ) 
				{ // ouput
					/*
					const int buff_size= 128;
					char buff[buff_size];
					memset( buff, 0, buff_size );

					// convert the PdfName into a c-string; omit initial slash
					// TODO: invoke PdfName::decodeName()
					strncpy( buff, 
									 (char*)elements( key_p->getBytes() )+ 1,
									 ( key_len< buff_size- 1 ) ? key_len : buff_size- 1 );

					ofs << "InfoKey: " << buff << endl;
					*/
					ofs << "InfoKey: ";
					OutputPdfName( ofs, key_p );
					ofs << endl;

					ofs << "InfoValue: ";
					OutputPdfString( ofs, (itext::PdfString*)value_p, utf8_b );
					ofs << endl;
				}
		}

	}
	else { // error
	}
}

static void
ReportPageLabels( ostream& ofs,
									itext::PdfDictionary* numtree_node_p,
									itext::PdfReader* reader_p,
									bool utf8_b )
	// if *numtree_node_p has Nums, report them;
	// else if *numtree_node_p has Kids, recurse
	// output 1-based page numbers; that's what we do for bookmarks
{
	itext::PdfArray* nums_p= (itext::PdfArray*)
		reader_p->getPdfObject( numtree_node_p->get( itext::PdfName::NUMS ) );
	if( nums_p && nums_p->isArray() ) {
		// report page numbers

		java::ArrayList* labels_p= nums_p->getArrayList();
		if( labels_p ) {
			for( jint labels_ii= 0; labels_ii< labels_p->size(); labels_ii+=2 ) {
				
				// label index
				itext::PdfNumber* index_p= (itext::PdfNumber*)
					reader_p->getPdfObject( (itext::PdfNumber*)(labels_p->get(labels_ii)) );

				// label dictionary
				itext::PdfDictionary* label_p= (itext::PdfDictionary*)
					reader_p->getPdfObject( (itext::PdfDictionary*)(labels_p->get(labels_ii+ 1)) );

				if( index_p && index_p->isNumber() &&
						label_p && label_p->isDictionary() )
					{
						// PageLabelNewIndex
						ofs << "PageLabelNewIndex: " << (long)(index_p->intValue())+ 1 << endl;
						
						{ // PageLabelStart
							ofs << "PageLabelStart: "; 
							itext::PdfNumber* start_p= (itext::PdfNumber*)
								reader_p->getPdfObject( label_p->get( itext::PdfName::ST ) );
							if( start_p && start_p->isNumber() ) {
								ofs << (long)(start_p->intValue()) << endl;
							}
							else {
								ofs << "1" << endl; // the default
							}
						}

						{ // PageLabelPrefix
							itext::PdfString* prefix_p= (itext::PdfString*)
								reader_p->getPdfObject( label_p->get( itext::PdfName::P ) );
							if( prefix_p && prefix_p->isString() ) {
								ofs << "PageLabelPrefix: ";
								OutputPdfString( ofs, prefix_p, utf8_b );
								ofs << endl;
							}
						}

						{ // PageLabelNumStyle
							itext::PdfName* r_p= new itext::PdfName(JvNewStringUTF("r"));
							itext::PdfName* a_p= new itext::PdfName(JvNewStringUTF("a"));

							itext::PdfName* style_p= (itext::PdfName*)
								reader_p->getPdfObject( label_p->get( itext::PdfName::S ) );
							ofs << "PageLabelNumStyle: ";
							if( style_p && style_p->isName() ) {
								if( style_p->equals( itext::PdfName::D ) ) {
									ofs << "DecimalArabicNumerals" << endl;
								}
								else if( style_p->equals( itext::PdfName::R ) ) {
									ofs << "UppercaseRomanNumerals" << endl;
								}
								else if( style_p->equals( r_p ) ) {
									ofs << "LowercaseRomanNumerals" << endl;
								}
								else if( style_p->equals( itext::PdfName::A ) ) {
									ofs << "UppercaseLetters" << endl;
								}
								else if( style_p->equals( a_p ) ) {
									ofs << "LowercaseLetters" << endl;
								}
								else { // error
									ofs << "[ERROR]" << endl;
								}
							}
							else { // default
								ofs << "NoNumber" << endl;
							}
						}

					}
				else { // error
					ofs << "[ERROR: INVALID label_p IN ReportPageLabelNode]" << endl;
				}
			}
		}
		else { // error
			ofs << "[ERROR: INVALID labels_p IN ReportPageLabelNode]" << endl;
		}
	}
	else { // try recursing
		itext::PdfArray* kids_p= (itext::PdfArray*)
			reader_p->getPdfObject( numtree_node_p->get( itext::PdfName::KIDS ) );
		if( kids_p && kids_p->isArray() ) {

			java::ArrayList* kids_ar_p= kids_p->getArrayList();
			if( kids_ar_p ) {
				for( jint kids_ii= 0; kids_ii< kids_ar_p->size(); ++kids_ii ) {

					itext::PdfDictionary* kid_p= (itext::PdfDictionary*)
						reader_p->getPdfObject( (itext::PdfDictionary*)kids_ar_p->get(kids_ii) );
					if( kid_p && kid_p->isDictionary() ) {

						// recurse
						ReportPageLabels( ofs, kid_p, reader_p, utf8_b );
					}
					else { // error
						ofs << "[ERROR: INVALID kid_p]" << endl;
					}
				}
			}
			else { // error
				ofs << "[ERROR: INVALID kids_ar_p]" << endl;
			}
		}
		else { // error; a number tree must have one or the other
			ofs << "[ERROR: INVALID PAGE LABEL NUMBER TREE]" << endl;
		}
	}
}

class FormField {
	public:
	string m_ft; // type
	string m_tt; // name
	string m_tu; // alt. name
	int m_ff; // flags
	string m_vv; // value
	string m_dv; // default value

	// variable-text features
	int m_qq; // quadding (justification)
	string m_ds; // default style (rich text)
	string m_rv; // rich text value

	int m_maxlen;

	// for checkboxes and such
	set< string > m_states; // possible states
	string m_state;

	FormField() : m_ft(), m_tt(), m_tu(), m_ff(0), m_vv(), m_dv(),
								m_qq(0), m_ds(), m_rv(),
								m_maxlen(0),
								m_states(), m_state() {}
};

static void
	OutputFormField( ostream& ofs,
									 const FormField& ff )
{
	ofs << "---" << endl; // delim
	ofs << "FieldType: " << ff.m_ft << endl;
	ofs << "FieldName: " << ff.m_tt << endl;
	if( !ff.m_tu.empty() )
		ofs << "FieldNameAlt: " << ff.m_tu << endl;
	ofs << "FieldFlags: " << ff.m_ff << endl;
	if( !ff.m_vv.empty() )
		ofs << "FieldValue: " << ff.m_vv << endl;
	if( !ff.m_dv.empty() )
		ofs << "FieldValueDefault: " << ff.m_dv << endl;

	ofs << "FieldJustification: ";
	switch( ff.m_qq ) {
	case 0:
		ofs << "Left";
		break;
	case 1:
		ofs << "Center";
		break;
	case 2:
		ofs << "Right";
		break;
	default:
		ofs << ff.m_qq;
		break;
	}
	ofs << endl;
	
	if( !ff.m_ds.empty() )
		ofs << "FieldStyleDefault: " << ff.m_ds << endl;
	if( !ff.m_rv.empty() )
		ofs << "FieldValueRichText: " << ff.m_rv << endl;
	if( 0< ff.m_maxlen )
		ofs << "FieldMaxLength: " << ff.m_maxlen << endl;

	for( set< string >::const_iterator it= ff.m_states.begin();
			 it!= ff.m_states.end(); ++it )
		{
			ofs << "FieldStateOption: " << *it << endl;
		}
}

static bool
ReportAcroFormFields( ostream& ofs,
											itext::PdfArray* kids_array_p,
											FormField& acc_state,
											itext::PdfReader* reader_p,
											bool utf8_b )
{
	FormField prev_state= acc_state;
	bool ret_val_b= false;

	java::ArrayList* kids_p= kids_array_p->getArrayList();
	if( kids_p ) {
		for( jint kids_ii= 0; kids_ii< kids_p->size(); ++kids_ii ) {

			itext::PdfDictionary* kid_p= (itext::PdfDictionary*)
				reader_p->getPdfObject( (itext::PdfDictionary*)(kids_p->get(kids_ii)) );
			if( kid_p && kid_p->isDictionary() ) {

				// field type
				if( kid_p->contains( itext::PdfName::FT ) ) {
					itext::PdfName* ft_p= (itext::PdfName*)
						reader_p->getPdfObject( kid_p->get( itext::PdfName::FT ) );
					if( ft_p && ft_p->isName() ) {
						
						if( ft_p->equals( itext::PdfName::BTN ) ) { // button
							acc_state.m_ft= "Button";
						}
						else if( ft_p->equals( itext::PdfName::TX ) ) { // text
							acc_state.m_ft= "Text";
						}
						else if( ft_p->equals( itext::PdfName::CH ) ) { // choice
							acc_state.m_ft= "Choice";
						}
						else if( ft_p->equals( itext::PdfName::SIG ) ) { // signature
							acc_state.m_ft= "Signature";
						}
						else { // error
							cerr << "Internal Error: unexpected field type in ReportAcroFormFields()" << endl;
						}
					}
				}

				// field name; special inheritance rule: prepend parent name
				if( kid_p->contains( itext::PdfName::T ) ) {
					itext::PdfString* pdfs_p= (itext::PdfString*)
						reader_p->getPdfObject( kid_p->get( itext::PdfName::T ) );
					if( pdfs_p && pdfs_p->isString() ) {

						ostringstream name_oss;
						OutputPdfString( name_oss, pdfs_p, utf8_b );

						if( !acc_state.m_tt.empty() ) {
							acc_state.m_tt+= ".";
						}
						acc_state.m_tt+= name_oss.str();
					}
				}

				// field alt. name
				if( kid_p->contains( itext::PdfName::TU ) ) {
					itext::PdfString* pdfs_p= (itext::PdfString*)
						reader_p->getPdfObject( kid_p->get( itext::PdfName::TU ) );
					if( pdfs_p && pdfs_p->isString() ) {

						ostringstream name_oss;
						OutputPdfString( name_oss, pdfs_p, utf8_b );
						acc_state.m_tu= name_oss.str();
					}
				}
				else {
					acc_state.m_tu.erase();
				}

				// field flags; inheritable
				if( kid_p->contains( itext::PdfName::FF ) ) {
					itext::PdfNumber* pdfs_p= (itext::PdfNumber*)
						reader_p->getPdfObject( kid_p->get( itext::PdfName::FF ) );
					if( pdfs_p && pdfs_p->isNumber() ) {

						acc_state.m_ff= pdfs_p->intValue();
					}
				}

				// field value; inheritable; may be string or name
				if( kid_p->contains( itext::PdfName::V ) ) {
					itext::PdfObject* pdfs_p= 
						reader_p->getPdfObject( kid_p->get( itext::PdfName::V ) );
					if( pdfs_p && pdfs_p->isString() ) {
						ostringstream name_oss;
						OutputPdfString( name_oss, (itext::PdfString*)pdfs_p, utf8_b );
						acc_state.m_vv= name_oss.str();
					}
					if( pdfs_p && pdfs_p->isName() ) {
						ostringstream name_oss;
						OutputPdfName( name_oss, (itext::PdfName*)pdfs_p );
						acc_state.m_vv= name_oss.str();
					}
				}

				// default value; inheritable
				if( kid_p->contains( itext::PdfName::DV ) ) {
					itext::PdfString* pdfs_p= (itext::PdfString*)
						reader_p->getPdfObject( kid_p->get( itext::PdfName::DV ) );
					if( pdfs_p && pdfs_p->isString() ) {

						ostringstream name_oss;
						OutputPdfString( name_oss, pdfs_p, utf8_b );
						acc_state.m_dv= name_oss.str();
					}
				}

				// quadding; inheritable
				if( kid_p->contains( itext::PdfName::Q ) ) {
					itext::PdfNumber* pdfs_p= (itext::PdfNumber*)
						reader_p->getPdfObject( kid_p->get( itext::PdfName::Q ) );
					if( pdfs_p && pdfs_p->isNumber() ) {

						acc_state.m_qq= pdfs_p->intValue();
					}
				}

				// default style
				if( kid_p->contains( itext::PdfName::DS ) ) {
					itext::PdfString* pdfs_p= (itext::PdfString*)
						reader_p->getPdfObject( kid_p->get( itext::PdfName::DS ) );
					if( pdfs_p && pdfs_p->isString() ) {

						ostringstream name_oss;
						OutputPdfString( name_oss, pdfs_p, utf8_b );
						acc_state.m_ds= name_oss.str();
					}
				}
				else {
					acc_state.m_ds.erase();
				}

				// rich text value; may be a string or a stream
				if( kid_p->contains( itext::PdfName::RV ) ) {
					itext::PdfObject* pdfo_p= (itext::PdfObject*)
						reader_p->getPdfObject( kid_p->get( itext::PdfName::RV ) );
					if( pdfo_p && pdfo_p->isString() ) { // string
						itext::PdfString* pdfs_p= (itext::PdfString*)pdfo_p;

						ostringstream name_oss;
						OutputPdfString( name_oss, pdfs_p, utf8_b );
						acc_state.m_rv= name_oss.str();
					}
					else if( pdfo_p && pdfo_p->isStream() ) { // stream
						itext::PRStream* pdfs_p= (itext::PRStream*)pdfo_p;
						jbyteArray pdfsa_p= pdfs_p->getBytes();
						acc_state.m_rv= (char*)(elements(pdfsa_p));
					}
				}
				else {
					acc_state.m_rv.erase();
				}

				// maximum length; inheritable
				if( kid_p->contains( itext::PdfName::MAXLEN ) ) {
					itext::PdfNumber* pdfs_p= (itext::PdfNumber*)
						reader_p->getPdfObject( kid_p->get( itext::PdfName::MAXLEN ) );
					if( pdfs_p && pdfs_p->isNumber() ) {

						acc_state.m_maxlen= pdfs_p->intValue();
					}
				}

				// available states
				if( kid_p->contains( itext::PdfName::AP ) ) {
					itext::PdfDictionary* ap_p= (itext::PdfDictionary*)
						reader_p->getPdfObject( kid_p->get( itext::PdfName::AP ) );
					if( ap_p && ap_p->isDictionary() ) {

						// this is one way to cull button option names: iterate over
						// appearance state names

						// N
						if( ap_p->contains( itext::PdfName::N ) ) {
							itext::PdfObject* n_p= 
								reader_p->getPdfObject( ap_p->get( itext::PdfName::N ) );
							if( n_p && n_p->isDictionary() ) {
								java::Set* n_set_p= ((itext::PdfDictionary*)n_p)->getKeys();
								for( java::Iterator* it= n_set_p->iterator(); it->hasNext(); ) {
									itext::PdfName* key_p= (itext::PdfName*)it->next();

									ostringstream oss;
									OutputPdfName( oss, key_p );
									acc_state.m_states.insert( oss.str() );
								}
							}
						}

						// D
						if( ap_p->contains( itext::PdfName::D ) ) {
							itext::PdfObject* n_p= 
								reader_p->getPdfObject( ap_p->get( itext::PdfName::D ) );
							if( n_p && n_p->isDictionary() ) {
								java::Set* n_set_p= ((itext::PdfDictionary*)n_p)->getKeys();
								for( java::Iterator* it= n_set_p->iterator(); it->hasNext(); ) {
									itext::PdfName* key_p= (itext::PdfName*)it->next();

									ostringstream oss;
									OutputPdfName( oss, key_p );
									acc_state.m_states.insert( oss.str() );
								}
							}
						}

						// R
						if( ap_p->contains( itext::PdfName::R ) ) {
							itext::PdfObject* n_p= 
								reader_p->getPdfObject( ap_p->get( itext::PdfName::N ) );
							if( n_p && n_p->isDictionary() ) {
								java::Set* n_set_p= ((itext::PdfDictionary*)n_p)->getKeys();
								for( java::Iterator* it= n_set_p->iterator(); it->hasNext(); ) {
									itext::PdfName* key_p= (itext::PdfName*)it->next();

									ostringstream oss;
									OutputPdfName( oss, key_p );
									acc_state.m_states.insert( oss.str() );
								}
							}
						}

					}
				}

				// list-box / combo-box possible states
				if( kid_p->contains( itext::PdfName::OPT ) ) {
					itext::PdfArray* kid_opts_p= (itext::PdfArray*)
						reader_p->getPdfObject( kid_p->get( itext::PdfName::OPT ) );
					if( kid_opts_p && kid_opts_p->isArray() ) {
						java::ArrayList* opts_p= kid_opts_p->getArrayList();
						for( jint opts_ii= 0; opts_ii< opts_p->size(); ++opts_ii ) {
							itext::PdfString* opt_p= (itext::PdfString*)
								reader_p->getPdfObject( (itext::PdfObject*)(opts_p->get(opts_ii)) );
							if( opt_p && opt_p->isString() ) {
								ostringstream name_oss;
								OutputPdfString( name_oss, opt_p, utf8_b );
								acc_state.m_states.insert( name_oss.str() );
							}
						}
					}
				}

				if( kid_p->contains( itext::PdfName::KIDS ) ) { // recurse
					itext::PdfArray* kid_kids_p= (itext::PdfArray*)
						reader_p->getPdfObject( kid_p->get( itext::PdfName::KIDS )  );
					if( kid_kids_p && kid_kids_p->isArray() ) {

						bool kids_have_names_b=
							ReportAcroFormFields( ofs, kid_kids_p, acc_state, reader_p, utf8_b );

						if( !kids_have_names_b &&
								kid_p->contains( itext::PdfName::T ) )
							{ 
								// dump form field
								OutputFormField( ofs, acc_state );
							}

						// reset state; 
						acc_state= prev_state;
					}
					else { // error
					}
				}
				else if( kid_p->contains( itext::PdfName::T ) ) { 
					// term. field; dump form field
					OutputFormField( ofs, acc_state );

					// reset state; 
					acc_state= prev_state;

					// record presense of field name
					ret_val_b= true;
				}

			}
		}
	}
	else { // error
		cerr << "Internal Error: unable to get ArrayList in ReportAcroFormFields()" << endl;
	}

	return ret_val_b;
}

void
ReportAcroFormFields( ostream& ofs,
											itext::PdfReader* reader_p,
											bool utf8_b )
{
	itext::PdfDictionary* catalog_p= reader_p->catalog;
	if( catalog_p && catalog_p->isDictionary() ) {
		
		itext::PdfDictionary* acro_form_p= (itext::PdfDictionary*)
			reader_p->getPdfObject( catalog_p->get( itext::PdfName::ACROFORM ) );
		if( acro_form_p && acro_form_p->isDictionary() ) {

			itext::PdfArray* fields_p= (itext::PdfArray*)
				reader_p->getPdfObject( acro_form_p->get( itext::PdfName::FIELDS ) );
			if( fields_p && fields_p->isArray() ) {

				// enter recursion
				FormField root_field_state;
				ReportAcroFormFields( ofs, fields_p, root_field_state, reader_p, utf8_b );
			}
		}
	}
	else { // error
		cerr << "Internal Error: unable to access PDF catalog from ReportAcroFormFields()" << endl;
	}
}

void
ReportOnPdf( ostream& ofs,
						 itext::PdfReader* reader_p,
						 bool utf8_b )
{
	{ // trailer data
		itext::PdfDictionary* trailer_p= reader_p->getTrailer();
		if( trailer_p && trailer_p->isDictionary() ) {

			{ // metadata
				itext::PdfDictionary* info_p= (itext::PdfDictionary*)
					reader_p->getPdfObject( trailer_p->get( itext::PdfName::INFO ) );
				if( info_p && info_p->isDictionary() ) {
						
					ReportInfo( ofs, info_p, utf8_b );
				}
				else { // warning
					cerr << "Warning: no info dictionary found" << endl;
				}
			}

			{ // pdf ID; optional
				itext::PdfArray* id_p= (itext::PdfArray*)
					reader_p->getPdfObject( trailer_p->get( itext::PdfName::ID ) );
				if( id_p && id_p->isArray() ) {

					java::ArrayList* id_al_p= id_p->getArrayList();
					if( id_al_p ) {

						for( jint ii= 0; ii< id_al_p->size(); ++ii ) {
							ofs << "PdfID" << (int)ii << ": ";

							itext::PdfString* id_ss_p= (itext::PdfString*)
								reader_p->getPdfObject( (itext::PdfObject*)id_al_p->get(ii) );
							if( id_ss_p && id_ss_p->isString() ) {
									
								OutputID( ofs, id_ss_p->toString() );
							}
							else { // error
								cerr << "Internal Error: invalid pdf id array string" << endl;
							}

							ofs << endl;
						}
					}
					else { // error
						cerr << "Internal Error: invalid ID ArrayList" << endl;
					}
				}
			}

		}
		else { // error
			cerr << "InternalError: invalid trailer" << endl;
		}
	}

	{ // number of pages and outlines
		itext::PdfDictionary* catalog_p= reader_p->catalog;
		if( catalog_p && catalog_p->isDictionary() ) {

			// number of pages
			itext::PdfDictionary* pages_p= (itext::PdfDictionary*)
				reader_p->getPdfObject( catalog_p->get( itext::PdfName::PAGES ) );
			if( pages_p && pages_p->isDictionary() ) {

				itext::PdfNumber* count_p= (itext::PdfNumber*)
					reader_p->getPdfObject( pages_p->get( itext::PdfName::COUNT ) );
				if( count_p && count_p->isNumber() ) {

					ofs << "NumberOfPages: " << (unsigned int)count_p->intValue() << endl;
				}
				else { // error
					cerr << "Internal Error: invalid count_p in ReportOnPdf()" << endl;
				}
			}
			else { // error
				cerr << "Internal Error: invalid pages_p in ReportOnPdf()" << endl;
			}

			// outlines; optional
			itext::PdfDictionary* outlines_p= (itext::PdfDictionary*)
				reader_p->getPdfObject( catalog_p->get( itext::PdfName::OUTLINES ) );
			if( outlines_p && outlines_p->isDictionary() ) {

				itext::PdfDictionary* top_outline_p= (itext::PdfDictionary*)
					reader_p->getPdfObject( outlines_p->get( itext::PdfName::FIRST ) );
				if( top_outline_p && top_outline_p->isDictionary() ) {

					ReportOutlines( ofs, top_outline_p, 0, reader_p, utf8_b );
				}
				else { // error
					// okay, not a big deal
					// cerr << "Internal Error: invalid top_outline_p in ReportOnPdf()" << endl;
				}
			}

		}
		else { // error
			cerr << "InternalError:" << endl;
		}
	}

	{ // page labels (a/k/a logical page numbers)
		itext::PdfDictionary* catalog_p= reader_p->catalog;
		if( catalog_p && catalog_p->isDictionary() ) {

			itext::PdfDictionary* pagelabels_p= (itext::PdfDictionary*)
				reader_p->getPdfObject( catalog_p->get( itext::PdfName::PAGELABELS ) );
			if( pagelabels_p && pagelabels_p->isDictionary() ) {

				ReportPageLabels( ofs, pagelabels_p, reader_p, utf8_b );
			}
		}
		else { // error
			cerr << "InternalError:" << endl;
		}
	}

} // end: ReportOnPdf

//////
////  writing data to PDF
// 

static bool
LoadInfoFile( istream& ifs,
							map< string, string >* info_map_p )
{
  if( ifs ) {
    string infokey, infovalue;
		const int buff_size= 4096;
		char buff[buff_size];

    while( ifs ) {
			ifs.getline( buff, buff_size );
			int buff_i= 0;
			if( strncmp( buff, "InfoKey:", 8 )== 0 ) {
				buff_i= 8;
				while( buff[buff_i] && isspace(buff[buff_i]) ) { ++buff_i; }
				if( buff[buff_i] ) {
					infokey= buff+ buff_i;
				}
			}
			else if( strncmp( buff, "InfoValue:", 10 )== 0 ) {
				buff_i= 10;
				while( buff[buff_i] && isspace(buff[buff_i]) ) { ++buff_i; }
				if( buff[buff_i] ) {
					infovalue= buff+ buff_i;
				}
				// empty infovalue OK

				if( !infokey.empty() ) {
					(*info_map_p)[infokey]= infovalue;

					infokey.erase();
					infovalue.erase();
				}
			}
			else {
        infokey.erase();
        infovalue.erase();
			}
    }
  }
  else { // error
    cerr << "Error: Unable to read Info data" << endl;
    return false;
  }

  return true;
}

void
XmlStringToJcharArray( jchar* jvs,
											 jsize jvs_size,
											 jsize* jvs_len_p,
											 string ss )
	// our dump_data and burst operations use HTML/XML
	// entities to represent non-ASCII character codes;
	// decode these entities and pack them into jvs;
	// keep in mind that not all input strings will use
	// entities;
{
	*jvs_len_p= 0;
	jsize jvs_i= 0;
	bool inside_entity_b= false;
	string buff;
	for( string::const_iterator it= ss.begin(); it!= ss.end() && jvs_i< jvs_size- 1; ++it ) {
		if( inside_entity_b ) {
			buff+= *it;
			if( *it== ';' ) { // end of entity
				if( buff== "&lt;" ) {
					jvs[jvs_i++]= (jchar)'<';
				}
				else if( buff== "&gt;" ) {
					jvs[jvs_i++]= (jchar)'>';
				}
				else if( buff== "&amp;" ) {
					jvs[jvs_i++]= (jchar)'&';
				}
				else if( buff== "&quot;" ) {
					jvs[jvs_i++]= (jchar)'"';
				}
				else { // numerical ent.?
					bool numerical_b= true;
					int number= 0;
					string::const_iterator jt= buff.begin();
					if( *jt!= '&' )
						numerical_b= false;
					if( *(++jt)!= '#' )
						numerical_b= false;
					while( ++jt!= buff.end() ) {
						if( '0'<= *jt && *jt<= '9' ) {
							number= number* 10+ *jt- '0';
						}
						else if( *jt== ';' && ++jt== buff.end() ) {
							break;
						}
						else {
							numerical_b= false;
							break;
						}
					}
					if( numerical_b ) {
						jvs[jvs_i++]= number;
					}
					else { // pack buff into jvs
						for( jt= buff.begin(); jt!= buff.end() && jvs_i< jvs_size- 1; ++jt ) {
							jvs[jvs_i++]= (jchar)*jt;
						}
					}
				}

				buff.erase();
				inside_entity_b= false;
			} // end: end of entity
		}
		else if( *it== '&' ) {
			buff= "&";
			inside_entity_b= true;
		}
		else {
			jvs[jvs_i++]= (jchar)*it;
		}
	}

	if( !buff.empty() ) {
		for( string::const_iterator jt= buff.begin(); 
				 jt!= buff.end() && jvs_i< jvs_size- 1; ++jt )
			{
				jvs[jvs_i++]= (jchar)*jt;
			}
	}

	jvs[jvs_i]= 0; // null term.
	*jvs_len_p= jvs_i;
}

bool
UpdateInfo( itext::PdfReader* reader_p,
						istream& ifs,
						bool utf8_b )
{
	bool ret_val_b= true;

	map< string, string > info_map;
	if( LoadInfoFile( ifs, &info_map ) ) 
		{ // trailer data
			itext::PdfDictionary* trailer_p= reader_p->getTrailer();
			if( trailer_p && trailer_p->isDictionary() ) {
				
				// metadata
				itext::PdfDictionary* info_p= (itext::PdfDictionary*)
					reader_p->getPdfObject( trailer_p->get( itext::PdfName::INFO ) );
				if( info_p && info_p->isDictionary() ) {
	
					for( map< string, string >::const_iterator it= info_map.begin();
							 it!= info_map.end(); ++it )
						{
							if( it->second.empty() ) {
								info_p->remove( new itext::PdfName( JvNewStringUTF(it->first.c_str()) ) );
							}
							else {
								const jsize jvs_size= 4096;
								jchar jvs[jvs_size];
								jsize jvs_len= 0;
								XmlStringToJcharArray( jvs, jvs_size, &jvs_len, it->second );

								if( utf8_b ) {
									info_p->put( new itext::PdfName( JvNewStringUTF(it->first.c_str()) ),
															 // patch by Quentin Godfroy <godfroy@clipper.ens.fr>, Chris Adams <cadams@salk.edu>
															 new itext::PdfString( JvNewStringUTF((char* )it->second.c_str()),
																										 ( strcmp(it->first.c_str(), "ModDate") && 
																											 strcmp(it->first.c_str(), "CreationDate") ) ?
																										 itext::PdfObject::TEXT_UNICODE :
																										 itext::PdfObject::TEXT_PDFDOCENCODING ) );
								}
								else {
									info_p->put( new itext::PdfName( JvNewStringUTF(it->first.c_str()) ),
															 new itext::PdfString( JvNewString(jvs, jvs_len),
																										 ( strcmp(it->first.c_str(), "ModDate") && 
																											 strcmp(it->first.c_str(), "CreationDate") ) ?
																										 itext::PdfObject::TEXT_UNICODE :
																										 itext::PdfObject::TEXT_PDFDOCENCODING ) );
								}
							}
						}
				}
				else { // error
					cerr << "Internal Error: no Info dictionary found, so no Info added." << endl;
					ret_val_b= false;
				}
			}
			else { // error
				cerr << "Internal Error: no document trailer found, so no Info added." << endl;
				ret_val_b= false;
			}
		}
	else { // error
		cerr << "Error: unable to load Info data" << endl;
		ret_val_b= false;
	}

	return ret_val_b;
}

/*

static bool
copyStdinToFile( const char* fn )
{
	bool ret_val_b= true;

	FILE* fp= fopen( fn, "wb" );
	if( fp ) {
		int cc= 0;
		while( (cc=fgetc(stdin))!= EOF ) {
			fputc( cc, fp );
		}
		ret_val_b= (ferror(fp)==0);
		fclose( fp );
	}
	else {
		ret_val_b= false;
	}

	return ret_val_b;
}

bool
ReplaceXmp( itext::PdfReader* reader_p,
						string xmp_filename )
{
	bool ret_val_b= true;

	char xmp_fn_1[L_tmpnam]= "";

	itext::PdfDictionary* catalog_p= reader_p->catalog;
	if( catalog_p && catalog_p->isDictionary() ) {

		// stdin? copy to temp file
		if( xmp_filename== "-" ) {
			tmpnam( xmp_fn_1 );
			ret_val_b= copyStdinToFile( xmp_fn_1 );
			xmp_filename= xmp_fn_1;
		}
		if( ret_val_b ) {

			string xmp_ss; {
				FILE* fp= fopen( xmp_filename.c_str(), "r" );
				if( fp ) {
					int cc= 0;
					while( (cc=fgetc(fp))!= EOF ) {
						xmp_ss+= cc;
					}
					ret_val_b= (ferror(fp)==0);
					fclose( fp );
				}
				else {
					ret_val_b= false;
				}
			}
			if( ret_val_b ) {

				jbyteArray xmp_sa_p= JvNewByteArray( xmp_ss.size() );
				memcpy( (char*)(elements(xmp_sa_p)),
								xmp_ss.c_str(),
								xmp_ss.size() );

				itext::PdfStream* xmp_str_p= new itext::PdfStream( xmp_sa_p );
				if( xmp_str_p ) {
					xmp_str_p->put( itext::PdfName::TYPE, itext::PdfName::METADATA );
					xmp_str_p->put( itext::PdfName::SUBTYPE, itext::PdfName::XML );
			
					itext::PdfIndirectReference* xmp_str_ref_p=
						(itext::PdfIndirectReference*)reader_p->getPRIndirectReference( xmp_str_p );

					catalog_p->put( itext::PdfName::METADATA, xmp_str_ref_p );
				}
				else {
					ret_val_b= false;
				}
			}
		}

		if( xmp_fn_1[0] ) {
			remove( xmp_fn_1 );
		}
	}
	else {
		ret_val_b= false;
	}

	return ret_val_b;
}

bool
UpdateXmp( itext::PdfReader* reader_p,
					 string xmp_filename )
{
	// assume we already tested for existence of rdfcat program;
	// using temp files here because it seems less expensive
	// than forking pdftk and then using pipes;

	bool ret_val_b= true;

	jbyteArray metadata_p= reader_p->getMetadata();
	if( metadata_p ) {

		char xmp_fn_1[L_tmpnam]= "";
		char xmp_fn_2[L_tmpnam]= "";
		char xmp_out_fn[L_tmpnam]= "";

		tmpnam( xmp_fn_2 );
		tmpnam( xmp_out_fn );

		// copy PDF's current XMP to temp file
		FILE* fp= fopen( xmp_fn_2, "wb" );
		if( fp ) {
			fputs( (char*)elements(metadata_p), fp );
			fclose( fp );

			// stdin? copy to temp file
			if( xmp_filename== "-" ) {
				tmpnam( xmp_fn_1 );
				ret_val_b= copyStdinToFile( xmp_fn_1 );
				xmp_filename= xmp_fn_1;
			}
			if( ret_val_b ) {

				string command= string("rdfcat -sxp ")+ xmp_filename+ 
					string(" ")+ string(xmp_fn_2)+ string(" > ")+ string(xmp_out_fn);
					
				// TODO: rdfcat needs to return success code
				ret_val_b= (system(command.c_str())== 0);
				if( ret_val_b ) {
						
					ret_val_b= ReplaceXmp( reader_p, xmp_out_fn );
				}

				remove( xmp_out_fn );
				if( xmp_fn_1[0] ) {
					remove( xmp_fn_1 );
				}
			}
			
			remove( xmp_fn_2 );
		}
		else { // error
			ret_val_b= false;
		}

	}
	else { // no local metadata; simply replace; TODO: check Info?
	}

	return ret_val_b;
}

*/
