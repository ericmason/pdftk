/*
 * $Id: PdfIndirectObject.java,v 1.35 2003/05/02 09:01:25 blowagie Exp $
 * $Name:  $
 *
 * Copyright 1999, 2000, 2001, 2002 Bruno Lowagie
 *
 *
 * The Original Code is 'iText, a free JAVA-PDF library'.
 *
 * The Initial Developer of the Original Code is Bruno Lowagie. Portions created by
 * the Initial Developer are Copyright (C) 1999, 2000, 2001, 2002 by Bruno Lowagie.
 * All Rights Reserved.
 * Co-Developer of the code is Paulo Soares. Portions created by the Co-Developer
 * are Copyright (C) 2000, 2001, 2002 by Paulo Soares. All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 * 
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301, USA.
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 * 
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301, USA.
 *
 *
 * If you didn't download this code from the following link, you should check if
 * you aren't using an obsolete version:
 * http://www.lowagie.com/iText/
 */

package pdftk.com.lowagie.text.pdf;

import java.io.IOException;
import java.io.OutputStream;

import pdftk.com.lowagie.text.DocWriter;

/**
 * <CODE>PdfIndirectObject</CODE> is the Pdf indirect object.
 * <P>
 * An <I>indirect object</I> is an object that has been labeled so that it can be referenced by
 * other objects. Any type of <CODE>PdfObject</CODE> may be labeled as an indirect object.<BR>
 * An indirect object consists of an object identifier, a direct object, and the <B>endobj</B>
 * keyword. The <I>object identifier</I> consists of an integer <I>object number</I>, an integer
 * <I>generation number</I>, and the <B>obj</B> keyword.<BR>
 * This object is described in the 'Portable Document Format Reference Manual version 1.3'
 * section 4.10 (page 53).
 *
 * @see		PdfObject
 * @see		PdfIndirectReference
 */

public class PdfIndirectObject {
    
    // membervariables
    
/** The object number */
    protected int number;
    
/** the generation number */
    protected int generation = 0;
    
    static final byte STARTOBJ[] = DocWriter.getISOBytes(" obj "); // ssteward
    static final byte ENDOBJ[] = DocWriter.getISOBytes("\nendobj "); // ssteward
    static final int SIZEOBJ = STARTOBJ.length + ENDOBJ.length;
    PdfObject object;
    PdfWriter writer;
    
    // constructors
    
/**
 * Constructs a <CODE>PdfIndirectObject</CODE>.
 *
 * @param		number			the object number
 * @param		object			the direct object
 */
    
    PdfIndirectObject(int number, PdfObject object, PdfWriter writer) {
        this(number, 0, object, writer);
    }
    
    PdfIndirectObject(PdfIndirectReference ref, PdfObject object, PdfWriter writer) {
        this(ref.getNumber(),ref.getGeneration(),object,writer);
    }
/**
 * Constructs a <CODE>PdfIndirectObject</CODE>.
 *
 * @param		number			the object number
 * @param		generation		the generation number
 * @param		object			the direct object
 */
    
    PdfIndirectObject(int number, int generation, PdfObject object, PdfWriter writer) {
        this.writer = writer;
        this.number = number;
        this.generation = generation;
        this.object = object;
        PdfEncryption crypto = null;
        if (writer != null)
            crypto = writer.getEncryption();
        if (crypto != null) {
            crypto.setHashKey(number, generation);
        }
    }
    
    // methods
    
/**
 * Return the length of this <CODE>PdfIndirectObject</CODE>.
 *
 * @return		the length of the PDF-representation of this indirect object.
 */
    
//    public int length() {
//        if (isStream)
//            return bytes.size() + SIZEOBJ + stream.getStreamLength(writer);
//        else
//            return bytes.size();
//    }
    
    
/**
 * Returns a <CODE>PdfIndirectReference</CODE> to this <CODE>PdfIndirectObject</CODE>.
 *
 * @return		a <CODE>PdfIndirectReference</CODE>
 */
    
    public PdfIndirectReference getIndirectReference() {
        return new PdfIndirectReference(object.type(), number, generation);
    }
    
/**
 * Writes eficiently to a stream
 *
 * @param os the stream to write to
 * @throws IOException on write error
 */
    void writeTo(OutputStream os) throws IOException
    {
	// ssteward: don't os.write() anything here, before the number, or you'll throw off the xref position
        os.write(DocWriter.getISOBytes(String.valueOf(number)));
        os.write(' ');
        os.write(DocWriter.getISOBytes(String.valueOf(generation)));
        os.write(STARTOBJ);
	// ssteward
        //int type = object.type();
        //if (type != PdfObject.ARRAY && type != PdfObject.DICTIONARY && type != PdfObject.NAME && type != PdfObject.STRING)
        //    os.write(' ');
        object.toPdf(writer, os);
        os.write(ENDOBJ);
    }
}
