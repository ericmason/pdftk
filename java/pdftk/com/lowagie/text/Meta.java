/*
 * $Id: Meta.java,v 1.65 2005/04/13 09:17:11 blowagie Exp $
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

import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;

/**
 * This is an <CODE>Element</CODE> that contains
 * some meta information about the document.
 * <P>
 * An object of type <CODE>Meta</CODE> can not be constructed by the user.
 * Userdefined meta information should be placed in a <CODE>Header</CODE>-object.
 * <CODE>Meta</CODE> is reserved for: Subject, Keywords, Author, Title, Producer
 * and Creationdate information.
 *
 * @see		Element
 * @see		Header
 */

public class Meta implements Element, MarkupAttributes {
    
    // membervariables
    
/** This is the type of Meta-information this object contains. */
    private int type;
    
/** This is the content of the Meta-information. */
    private StringBuffer content;

/** Contains extra markupAttributes */
    protected Properties markupAttributes;
    
    // constructors
    
/**
 * Constructs a <CODE>Meta</CODE>.
 *
 * @param	type		the type of meta-information
 * @param	content		the content
 */
    
    Meta(int type, String content) {
        this.type = type;
        this.content = new StringBuffer(content);
    }
    
/**
 * Constructs a <CODE>Meta</CODE>.
 *
 * @param	tag		    the tagname of the meta-information
 * @param	content		the content
 */
    
    public Meta(String tag, String content) {
        this.type = Meta.getType(tag);
        this.content = new StringBuffer(content);
    }
    
    // implementation of the Element-methods
    
/**
 * Processes the element by adding it (or the different parts) to a
 * <CODE>ElementListener</CODE>.
 *
 * @param	listener		the <CODE>ElementListener</CODE>
 * @return	<CODE>true</CODE> if the element was processed successfully
 */
    
    public boolean process(ElementListener listener) {
        try {
            return listener.add(this);
        }
        catch(DocumentException de) {
            return false;
        }
    }
    
/**
 * Gets the type of the text element.
 *
 * @return	a type
 */
    
    public int type() {
        return type;
    }
    
/**
 * Gets all the chunks in this element.
 *
 * @return	an <CODE>ArrayList</CODE>
 */
    public ArrayList getChunks() {
        return new ArrayList();
    }
    
    // methods
    
/**
 * appends some text to this <CODE>Meta</CODE>.
 *
 * @param	string      a <CODE>String</CODE>
 * @return	a <CODE>StringBuffer</CODE>
 */
    
    public StringBuffer append(String string) {
        return content.append(string);
    }
    
    // methods to retrieve information
    
/**
 * Returns the content of the meta information.
 *
 * @return	a <CODE>String</CODE>
 */
    
    public String content() {
        return content.toString();
    }
    
/**
 * Returns the name of the meta information.
 *
 * @return	a <CODE>String</CODE>
 */
    
    public String name() {
        switch (type) {
            case Element.SUBJECT:
                return ElementTags.SUBJECT;
            case Element.KEYWORDS:
                return ElementTags.KEYWORDS;
            case Element.AUTHOR:
                return ElementTags.AUTHOR;
            case Element.TITLE:
                return ElementTags.TITLE;
            case Element.PRODUCER:
                return ElementTags.PRODUCER;
            case Element.CREATIONDATE:
                return ElementTags.CREATIONDATE;
                default:
                    return ElementTags.UNKNOWN;
        }
    }
    
/**
 * Returns the name of the meta information.
 * 
 * @param tag iText tag for meta information
 * @return	the Element value corresponding with the given tag
 */
    
    public static int getType(String tag) {
        if (ElementTags.SUBJECT.equals(tag)) {
            return Element.SUBJECT;
        }
        if (ElementTags.KEYWORDS.equals(tag)) {
            return Element.KEYWORDS;
        }
        if (ElementTags.AUTHOR.equals(tag)) {
            return Element.AUTHOR;
        }
        if (ElementTags.TITLE.equals(tag)) {
            return Element.TITLE;
        }
        if (ElementTags.PRODUCER.equals(tag)) {
            return Element.PRODUCER;
        }
        if (ElementTags.CREATIONDATE.equals(tag)) {
            return Element.CREATIONDATE;
        }
        return Element.HEADER;
    }
    
    
/**
 * @see pdftk.com.lowagie.text.MarkupAttributes#setMarkupAttribute(java.lang.String, java.lang.String)
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
        return (markupAttributes == null) ? null : String.valueOf(markupAttributes.get(name));
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