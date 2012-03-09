/*
 * $Id: PRStream.java,v 1.12 2002/06/20 13:30:25 blowagie Exp $
 * $Name:  $
 *
 * Copyright 2001, 2002 by Paulo Soares.
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

import java.io.*;
import com.lowagie.text.ExceptionConverter;
import java.util.zip.DeflaterOutputStream;
import com.lowagie.text.Document;
import java.io.OutputStream;
import java.io.IOException;
import java.util.ArrayList; // ssteward

public class PRStream extends PdfStream {
    
    protected PdfReader reader;
    protected int offset;
    protected int length;
    
    //added by ujihara for decryption
    protected int objNum = 0;
    protected int objGen = 0;
    
    public PRStream(PRStream stream, PdfDictionary newDic) {
        reader = stream.reader;
        offset = stream.offset;
        length = stream.length;
        compressed = stream.compressed;
        streamBytes = stream.streamBytes;
        bytes = stream.bytes;
        objNum = stream.objNum;
        objGen = stream.objGen;
        if (newDic != null)
            putAll(newDic);
        else
            hashMap.putAll(stream.hashMap);
    }

    public PRStream(PRStream stream, PdfDictionary newDic, PdfReader reader) {
        this(stream, newDic);
        this.reader = reader;
    }

    public PRStream(PdfReader reader, int offset) {
        this.reader = reader;
        this.offset = offset;
    }
    
    public PRStream(PdfReader reader, byte conts[]) {
        this.reader = reader;
        this.offset = -1;
        if (Document.compress) {
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                DeflaterOutputStream zip = new DeflaterOutputStream(stream);
                zip.write(conts);
                zip.close();
                bytes = stream.toByteArray();
            }
            catch (IOException ioe) {
                throw new ExceptionConverter(ioe);
            }
            put(PdfName.FILTER, PdfName.FLATEDECODE);
        }
        else
            bytes = conts;
        setLength(bytes.length);
    }
    
    /**Sets the data associated with the stream
     * @param data raw data, decrypted and uncompressed.
     */
    public void setData(byte[] data) {
        remove(PdfName.FILTER);
        this.offset = -1;
        if (Document.compress) {
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                DeflaterOutputStream zip = new DeflaterOutputStream(stream);
                zip.write(data);
                zip.close();
                bytes = stream.toByteArray();
            }
            catch (IOException ioe) {
                throw new ExceptionConverter(ioe);
            }
            put(PdfName.FILTER, PdfName.FLATEDECODE);
        }
        else
            bytes = data;
        setLength(bytes.length);
    }

    public void setLength(int length) {
        this.length = length;
        put(PdfName.LENGTH, new PdfNumber(length));
    }
    
    public int getOffset() {
        return offset;
    }
    
    public int getLength() {
        return length;
    }
    
    public PdfReader getReader() {
        return reader;
    }
    
    public byte[] getBytes() {
        return bytes;
    }
    
    public void setObjNum(int objNum, int objGen) {
        this.objNum = objNum;
        this.objGen = objGen;
    }
    
    int getObjNum() {
        return objNum;
    }
    
    int getObjGen() {
        return objGen;
    }

    // ssteward: added material to allow decoded, or "filtered," output;
    // perhaps decryption should be moved to PdfReader, a'la PdfReader.getStreamBytes(),
    // and encryption moved to PdfWriter?  it just seems unwieldy to have some
    // of this code duplicated in PdfReader.getStreamBytes();
    //
    public void toPdf(PdfWriter writer, OutputStream os) throws IOException {

	{ // ssteward (right?)
	    // the filters to apply to this, if any
	    ArrayList filters= new ArrayList(); {
		PdfObject filter= this.reader.getPdfObject(this.get(PdfName.FILTER));
		if (filter != null) {
		    if (filter.type() == PdfObject.NAME) {
			filters.add(filter);
		    }
		    else if (filter.type() == PdfObject.ARRAY) {
			filters = ((PdfArray)filter).getArrayList();
		    }
		}
	    }

	    // apply filters to our stream data before streaming?
	    boolean filterStream_b= 
		( writer.filterStreams &&
		  0< this.offset && // our stream data must be stored in a file, not in this.bytes
		  this.reader.getPdfObject( this.get(PdfName.DECODEPARMS) )== null && 
		  !filters.isEmpty() && allKnownFilters( this.reader, filters ) );

	    if( filterStream_b ) { // apply filters
		RandomAccessFileOrArray file= writer.getReaderFile( this.reader );
		this.bytes= PdfReader.getStreamBytes( this, file ); // decrypts, too

		this.remove(PdfName.FILTER);
		this.setLength( this.bytes.length );
		this.offset= -1; // indicate that we have read the stream into this.bytes
	    }

	    // apply compression to our stream data before streaming?
	    // our stream data may be in this.bytes or in a file
	    boolean compressStream_b=
		( writer.compressStreams &&
		  this.reader.getPdfObject( this.get(PdfName.DECODEPARMS) )== null && 
		  filters.isEmpty() );

	    if( compressStream_b ) { // apply compression
		if( 0< this.offset ) { // our data is in file; pull into this.bytes
		    RandomAccessFileOrArray file= writer.getReaderFile( this.reader );
		    this.bytes= PdfReader.getStreamBytes( this, file ); // decrypts, too
		}

		ByteArrayOutputStream stream= new ByteArrayOutputStream();
		DeflaterOutputStream zip= new DeflaterOutputStream( stream );
		zip.write( this.bytes );
		zip.close();
		this.bytes= stream.toByteArray();

		this.put( PdfName.FILTER, PdfName.FLATEDECODE );
		this.setLength( this.bytes.length );
		this.offset= -1; // indicate that we have read the stream into this.bytes
	    }
	}

        superToPdf(writer, os); // PdfDictionary.toPdf(), outputs FILTER, LENGTH, etc.
        os.write(STARTSTREAM);
        if (length > 0) {
            PdfEncryption crypto = null;
            if (writer != null) { // ssteward
                crypto = writer.getEncryption();
	    }
            if (offset < 0) { // our stream data is stored in this.bytes
                if (crypto == null) {
                    os.write(bytes);
		}
                else { // encrypt and output
                    crypto.prepareKey();
                    byte buf[] = new byte[length];
                    System.arraycopy(bytes, 0, buf, 0, length);
                    crypto.encryptRC4(buf);
                    os.write(buf);
                }
            }
            else { // our stream data is stored in a file
                byte buf[] = new byte[Math.min(length, 4092)];
                RandomAccessFileOrArray file = writer.getReaderFile(reader);
                boolean isOpen = file.isOpen();
                try {
                    file.seek(offset);
                    int size = length;

                    //added by ujihara for decryption
                    PdfEncryption decrypt = reader.getDecrypt();
                    if (decrypt != null) {
                        decrypt.setHashKey(objNum, objGen);
                        decrypt.prepareKey();
                    }

                    if (crypto != null)
                        crypto.prepareKey();
                    while (size > 0) {
                        int r = file.read(buf, 0, Math.min(size, buf.length));
                        size -= r;

                        if (decrypt != null)
                            decrypt.encryptRC4(buf, 0, r); //added by ujihara for decryption

                        if (crypto != null)
                            crypto.encryptRC4(buf, 0, r);
                        os.write(buf, 0, r);
                    }
                }
                finally {
                    if (!isOpen)
                        try{file.close();}catch(Exception e){}
                }
            }
        }
        os.write(ENDSTREAM);
    }

    // ssteward
    // do we know how to apply all of the filters in (ArrayList filters)?
    public static boolean allKnownFilters( PdfReader reader, ArrayList filters ) {
	boolean retVal= true;
	String name;
	for (int j = 0; j < filters.size(); ++j) {
	    name = ((PdfName)reader.getPdfObject((PdfObject)filters.get(j))).toString();
	    retVal= retVal &&
		( (name.equals("/FlateDecode") || name.equals("/Fl")) ||
		  (name.equals("/ASCIIHexDecode") || name.equals("/AHx")) ||
		  (name.equals("/ASCII85Decode") || name.equals("/A85")) ||
		  (name.equals("/LZWDecode")) );
	}
	return retVal;
    }

}
