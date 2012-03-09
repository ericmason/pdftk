# Copyright 2003, 2004, 2010 Sid Steward
# This is part of pdftk; visit: www.pdftk.com
#

##
# variables

library= xmp.a

sources= $(wildcard *.java)
objects= $(patsubst %.java, %.o, $(sources))
headers= $(patsubst %.java, %.h, $(sources))
classes= $(patsubst %.java, %.class, $(sources))

##
# implicit rules for creating A from B

%.o : %.java
	$(GCJ) $(GCJFLAGS) -c $< -o $@

%.class : %.java
	$(GCJ) $(GCJFLAGS) -C $<

# gcjh doesn't want the ".class" extension, so use
# the "$*" automatic variable, here
#
%.h : %.class
	$(GCJH) --classpath="." $*;
	$(RM) $<

##
# targets

all : $(library) $(headers)

$(library) : $(objects)
	$(AR) $(ARFLAGS) $(library) $(objects);

clean :
	$(RM) $(RMFLAGS) *.o *.h *.class *.a
