// This is for the MinGW compiler which does not support wmain.
// A better version of mingw-unicode.c written by Sid Steward for PDFtk
//
// Do not compile this file, but instead include it right before your _tmain function like:
//
// #ifdef WIN32 // input is wide, so we perform input processing
// #include "win32_utf8_include.cc"
// int wmain( int argc, char *argv[] )
// #else // input is already UTF-8
// int main( int argc, char *argv[] )
// #endif
//
// resources:
//
// http://msdn.microsoft.com/en-us/library/ff770599.aspx
// https://github.com/mutoso-mirrors/reactos/blob/master/rostests/winetests/msvcrt/environ.c
// http://bioen.okstate.edu/Home/prashm%20-%20keep/prashant/VS.NET%20setup%20files/PROGRAM%20FILES/MICROSOFT%20VISUAL%20STUDIO%20.NET/VC7/CRT/SRC/CRTLIB.C
// http://rossy.oni2.net/files/daodan.c
//

#ifndef __MSVCRT__
#error Unicode main function requires linking to MSVCRT
#endif
#ifndef _UNICODE
#error Unicode main function requires -D_UNICODE
#endif

#include <wchar.h>
#include <stdlib.h>

typedef struct {
  int newmode;
} _startupinfo;

// extern int _CRT_glob;

// from: http://bioen.okstate.edu/Home/prashm%20-%20keep/prashant/VS.NET%20setup%20files/PROGRAM%20FILES/MICROSOFT%20VISUAL%20STUDIO%20.NET/VC7/CRT/SRC/CRTLIB.C
//
/***
*int __[w]getmainargs - get values for args to main()
*
*Purpose:
*       This function invokes the command line parsing and copies the args
*       to main back through the passsed pointers. The reason for doing
*       this here, rather than having _CRTDLL_INIT do the work and exporting
*       the __argc and __argv, is to support the linked-in option to have
*       wildcard characters in filename arguments expanded.
*
*Entry:
*       int *pargc              - pointer to argc
*       _TCHAR ***pargv         - pointer to argv
*       _TCHAR ***penvp         - pointer to envp
*       int dowildcard          - flag (true means expand wildcards in cmd line)
*       _startupinfo * startinfo- other info to be passed to CRT DLL
*
*Exit:
*       Returns 0 on success, negative if _*setargv returns an error. Values
*       for the arguments to main() are copied through the passed pointers.
*
*******************************************************************************/

// this is the function to which we'll pass our converted args
int win32_utf8_main( int argc, char *argv[] );

typedef int (*WGETMAINARGS_TYPE)(int*, wchar_t***, wchar_t***, int, _startupinfo*);

int main() {

  int ret_val= 100; // default: error

  HMODULE hmod= GetModuleHandleA( "msvcrt.dll" );
  if( hmod ) { // success

    WGETMAINARGS_TYPE wgetmainargs= (WGETMAINARGS_TYPE)GetProcAddress( hmod, "__wgetmainargs" );
    if( wgetmainargs ) { // success

      int argc;
      wchar_t** wargv;
      wchar_t** wenvp;
      _startupinfo si; si.newmode= 0;
      int rr= wgetmainargs(&argc, &wargv, &wenvp, 1 /*globbing flag, was: _CRT_glob*/, &si);
      if( rr== 0 ) { // success
      
	// convert 16-bit wide characters to UTF-8
	char** argv= (char**)malloc( (argc+ 1)* sizeof( char* ) );
	if( argv ) {
	  memset( argv, 0, (argc+ 1)* sizeof( char* ) );
	  bool success_b= true;

	  for( int ii= 0; ii< argc; ++ii ) {
	    int len= WideCharToMultiByte( CP_UTF8, 0, (wargv)[ii], -1, NULL, 0, NULL, NULL );
	    argv[ii]= (char*)malloc( (len+ 1)* sizeof( char ) );
	    if( !argv[ii] ) { // error
	      success_b= false;
	      break;
	    }
	    memset( argv[ii], 0, (len+ 1)* sizeof( char ) );

	    WideCharToMultiByte( CP_UTF8, 0, (wargv)[ii], -1, argv[ii], len, NULL, NULL );
	    argv[ii][len]= 0; // add term null just in case
	  }

	  if( success_b ) {
	    // pass to main
	    ret_val= win32_utf8_main( argc, argv );
	  }
	  else { // error
	    cerr << "PDFtk Error trying to malloc space for argv elements" << endl;
	  }

	  // release memory
	  for( int ii= 0; ii< argc; ++ii ) {
	    free( argv[ii] );
	    argv[ii]= 0;
	  }
	  free( argv );
	  argv= 0;

	}
	else { // error
	  cerr << "PDFtk Error trying to malloc space for argv" << endl;
	}
      }
      else { // error
	cerr << "PDFtk Error trying to call wgetmainargs" << endl;
      }
    }
    else { // error
      cerr << "PDFtk Error trying to get proc address for _wgetmainargs" << endl;
    }
  }
  else { // error
    cerr << "PDFtk Error trying to get a module handle of msvcrt.dll" << endl;
  }

  return ret_val;
}
