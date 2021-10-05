/*
 * $Id: Annotation.java,v 1.70 2005/04/13 09:17:09 blowagie Exp $
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

import java.net.URL;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;

/**
 * An <CODE>Annotation</CODE> is a little note that can be added to a page on
 * a document.
 * 
 * @see Element
 * @see Anchor
 */

public class Annotation implements Element, MarkupAttributes {

	// membervariables

	/** This is a possible annotation type. */
	public static final int TEXT = 0;

	/** This is a possible annotation type. */
	public static final int URL_NET = 1;

	/** This is a possible annotation type. */
	public static final int URL_AS_STRING = 2;

	/** This is a possible annotation type. */
	public static final int FILE_DEST = 3;

	/** This is a possible annotation type. */
	public static final int FILE_PAGE = 4;

	/** This is a possible annotation type. */
	public static final int NAMED_DEST = 5;

	/** This is a possible annotation type. */
	public static final int LAUNCH = 6;

	/** This is a possible annotation type. */
	public static final int SCREEN = 7;

	/** This is a possible attribute. */
	public static String TITLE = "title";

	/** This is a possible attribute. */
	public static String CONTENT = "content";

	/** This is a possible attribute. */
	public static String URL = "url";

	/** This is a possible attribute. */
	public static String FILE = "file";

	/** This is a possible attribute. */
	public static String DESTINATION = "destination";

	/** This is a possible attribute. */
	public static String PAGE = "page";

	/** This is a possible attribute. */
	public static String NAMED = "named";

	/** This is a possible attribute. */
	public static String APPLICATION = "application";

	/** This is a possible attribute. */
	public static String PARAMETERS = "parameters";

	/** This is a possible attribute. */
	public static String OPERATION = "operation";

	/** This is a possible attribute. */
	public static String DEFAULTDIR = "defaultdir";

	/** This is a possible attribute. */
	public static String LLX = "llx";

	/** This is a possible attribute. */
	public static String LLY = "lly";

	/** This is a possible attribute. */
	public static String URX = "urx";

	/** This is a possible attribute. */
	public static String URY = "ury";

	/** This is a possible attribute. */
	public static String MIMETYPE = "mime";

	/** This is the type of annotation. */
	protected int annotationtype;

	/** This is the title of the <CODE>Annotation</CODE>. */
	protected HashMap annotationAttributes = new HashMap();

	/** Contains extra markupAttributes */
	protected Properties markupAttributes = null;

	/** This is the lower left x-value */
	protected float llx = Float.NaN;

	/** This is the lower left y-value */
	protected float lly = Float.NaN;

	/** This is the upper right x-value */
	protected float urx = Float.NaN;

	/** This is the upper right y-value */
	protected float ury = Float.NaN;

	// constructors

	/**
	 * Constructs an <CODE>Annotation</CODE> with a certain title and some
	 * text.
	 * 
	 * @param llx
	 *            lower left x coordinate
	 * @param lly
	 *            lower left y coordinate
	 * @param urx
	 *            upper right x coordinate
	 * @param ury
	 *            upper right y coordinate
	 */

	private Annotation(float llx, float lly, float urx, float ury) {
		this.llx = llx;
		this.lly = lly;
		this.urx = urx;
		this.ury = ury;
	}

    public Annotation(Annotation an) {
        annotationtype = an.annotationtype;
        annotationAttributes = an.annotationAttributes;
        markupAttributes = an.markupAttributes;
        llx = an.llx;
        lly = an.lly;
        urx = an.urx;
        ury = an.ury;
    }
    
	/**
	 * Constructs an <CODE>Annotation</CODE> with a certain title and some
	 * text.
	 * 
	 * @param title
	 *            the title of the annotation
	 * @param text
	 *            the content of the annotation
	 */

	public Annotation(String title, String text) {
		annotationtype = TEXT;
		annotationAttributes.put(TITLE, title);
		annotationAttributes.put(CONTENT, text);
	}

	/**
	 * Constructs an <CODE>Annotation</CODE> with a certain title and some
	 * text.
	 * 
	 * @param title
	 *            the title of the annotation
	 * @param text
	 *            the content of the annotation
	 * @param llx
	 *            the lower left x-value
	 * @param lly
	 *            the lower left y-value
	 * @param urx
	 *            the upper right x-value
	 * @param ury
	 *            the upper right y-value
	 */

