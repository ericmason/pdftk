/*
 * Copyright 2005 by Paulo Soares.
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

import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.IOException;

/**
 * Creates a number tree.
 * @author Paulo Soares (psoares@consiste.pt)
 */
public class PdfNumberTree {
    
    private static final int leafSize = 64;
    
    /**
     * Creates a number tree.
     * @param items the item of the number tree. The key is an <CODE>Integer</CODE>
     * and the value is a <CODE>PdfIndirectReference</CODE>.
     * @param writer the writer
     * @throws IOException on error
     * @return the dictionary with the number tree.
     */    
    public static PdfDictionary writeTree(HashMap items, PdfWriter writer) throws IOException {
        if (items.size() == 0)
            return null;
        Integer numbers[] = new Integer[items.size()];
        numbers = (Integer[])items.keySet().toArray(numbers);
        Arrays.sort(numbers);
        if (numbers.length <= leafSize) {
            PdfDictionary dic = new PdfDictionary();
            PdfArray ar = new PdfArray();
            for (int k = 0; k < numbers.length; ++k) {
                ar.add(new PdfNumber(numbers[k].intValue()));
                ar.add((PdfIndirectReference)items.get(numbers[k]));
            }
            dic.put(PdfName.NUMS, ar);
            return dic;
        }
        int skip = leafSize;
        PdfIndirectReference kids[] = new PdfIndirectReference[(numbers.length + leafSize - 1) / leafSize];
        for (int k = 0; k < kids.length; ++k) {
            int offset = k * leafSize;
            int end = Math.min(offset + leafSize, numbers.length);
            PdfDictionary dic = new PdfDictionary();
            PdfArray arr = new PdfArray();
            arr.add(new PdfNumber(numbers[offset].intValue()));
            arr.add(new PdfNumber(numbers[end - 1].intValue()));
            dic.put(PdfName.LIMITS, arr);
            arr = new PdfArray();
            for (; offset < end; ++offset) {
                arr.add(new PdfNumber(numbers[offset].intValue()));
                arr.add((PdfIndirectReference)items.get(numbers[offset]));
            }
            dic.put(PdfName.NUMS, arr);
            kids[k] = writer.addToBody(dic).getIndirectReference();
        }
        int top = kids.length;
        while (true) {
            if (top <= leafSize) {
                PdfArray arr = new PdfArray();
                for (int k = 0; k < top; ++k)
                    arr.add(kids[k]);
                PdfDictionary dic = new PdfDictionary();
                dic.put(PdfName.KIDS, arr);
                return dic;
            }
            skip *= leafSize;
            int tt = (numbers.length + skip - 1 )/ skip;
            for (int k = 0; k < tt; ++k) {
                int offset = k * leafSize;
                int end = Math.min(offset + leafSize, top);
                PdfDictionary dic = new PdfDictionary();
                PdfArray arr = new PdfArray();
                arr.add(new PdfNumber(numbers[k * skip].intValue()));
                arr.add(new PdfNumber(numbers[Math.min((k + 1) * skip, numbers.length) - 1].intValue()));
                dic.put(PdfName.LIMITS, arr);
                arr = new PdfArray();
                for (; offset < end; ++offset) {
                    arr.add(kids[offset]);
                }
                dic.put(PdfName.KIDS, arr);
                kids[k] = writer.addToBody(dic).getIndirectReference();
            }
            top = tt;
        }
    }
    
    private static void iterateItems(PdfDictionary dic, HashMap items) {
        PdfArray nn = (PdfArray)PdfReader.getPdfObjectRelease(dic.get(PdfName.NUMS));
        if (nn != null) {
            ArrayList arr = nn.getArrayList();
            for (int k = 0; k < arr.size(); ++k) {
                PdfNumber s = (PdfNumber)PdfReader.getPdfObjectRelease((PdfObject)arr.get(k++));
                items.put(new Integer(s.intValue()), arr.get(k));
            }
        }
        else if ((nn = (PdfArray)PdfReader.getPdfObjectRelease(dic.get(PdfName.KIDS))) != null) {
            ArrayList arr = nn.getArrayList();
            for (int k = 0; k < arr.size(); ++k) {
                PdfDictionary kid = (PdfDictionary)PdfReader.getPdfObjectRelease((PdfObject)arr.get(k));
                iterateItems(kid, items);
            }
        }
    }
    
    public static HashMap readTree(PdfDictionary dic) {
        HashMap items = new HashMap();
        if (dic != null)
            iterateItems(dic, items);
        return items;
    }
}