/*
 * $Id: DublinCoreSchema.java,v 1.5 2005/09/08 07:50:15 blowagie Exp $
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

import java.io.IOException;

/**
 * An implementation of an XmpSchema.
 */
public class DublinCoreSchema extends XmpSchema {

	/** default namespace identifier*/
	public static final String DEFAULT_XPATH_ID = "dc";
	/** default namespace uri*/
	public static final String DEFAULT_XPATH_URI = "http://purl.org/dc/elements/1.1/";
	
	/** External Contributors to the resource (other than the authors). */
	public static final String CONTRIBUTOR = "dc:contributor";
	/** The extent or scope of the resource. */
	public static final String COVERAGE = "dc:coverage";
	/** The authors of the resource (listed in order of precedence, if significant). */
	public static final String CREATOR = "dc:creator";
	/** Date(s) that something interesting happened to the resource. */
	public static final String DATE = "dc:date";
	/** A textual description of the content of the resource. Multiple values may be present for different languages. */
	public static final String DESCRIPTION = "dc:description";
	/** The file format used when saving the resource. Tools and applications should set this property to the save format of the data. It may include appropriate qualifiers. */
	public static final String FORMAT = "dc:format";
	/** Unique identifier of the resource. */
	public static final String IDENTIFIER = "dc:identifier";
	/** An unordered array specifying the languages used in the	resource. */
	public static final String LANGUAGE = "dc:language";
	/** Publishers. */
	public static final String PUBLISHER = "dc:publisher";
	/** Relationships to other documents. */
	public static final String RELATION = "dc:relation";
	/** Informal rights statement, selected by language. */
	public static final String RIGHTS = "dc:rights";
	/** Unique identifier of the work from which this resource was derived. */
	public static final String SOURCE = "dc:source";
	/** An unordered array of descriptive phrases or keywords that specify the topic of the content of the resource. */
	public static final String SUBJECT = "dc:subject";
	/** The title of the document, or the name given to the resource. Typically, it will be a name by which the resource is formally known. */
	public static final String TITLE = "dc:title";
	/** A document type; for example, novel, poem, or working paper. */
	public static final String TYPE = "dc:type";

	
	/**
	 * @throws IOException
	 */
	public DublinCoreSchema() throws IOException {
		super("xmlns:" + DEFAULT_XPATH_ID + "=\"" + DEFAULT_XPATH_URI + "\"");
		setProperty(FORMAT, "application/pdf");
	}
	
	/**
	 * Adds a title.
	 * @param title
	 */
	public void addTitle(String title) {
		setProperty(TITLE, title);
	}

	/**
	 * Adds a description.
	 * @param desc
	 */
	public void addDescription(String desc) {
		setProperty(DESCRIPTION, desc);
	}

	/**
	 * Adds a subject.
	 * @param subject
	 */
	public void addSubject(String subject) {
		XmpArray array = new XmpArray(XmpArray.UNORDERED);
		array.add(subject);
		setProperty(SUBJECT, array);
	}

	
	/**
	 * Adds a subject.
	 * @param subject array of subjects
	 */
	public void addSubject(String[] subject) {
		XmpArray array = new XmpArray(XmpArray.UNORDERED);
		for (int i = 0; i < subject.length; i++) {
			array.add(subject[i]);
		}
		setProperty(SUBJECT, array);
	}
	
	/**
	 * Adds a single author.
	 * @param author
	 */
	public void addAuthor(String author) {
		XmpArray array = new XmpArray(XmpArray.ORDERED);
		array.add(author);
		setProperty(CREATOR, array);
	}

	/**
	 * Adds an array of authors.
	 * @param author
	 */
	public void addAuthor(String[] author) {
		XmpArray array = new XmpArray(XmpArray.ORDERED);
		for (int i = 0; i < author.length; i++) {
			array.add(author[i]);
		}
		setProperty(CREATOR, array);
	}

	/**
	 * Adds a single publisher.
	 * @param publisher
	 */
	public void addPublisher(String publisher) {
		XmpArray array = new XmpArray(XmpArray.ORDERED);
		array.add(publisher);
		setProperty(PUBLISHER, array);
	}

	/**
	 * Adds an array of publishers.
	 * @param publisher
	 */
	public void addPublisher(String[] publisher) {
		XmpArray array = new XmpArray(XmpArray.ORDERED);
		for (int i = 0; i < publisher.length; i++) {
			array.add(publisher[i]);
		}
		setProperty(PUBLISHER, array);
	}
}
