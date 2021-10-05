/* -*- Mode: C++; tab-width: 2; c-basic-offset: 2 -*- */
/*
	PDFtk, the PDF Toolkit
	Copyright (c) 2003-2013 Steward and Lee, LLC


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

class TK_Session {
	
	bool m_valid_b;
	bool m_authorized_b;
	bool m_input_pdf_readers_opened_b; // have m_input_pdf readers been opened?
	bool m_verbose_reporting_b;
	bool m_ask_about_warnings_b;

public:

  typedef unsigned long PageNumber;
	typedef enum { NORTH= 0, EAST= 90, SOUTH= 180, WEST= 270 } PageRotate; // DF rotation
	typedef bool PageRotateAbsolute; // DF absolute / relative rotation

	struct InputPdf {
		string m_filename;
		string m_password;
		bool m_authorized_b;

		// keep track of which pages get output under which readers,
		// because one reader mayn't output the same page twice;
		vector< pair< set<jint>, itext::PdfReader* > > m_readers;

		PageNumber m_num_pages;

		InputPdf() : m_filename(), m_password(), m_authorized_b(true), m_readers(), m_num_pages(0) {}
	};
	// pack input PDF in the order they're given on the command line
	vector< InputPdf > m_input_pdf;
	typedef vector< InputPdf >::size_type InputPdfIndex;

	// store input PDF handles here
	map< string, InputPdfIndex > m_input_pdf_index;

	bool add_reader( InputPdf* input_pdf_p, bool keep_artifacts_b );
	bool open_input_pdf_readers();

	vector< string > m_input_attach_file_filename;
	jint m_input_attach_file_pagenum;

	string m_update_info_filename;
	bool m_update_info_utf8_b;
	string m_update_xmp_filename;

  enum keyword {
    none_k= 0,

    // the operations
    cat_k, // combine pages from input PDFs into a single output
		shuffle_k, // like cat, but interleaves pages from input ranges
		burst_k, // split a single, input PDF into individual pages
		barcode_burst_k, // barcode_burst project
		filter_k, // apply 'filters' to a single, input PDF based on output args
		dump_data_k, // no PDF output
		dump_data_utf8_k,
		dump_data_fields_k,
		dump_data_fields_utf8_k,
		dump_data_annots_k,
		generate_fdf_k,
		unpack_files_k, // unpack files from input; no PDF output
		//
		first_operation_k= cat_k,
    final_operation_k= unpack_files_k,

		// these are treated the same as operations,
		// but they are processed using the filter operation
		fill_form_k, // read FDF file and fill PDF form fields
		attach_file_k, // attach files to output
		update_info_k,
		update_info_utf8_k, // if info isn't utf-8, it is encoded using xml entities
		update_xmp_k,
		background_k, // promoted from output option to operation in pdftk 1.10
		multibackground_k, // feature added by Bernhard R. Link <brlink@debian.org>, Johann Felix Soden <johfel@gmx.de>
		stamp_k,
		multistamp_k, // feature added by Bernhard R. Link <brlink@debian.org>, Johann Felix Soden <johfel@gmx.de>
		rotate_k, // rotate given pages as directed

		// optional attach_file argument
		attach_file_to_page_k,

		// cat page range keywords
    end_k,
    even_k,
    odd_k,

    output_k,

		// encryption & decryption
		input_pw_k,
		owner_pw_k,
		user_pw_k,
		user_perms_k,

		// output arg.s, only
		encrypt_40bit_k,
		encrypt_128bit_k,

		// user permissions
		perm_printing_k,
		perm_modify_contents_k,
		perm_copy_contents_k,
		perm_modify_annotations_k,
		perm_fillin_k,
		perm_screen_readers_k,
		perm_assembly_k,
		perm_degraded_printing_k,
		perm_all_k,

		// filters
		filt_uncompress_k,
		filt_compress_k,

		// forms
		flatten_k,
		need_appearances_k,
		drop_xfa_k,
		drop_xmp_k,
		keep_first_id_k,
		keep_final_id_k,

		// pdftk options
		verbose_k,
		dont_ask_k,
		do_ask_k,

		// page rotation
		rot_north_k,
		rot_east_k,
		rot_south_k,
		rot_west_k,
		rot_left_k,
		rot_right_k,
		rot_upside_down_k
  };
  static keyword is_keyword( char* ss, int* keyword_len_p );

  keyword m_operation;

  struct PageRef {
		InputPdfIndex m_input_pdf_index;
    PageNumber m_page_num; // 1-based
    PageRotate m_page_rot; // DF rotation
    PageRotateAbsolute m_page_abs; // DF absolute / relative rotation

		PageRef( InputPdfIndex input_pdf_index, PageNumber page_num, PageRotate page_rot= NORTH, PageRotateAbsolute page_abs= false ) :
			m_input_pdf_index( input_pdf_index ),
			m_page_num( page_num ),
			m_page_rot( page_rot ),
			m_page_abs( page_abs ) {}
  };
  vector< vector< PageRef > > m_page_seq; // one vector for each given page range

	string m_form_data_filename;
	string m_background_filename;
	string m_stamp_filename;
  string m_output_filename;
	bool m_output_utf8_b;
	string m_output_owner_pw;
	string m_output_user_pw;
	jint m_output_user_perms;
	bool m_multistamp_b; // use all pages of input stamp PDF, not just the first
	bool m_multibackground_b; // use all pages of input background PDF, not just the first
	bool m_output_uncompress_b;
	bool m_output_compress_b;
	bool m_output_flatten_b;
	bool m_output_need_appearances_b;
	bool m_output_drop_xfa_b;
	bool m_output_drop_xmp_b;
	bool m_output_keep_first_id_b;
	bool m_output_keep_final_id_b;
	bool m_cat_full_pdfs_b; // we are merging entire docs, not select pages

	enum encryption_strength {
		none_enc= 0,
		bits40_enc,
		bits128_enc
	} m_output_encryption_strength;

  TK_Session( int argc, 
							char** argv );

	~TK_Session();

  bool is_valid() const;

	void dump_session_data() const;

	void attach_files
	( itext::PdfReader* input_reader_p,
		itext::PdfWriter* writer_p );

	void unpack_files
	( itext::PdfReader* input_reader_p );

	int create_output_page( itext::PdfCopy*, PageRef, int );
	int create_output();

private:
	enum ArgState {
    input_files_e,
		input_pw_e,

    page_seq_e,
		form_data_filename_e,
		
		attach_file_filename_e,
		attach_file_pagenum_e,

		update_info_filename_e,
		update_xmp_filename_e,

		output_e, // state where we expect output_k, next
    output_filename_e,

		output_args_e, // output args are order-independent; switch here
		output_owner_pw_e,
		output_user_pw_e,
		output_user_perms_e,

		background_filename_e,
		stamp_filename_e,

		done_e
	};

	// convenience function; return true iff handled
	bool handle_some_output_options( TK_Session::keyword kw, ArgState* arg_state_p );

};

void
prompt_for_filename( const string fn_name,
										 string& fn );
