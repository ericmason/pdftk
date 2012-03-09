/*
 * $Id: PdfSchema.java,v 1.4 2005/09/08 07:50:15 blowagie Exp $
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

import com.lowagie.text.Document;
import java.io.IOException;

/**
 * An implementation of an XmpSchema.
 */
public class PdfSchema extends XmpSchema {

	/** default namespace identifier*/
	public static final String DEFAULT_XPATH_ID = "pdf";
	/** default namespace uri*/
	public static final String DEFAULT_XPATH_URI = "http://ns.adobe.com/pdf/1.3/";
	
	/** Keywords. */
	public static final String KEYWORDS = "pdf:Keywords";
	/** The PDF file version (for example: 1.0, 1.3, and so on). */
	public static final String VERSION = "pdf:PDFVersion";
	/** The Producer. */
	public static final String PRODUCER = "pdf:Producer";


	/**
	 * @throws IOException
	 */
	public PdfSchema() throws IOException {
		super("xmlns:" + DEFAULT_XPATH_ID + "=\"" + DEFAULT_XPATH_URI + "\"");
		addProducer(Document.getVersion());
	}
	
	/**
	 * Adds keywords.
	 * @param keywords
	 */
	public void addKeywords(String keywords) {
		setProperty(KEYWORDS, keywords);
	}
	
	/**
	 * Adds the producer.
	 * @param producer
	 */
	public void addProducer(String producer) {
		setProperty(PRODUCER, producer);
	}

	/**
	 * Adds the version.
	 * @param version
	 */
	public void addVersion(String version) {
		setProperty(VERSION, version);
	}
}
