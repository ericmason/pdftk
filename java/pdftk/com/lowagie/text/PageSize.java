/*
 * $Id: PageSize.java,v 1.26 2002/06/20 13:30:24 blowagie Exp $
 * $Name:  $
 *
 * Copyright 1999, 2000, 2001, 2002 by Bruno Lowagie.
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

package pdftk.com.lowagie.text;

/**
 * The <CODE>PageSize</CODE>-object contains a number of rectangles representing the most common papersizes.
 *
 * @see		Rectangle
 */

public class PageSize {
    
    // membervariables
    
/** This is the letter format */
    public static final Rectangle LETTER = new Rectangle(612,792);
    
/** This is the note format */
    public static final Rectangle NOTE = new Rectangle(540,720);
    
/** This is the legal format */
    public static final Rectangle LEGAL = new Rectangle(612,1008);
    
/** This is the a0 format */
    public static final Rectangle A0 = new Rectangle(2384,3370);
    
/** This is the a1 format */
    public static final Rectangle A1 = new Rectangle(1684,2384);
    
/** This is the a2 format */
    public static final Rectangle A2 = new Rectangle(1190,1684);
    
/** This is the a3 format */
    public static final Rectangle A3 = new Rectangle(842,1190);
    
/** This is the a4 format */
    public static final Rectangle A4 = new Rectangle(595,842);
    
/** This is the a5 format */
    public static final Rectangle A5 = new Rectangle(421,595);
    
/** This is the a6 format */
    public static final Rectangle A6 = new Rectangle(297,421);
    
/** This is the a7 format */
    public static final Rectangle A7 = new Rectangle(210,297);
    
/** This is the a8 format */
    public static final Rectangle A8 = new Rectangle(148,210);
    
/** This is the a9 format */
    public static final Rectangle A9 = new Rectangle(105,148);
    
/** This is the a10 format */
    public static final Rectangle A10 = new Rectangle(74,105);
    
/** This is the b0 format */
    public static final Rectangle B0 = new Rectangle(2836,4008);
    
/** This is the b1 format */
    public static final Rectangle B1 = new Rectangle(2004,2836);
    
/** This is the b2 format */
    public static final Rectangle B2 = new Rectangle(1418,2004);
    
/** This is the b3 format */
    public static final Rectangle B3 = new Rectangle(1002,1418);
    
/** This is the b4 format */
    public static final Rectangle B4 = new Rectangle(709,1002);
    
/** This is the b5 format */
    public static final Rectangle B5 = new Rectangle(501,709);
    
/** This is the archE format */
    public static final Rectangle ARCH_E = new Rectangle(2592,3456);
    
/** This is the archD format */
    public static final Rectangle ARCH_D = new Rectangle(1728,2592);
    
/** This is the archC format */
    public static final Rectangle ARCH_C = new Rectangle(1296,1728);
    
/** This is the archB format */
    public static final Rectangle ARCH_B = new Rectangle(864,1296);
    
/** This is the archA format */
    public static final Rectangle ARCH_A = new Rectangle(648,864);
    
/** This is the flsa format */
    public static final Rectangle FLSA = new Rectangle(612,936);
    
/** This is the flse format */
    public static final Rectangle FLSE = new Rectangle(612,936);
    
/** This is the halfletter format */
    public static final Rectangle HALFLETTER = new Rectangle(396,612);
    
/** This is the 11x17 format */
    public static final Rectangle _11X17 = new Rectangle(792,1224);
    
/** This is the ledger format */
    public static final Rectangle LEDGER = new Rectangle(1224,792);
}
