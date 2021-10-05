/* -*- Mode: C++; tab-width: 2; c-basic-offset: 2 -*- */
/*
	PDFtk, the PDF Toolkit
	Copyright (c) 2003, 2004, 2010, 2013 Steward and Lee, LLC


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

void
ReportAcroFormFields( ostream& ofs,
											itext::PdfReader* reader_p,
											bool utf8_b );

void
ReportAnnots( ostream& ofs,
							itext::PdfReader* reader_p,
							bool utf8_b );

void
ReportOnPdf( ostream& ofs,
						 itext::PdfReader* reader_p,
						 bool utf8_b );

bool
UpdateInfo( itext::PdfReader* reader_p,
						istream& ifs,
						bool utf8_b );

//
class PdfBookmark {
public:
	static const string m_prefix;
	static const string m_begin_mark;
	static const string m_title_label;
	static const string m_level_label;
	static const string m_page_number_label;
	//static const string m_empty_string;

	string m_title;
	int m_level;
	int m_page_num; // zero means no destination
	
	PdfBookmark();
	bool valid();
};
//
ostream& operator<<( ostream& ss, const PdfBookmark& bb );

int
ReadOutlines( vector<PdfBookmark>& bookmark_data,
							itext::PdfDictionary* outline_p,
							int level,
							itext::PdfReader* reader_p,							
							bool utf8_b );

int
BuildBookmarks( itext::PdfWriter* writer_p,
								vector<PdfBookmark>::const_iterator& it,
								vector<PdfBookmark>::const_iterator it_end,
								itext::PdfDictionary* parent_p,
								itext::PdfIndirectReference* parent_ref_p,
								itext::PdfDictionary* after_child_p,
								itext::PdfIndirectReference* after_child_ref_p,
								itext::PdfDictionary*& final_child_p,
								itext::PdfIndirectReference*& final_child_ref_p,
								int parent_level,
								int& num_bookmarks_total,
								int page_num_offset,
								int level_offset,
								bool utf8_b );

/* not fully implemented, yet

bool
ReplaceXmp( itext::PdfReader* reader_p,
						string xmp_filename );

bool
UpdateXmp( itext::PdfReader* reader_p,
					 string xmp_filename );

*/
