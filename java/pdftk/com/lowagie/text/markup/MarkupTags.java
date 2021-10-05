/*
 * $Id: MarkupTags.java,v 1.50 2005/05/03 14:44:38 blowagie Exp $
 * $Name:  $
 *
 * Copyright 2001, 2002 by Bruno Lowagie.
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

package pdftk.com.lowagie.text.markup;

/**
 * A class that contains all the possible tagnames and their attributes.
 */

public class MarkupTags {
	
	// iText specific
	
	/** the key for any tag */
	public static final String ITEXT_TAG = "tag";

	// HTML tags

	/** the markup for the body part of a file */
	public static final String HTML_TAG_BODY = "body";
	
	/** The DIV tag. */
	public static final String HTML_TAG_DIV = "div";

	/** This is a possible HTML-tag. */
	public static final String HTML_TAG_LINK = "link";

	/** The SPAN tag. */
	public static final String HTML_TAG_SPAN = "span";

	// HTML attributes

	/** the height attribute. */
	public static final String HTML_ATTR_HEIGHT = "height";

	/** the hyperlink reference attribute. */
	public static final String HTML_ATTR_HREF = "href";

	/** This is a possible HTML attribute for the LINK tag. */
	public static final String HTML_ATTR_REL = "rel";

	/** This is used for inline css style information */
	public static final String HTML_ATTR_STYLE = "style";

	/** This is a possible HTML attribute for the LINK tag. */
	public static final String HTML_ATTR_TYPE = "type";

	/** This is a possible HTML attribute. */
	public static final String HTML_ATTR_STYLESHEET = "stylesheet";

	/** the width attribute. */
	public static final String HTML_ATTR_WIDTH = "width";

	/** attribute for specifying externally defined CSS class */
	public static final String HTML_ATTR_CSS_CLASS = "class";

	/** The ID attribute. */
	public static final String HTML_ATTR_CSS_ID = "id";

	// HTML values
	
	/** This is a possible value for the language attribute (SCRIPT tag). */
	public static final String HTML_VALUE_JAVASCRIPT = "text/javascript";
	
	/** This is a possible HTML attribute for the LINK tag. */
	public static final String HTML_VALUE_CSS = "text/css";

	// CSS keys

	/** the CSS tag for background color */
	public static final String CSS_KEY_BGCOLOR = "background-color";

	/** the CSS tag for text color */
	public static final String CSS_KEY_COLOR = "color";

	/** CSS key that indicate the way something has to be displayed */
	public static final String CSS_KEY_DISPLAY = "display";

	/** the CSS tag for the font family */
	public static final String CSS_KEY_FONTFAMILY = "font-family";

	/** the CSS tag for the font size */
	public static final String CSS_KEY_FONTSIZE = "font-size";

	/** the CSS tag for the font style */
	public static final String CSS_KEY_FONTSTYLE = "font-style";

	/** the CSS tag for the font weight */
	public static final String CSS_KEY_FONTWEIGHT = "font-weight";

	/** the CSS tag for text decorations */
	public static final String CSS_KEY_LINEHEIGHT = "line-height";

	/** the CSS tag for the margin of an object */
	public static final String CSS_KEY_MARGIN = "margin";

	/** the CSS tag for the margin of an object */
	public static final String CSS_KEY_MARGINLEFT = "margin-left";

	/** the CSS tag for the margin of an object */
	public static final String CSS_KEY_MARGINRIGHT = "margin-right";

	/** the CSS tag for the margin of an object */
	public static final String CSS_KEY_MARGINTOP = "margin-top";

	/** the CSS tag for the margin of an object */
	public static final String CSS_KEY_MARGINBOTTOM = "margin-bottom";

	/** the CSS tag for the margin of an object */
	public static final String CSS_KEY_PADDING = "padding";

	/** the CSS tag for the margin of an object */
	public static final String CSS_KEY_PADDINGLEFT = "padding-left";

	/** the CSS tag for the margin of an object */
	public static final String CSS_KEY_PADDINGRIGHT = "padding-right";

