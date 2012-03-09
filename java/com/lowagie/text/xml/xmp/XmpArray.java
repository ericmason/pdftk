/*
 * $Id: XmpArray.java,v 1.2 2005/09/03 12:50:41 blowagie Exp $
 * $Name:  $
 *
 * Copyright 2005 by Bruno Lowagie.
 *
 *
 * The Original Code is 'iText, a free JAVA-PDF library'.
 *
 * The Initial Developer of the Original Code is Bruno Lowagie. Portions created by
 * the Initial Developer are Copyright (C) 1999-2005 by Bruno Lowagie.
 * All Rights Reserved.
 * Co-Developer of the code is Paulo Soares. Portions created by the Co-Developer
 * are Copyright (C) 2000-2005 by Paulo Soares. All Rights Reserved.
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

package com.lowagie.text.xml.xmp;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * StringBuffer to construct an XMP array.
 */
public class XmpArray extends ArrayList {

	/** An array that is unordered. */
	public static final String UNORDERED = "rdf:Bag";
	/** An array that is ordered. */
	public static final String ORDERED = "rdf:Seq";
	/** An array with alternatives. */
	public static final String ALTERNATIVE = "rdf:Alt";
	
	/** the type of array. */
	protected String type;
	
	/**
	 * Creates an XmpArray.
	 * @param type the type of array: UNORDERED, ORDERED or ALTERNATIVE.
	 */
	public XmpArray(String type) {
		this.type = type;
	}
	
	/**
	 * Returns the String representation of the XmpArray.
	 * @return a String representation
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer("<");
		buf.append(type);
		buf.append(">");
		String s;
		for (Iterator i = iterator(); i.hasNext(); ) {
			s = (String) i.next();
			buf.append("<rdf:li>");
			buf.append(XmpSchema.escape(s));
			buf.append("</rdf:li>");
		}
		buf.append("</");
		buf.append(type);
		buf.append(">");
		return buf.toString();
	}
}