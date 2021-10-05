/*
 * $Id: PdfReaderInstance.java,v 1.27 2003/05/02 09:01:27 blowagie Exp $
 * $Name:  $
 *
 * Copyright 2001, 2002 Paulo Soares
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.io.*;
/**
 * Instance of PdfReader in each output document.
 *
 * @author Paulo Soares (psoares@consiste.pt)
 */
class PdfReaderInstance {
    static final PdfLiteral IDENTITYMATRIX = new PdfLiteral("[1 0 0 1 0 0]");
    static final PdfNumber ONE = new PdfNumber(1);
    int myXref[];
    PdfReader reader;
    RandomAccessFileOrArray file;
    HashMap importedPages = new HashMap();
    PdfWriter writer;
    HashMap visited = new HashMap();
    ArrayList nextRound = new ArrayList();
    
    PdfReaderInstance(PdfReader reader, PdfWriter writer) throws IOException {
        this.reader = reader;
        this.writer = writer;
        file = reader.getSafeFile();
        myXref = new int[reader.getXrefSize()];
    }
    
    PdfReader getReader() {
        return reader;
    }
    
    PdfImportedPage getImportedPage(int pageNumber) {
        if (pageNumber < 1 || pageNumber > reader.getNumberOfPages())
            throw new IllegalArgumentException("Invalid page number");
        Integer i = new Integer(pageNumber);
        PdfImportedPage pageT = (PdfImportedPage)importedPages.get(i);
        if (pageT == null) {
            pageT = new PdfImportedPage(this, writer, pageNumber);
            importedPages.put(i, pageT);
        }
        return pageT;
    }
    
    int getNewObjectNumber(int number, int generation) {
        if (myXref[number] == 0) {
            myXref[number] = writer.getIndirectReferenceNumber();
            nextRound.add(new Integer(number));
        }
        return myXref[number];
    }
    
    RandomAccessFileOrArray getReaderFile() {
        return file;
    }
    
    PdfObject getResources(int pageNumber) {
        PdfObject obj = PdfReader.getPdfObjectRelease(reader.getPageNRelease(pageNumber).get(PdfName.RESOURCES));
        return obj;
    }
    
    
    PdfStream getFormXObject(int pageNumber) throws IOException {
        PdfDictionary page = reader.getPageNRelease(pageNumber);
        PdfObject contents = PdfReader.getPdfObjectRelease(page.get(PdfName.CONTENTS));
        PdfDictionary dic = new PdfDictionary();
        byte bout[] = null;
        if (contents != null) {
            if (contents.isStream())
                dic.putAll((PRStream)contents);
            else
                bout = reader.getPageContent(pageNumber, file);
        }
        else
            bout = new byte[0];
        dic.put(PdfName.RESOURCES, PdfReader.getPdfObjectRelease(page.get(PdfName.RESOURCES)));
        dic.put(PdfName.TYPE, PdfName.XOBJECT);
        dic.put(PdfName.SUBTYPE, PdfName.FORM);
        PdfImportedPage impPage = (PdfImportedPage)importedPages.get(new Integer(pageNumber));
        dic.put(PdfName.BBOX, new PdfRectangle(impPage.getBoundingBox()));
        PdfArray matrix = impPage.getMatrix();
        if (matrix == null)
            dic.put(PdfName.MATRIX, IDENTITYMATRIX);
        else
            dic.put(PdfName.MATRIX, matrix);
        dic.put(PdfName.FORMTYPE, ONE);
        PRStream stream;
        if (bout == null) {
            stream = new PRStream((PRStream)contents, dic);
        }
        else {
            stream = new PRStream(reader, bout);
            stream.putAll(dic);
        }
        return stream;
    }
    
    void writeAllVisited() throws IOException {
        while (nextRound.size() > 0) {
            ArrayList vec = nextRound;
            nextRound = new ArrayList();
            for (int k = 0; k < vec.size(); ++k) {
                Integer i = (Integer)vec.get(k);
                if (!visited.containsKey(i)) {
                    visited.put(i, null);
                    int n = i.intValue();
                    writer.addToBody(reader.getPdfObjectRelease(n), myXref[n]);
                }
            }
        }
    }
    
    void writeAllPages() throws IOException {
        try {
            file.reOpen();
            for (Iterator it = importedPages.values().iterator(); it.hasNext();) {
                PdfImportedPage ip = (PdfImportedPage)it.next();
                writer.addToBody(ip.getFormXObject(), ip.getIndirectReference());
            }
            writeAllVisited();
        }
        finally {
            try {
                reader.close();
                file.close();
            }
            catch (Exception e) {
                //Empty on purpose
            }
        }
    }
}