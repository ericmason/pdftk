# Copyright 2003, 2004, 2010 Sid Steward
# This is part of pdftk; visit: www.pdftk.com
#

##
# variables

library= fonts.a

sources= $(wildcard *.java)
objects= $(patsubst %.java, %.o, $(sources))
headers= $(patsubst %.java, %.h, $(sources))
classes= $(patsubst %.java, %.class, $(sources))

# font metric files, included as resources
afms= $(wildcard *.afm)
afm_objects= $(patsubst %.afm, %.o, $(afms))

##
# implicit rules for creating A from B

%.o : %.java
	$(GCJ) $(GCJFLAGS) -c $< -o $@

%.class : %.java
	$(GCJ) $(GCJFLAGS) -C $<

# the --resource argument gives the name by which this resource is
# referenced; I took this prefix from RESOURCE_PATH in BaseFont.java
%.o : %.afm
	$(GCJ) -c --resource com/lowagie/text/pdf/fonts/$< $< -o $@

# gcjh doesn't want the ".class" extension, so use
# the "$*" automatic variable, here
#
%.h : %.class
	$(GCJH) --classpath="." $*;
	$(RM) $<

##
# targets

# we don't include afm_objects in the final fonts.a, since
# they must be referenced directly at link-time (for some reason)

all : $(library) $(headers) $(afm_objects)

$(library) : $(objects)
	$(AR) $(ARFLAGS) $(library) $(objects);

clean :
	$(RM) $(RMFLAGS) *.o *.h *.class *.a