	public Annotation(String title, String text, float llx, float lly,
			float urx, float ury) {
		this(llx, lly, urx, ury);
		annotationtype = TEXT;
		annotationAttributes.put(TITLE, title);
		annotationAttributes.put(CONTENT, text);
	}

	/**
	 * Constructs an <CODE>Annotation</CODE>.
	 * 
	 * @param llx
	 *            the lower left x-value
	 * @param lly
	 *            the lower left y-value
	 * @param urx
	 *            the upper right x-value
	 * @param ury
	 *            the upper right y-value
	 * @param url
	 *            the external reference
	 */

	public Annotation(float llx, float lly, float urx, float ury, URL url) {
		this(llx, lly, urx, ury);
		annotationtype = URL_NET;
		annotationAttributes.put(URL, url);
	}

	/**
	 * Constructs an <CODE>Annotation</CODE>.
	 * 
	 * @param llx
	 *            the lower left x-value
	 * @param lly
	 *            the lower left y-value
	 * @param urx
	 *            the upper right x-value
	 * @param ury
	 *            the upper right y-value
	 * @param url
	 *            the external reference
	 */

	public Annotation(float llx, float lly, float urx, float ury, String url) {
		this(llx, lly, urx, ury);
		annotationtype = URL_AS_STRING;
		annotationAttributes.put(FILE, url);
	}

	/**
	 * Constructs an <CODE>Annotation</CODE>.
	 * 
	 * @param llx
	 *            the lower left x-value
	 * @param lly
	 *            the lower left y-value
	 * @param urx
	 *            the upper right x-value
	 * @param ury
	 *            the upper right y-value
	 * @param file
	 *            an external PDF file
	 * @param dest
	 *            the destination in this file
	 */

	public Annotation(float llx, float lly, float urx, float ury, String file,
			String dest) {
		this(llx, lly, urx, ury);
		annotationtype = FILE_DEST;
		annotationAttributes.put(FILE, file);
		annotationAttributes.put(DESTINATION, dest);
	}

	/**
	 * Creates a Screen anotation to embed media clips
	 * 
	 * @param llx
	 * @param lly
	 * @param urx
	 * @param ury
	 * @param moviePath
	 *            path to the media clip file
	 * @param mimeType
	 *            mime type of the media
	 * @param showOnDisplay
	 *            if true play on display of the page
	 */
	public Annotation(float llx, float lly, float urx, float ury,
			String moviePath, String mimeType, boolean showOnDisplay) {
		this(llx, lly, urx, ury);
		annotationtype = SCREEN;
		annotationAttributes.put(FILE, moviePath);
		annotationAttributes.put(MIMETYPE, mimeType);
		annotationAttributes.put(PARAMETERS, new boolean[] {
				false /* embedded */, showOnDisplay });
	}

	/**
	 * Constructs an <CODE>Annotation</CODE>.
	 * 
	 * @param llx
	 *            the lower left x-value
	 * @param lly
	 *            the lower left y-value
	 * @param urx
	 *            the upper right x-value
	 * @param ury
	 *            the upper right y-value
	 * @param file
	 *            an external PDF file
	 * @param page
	 *            a page number in this file
	 */

	public Annotation(float llx, float lly, float urx, float ury, String file,
			int page) {
		this(llx, lly, urx, ury);
		annotationtype = FILE_PAGE;
		annotationAttributes.put(FILE, file);
		annotationAttributes.put(PAGE, new Integer(page));
	}

	/**
	 * Constructs an <CODE>Annotation</CODE>.
	 * 
	 * @param llx
	 *            the lower left x-value
	 * @param lly
	 *            the lower left y-value
	 * @param urx
	 *            the upper right x-value
	 * @param ury
	 *            the upper right y-value
	 * @param named
	 *            a named destination in this file
	 */

	public Annotation(float llx, float lly, float urx, float ury, int named) {
		this(llx, lly, urx, ury);
		annotationtype = NAMED_DEST;
		annotationAttributes.put(NAMED, new Integer(named));
	}

	/**
	 * Constructs an <CODE>Annotation</CODE>.
	 * 
	 * @param llx
	 *            the lower left x-value
	 * @param lly
	 *            the lower left y-value
	 * @param urx
	 *            the upper right x-value
	 * @param ury
	 *            the upper right y-value
	 * @param application
	 *            an external application
	 * @param parameters
	 *            parameters to pass to this application
	 * @param operation
	 *            the operation to pass to this application
	 * @param defaultdir
	 *            the default directory to run this application in
	 */

