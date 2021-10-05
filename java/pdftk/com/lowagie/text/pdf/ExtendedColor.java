/*
 * $Id: ExtendedColor.java,v 1.43 2005/03/29 14:08:15 blowagie Exp $
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

import java.awt.Color;
/**
 *
 * @author  Paulo Soares (psoares@consiste.pt)
 */
public class ExtendedColor extends Color{
    
    /** A serial version UID */
    private static final long serialVersionUID = 2722660170712380080L;

	/** a type of extended color. */
    public static final int TYPE_RGB = 0;
    /** a type of extended color. */
    public static final int TYPE_GRAY = 1;
    /** a type of extended color. */
    public static final int TYPE_CMYK = 2;
    /** a type of extended color. */
    public static final int TYPE_SEPARATION = 3;
    /** a type of extended color. */
    public static final int TYPE_PATTERN = 4;
    /** a type of extended color. */
    public static final int TYPE_SHADING = 5;
    
    protected int type;

    /**
     * Constructs an extended color of a certain type.
     * @param type
     */
    public ExtendedColor(int type) {
        super(0, 0, 0);
        this.type = type;
    }
    
    /**
     * Constructs an extended color of a certain type and a certain color.
     * @param type
     * @param red
     * @param green
     * @param blue
     */
    public ExtendedColor(int type, float red, float green, float blue) {
        super(normalize(red), normalize(green), normalize(blue));
        this.type = type;
    }
    
    /**
     * Gets the type of this color.
     * @return one of the types (see constants)
     */
    public int getType() {
        return type;
    }
    
    /**
     * Gets the type of a given color.
     * @param color
     * @return one of the types (see constants)
     */
    public static int getType(Color color) {
        if (color instanceof ExtendedColor)
            return ((ExtendedColor)color).getType();
        return TYPE_RGB;
    }

    static final float normalize(float value) {
        if (value < 0)
            return 0;
        if (value > 1)
            return 1;
        return value;
    }
}