	/** the CSS tag for the margin of an object */
	public static final String CSS_KEY_PADDINGTOP = "padding-top";

	/** the CSS tag for the margin of an object */
	public static final String CSS_KEY_PADDINGBOTTOM = "padding-bottom";

	/** the CSS tag for the margin of an object */
	public static final String CSS_KEY_BORDERCOLOR = "border-color";

	/** the CSS tag for the margin of an object */
	public static final String CSS_KEY_BORDERWIDTH = "border-width";

	/** the CSS tag for the margin of an object */
	public static final String CSS_KEY_BORDERWIDTHLEFT = "border-left-width";

	/** the CSS tag for the margin of an object */
	public static final String CSS_KEY_BORDERWIDTHRIGHT = "border-right-width";

	/** the CSS tag for the margin of an object */
	public static final String CSS_KEY_BORDERWIDTHTOP = "border-top-width";

	/** the CSS tag for the margin of an object */
	public static final String CSS_KEY_BORDERWIDTHBOTTOM = "border-bottom-width";

	/** the CSS tag for adding a page break when the document is printed */
	public static final String CSS_KEY_PAGE_BREAK_AFTER = "page-break-after";

	/** the CSS tag for adding a page break when the document is printed */
	public static final String CSS_KEY_PAGE_BREAK_BEFORE = "page-break-before";

	/** the CSS tag for the horizontal alignment of an object */
	public static final String CSS_KEY_TEXTALIGN = "text-align";

	/** the CSS tag for text decorations */
	public static final String CSS_KEY_TEXTDECORATION = "text-decoration";

	/** the CSS tag for text decorations */
	public static final String CSS_KEY_VERTICALALIGN = "vertical-align";

	/** the CSS tag for the visibility of objects */
	public static final String CSS_KEY_VISIBILITY = "visibility";

	// CSS values

	/** value for the CSS tag for adding a page break when the document is printed */
	public static final String CSS_VALUE_ALWAYS = "always";

	/** A possible value for the DISPLAY key */
	public static final String CSS_VALUE_BLOCK = "block";

	/** a CSS value for text font weight */
	public static final String CSS_VALUE_BOLD = "bold";

	/** the value if you want to hide objects. */
	public static final String CSS_VALUE_HIDDEN = "hidden";

	/** A possible value for the DISPLAY key */
	public static final String CSS_VALUE_INLINE = "inline";
	
	/** a CSS value for text font style */
	public static final String CSS_VALUE_ITALIC = "italic";

	/** a CSS value for text decoration */
	public static final String CSS_VALUE_LINETHROUGH = "line-through";

	/** A possible value for the DISPLAY key */
	public static final String CSS_VALUE_LISTITEM = "list-item";
	
	/** a CSS value */
	public static final String CSS_VALUE_NONE = "none";

	/** a CSS value */
	public static final String CSS_VALUE_NORMAL = "normal";

	/** a CSS value for text font style */
	public static final String CSS_VALUE_OBLIQUE = "oblique";

	/** A possible value for the DISPLAY key */
	public static final String CSS_VALUE_TABLE = "table";

	/** A possible value for the DISPLAY key */
	public static final String CSS_VALUE_TABLEROW = "table-row";

	/** A possible value for the DISPLAY key */
	public static final String CSS_VALUE_TABLECELL = "table-cell";

	/** the CSS value for a horizontal alignment of an object */
	public static final String CSS_VALUE_TEXTALIGNLEFT = "left";

	/** the CSS value for a horizontal alignment of an object */
	public static final String CSS_VALUE_TEXTALIGNRIGHT = "right";

	/** the CSS value for a horizontal alignment of an object */
	public static final String CSS_VALUE_TEXTALIGNCENTER = "center";

	/** the CSS value for a horizontal alignment of an object */
	public static final String CSS_VALUE_TEXTALIGNJUSTIFY = "justify";

	/** a CSS value for text decoration */
	public static final String CSS_VALUE_UNDERLINE = "underline";

}