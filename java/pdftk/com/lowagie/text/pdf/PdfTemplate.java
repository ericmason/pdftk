/*
 * $Id: PdfTemplate.java,v 1.36 2003/05/02 09:01:28 blowagie Exp $
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
import java.io.IOException;

import pdftk.com.lowagie.text.Rectangle;

/**
 * Implements the form XObject.
 */

public class PdfTemplate extends PdfContentByte {
    public static final int TYPE_TEMPLATE = 1;
    public static final int TYPE_IMPORTED = 2;
    public static final int TYPE_PATTERN = 3;
    protected int type;
    /** The indirect reference to this template */
    protected PdfIndirectReference thisReference;
    
    /** The resources used by this template */
    protected PageResources pageResources;
    
    
    /** The bounding box of this template */
    protected Rectangle bBox = new Rectangle(0, 0);
    
    protected PdfArray matrix;
    
    protected PdfTransparencyGroup group;
    
    protected PdfOCG layer;
    
    /**
     *Creates a <CODE>PdfTemplate</CODE>.
     */
    
    protected PdfTemplate() {
        super(null);
        type = TYPE_TEMPLATE;
    }
    
    /**
     * Creates new PdfTemplate
     *
     * @param wr the <CODE>PdfWriter</CODE>
     */
    
    PdfTemplate(PdfWriter wr) {
        super(wr);
        type = TYPE_TEMPLATE;
        pageResources = new PageResources();
        pageResources.addDefaultColor(wr.getDefaultColorspace());
        thisReference = writer.getPdfIndirectReference();
    }
    
    /**
     * Sets the bounding width of this template.
     *
     * @param width the bounding width
     */
    
    public void setWidth(float width) {
        bBox.setLeft(0);
        bBox.setRight(width);
    }
    
    /**
     * Sets the bounding heigth of this template.
     *
     * @param height the bounding height
     */
    
    public void setHeight(float height) {
        bBox.setBottom(0);
        bBox.setTop(height);
    }
    
    /**
     * Gets the bounding width of this template.
     *
     * @return width the bounding width
     */
    public float getWidth() {
        return bBox.width();
    }
    
    /**
     * Gets the bounding heigth of this template.
     *
     * @return heigth the bounding height
     */
    
    public float getHeight() {
        return bBox.height();
    }
    
    public Rectangle getBoundingBox() {
        return bBox;
    }
    
    public void setBoundingBox(Rectangle bBox) {
        this.bBox = bBox;
    }
    
    /**
     * Sets the layer this template belongs to.
     * @param layer the layer this template belongs to
     */    
    public void setLayer(PdfOCG layer) {
        this.layer = layer;
    }
    
    /**
     * Gets the layer this template belongs to.
     * @return the layer this template belongs to or <code>null</code> for no layer defined
     */
    public PdfOCG getLayer() {
        return layer;
    }

    public void setMatrix(float a, float b, float c, float d, float e, float f) {
		matrix = new PdfArray();
		matrix.add(new PdfNumber(a));
		matrix.add(new PdfNumber(b));
		matrix.add(new PdfNumber(c));
		matrix.add(new PdfNumber(d));
		matrix.add(new PdfNumber(e));
		matrix.add(new PdfNumber(f));
	}

	PdfArray getMatrix() {
		return matrix;
	}
    
    /**
     * Gets the indirect reference to this template.
     *
     * @return the indirect reference to this template
     */
    
    PdfIndirectReference getIndirectReference() {
        return thisReference;
    }
        
    public void beginVariableText() {
        content.append("/Tx BMC ");
    }
    
    public void endVariableText() {
        content.append("EMC ");
    }
    
    /**
     * Constructs the resources used by this template.
     *
     * @return the resources used by this template
     */
    
    PdfObject getResources() {
        return getPageResources().getResources();
    }
    
    /**
     * Gets the stream representing this template.
     *
     * @return the stream representing this template
     */
    
    PdfStream getFormXObject() throws IOException {
        return new PdfFormXObject(this);
    }
        
    /**
     * Gets a duplicate of this <CODE>PdfTemplate</CODE>. All
     * the members are copied by reference but the buffer stays different.
     * @return a copy of this <CODE>PdfTemplate</CODE>
     */
    
    public PdfContentByte getDuplicate() {
        PdfTemplate tpl = new PdfTemplate();
        tpl.writer = writer;
        tpl.pdf = pdf;
        tpl.thisReference = thisReference;
        tpl.pageResources = pageResources;
        tpl.bBox = new Rectangle(bBox);
        tpl.group = group;
        tpl.layer = layer;
        if (matrix != null) {
            tpl.matrix = new PdfArray(matrix);
        }
        tpl.separator = separator;
        return tpl;
    }
    
    public int getType() {
        return type;
    }
    
    PageResources getPageResources() {
        return pageResources;
    }
    
    /** Getter for property group.
     * @return Value of property group.
     *
     */
    public PdfTransparencyGroup getGroup() {
        return this.group;
    }
    
    /** Setter for property group.
     * @param group New value of property group.
     *
     */
    public void setGroup(PdfTransparencyGroup group) {
        this.group = group;
    }
    
}