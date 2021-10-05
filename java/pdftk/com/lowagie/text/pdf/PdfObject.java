/*
 * $Id: PdfObject.java,v 1.26 2002/07/09 11:28:23 blowagie Exp $
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
import java.io.OutputStream;
import java.io.IOException;

/**
 * <CODE>PdfObject</CODE> is the abstract superclass of all PDF objects.
 * <P>
 * PDF supports seven basic types of objects: Booleans, numbers, strings, names,
 * arrays, dictionaries and streams. In addition, PDF provides a null object.
 * Objects may be labeled so that they can be referred to by other objects.<BR>
 * All these basic PDF objects are described in the 'Portable Document Format
 * Reference Manual version 1.3' Chapter 4 (pages 37-54).
 *
 * @see		PdfNull
 * @see		PdfBoolean
 * @see		PdfNumber
 * @see		PdfString
 * @see		PdfName
 * @see		PdfArray
 * @see		PdfDictionary
 * @see		PdfStream
 * @see		PdfIndirectReference
 */

public abstract class PdfObject {
    
    // static membervariables (all the possible types of a PdfObject)
    
/** a possible type of <CODE>PdfObject</CODE> */
    public static final int BOOLEAN = 1;
    
/** a possible type of <CODE>PdfObject</CODE> */
    public static final int NUMBER = 2;
    
/** a possible type of <CODE>PdfObject</CODE> */
    public static final int STRING = 3;
    
/** a possible type of <CODE>PdfObject</CODE> */
    public static final int NAME = 4;
    
/** a possible type of <CODE>PdfObject</CODE> */
    public static final int ARRAY = 5;
    
/** a possible type of <CODE>PdfObject</CODE> */
    public static final int DICTIONARY = 6;
    
/** a possible type of <CODE>PdfObject</CODE> */
    public static final int STREAM = 7;

/** a possible type of <CODE>PdfObject</CODE> */
    // ssteward
    //public static final int NULL = 8;
    // renamed this member to m_NULL to prevent confusion w/ gcj
    public static final int m_NULL = 8;
    
    /** a possible type of <CODE>PdfObject</CODE> */
    public static final int INDIRECT = 10;    

/** This is an empty string used for the <CODE>PdfNull</CODE>-object and for an empty <CODE>PdfString</CODE>-object. */
    public static final String NOTHING = "";
    
/** This is the default encoding to be used for converting Strings into bytes and vice versa.
 * The default encoding is PdfDocEncoding.
 */
    public static final String TEXT_PDFDOCENCODING = "PDF";
    
/** This is the encoding to be used to output text in Unicode. */
    //public static final String TEXT_UNICODE = "UnicodeBig";
    public static final String TEXT_UNICODE = "UTF-16"; // ssteward; uses byte order mark

    // membervariables
    
/** the content of this <CODE>PdfObject</CODE> */
    protected byte[] bytes;
    
/** the type of this <CODE>PdfObject</CODE> */
    protected int type;
    
    /**
     * Holds value of property indRef.
     */
    protected PRIndirectReference indRef;
    
    // constructors
    
/**
 * Constructs a <CODE>PdfObject</CODE> of a certain <VAR>type</VAR> without any <VAR>content</VAR>.
 *
 * @param		type			type of the new <CODE>PdfObject</CODE>
 */
    
    protected PdfObject(int type) {
        this.type = type;
	this.bytes = null;
    }
    
/**
 * Constructs a <CODE>PdfObject</CODE> of a certain <VAR>type</VAR> with a certain <VAR>content</VAR>.
 *
 * @param		type			type of the new <CODE>PdfObject</CODE>
 * @param		content			content of the new <CODE>PdfObject</CODE> as a <CODE>String</CODE>.
 */
    
    protected PdfObject(int type, String content) {
        this.type = type;
        bytes = PdfEncodings.convertToBytes(content, null);
    }
    
/**
 * Constructs a <CODE>PdfObject</CODE> of a certain <VAR>type</VAR> with a certain <VAR>content</VAR>.
 *
 * @param		type			type of the new <CODE>PdfObject</CODE>
 * @param		bytes			content of the new <CODE>PdfObject</CODE> as an array of <CODE>byte</CODE>.
 */
    
    protected PdfObject(int type, byte[] bytes) {
        this.bytes = bytes;
        this.type = type;
    }
    
    // methods dealing with the content of this object
    
/**
 * Writes the PDF representation of this <CODE>PdfObject</CODE> as an array of <CODE>byte</CODE>s to the writer.
 * @param writer for backwards compatibility
 * @param os the outputstream to write the bytes to.
 * @throws IOException
 */
    
    public void toPdf(PdfWriter writer, OutputStream os) throws IOException {
        if (bytes != null)
            os.write(bytes);
    }
    
