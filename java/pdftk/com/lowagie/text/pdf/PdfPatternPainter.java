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

// import pdftk.com.lowagie.text.DocumentException;
// import pdftk.com.lowagie.text.Image; ssteward: dropped in 1.44
import pdftk.com.lowagie.text.Rectangle;
import java.awt.Color;

/**
 * Implements the pattern.
 */

public class PdfPatternPainter extends PdfTemplate {
    
    protected float xstep, ystep;
    protected boolean stencil = false;
    protected Color defaultColor;
    
    /**
     *Creates a <CODE>PdfPattern</CODE>.
     */
    
    private PdfPatternPainter() {
        super(null);
        type = TYPE_PATTERN;
    }
    
    /**
     * Creates new PdfPattern
     *
     * @param wr the <CODE>PdfWriter</CODE>
     */
    
    PdfPatternPainter(PdfWriter wr) {
        super(wr);
        type = TYPE_PATTERN;
    }
    
    PdfPatternPainter(PdfWriter wr, Color defaultColor) {
        this(wr);
        stencil = true;
        if (defaultColor == null)
            this.defaultColor = Color.gray;
        else
            this.defaultColor = defaultColor;
    }
    
    /**
     * Sets the horizontal interval of this pattern.
     *
     * @param xstep the xstep in horizontal painting
     */
    
    public void setXStep(float xstep) {
        this.xstep = xstep;
    }
    
    /**
     * Sets the vertical interval of this pattern.
     *
     * @param ystep in vertical painting
     */
    
    public void setYStep(float ystep) {
        this.ystep = ystep;
    }
    
    /**
     * Returns the horizontal interval when repeating the pattern.
     * @return a value
     */
    public float getXStep() {
        return this.xstep;
    }
    
    /**
     * Returns the vertical interval when repeating the pattern.
     * @return a value
     */
    public float getYStep() {
        return this.ystep;
    }
    
    /**
     * Tells you if this pattern is colored/uncolored (stencil = uncolored, you need to set a default color).
     * @return true if the pattern is an uncolored tiling pattern (stencil).
     */
    public boolean isStencil() {
        return stencil;
    }
    
    /**
     * Sets the transformation matrix for the pattern.
     * @param a
     * @param b
     * @param c
     * @param d
     * @param e
     * @param f
     */
    public void setPatternMatrix(float a, float b, float c, float d, float e, float f) {
        setMatrix(a, b, c, d, e, f);
    }
    /**
     * Gets the stream representing this pattern
     *
     * @return the stream representing this pattern
     */
    
    PdfPattern getPattern() {
        return new PdfPattern(this);
    }
    
    /**
     * Gets a duplicate of this <CODE>PdfPatternPainter</CODE>. All
     * the members are copied by reference but the buffer stays different.
     * @return a copy of this <CODE>PdfPatternPainter</CODE>
     */
    
    public PdfContentByte getDuplicate() {
        PdfPatternPainter tpl = new PdfPatternPainter();
        tpl.writer = writer;
        tpl.pdf = pdf;
        tpl.thisReference = thisReference;
        tpl.pageResources = pageResources;
        tpl.bBox = new Rectangle(bBox);
        tpl.xstep = xstep;
        tpl.ystep = ystep;
        tpl.matrix = matrix;
        tpl.stencil = stencil;
        tpl.defaultColor = defaultColor;
        return tpl;
    }
    
    /**
     * Returns the default color of the pattern.
     * @return a Color
     */
    public Color getDefaultColor() {
        return defaultColor;
    }
    
    /**
     * @see pdftk.com.lowagie.text.pdf.PdfContentByte#setGrayFill(float)
     */
    public void setGrayFill(float gray) {
        checkNoColor();
        super.setGrayFill(gray);
    }
    
    /**
     * @see pdftk.com.lowagie.text.pdf.PdfContentByte#resetGrayFill()
     */
    public void resetGrayFill() {
        checkNoColor();
        super.resetGrayFill();
    }
    
    /**
     * @see pdftk.com.lowagie.text.pdf.PdfContentByte#setGrayStroke(float)
     */
    public void setGrayStroke(float gray) {
        checkNoColor();
        super.setGrayStroke(gray);
    }
    
    /**
     * @see pdftk.com.lowagie.text.pdf.PdfContentByte#resetGrayStroke()
     */
    public void resetGrayStroke() {
        checkNoColor();
        super.resetGrayStroke();
    }
    
    /**
     * @see pdftk.com.lowagie.text.pdf.PdfContentByte#setRGBColorFillF(float, float, float)
     */
    public void setRGBColorFillF(float red, float green, float blue) {
        checkNoColor();
        super.setRGBColorFillF(red, green, blue);
    }
    
    /**
     * @see pdftk.com.lowagie.text.pdf.PdfContentByte#resetRGBColorFill()
     */
    public void resetRGBColorFill() {
        checkNoColor();
        super.resetRGBColorFill();
    }
    
    /**
     * @see pdftk.com.lowagie.text.pdf.PdfContentByte#setRGBColorStrokeF(float, float, float)
     */
    public void setRGBColorStrokeF(float red, float green, float blue) {
        checkNoColor();
        super.setRGBColorStrokeF(red, green, blue);
    }
    
    /**
     * @see pdftk.com.lowagie.text.pdf.PdfContentByte#resetRGBColorStroke()
     */
    public void resetRGBColorStroke() {
        checkNoColor();
        super.resetRGBColorStroke();
    }
    
