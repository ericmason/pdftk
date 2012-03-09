/*
/*
 * Copyright 2002 Paulo Soares
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
import com.lowagie.text.DocumentException;
import java.util.HashMap;

/** This class takes any PDF and returns exactly the same but
 * encrypted. All the content, links, outlines, etc, are kept.
 * It is also possible to change the info dictionary.
 */
public class PdfEncryptor {
    
    private PdfEncryptor(){
    }
    
    /** Entry point to encrypt a PDF document. The encryption parameters are the same as in
     * <code>PdfWriter</code>. The userPassword and the
     *  ownerPassword can be null or have zero length. In this case the ownerPassword
     *  is replaced by a random string. The open permissions for the document can be
     *  AllowPrinting, AllowModifyContents, AllowCopy, AllowModifyAnnotations,
     *  AllowFillIn, AllowScreenReaders, AllowAssembly and AllowDegradedPrinting.
     *  The permissions can be combined by ORing them.
     * @param reader the read PDF
     * @param os the output destination
     * @param userPassword the user password. Can be null or empty
     * @param ownerPassword the owner password. Can be null or empty
     * @param permissions the user permissions
     * @param strength128Bits <code>true</code> for 128 bit key length, <code>false</code> for 40 bit key length
     * @throws DocumentException on error
     * @throws IOException on error */
    public static void encrypt(PdfReader reader, OutputStream os, byte userPassword[], byte ownerPassword[], int permissions, boolean strength128Bits) throws DocumentException, IOException {
        PdfStamper stamper = new PdfStamper(reader, os);
        stamper.setEncryption(userPassword, ownerPassword, permissions, strength128Bits);
        stamper.close();
    }
    
    /** Entry point to encrypt a PDF document. The encryption parameters are the same as in
     * <code>PdfWriter</code>. The userPassword and the
     *  ownerPassword can be null or have zero length. In this case the ownerPassword
     *  is replaced by a random string. The open permissions for the document can be
     *  AllowPrinting, AllowModifyContents, AllowCopy, AllowModifyAnnotations,
     *  AllowFillIn, AllowScreenReaders, AllowAssembly and AllowDegradedPrinting.
     *  The permissions can be combined by ORing them.
     * @param reader the read PDF
     * @param os the output destination
     * @param userPassword the user password. Can be null or empty
     * @param ownerPassword the owner password. Can be null or empty
     * @param permissions the user permissions
     * @param strength128Bits <code>true</code> for 128 bit key length, <code>false</code> for 40 bit key length
     * @param newInfo an optional <CODE>String</CODE> map to add or change
     * the info dictionary. Entries with <CODE>null</CODE>
     * values delete the key in the original info dictionary
     * @throws DocumentException on error
     * @throws IOException on error
     */
    public static void encrypt(PdfReader reader, OutputStream os, byte userPassword[], byte ownerPassword[], int permissions, boolean strength128Bits, HashMap newInfo) throws DocumentException, IOException {
        PdfStamper stamper = new PdfStamper(reader, os);
        stamper.setEncryption(userPassword, ownerPassword, permissions, strength128Bits);
        stamper.setMoreInfo(newInfo);
        stamper.close();
    }
    
    /** Entry point to encrypt a PDF document. The encryption parameters are the same as in
     * <code>PdfWriter</code>. The userPassword and the
     *  ownerPassword can be null or have zero length. In this case the ownerPassword
     *  is replaced by a random string. The open permissions for the document can be
     *  AllowPrinting, AllowModifyContents, AllowCopy, AllowModifyAnnotations,
     *  AllowFillIn, AllowScreenReaders, AllowAssembly and AllowDegradedPrinting.
     *  The permissions can be combined by ORing them.
     * @param reader the read PDF
     * @param os the output destination
     * @param strength <code>true</code> for 128 bit key length, <code>false</code> for 40 bit key length
     * @param userPassword the user password. Can be null or empty
     * @param ownerPassword the owner password. Can be null or empty
     * @param permissions the user permissions
     * @throws DocumentException on error
     * @throws IOException on error */
    public static void encrypt(PdfReader reader, OutputStream os, boolean strength, String userPassword, String ownerPassword, int permissions) throws DocumentException, IOException {
        PdfStamper stamper = new PdfStamper(reader, os);
        stamper.setEncryption(strength, userPassword, ownerPassword, permissions);
        stamper.close();
    }
    
    /** Entry point to encrypt a PDF document. The encryption parameters are the same as in
     * <code>PdfWriter</code>. The userPassword and the
     *  ownerPassword can be null or have zero length. In this case the ownerPassword
     *  is replaced by a random string. The open permissions for the document can be
     *  AllowPrinting, AllowModifyContents, AllowCopy, AllowModifyAnnotations,
     *  AllowFillIn, AllowScreenReaders, AllowAssembly and AllowDegradedPrinting.
     *  The permissions can be combined by ORing them.
     * @param reader the read PDF
     * @param os the output destination
     * @param strength <code>true</code> for 128 bit key length, <code>false</code> for 40 bit key length
     * @param userPassword the user password. Can be null or empty
     * @param ownerPassword the owner password. Can be null or empty
     * @param permissions the user permissions
     * @param newInfo an optional <CODE>String</CODE> map to add or change
     * the info dictionary. Entries with <CODE>null</CODE>
     * values delete the key in the original info dictionary
     * @throws DocumentException on error
     * @throws IOException on error
     */
    public static void encrypt(PdfReader reader, OutputStream os, boolean strength, String userPassword, String ownerPassword, int permissions, HashMap newInfo) throws DocumentException, IOException {
        PdfStamper stamper = new PdfStamper(reader, os);
        stamper.setEncryption(strength, userPassword, ownerPassword, permissions);
        stamper.setMoreInfo(newInfo);
        stamper.close();
    }
    
    /**
     * Give you a verbose analysis of the permissions.
     * @param permissions the permissions value of a PDF file
     * @return a String that explains the meaning of the permissions value
     */
    public static String getPermissionsVerbose(int permissions) {
    	StringBuffer buf = new StringBuffer("Allowed:");
    	if ((PdfWriter.AllowPrinting & permissions) == PdfWriter.AllowPrinting) buf.append(" Printing");
        if ((PdfWriter.AllowModifyContents & permissions) == PdfWriter.AllowModifyContents) buf.append(" Modify contents");
        if ((PdfWriter.AllowCopy & permissions) == PdfWriter.AllowCopy) buf.append(" Copy");
        if ((PdfWriter.AllowModifyAnnotations & permissions) == PdfWriter.AllowModifyAnnotations) buf.append(" Modify annotations");
        if ((PdfWriter.AllowFillIn & permissions) == PdfWriter.AllowFillIn) buf.append(" Fill in");
        if ((PdfWriter.AllowScreenReaders & permissions) == PdfWriter.AllowScreenReaders) buf.append(" Screen readers");
        if ((PdfWriter.AllowAssembly & permissions) == PdfWriter.AllowAssembly) buf.append(" Assembly");
        if ((PdfWriter.AllowDegradedPrinting & permissions) == PdfWriter.AllowDegradedPrinting) buf.append(" Degraded printing");
        return buf.toString();
    }
}