	public Annotation(float llx, float lly, float urx, float ury,
			String application, String parameters, String operation,
			String defaultdir) {
		this(llx, lly, urx, ury);
		annotationtype = LAUNCH;
		annotationAttributes.put(APPLICATION, application);
		annotationAttributes.put(PARAMETERS, parameters);
		annotationAttributes.put(OPERATION, operation);
		annotationAttributes.put(DEFAULTDIR, defaultdir);
	}

	/**
	 * Returns an <CODE>Annotation</CODE> that has been constructed taking in
	 * account the value of some <VAR>attributes </VAR>.
	 * 
	 * @param attributes
	 *            Some attributes
	 */

	public Annotation(Properties attributes) {
		String value = (String) attributes.remove(ElementTags.LLX);
		if (value != null) {
			llx = Float.valueOf(value + "f").floatValue();
		}
		value = (String) attributes.remove(ElementTags.LLY);
		if (value != null) {
			lly = Float.valueOf(value + "f").floatValue();
		}
		value = (String) attributes.remove(ElementTags.URX);
		if (value != null) {
			urx = Float.valueOf(value + "f").floatValue();
		}
		value = (String) attributes.remove(ElementTags.URY);
		if (value != null) {
			ury = Float.valueOf(value + "f").floatValue();
		}
		String title = (String) attributes.remove(ElementTags.TITLE);
		String text = (String) attributes.remove(ElementTags.CONTENT);
		if (title != null || text != null) {
			annotationtype = TEXT;
		} else if ((value = (String) attributes.remove(ElementTags.URL)) != null) {
			annotationtype = URL_AS_STRING;
			annotationAttributes.put(FILE, value);
		} else if ((value = (String) attributes.remove(ElementTags.NAMED)) != null) {
			annotationtype = NAMED_DEST;
			annotationAttributes.put(NAMED, Integer.valueOf(value));
		} else {
			String file = (String) attributes.remove(ElementTags.FILE);
			String destination = (String) attributes
					.remove(ElementTags.DESTINATION);
			String page = (String) attributes.remove(ElementTags.PAGE);
			if (file != null) {
				annotationAttributes.put(FILE, file);
				if (destination != null) {
					annotationtype = FILE_DEST;
					annotationAttributes.put(DESTINATION, destination);
				} else if (page != null) {
					annotationtype = FILE_PAGE;
					annotationAttributes.put(FILE, file);
					annotationAttributes.put(PAGE, Integer.valueOf(page));
				}
			} else if ((value = (String) attributes.remove(ElementTags.NAMED)) != null) {
				annotationtype = LAUNCH;
				annotationAttributes.put(APPLICATION, value);
				annotationAttributes.put(PARAMETERS, attributes
						.remove(ElementTags.PARAMETERS));
				annotationAttributes.put(OPERATION, attributes
						.remove(ElementTags.OPERATION));
				annotationAttributes.put(DEFAULTDIR, attributes
						.remove(ElementTags.DEFAULTDIR));
			}
		}
		if (annotationtype == TEXT) {
			if (title == null)
				title = "";
			if (text == null)
				text = "";
			annotationAttributes.put(TITLE, title);
			annotationAttributes.put(CONTENT, text);
		}
		if (attributes.size() > 0)
			setMarkupAttributes(attributes);
	}

	// implementation of the Element-methods

	/**
	 * Gets the type of the text element.
	 * 
	 * @return a type
	 */

	public int type() {
		return Element.ANNOTATION;
	}

	// methods

	/**
	 * Processes the element by adding it (or the different parts) to an <CODE>
	 * ElementListener</CODE>.
	 * 
	 * @param listener
	 *            an <CODE>ElementListener</CODE>
	 * @return <CODE>true</CODE> if the element was processed successfully
	 */

	public boolean process(ElementListener listener) {
		try {
			return listener.add(this);
		} catch (DocumentException de) {
			return false;
		}
	}

	/**
	 * Gets all the chunks in this element.
	 * 
	 * @return an <CODE>ArrayList</CODE>
	 */

	public ArrayList getChunks() {
		return new ArrayList();
	}

	// methods

