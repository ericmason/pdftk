/*
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

import pdftk.com.lowagie.text.ExceptionConverter;

/**
 * A <CODE>PdfPattern</CODE> defines a ColorSpace
 *
 * @see		PdfStream
 */

public class PdfPattern extends PdfStream {
    
    PdfPattern(PdfPatternPainter painter) {
        super();
        PdfNumber one = new PdfNumber(1);
        PdfArray matrix = painter.getMatrix();
        if ( matrix != null ) {
            put(PdfName.MATRIX, matrix);
        }
        put(PdfName.TYPE, PdfName.PATTERN);
        put(PdfName.BBOX, new PdfRectangle(painter.getBoundingBox()));
        put(PdfName.RESOURCES, painter.getResources());
        put(PdfName.TILINGTYPE, one);
        put(PdfName.PATTERNTYPE, one);
        if (painter.isStencil())
            put(PdfName.PAINTTYPE, new PdfNumber(2));
        else
            put(PdfName.PAINTTYPE, one);
        put(PdfName.XSTEP, new PdfNumber(painter.getXStep()));
        put(PdfName.YSTEP, new PdfNumber(painter.getYStep()));
        bytes = painter.toPdf(null);
        put(PdfName.LENGTH, new PdfNumber(bytes.length));
        try {
            flateCompress();
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }
}
