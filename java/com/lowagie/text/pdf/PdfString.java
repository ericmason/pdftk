/*
 * $Id: PdfString.java,v 1.26 2002/07/09 11:28:24 blowagie Exp $
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
 * If you didn't download this code from the following link, you should check if
 * you aren't using an obsolete version:
 * http://www.lowagie.com/iText/
 */

package com.lowagie.text.pdf;
import java.io.OutputStream;
import java.io.IOException;

/**
 * A <CODE>PdfString</CODE>-class is the PDF-equivalent of a JAVA-<CODE>String</CODE>-object.
 * <P>
 * A string is a sequence of characters delimited by parenthesis. If a string is too long
 * to be conveniently placed on a single line, it may be split across multiple lines by using
 * the backslash character (\) at the end of a line to indicate that the string continues
 * on the following line. Within a string, the backslash character is used as an escape to
 * specify unbalanced parenthesis, non-printing ASCII characters, and the backslash character
 * itself. Use of the \<I>ddd</I> escape sequence is the preferred way to represent characters
 * outside the printable ASCII character set.<BR>
 * This object is described in the 'Portable Document Format Reference Manual version 1.3'
 * section 4.4 (page 37-39).
 *
 * @see		PdfObject
 * @see		BadPdfFormatException
 */

public class PdfString extends PdfObject {
    
    // membervariables
    
    /** The value of this object. */
    protected String value = NOTHING;
    protected String originalValue = null;
    
    /** The encoding. */
    protected String encoding = TEXT_PDFDOCENCODING;
    protected int objNum = 0;
    protected int objGen = 0;
    protected boolean hexWriting = false;

    // constructors
    
    /**
     * Constructs an empty <CODE>PdfString</CODE>-object.
     */
    
    public PdfString() {
        super(STRING);
    }
    
    /**
     * Constructs a <CODE>PdfString</CODE>-object.
     *
     * @param		value		the content of the string
     */
    
    public PdfString(String value) {
        super(STRING);
        this.value = value;
    }
    
    /**
     * Constructs a <CODE>PdfString</CODE>-object.
     *
     * @param		value		the content of the string
     * @param		encoding	an encoding
     */
    
    public PdfString(String value, String encoding) {
        super(STRING);
        this.value = value;
        this.encoding = encoding;
    }
    
    /**
     * Constructs a <CODE>PdfString</CODE>-object.
     *
     * @param		bytes	an array of <CODE>byte</CODE>
     */
    
    public PdfString(byte[] bytes) {
        super(STRING);
        value = PdfEncodings.convertToString(bytes, null);
        encoding = NOTHING;
    }
    
    // methods overriding some methods in PdfObject
    
    /**
     * Returns the PDF representation of this <CODE>PdfString</CODE>.
     *
     * @return		an array of <CODE>byte</CODE>s
     */
    
    public void toPdf(PdfWriter writer, OutputStream os) throws IOException {
        byte b[] = getBytes();
        PdfEncryption crypto = null;
        if (writer != null)
            crypto = writer.getEncryption();
        if (crypto != null) {
            b = (byte[])bytes.clone();
            crypto.prepareKey();
            crypto.encryptRC4(b);
        }
        if (hexWriting) {
            ByteBuffer buf = new ByteBuffer();
            buf.append('<');
            int len = b.length;
            for (int k = 0; k < len; ++k)
                buf.appendHex(b[k]);
            buf.append('>');
            os.write(buf.toByteArray());
        }
        else
            os.write(PdfContentByte.escapeString(b));
    }
    
    /**
     * Returns the <CODE>String</CODE> value of the <CODE>PdfString</CODE>-object.
     *
     * @return		a <CODE>String</CODE>
     */
    
    public String toString() {
        return value;
    }
    
    // other methods
    
    /**
     * Gets the encoding of this string.
     *
     * @return		a <CODE>String</CODE>
     */
    
    public String getEncoding() {
        return encoding;
    }
    
    public String toUnicodeString() {
        if (encoding != null && encoding.length() != 0)
            return value;
        getBytes();
        if (bytes.length >= 2 && bytes[0] == (byte)254 && bytes[1] == (byte)255) {

	    // ssteward
	    // pdftk-0.94: trim off these signature bytes (problem reading bookmarks);
	    // pdftk-1.00: still an issue; problem reading EmbeddedFiles name tree keys,
	    //    see PdfNameTree::iterateItems(); resulted in an extra null char at end of string
	    byte b_copy[]= new byte[bytes.length- 2];
	    System.arraycopy( bytes, 2, b_copy, 0, b_copy.length );

            //return PdfEncodings.convertToString(bytes, PdfObject.TEXT_UNICODE);
	    return PdfEncodings.convertToString(b_copy, PdfObject.TEXT_UNICODE);
	}
        else
            return PdfEncodings.convertToString(bytes, PdfObject.TEXT_PDFDOCENCODING);
    }
    
    void setObjNum(int objNum, int objGen) {
        this.objNum = objNum;
        this.objGen = objGen;
    }
    
    void decrypt(PdfReader reader) {
        PdfEncryption decrypt = reader.getDecrypt();
        if (decrypt != null) {
            originalValue = value;
            decrypt.setHashKey(objNum, objGen);
            decrypt.prepareKey();
            bytes = PdfEncodings.convertToBytes(value, null);
            decrypt.encryptRC4(bytes);
            value = PdfEncodings.convertToString(bytes, null);
        }
    }
   
    public byte[] getBytes() {
        if (bytes == null) {
	    /* ssteward, pdftk 1.0: I really needed the PdfString to output TEXT_UNICODE
	       when I asked it to; this had to do with EmbeddedFiles name tree names on 
	       Acrobat 5--it wouldn't take any other encoding; see PdfNameTree.java for 
	       related changes
            if (encoding != null && encoding.equals(TEXT_UNICODE) && PdfEncodings.isPdfDocEncoding(value))
                bytes = PdfEncodings.convertToBytes(value, TEXT_PDFDOCENCODING);
            else
	    */
	    bytes = PdfEncodings.convertToBytes(value, encoding);
        }
        return bytes;
    }
    
    public byte[] getOriginalBytes() {
        if (originalValue == null)
            return getBytes();
        return PdfEncodings.convertToBytes(originalValue, null);
    }
    
    public PdfString setHexWriting(boolean hexWriting) {
        this.hexWriting = hexWriting;
        return this;
    }
    
    public boolean isHexWriting() {
        return hexWriting;
    }

    // ssteward; this.encoding not reliable?
    public boolean isUnicode() {
	getBytes();
	return( bytes.length >= 2 && bytes[0] == (byte)254 && bytes[1] == (byte)255 );
    }
}