	/**
	 * Sets the dimensions of this annotation.
	 * 
	 * @param llx
	 *            the lower left x-value
	 * @param lly
	 *            the lower left y-value
	 * @param urx
	 *            the upper right x-value
	 * @param ury
	 *            the upper right y-value
	 */

	public void setDimensions(float llx, float lly, float urx, float ury) {
		this.llx = llx;
		this.lly = lly;
		this.urx = urx;
		this.ury = ury;
	}

	// methods to retrieve information

	/**
	 * Returns the lower left x-value.
	 * 
	 * @return a value
	 */

	public float llx() {
		return llx;
	}

	/**
	 * Returns the lower left y-value.
	 * 
	 * @return a value
	 */

	public float lly() {
		return lly;
	}

	/**
	 * Returns the uppper right x-value.
	 * 
	 * @return a value
	 */

	public float urx() {
		return urx;
	}

	/**
	 * Returns the uppper right y-value.
	 * 
	 * @return a value
	 */

	public float ury() {
		return ury;
	}

	/**
	 * Returns the lower left x-value.
	 * 
	 * @param def
	 *            the default value
	 * @return a value
	 */

	public float llx(float def) {
		if (Float.isNaN(llx))
			return def;
		return llx;
	}

	/**
	 * Returns the lower left y-value.
	 * 
	 * @param def
	 *            the default value
	 * @return a value
	 */

	public float lly(float def) {
		if (Float.isNaN(lly))
			return def;
		return lly;
	}

	/**
	 * Returns the upper right x-value.
	 * 
	 * @param def
	 *            the default value
	 * @return a value
	 */

	public float urx(float def) {
		if (Float.isNaN(urx))
			return def;
		return urx;
	}

	/**
	 * Returns the upper right y-value.
	 * 
	 * @param def
	 *            the default value
	 * @return a value
	 */

	public float ury(float def) {
		if (Float.isNaN(ury))
			return def;
		return ury;
	}

	/**
	 * Returns the type of this <CODE>Annotation</CODE>.
	 * 
	 * @return a type
	 */

	public int annotationType() {
		return annotationtype;
	}

	/**
	 * Returns the title of this <CODE>Annotation</CODE>.
	 * 
	 * @return a name
	 */

	public String title() {
		String s = (String) annotationAttributes.get(TITLE);
		if (s == null)
			s = "";
		return s;
	}

	/**
	 * Gets the content of this <CODE>Annotation</CODE>.
	 * 
	 * @return a reference
	 */

	public String content() {
		String s = (String) annotationAttributes.get(CONTENT);
		if (s == null)
			s = "";
		return s;
	}

	/**
	 * Gets the content of this <CODE>Annotation</CODE>.
	 * 
	 * @return a reference
	 */

	public HashMap attributes() {
		return annotationAttributes;
	}

	/**
	 * Checks if a given tag corresponds with this object.
	 * 
	 * @param tag
	 *            the given tag
	 * @return true if the tag corresponds
	 */

	public static boolean isTag(String tag) {
		return ElementTags.ANNOTATION.equals(tag);
	}

	/**
	 * @see pdftk.com.lowagie.text.MarkupAttributes#setMarkupAttribute(java.lang.String,
	 *      java.lang.String)
	 */
	public void setMarkupAttribute(String name, String value) {
		if (markupAttributes == null) markupAttributes = new Properties();
		markupAttributes.put(name, value);
	}

	/**
	 * @see pdftk.com.lowagie.text.MarkupAttributes#setMarkupAttributes(java.util.Properties)
	 */
	public void setMarkupAttributes(Properties markupAttributes) {
		this.markupAttributes = markupAttributes;
	}

	/**
	 * @see pdftk.com.lowagie.text.MarkupAttributes#getMarkupAttribute(java.lang.String)
	 */
	public String getMarkupAttribute(String name) {
		return (markupAttributes == null) ? null : String
				.valueOf(markupAttributes.get(name));
	}

	/**
	 * @see pdftk.com.lowagie.text.MarkupAttributes#getMarkupAttributeNames()
	 */
	public Set getMarkupAttributeNames() {
		return Chunk.getKeySet(markupAttributes);
	}

	/**
	 * @see pdftk.com.lowagie.text.MarkupAttributes#getMarkupAttributes()
	 */
	public Properties getMarkupAttributes() {
		return markupAttributes;
	}
}