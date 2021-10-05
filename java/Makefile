# -*- Mode: Makefile -*-
#
# Copyright (c) 2003-2013 Steward and Lee, LLC
# This is part of pdftk
#
# Visit: www.pdftk.com for pdftk information and articles
# Permalink: http://www.pdflabs.com/tools/pdftk-the-pdf-toolkit/
#
# Please email Sid Steward with questions or bug reports.
# Include "pdftk" in the subject line to ensure successful delivery:
# sid.steward at pdflabs dot com
#

JAVALIBPATH= $(CURDIR)

#
export GCJFLAGS+= --encoding=UTF-8 --classpath="$(LIBGCJ):$(JAVALIBPATH):."
export GCJHFLAGS+= --classpath="$(LIBGCJ):$(JAVALIBPATH):."

sources = $(wildcard pdftk/com/lowagie/text/*.java)
sources+= $(wildcard pdftk/com/lowagie/text/exceptions/*.java)
sources+= $(wildcard pdftk/com/lowagie/text/pdf/*.java)
sources+= $(wildcard pdftk/com/lowagie/text/pdf/crypto/*.java)
sources+= $(wildcard pdftk/com/lowagie/text/pdf/fonts/*.java)
sources+= $(wildcard pdftk/com/lowagie/text/pdf/interfaces/*.java)
sources+= $(wildcard pdftk/com/lowagie/text/pdf/internal/*.java)
#
sources+= $(wildcard pdftk/com/lowagie/text/markup/*.java)
sources+= $(wildcard pdftk/com/lowagie/text/xml/xmp/*.java)

# this next line isn't necessary for newer build tools
# regen Makefile.bc_sources after upgrading bouncycastle
include Makefile.bc_sources

headers= $(patsubst %.java, %.h, $(sources))

# for afm resources
afms= $(wildcard pdftk/com/lowagie/text/pdf/fonts/*.afm)

# don't automatically delete intermediate class files
.PRECIOUS : %.class

%.class : %.java
	$(GCJ) $(GCJFLAGS) -C $<

%.h : %.class
	$(GCJH) $(GCJHFLAGS) $*

# don't create class list from java filenames because it omits local classes
java_lib.o : $(headers) $(sources)
	$(GJAR) -cf java_lib.jar pdftk/com/lowagie/*/*/*/*.class pdftk/com/lowagie/*/*/*.class pdftk/com/lowagie/*/*.class pdftk/org/bouncycastle/*/*.class pdftk/org/bouncycastle/*/*/*.class $(afms)
	$(GCJ) $(GCJFLAGS) -c java_lib.jar


ifdef LIBGCJ_SUPPLEMENT
libgcj_supplement_sources = $(wildcard java/security/*.java)
libgcj_supplement_sources+= $(wildcard gnu/java/locale/*.java)
libgcj_supplement_classes = $(patsubst %.java, %.class, $(libgcj_supplement_sources))

LIBGCJ_SUPPLEMENT_O= $(LIBGCJ_SUPPLEMENT).o
LIBGCJ_SUPPLEMENT_JAR= $(LIBGCJ_SUPPLEMENT).jar

# don't use class list from java filenames because it omits local classes
$(LIBGCJ_SUPPLEMENT_O) : $(libgcj_supplement_classes) $(libgcj_supplement_sources)
	$(GJAR) -cf $(LIBGCJ_SUPPLEMENT_JAR) java/security/*.class gnu/java/locale/*.class
	$(GCJ) $(GCJFLAGS) -c $(LIBGCJ_SUPPLEMENT_JAR)
endif


all : java_lib.o $(LIBGCJ_SUPPLEMENT_O)

clean :
	$(RM) $(RMFLAGS) java_lib.jar java_lib.o;
	$(RM) $(RMFLAGS) pdftk/com/lowagie/*/*/*/*.class pdftk/com/lowagie/*/*/*.class pdftk/com/lowagie/*/*.class pdftk/org/bouncycastle/*/*.class pdftk/org/bouncycastle/*/*/*.class;
	$(RM) $(RMFLAGS) $(headers);
ifdef LIBGCJ_SUPPLEMENT
	$(RM) $(RMFLAGS) $(LIBGCJ_SUPPLEMENT_JAR) $(LIBGCJ_SUPPLEMENT_O);
	$(RM) $(RMFLAGS) java/security/*.class gnu/java/locale/*.class
endif