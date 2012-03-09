# -*- Mode: Makefile -*-
# Copyright 2003, 2004, 2010 Sid Steward
# This is part of pdftk
#
# Visit: www.pdftk.com for pdftk information and articles
# Permalink: http://www.pdflabs.com/tools/pdftk-the-pdf-toolkit/
#
# Please email Sid Steward with questions or bug reports.
# Include "pdftk" in the subject line to ensure successful delivery:
# sid.steward at pdflabs dot com
#
# Skipping RTF package because gcjh is complaining that
# a static member has the same name as a method, but doesn't
# tell me where the ambiguity is.  Since it isn't needed for
# pdftk, I left it out until I have time to find the trouble.
#
# also omitting hyphenation, html, since they aren't needed by pdftk

JAVALIBPATH= $(CURDIR)

# preprocess this file so it might include our local copies of library objects: libgcj_local
com/lowagie/text/pdf/PdfEncryption.java : com/lowagie/text/pdf/PdfEncryption.java.c
ifdef USE_LOCAL_LIBGCJ
	cpp -C -P -DPDFTK_JAVA_LOCAL com/lowagie/text/pdf/PdfEncryption.java.c com/lowagie/text/pdf/PdfEncryption.java
else
	cpp -C -P com/lowagie/text/pdf/PdfEncryption.java.c com/lowagie/text/pdf/PdfEncryption.java
endif

#
export GCJFLAGS+= --encoding=UTF-8 --classpath="$(LIBGCJ):$(JAVALIBPATH):."
export GCJHFLAGS+= --classpath="$(LIBGCJ):$(JAVALIBPATH):."

sources= $(wildcard com/lowagie/text/*.java)
sources+= $(wildcard com/lowagie/text/markup/*.java)
sources+= $(wildcard com/lowagie/text/pdf/*.java)
sources+= com/lowagie/text/pdf/PdfEncryption.java
#sources+= $(wildcard com/lowagie/text/pdf/codec/*.java)
#sources+= $(wildcard com/lowagie/text/pdf/codec/wmf/*.java)
#sources+= $(wildcard com/lowagie/text/pdf/codec/postscript/*.java)
sources+= $(wildcard com/lowagie/text/pdf/fonts/*.java)
sources+= $(wildcard com/lowagie/text/xml/xmp/*.java)
#sources+= $(wildcard com/lowagie/bc/asn1/*.java)
sources+= $(wildcard org/bouncycastle/util/*.java)
sources+= $(wildcard org/bouncycastle/util/encoders/*.java)
sources+= $(wildcard org/bouncycastle/util/io/*.java)
sources+= $(wildcard org/bouncycastle/asn1/*.java)

headers= $(patsubst %.java, %.h, $(sources))

# for afm resources
afms= $(wildcard com/lowagie/text/pdf/fonts/*.afm)

ifdef GCJ_LOCAL_LIB
libgcj_local_sources= $(wildcard gnu_local/java/security/*.java)
libgcj_local_sources+= $(wildcard gnu_local/java/security/provider/*.java)
libgcj_local_sources+= $(wildcard java_local/security/*.java)
#
libgcj_local_classes= $(patsubst %.java, %.class, $(libgcj_local_sources))
endif

# don't automatically delete intermediate class files
.PRECIOUS : %.class

%.class : %.java
	$(GCJ) $(GCJFLAGS) -C $<

%.h : %.class
	$(GCJH) $(GCJHFLAGS) $*

# don't create class list from java filenames because it omits local classes
java_lib.o : $(headers) $(sources)
	$(GJAR) -cf java_lib.jar com/lowagie/*/*/*/*.class com/lowagie/*/*/*.class com/lowagie/*/*.class org/bouncycastle/*/*.class org/bouncycastle/*/*/*.class $(afms)
	$(GCJ) $(GCJFLAGS) -c java_lib.jar

ifdef GCJ_LOCAL_LIB
$(GCJ_LOCAL_LIB) : $(libgcj_local_classes)
	$(GJAR) -cf gcj_local_lib.jar gnu_local/java/security/*.class gnu_local/java/security/provider/*.class java_local/security/*.class
	$(GCJ) $(GCJFLAGS) -c gcj_local_lib.jar
endif

all : com/lowagie/text/pdf/PdfEncryption.java java_lib.o $(GCJ_LOCAL_LIB)

clean : itext_clean libgcj_local_clean

itext_clean :
	$(RM) $(RMFLAGS) java_lib.jar java_lib.o;
	$(RM) $(RMFLAGS) com/lowagie/text/pdf/PdfEncryption.java;
	$(MAKE) -iC "$(JAVALIBPATH)/com/lowagie/text" clean;
	$(MAKE) -iC "$(JAVALIBPATH)/com/lowagie/text/markup" clean;
	$(MAKE) -iC "$(JAVALIBPATH)/com/lowagie/text/pdf" clean;
#	$(MAKE) -iC "$(JAVALIBPATH)/com/lowagie/text/pdf/codec" clean;
#	$(MAKE) -iC "$(JAVALIBPATH)/com/lowagie/text/pdf/codec/wmf" clean;
#	$(MAKE) -iC "$(JAVALIBPATH)/com/lowagie/text/pdf/codec/postscript" clean;
	$(MAKE) -iC "$(JAVALIBPATH)/com/lowagie/text/pdf/fonts" clean;
	$(MAKE) -iC "$(JAVALIBPATH)/com/lowagie/text/xml/xmp" clean;
#	$(MAKE) -iC "$(JAVALIBPATH)/com/lowagie/bc/asn1" clean;
	$(MAKE) -iC "$(JAVALIBPATH)/org/bouncycastle/util" clean;
	$(MAKE) -iC "$(JAVALIBPATH)/org/bouncycastle/util/encoders" clean;
	$(MAKE) -iC "$(JAVALIBPATH)/org/bouncycastle/util/io" clean;
	$(MAKE) -iC "$(JAVALIBPATH)/org/bouncycastle/asn1" clean;

libgcj_local_clean :
	$(MAKE) -iC "$(JAVALIBPATH)/gnu_local/java/security" clean;
	$(MAKE) -iC "$(JAVALIBPATH)/gnu_local/java/security/provider" clean;
	$(MAKE) -iC "$(JAVALIBPATH)/java_local/security" clean;