    /**
     * Gets the presentation of this object in a byte array
     * @return a byte array
     */
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * Can this object be in an object stream?
     * @return true if this object can be in an object stream.
     */
    public boolean canBeInObjStm() {
        return (type >= 1 && type <= 6) || type == 8;
    }
    
/**
 * Returns the length of the PDF representation of the <CODE>PdfObject</CODE>.
 * <P>
 * In some cases, namely for <CODE>PdfString</CODE> and <CODE>PdfStream</CODE>,
 * this method differs from the method <CODE>length</CODE> because <CODE>length</CODE>
 * returns the length of the actual content of the <CODE>PdfObject</CODE>.</P>
 * <P>
 * Remark: the actual content of an object is in most cases identical to its representation.
 * The following statement is always true: length() &gt;= pdfLength().</P>
 *
 * @return		a length
 */
    
//    public int pdfLength() {
//        return toPdf(null).length;
//    }
    
/**
 * Returns the <CODE>String</CODE>-representation of this <CODE>PdfObject</CODE>.
 *
 * @return		a <CODE>String</CODE>
 */
    
    public String toString() {
        if (bytes == null)
            return super.toString();
        else
            return PdfEncodings.convertToString(bytes, null);
    }
    
/**
 * Returns the length of the actual content of the <CODE>PdfObject</CODE>.
 * <P>
 * In some cases, namely for <CODE>PdfString</CODE> and <CODE>PdfStream</CODE>,
 * this method differs from the method <CODE>pdfLength</CODE> because <CODE>pdfLength</CODE>
 * returns the length of the PDF representation of the object, not of the actual content
 * as does the method <CODE>length</CODE>.</P>
 * <P>
 * Remark: the actual content of an object is in some cases identical to its representation.
 * The following statement is always true: length() &gt;= pdfLength().</P>
 *
 * @return		a length
 */
    
    public int length() {
        return toString().length();
    }
    
/**
 * Changes the content of this <CODE>PdfObject</CODE>.
 *
 * @param		content			the new content of this <CODE>PdfObject</CODE>
 */
    
    protected void setContent(String content) {
        bytes = PdfEncodings.convertToBytes(content, null);
    }
    
    // methods dealing with the type of this object
    
/**
 * Returns the type of this <CODE>PdfObject</CODE>.
 *
 * @return		a type
 */
    
    public int type() {
        return type;
    }
    
/**
 * Checks if this <CODE>PdfObject</CODE> is of the type <CODE>PdfNull</CODE>.
 *
 * @return		<CODE>true</CODE> or <CODE>false</CODE>
 */
    
    public boolean isNull() {
        return (this.type == m_NULL); // ssteward
    }
    
/**
 * Checks if this <CODE>PdfObject</CODE> is of the type <CODE>PdfBoolean</CODE>.
 *
 * @return		<CODE>true</CODE> or <CODE>false</CODE>
 */
    
    public boolean isBoolean() {
        return (this.type == BOOLEAN);
    }
    
/**
 * Checks if this <CODE>PdfObject</CODE> is of the type <CODE>PdfNumber</CODE>.
 *
 * @return		<CODE>true</CODE> or <CODE>false</CODE>
 */
    
    public boolean isNumber() {
        return (this.type == NUMBER);
    }
    
/**
 * Checks if this <CODE>PdfObject</CODE> is of the type <CODE>PdfString</CODE>.
 *
 * @return		<CODE>true</CODE> or <CODE>false</CODE>
 */
    
    public boolean isString() {
        return (this.type == STRING);
    }
    
/**
 * Checks if this <CODE>PdfObject</CODE> is of the type <CODE>PdfName</CODE>.
 *
 * @return		<CODE>true</CODE> or <CODE>false</CODE>
 */
    
    public boolean isName() {
        return (this.type == NAME);
    }
    
/**
 * Checks if this <CODE>PdfObject</CODE> is of the type <CODE>PdfArray</CODE>.
 *
 * @return		<CODE>true</CODE> or <CODE>false</CODE>
 */
    
    public boolean isArray() {
        return (this.type == ARRAY);
    }
    
/**
 * Checks if this <CODE>PdfObject</CODE> is of the type <CODE>PdfDictionary</CODE>.
 *
 * @return		<CODE>true</CODE> or <CODE>false</CODE>
 */
    
    public boolean isDictionary() {
        return (this.type == DICTIONARY);
    }
    
/**
 * Checks if this <CODE>PdfObject</CODE> is of the type <CODE>PdfStream</CODE>.
 *
 * @return		<CODE>true</CODE> or <CODE>false</CODE>
 */
    
    public boolean isStream() {
        return (this.type == STREAM);
    }

    /**
     * Checks if this is an indirect object.
     * @return true if this is an indirect object
     */
    public boolean isIndirect() {
        return (this.type == INDIRECT);
    }
    
    /**
     * Getter for property indRef.
     * @return Value of property indRef.
     */
    public PRIndirectReference getIndRef() {
        return this.indRef;
    }
    
    /**
     * Setter for property indRef.
     * @param indRef New value of property indRef.
     */
    public void setIndRef(PRIndirectReference indRef) {
        this.indRef = indRef;
    }
    
}