    /**
     * @see pdftk.com.lowagie.text.pdf.PdfContentByte#setCMYKColorFillF(float, float, float, float)
     */
    public void setCMYKColorFillF(float cyan, float magenta, float yellow, float black) {
        checkNoColor();
        super.setCMYKColorFillF(cyan, magenta, yellow, black);
    }
    
    /**
     * @see pdftk.com.lowagie.text.pdf.PdfContentByte#resetCMYKColorFill()
     */
    public void resetCMYKColorFill() {
        checkNoColor();
        super.resetCMYKColorFill();
    }
    
    /**
     * @see pdftk.com.lowagie.text.pdf.PdfContentByte#setCMYKColorStrokeF(float, float, float, float)
     */
    public void setCMYKColorStrokeF(float cyan, float magenta, float yellow, float black) {
        checkNoColor();
        super.setCMYKColorStrokeF(cyan, magenta, yellow, black);
    }
    
    /**
     * @see pdftk.com.lowagie.text.pdf.PdfContentByte#resetCMYKColorStroke()
     */
    public void resetCMYKColorStroke() {
        checkNoColor();
        super.resetCMYKColorStroke();
    }
    
    /**
     * @see pdftk.com.lowagie.text.pdf.PdfContentByte#addImage(pdftk.com.lowagie.text.Image, float, float, float, float, float, float)
     */
    /* ssteward: dropped in 1.44
    public void addImage(Image image, float a, float b, float c, float d, float e, float f) throws DocumentException {
        if (stencil && !image.isMask())
            checkNoColor();
        super.addImage(image, a, b, c, d, e, f);
    }
    */
    
    /**
     * @see pdftk.com.lowagie.text.pdf.PdfContentByte#setCMYKColorFill(int, int, int, int)
     */
    public void setCMYKColorFill(int cyan, int magenta, int yellow, int black) {
        checkNoColor();
        super.setCMYKColorFill(cyan, magenta, yellow, black);
    }
    
    /**
     * @see pdftk.com.lowagie.text.pdf.PdfContentByte#setCMYKColorStroke(int, int, int, int)
     */
    public void setCMYKColorStroke(int cyan, int magenta, int yellow, int black) {
        checkNoColor();
        super.setCMYKColorStroke(cyan, magenta, yellow, black);
    }
    
    /**
     * @see pdftk.com.lowagie.text.pdf.PdfContentByte#setRGBColorFill(int, int, int)
     */
    public void setRGBColorFill(int red, int green, int blue) {
        checkNoColor();
        super.setRGBColorFill(red, green, blue);
    }
    
    /**
     * @see pdftk.com.lowagie.text.pdf.PdfContentByte#setRGBColorStroke(int, int, int)
     */
    public void setRGBColorStroke(int red, int green, int blue) {
        checkNoColor();
        super.setRGBColorStroke(red, green, blue);
    }
    
    /**
     * @see pdftk.com.lowagie.text.pdf.PdfContentByte#setColorStroke(java.awt.Color)
     */
    public void setColorStroke(Color color) {
        checkNoColor();
        super.setColorStroke(color);
    }
    
    /**
     * @see pdftk.com.lowagie.text.pdf.PdfContentByte#setColorFill(java.awt.Color)
     */
    public void setColorFill(Color color) {
        checkNoColor();
        super.setColorFill(color);
    }
    
    /**
     * @see pdftk.com.lowagie.text.pdf.PdfContentByte#setColorFill(pdftk.com.lowagie.text.pdf.PdfSpotColor, float)
     */
    public void setColorFill(PdfSpotColor sp, float tint) {
        checkNoColor();
        super.setColorFill(sp, tint);
    }
    
    /**
     * @see pdftk.com.lowagie.text.pdf.PdfContentByte#setColorStroke(pdftk.com.lowagie.text.pdf.PdfSpotColor, float)
     */
    public void setColorStroke(PdfSpotColor sp, float tint) {
        checkNoColor();
        super.setColorStroke(sp, tint);
    }
    
    /**
     * @see pdftk.com.lowagie.text.pdf.PdfContentByte#setPatternFill(pdftk.com.lowagie.text.pdf.PdfPatternPainter)
     */
    public void setPatternFill(PdfPatternPainter p) {
        checkNoColor();
        super.setPatternFill(p);
    }
    
    /**
     * @see pdftk.com.lowagie.text.pdf.PdfContentByte#setPatternFill(pdftk.com.lowagie.text.pdf.PdfPatternPainter, java.awt.Color, float)
     */
    public void setPatternFill(PdfPatternPainter p, Color color, float tint) {
        checkNoColor();
        super.setPatternFill(p, color, tint);
    }
    
    /**
     * @see pdftk.com.lowagie.text.pdf.PdfContentByte#setPatternStroke(pdftk.com.lowagie.text.pdf.PdfPatternPainter, java.awt.Color, float)
     */
    public void setPatternStroke(PdfPatternPainter p, Color color, float tint) {
        checkNoColor();
        super.setPatternStroke(p, color, tint);
    }
    
    /**
     * @see pdftk.com.lowagie.text.pdf.PdfContentByte#setPatternStroke(pdftk.com.lowagie.text.pdf.PdfPatternPainter)
     */
    public void setPatternStroke(PdfPatternPainter p) {
        checkNoColor();
        super.setPatternStroke(p);
    }
    
    void checkNoColor() {
        if (stencil)
            throw new RuntimeException("Colors are not allowed in uncolored tile patterns.");
    }
}
