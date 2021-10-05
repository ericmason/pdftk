/*
 * $Id: ListItem.java,v 1.78 2005/05/03 13:03:48 blowagie Exp $
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

import java.util.Properties;

import pdftk.com.lowagie.text.markup.MarkupTags;
import pdftk.com.lowagie.text.markup.MarkupParser;

/**
 * A <CODE>ListItem</CODE> is a <CODE>Paragraph</CODE>
 * that can be added to a <CODE>List</CODE>.
 * <P>
 * <B>Example 1:</B>
 * <BLOCKQUOTE><PRE>
 * List list = new List(true, 20);
 * list.add(<STRONG>new ListItem("First line")</STRONG>);
 * list.add(<STRONG>new ListItem("The second line is longer to see what happens once the end of the line is reached. Will it start on a new line?")</STRONG>);
 * list.add(<STRONG>new ListItem("Third line")</STRONG>);
 * </PRE></BLOCKQUOTE>
 *
 * The result of this code looks like this:
 *	<OL>
 *		<LI>
 *			First line
 *		</LI>
 *		<LI>
 *			The second line is longer to see what happens once the end of the line is reached. Will it start on a new line?
 *		</LI>
 *		<LI>
 *			Third line
 *		</LI>
 *	</OL>
 *
 * <B>Example 2:</B>
 * <BLOCKQUOTE><PRE>
 * List overview = new List(false, 10);
 * overview.add(<STRONG>new ListItem("This is an item")</STRONG>);
 * overview.add("This is another item");
 * </PRE></BLOCKQUOTE>
 *
 * The result of this code looks like this:
 *	<UL>
 *		<LI>
 *			This is an item
 *		</LI>
 *		<LI>
 *			This is another item
 *		</LI>
 *	</UL>
 *
 * @see	Element
 * @see List
 * @see	Paragraph
 */

public class ListItem extends Paragraph implements TextElementArray, MarkupAttributes {
    
    /** A serial version UID */
    private static final long serialVersionUID = 1970670787169329006L;

    // membervariables
    
/** this is the symbol that wil proceed the listitem. */
    private Chunk symbol;
    
    // constructors
    
/**
 * Constructs a <CODE>ListItem</CODE>.
 */
    
    public ListItem() {
        super();
    }
    
/**
 * Constructs a <CODE>ListItem</CODE> with a certain leading.
 *
 * @param	leading		the leading
 */
    
    public ListItem(float leading) {
        super(leading);
    }
    
/**
 * Constructs a <CODE>ListItem</CODE> with a certain <CODE>Chunk</CODE>.
 *
 * @param	chunk		a <CODE>Chunk</CODE>
 */
    
    public ListItem(Chunk chunk) {
        super(chunk);
    }
    
/**
 * Constructs a <CODE>ListItem</CODE> with a certain <CODE>String</CODE>.
 *
 * @param	string		a <CODE>String</CODE>
 */
    
    public ListItem(String string) {
        super(string);
    }
    
/**
 * Constructs a <CODE>ListItem</CODE> with a certain <CODE>String</CODE>
 * and a certain <CODE>Font</CODE>.
 *
 * @param	string		a <CODE>String</CODE>
 * @param	font		a <CODE>String</CODE>
 */
    
    public ListItem(String string, Font font) {
        super(string, font);
    }
    
/**
 * Constructs a <CODE>ListItem</CODE> with a certain <CODE>Chunk</CODE>
 * and a certain leading.
 *
 * @param	leading		the leading
 * @param	chunk		a <CODE>Chunk</CODE>
 */
    
    public ListItem(float leading, Chunk chunk) {
        super(leading, chunk);
    }
    
/**
 * Constructs a <CODE>ListItem</CODE> with a certain <CODE>String</CODE>
 * and a certain leading.
 *
 * @param	leading		the leading
 * @param	string		a <CODE>String</CODE>
 */
    
    public ListItem(float leading, String string) {
        super(leading, string);
    }
    
/**
 * Constructs a <CODE>ListItem</CODE> with a certain leading, <CODE>String</CODE>
 * and <CODE>Font</CODE>.
 *
 * @param	leading		the leading
 * @param	string		a <CODE>String</CODE>
 * @param	font		a <CODE>Font</CODE>
 */
    
    public ListItem(float leading, String string, Font font) {
        super(leading, string, font);
    }
    
/**
 * Constructs a <CODE>ListItem</CODE> with a certain <CODE>Phrase</CODE>.
 *
 * @param	phrase		a <CODE>Phrase</CODE>
 */
    
    public ListItem(Phrase phrase) {
        super(phrase);
    }
    
        /**
         * Returns a <CODE>ListItem</CODE> that has been constructed taking in account
         * the value of some <VAR>attributes</VAR>.
         *
         * @param	attributes		Some attributes
         */
    
    public ListItem(Properties attributes) {
        super("", FontFactory.getFont(attributes));
        String value;
        if ((value = (String)attributes.remove(ElementTags.ITEXT)) != null) {
            add(new Chunk(value));
        }
        if ((value = (String)attributes.remove(ElementTags.LEADING)) != null) {
            setLeading(Float.valueOf(value + "f").floatValue());
        }
        else if ((value = (String)attributes.remove(MarkupTags.CSS_KEY_LINEHEIGHT)) != null) {
            setLeading(MarkupParser.parseLength(value));
        }
        if ((value = (String)attributes.remove(ElementTags.INDENTATIONLEFT)) != null) {
            setIndentationLeft(Float.valueOf(value + "f").floatValue());
        }
        if ((value = (String)attributes.remove(ElementTags.INDENTATIONRIGHT)) != null) {
            setIndentationRight(Float.valueOf(value + "f").floatValue());
        }
        if ((value = (String)attributes.remove(ElementTags.ALIGN)) != null) {
            setAlignment(value);
        }
        if (attributes.size() > 0) setMarkupAttributes(attributes);
    }
    
    // implementation of the Element-methods
    
/**
 * Gets the type of the text element.
 *
 * @return	a type
 */
    
    public int type() {
        return Element.LISTITEM;
    }
    
    // methods
    
/**
 * Sets the listsymbol.
 *
 * @param	symbol	a <CODE>Chunk</CODE>
 */
    
    public void setListSymbol(Chunk symbol) {
        this.symbol = symbol;
        if (this.symbol.font().isStandardFont()) {
            this.symbol.setFont(font);
        }
    }
    
    // methods to retrieve information
    
/**
 * Returns the listsymbol.
 *
 * @return	a <CODE>Chunk</CODE>
 */
    
    public Chunk listSymbol() {
        return symbol;
    }
    
/**
 * Checks if a given tag corresponds with this object.
 *
 * @param   tag     the given tag
 * @return  true if the tag corresponds
 */
    
    public static boolean isTag(String tag) {
        return ElementTags.LISTITEM.equals(tag);
    }
}
