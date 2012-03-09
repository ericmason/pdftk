/*
 * $Id: XmpSchema.java,v 1.5 2005/09/08 10:27:29 blowagie Exp $
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

import java.util.Enumeration;
import java.util.Properties;

/**
 * Abstract superclass of the XmpSchemas supported by iText.
 */
public abstract class XmpSchema extends Properties {
	
	/** the namesspace */
	protected String xmlns;
	
	/** Constructs an XMP schema. 
	 * @param xmlns
	 */
	public XmpSchema(String xmlns) {
		super();
		this.xmlns = xmlns;
	}
	/**
	 * The String representation of the contents.
	 * @return a String representation.
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (Enumeration e = this.propertyNames(); e.hasMoreElements(); ) {
			process(buf, e.nextElement());
		}
		return buf.toString();
	}
	/**
	 * Processes a property
	 * @param buf
	 * @param p
	 */
	protected void process(StringBuffer buf, Object p) {
		buf.append("<");
		buf.append(p);
		buf.append(">");
		buf.append(this.get(p));
		buf.append("</");
		buf.append(p);
		buf.append(">");
	}
	/**
	 * @return Returns the xmlns.
	 */
	public String getXmlns() {
		return xmlns;
	}	
	
	/**
	 * @param key
	 * @param value
	 * @return the previous property (null if there wasn't one)
	 */
	public synchronized Object addProperty(String key, String value) {
		return this.setProperty(key, value);
	}
	
	/**
	 * @see java.util.Properties#setProperty(java.lang.String, java.lang.String)
	 */
	public synchronized Object setProperty(String key, String value) {
		return super.setProperty(key, escape(value));
	}
	
	/**
	 * @see java.util.Properties#setProperty(java.lang.String, java.lang.String)
	 * 
	 * @param key
	 * @param value
	 * @return the previous property (null if there wasn't one)
	 */
	public synchronized Object setProperty(String key, XmpArray value) {
		return super.setProperty(key, value.toString());
	}
	/**
	 * @param content
	 * @return an escaped string
	 */
	public static String escape(String content) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < content.length(); i++) {
			switch(content.charAt(i)) {
			case '<':
				buf.append("&lt;");
				break;
			case '>':
				buf.append("&gt;");
				break;
			case '\'':
				buf.append("&apos;");
				break;
			case '\"':
				buf.append("&quot;");
				break;
			case '&':
				buf.append("&amp;");
				break;
			default:
				buf.append(content.charAt(i));
			}
		}
		return buf.toString();
	}
}
