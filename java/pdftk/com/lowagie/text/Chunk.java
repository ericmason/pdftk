/*
 * $Id: Chunk.java,v 1.112 2005/10/05 07:23:47 blowagie Exp $
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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;
import java.net.URL;

import pdftk.com.lowagie.text.pdf.PdfAction;
import pdftk.com.lowagie.text.pdf.PdfAnnotation;
import pdftk.com.lowagie.text.pdf.HyphenationEvent;
import pdftk.com.lowagie.text.pdf.PdfContentByte;
import pdftk.com.lowagie.text.markup.MarkupTags;
import pdftk.com.lowagie.text.markup.MarkupParser;

/**
 * This is the smallest significant part of text that can be added to a
 * document.
 * <P>
 * Most elements can be divided in one or more <CODE>Chunk</CODE>s. A chunk
 * is a <CODE>String</CODE> with a certain <CODE>Font</CODE>. all other
 * layoutparameters should be defined in the object to which this chunk of text
 * is added.
 * <P>
 * Example: <BLOCKQUOTE>
 * 
 * <PRE>
 * 
 * <STRONG>Chunk chunk = new Chunk("Hello world",
 * FontFactory.getFont(FontFactory.COURIER, 20, Font.ITALIC, new Color(255, 0,
 * 0))); </STRONG> document.add(chunk);
 * 
 * </PRE>
 * 
 * </BLOCKQUOTE>
 */

public class Chunk implements Element, MarkupAttributes {

	// public static membervariables

	/**
	 * The character stand in for an image.
	 */
	public static final String OBJECT_REPLACEMENT_CHARACTER = "\ufffc";

	/** This is a Chunk containing a newline. */
	public static final Chunk NEWLINE = new Chunk("\n");

	/** This is a Chunk containing a newpage. */
	public static final Chunk NEXTPAGE = new Chunk("");
	static {
		NEXTPAGE.setNewPage();
	}

	/** Key for sub/superscript. */
	public static final String SUBSUPSCRIPT = "SUBSUPSCRIPT";

	/** Key for underline. */
	public static final String UNDERLINE = "UNDERLINE";

	/** Key for color. */
	public static final String COLOR = "COLOR";

	/** Key for encoding. */
	public static final String ENCODING = "ENCODING";

	/** Key for remote goto. */
	public static final String REMOTEGOTO = "REMOTEGOTO";

	/** Key for local goto. */
	public static final String LOCALGOTO = "LOCALGOTO";

	/** Key for local destination. */
	public static final String LOCALDESTINATION = "LOCALDESTINATION";

	/** Key for image. */
	public static final String IMAGE = "IMAGE";

	/** Key for generic tag. */
	public static final String GENERICTAG = "GENERICTAG";

	/** Key for newpage. */
	public static final String NEWPAGE = "NEWPAGE";

	/** Key for split character. */
	public static final String SPLITCHARACTER = "SPLITCHARACTER";

	/** Key for Action. */
	public static final String ACTION = "ACTION";

	/** Key for background. */
	public static final String BACKGROUND = "BACKGROUND";

	/** Key for annotation. */
	public static final String PDFANNOTATION = "PDFANNOTATION";

	/** Key for hyphenation. */
	public static final String HYPHENATION = "HYPHENATION";

	/** Key for text rendering mode. */
	public static final String TEXTRENDERMODE = "TEXTRENDERMODE";

	/** Key for text skewing. */
	public static final String SKEW = "SKEW";

	/** Key for text horizontal scaling. */
	public static final String HSCALE = "HSCALE";

	// member variables

	/** This is the content of this chunk of text. */
	protected StringBuffer content = null;

	/** This is the <CODE>Font</CODE> of this chunk of text. */
	protected Font font = null;

	/** Contains some of the attributes for this Chunk. */
	protected HashMap attributes = null;

	/** Contains extra markupAttributes */
	protected Properties markupAttributes = null;

	// constructors

	/**
	 * Empty constructor.
	 */
	protected Chunk() {
	}

	/**
	 * Constructs a chunk of text with a certain content and a certain <CODE>
	 * Font</CODE>.
	 * 
	 * @param content
	 *            the content
	 * @param font
	 *            the font
	 */

	public Chunk(String content, Font font) {
		this.content = new StringBuffer(content);
		this.font = font;
	}

	/**
	 * Constructs a chunk of text with a certain content, without specifying a
	 * <CODE>Font</CODE>.
	 * 
	 * @param content
	 *            the content
	 */
	public Chunk(String content) {
		this(content, new Font());
	}

	/**
	 * Constructs a chunk of text with a char and a certain <CODE>Font</CODE>.
	 * 
	 * @param c
	 *            the content
	 * @param font
	 *            the font
	 */
	public Chunk(char c, Font font) {
		this.content = new StringBuffer();
		this.content.append(c);
		this.font = font;
	}

	/**
	 * Constructs a chunk of text with a char, without specifying a <CODE>Font
	 * </CODE>.
	 * 
	 * @param c
	 *            the content
	 */
	public Chunk(char c) {
		this(c, new Font());
	}

	/**
	 * Constructs a chunk containing an <CODE>Image</CODE>.
	 * 
	 * @param image
	 *            the image
	 * @param offsetX
	 *            the image offset in the x direction
	 * @param offsetY
	 *            the image offset in the y direction
	 */
    /* ssteward: dropped in 1.44
	public Chunk(Image image, float offsetX, float offsetY) {
		this(OBJECT_REPLACEMENT_CHARACTER, new Font());
		Image copyImage = Image.getInstance(image);
		copyImage.setAbsolutePosition(Float.NaN, Float.NaN);
		setAttribute(IMAGE, new Object[] { copyImage, new Float(offsetX),
				new Float(offsetY), new Boolean(false) });
	}
    */

	/**
	 * Constructs a chunk containing an <CODE>Image</CODE>.
	 * 
	 * @param image
	 *            the image
	 * @param offsetX
	 *            the image offset in the x direction
	 * @param offsetY
	 *            the image offset in the y direction
	 * @param changeLeading
	 *            true if the leading has to be adapted to the image
	 */
    /* ssteward: dropped in 1.44
	public Chunk(Image image, float offsetX, float offsetY,
			boolean changeLeading) {
		this(OBJECT_REPLACEMENT_CHARACTER, new Font());
		setAttribute(IMAGE, new Object[] { image, new Float(offsetX),
				new Float(offsetY), new Boolean(changeLeading) });
	}
    */
	/**
	 * Returns a <CODE>Chunk</CODE> that has been constructed taking in
	 * account the value of some <VAR>attributes </VAR>.
	 * 
	 * @param attributes
	 *            Some attributes
	 */

	public Chunk(Properties attributes) {
		this("", FontFactory.getFont(attributes));
		String value;
		if ((value = (String) attributes.remove(ElementTags.ITEXT)) != null) {
			append(value);
		}
		if ((value = (String) attributes.remove(ElementTags.LOCALGOTO)) != null) {
			setLocalGoto(value);
		}
		if ((value = (String) attributes.remove(ElementTags.REMOTEGOTO)) != null) {
			String destination = (String) attributes
					.remove(ElementTags.DESTINATION);
			String page = (String) attributes.remove(ElementTags.PAGE);
			if (page != null) {
				setRemoteGoto(value, Integer.valueOf(page).intValue());
			} else if (destination != null) {
				setRemoteGoto(value, destination);
			}
		}
		if ((value = (String) attributes.remove(ElementTags.LOCALDESTINATION)) != null) {
			setLocalDestination(value);
		}
		if ((value = (String) attributes.remove(ElementTags.SUBSUPSCRIPT)) != null) {
			setTextRise(Float.valueOf(value + "f").floatValue());
		}
		if ((value = (String) attributes
				.remove(MarkupTags.CSS_KEY_VERTICALALIGN)) != null
				&& value.endsWith("%")) {
			float p = Float.valueOf(
					value.substring(0, value.length() - 1) + "f").floatValue() / 100f;
			setTextRise(p * font.size());
		}
		if ((value = (String) attributes.remove(ElementTags.GENERICTAG)) != null) {
			setGenericTag(value);
		}
		if ((value = (String) attributes.remove(ElementTags.BACKGROUNDCOLOR)) != null) {
			setBackground(MarkupParser.decodeColor(value));
		}
		if (attributes.size() > 0)
			setMarkupAttributes(attributes);
	}

	// implementation of the Element-methods

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
	 * Gets the type of the text element.
	 * 
	 * @return a type
	 */

	public int type() {
		return Element.CHUNK;
	}

	/**
	 * Gets all the chunks in this element.
	 * 
	 * @return an <CODE>ArrayList</CODE>
	 */

	public ArrayList getChunks() {
		ArrayList tmp = new ArrayList();
		tmp.add(this);
		return tmp;
	}

	// methods

	/**
	 * appends some text to this <CODE>Chunk</CODE>.
	 * 
	 * @param string
	 *            <CODE>String</CODE>
	 * @return a <CODE>StringBuffer</CODE>
	 */

	public StringBuffer append(String string) {
		return content.append(string);
	}

	// methods to retrieve information

	/**
	 * Gets the font of this <CODE>Chunk</CODE>.
	 * 
	 * @return a <CODE>Font</CODE>
	 */

	public Font font() {
		return font;
	}

	/**
	 * Sets the font of this <CODE>Chunk</CODE>.
	 * 
	 * @param font
	 *            a <CODE>Font</CODE>
	 */

	public void setFont(Font font) {
		this.font = font;
	}

	/**
	 * Returns the content of this <CODE>Chunk</CODE>.
	 * 
	 * @return a <CODE>String</CODE>
	 */

	public String content() {
		return content.toString();
	}

	/**
	 * Returns the content of this <CODE>Chunk</CODE>.
	 * 
	 * @return a <CODE>String</CODE>
	 */

	public String toString() {
		return content.toString();
	}

	/**
	 * Checks is this <CODE>Chunk</CODE> is empty.
	 * 
	 * @return <CODE>false</CODE> if the Chunk contains other characters than
	 *         space.
	 */

	public boolean isEmpty() {
		return (content.toString().trim().length() == 0)
				&& (content.toString().indexOf("\n") == -1)
				&& (attributes == null);
	}

	/**
	 * Gets the width of the Chunk in points.
	 * 
	 * @return a width in points
	 */
	public float getWidthPoint() {
	    /* ssteward: dropped in 1.44
		if (getImage() != null) {
			return getImage().scaledWidth();
		}
	    */
		return font.getCalculatedBaseFont(true).getWidthPoint(content(),
				font.getCalculatedSize())
				* getHorizontalScaling();
	}

	/**
	 * Sets the text displacement relative to the baseline. Positive values rise
	 * the text, negative values lower the text.
	 * <P>
	 * It can be used to implement sub/superscript.
	 * 
	 * @param rise
	 *            the displacement in points
	 * @return this <CODE>Chunk</CODE>
	 */

	public Chunk setTextRise(float rise) {
		return setAttribute(SUBSUPSCRIPT, new Float(rise));
	}

	/**
	 * Gets the text displacement relatiev to the baseline.
	 * 
	 * @return a displacement in points
	 */
	public float getTextRise() {
		if (attributes.containsKey(SUBSUPSCRIPT)) {
			Float f = (Float) attributes.get(SUBSUPSCRIPT);
			return f.floatValue();
		}
		return 0.0f;
	}

	/**
	 * Sets the text rendering mode. It can outline text, simulate bold and make
	 * text invisible.
	 * 
	 * @param mode
	 *            the text rendering mode. It can be <CODE>
	 *            PdfContentByte.TEXT_RENDER_MODE_FILL</CODE>,<CODE>
	 *            PdfContentByte.TEXT_RENDER_MODE_STROKE</CODE>,<CODE>
	 *            PdfContentByte.TEXT_RENDER_MODE_FILL_STROKE</CODE> and <CODE>
	 *            PdfContentByte.TEXT_RENDER_MODE_INVISIBLE</CODE>.
	 * @param strokeWidth
	 *            the stroke line width for the modes <CODE>
	 *            PdfContentByte.TEXT_RENDER_MODE_STROKE</CODE> and <CODE>
	 *            PdfContentByte.TEXT_RENDER_MODE_FILL_STROKE</CODE>.
	 * @param strokeColor
	 *            the stroke color or <CODE>null</CODE> to follow the text
	 *            color
	 * @return this <CODE>Chunk</CODE>
	 */
	public Chunk setTextRenderMode(int mode, float strokeWidth,
			Color strokeColor) {
		return setAttribute(TEXTRENDERMODE, new Object[] { new Integer(mode),
				new Float(strokeWidth), strokeColor });
	}

	/**
	 * Skews the text to simulate italic and other effects. Try <CODE>alpha=0
	 * </CODE> and <CODE>beta=12</CODE>.
	 * 
	 * @param alpha
	 *            the first angle in degrees
	 * @param beta
	 *            the second angle in degrees
	 * @return this <CODE>Chunk</CODE>
	 */
	public Chunk setSkew(float alpha, float beta) {
		alpha = (float) Math.tan(alpha * Math.PI / 180);
		beta = (float) Math.tan(beta * Math.PI / 180);
		return setAttribute(SKEW, new float[] { alpha, beta });
	}

	/**
	 * Sets the text horizontal scaling. A value of 1 is normal and a value of
	 * 0.5f shrinks the text to half it's width.
	 * 
	 * @param scale
	 *            the horizontal scaling factor
	 * @return this <CODE>Chunk</CODE>
	 */
	public Chunk setHorizontalScaling(float scale) {
		return setAttribute(HSCALE, new Float(scale));
	}

	/**
	 * Gets the horizontal scaling.
	 * 
	 * @return a percentage in float
	 */
	public float getHorizontalScaling() {
		if (attributes == null)
			return 1f;
		Float f = (Float) attributes.get(HSCALE);
		if (f == null)
			return 1f;
		return f.floatValue();
	}

	/**
	 * Sets an action for this <CODE>Chunk</CODE>.
	 * 
	 * @param action
	 *            the action
	 * @return this <CODE>Chunk</CODE>
	 */

	public Chunk setAction(PdfAction action) {
		return setAttribute(ACTION, action);
	}

	/**
	 * Sets an anchor for this <CODE>Chunk</CODE>.
	 * 
	 * @param url
	 *            the <CODE>URL</CODE> to link to
	 * @return this <CODE>Chunk</CODE>
	 */

	public Chunk setAnchor(URL url) {
		return setAttribute(ACTION, new PdfAction(url.toExternalForm()));
	}

	/**
	 * Sets an anchor for this <CODE>Chunk</CODE>.
	 * 
	 * @param url
	 *            the url to link to
	 * @return this <CODE>Chunk</CODE>
	 */

	public Chunk setAnchor(String url) {
		return setAttribute(ACTION, new PdfAction(url));
	}

	/**
	 * Sets a local goto for this <CODE>Chunk</CODE>.
	 * <P>
	 * There must be a local destination matching the name.
	 * 
	 * @param name
	 *            the name of the destination to go to
	 * @return this <CODE>Chunk</CODE>
	 */

	public Chunk setLocalGoto(String name) {
		return setAttribute(LOCALGOTO, name);
	}

	/**
	 * Sets the color of the background <CODE>Chunk</CODE>.
	 * 
	 * @param color
	 *            the color of the background
	 * @return this <CODE>Chunk</CODE>
	 */
	public Chunk setBackground(Color color) {
		return setBackground(color, 0, 0, 0, 0);
	}

	/**
	 * Sets the color and the size of the background <CODE>Chunk</CODE>.
	 * 
	 * @param color
	 *            the color of the background
	 * @param extraLeft
	 *            increase the size of the rectangle in the left
	 * @param extraBottom
	 *            increase the size of the rectangle in the bottom
	 * @param extraRight
	 *            increase the size of the rectangle in the right
	 * @param extraTop
	 *            increase the size of the rectangle in the top
	 * @return this <CODE>Chunk</CODE>
	 */
	public Chunk setBackground(Color color, float extraLeft, float extraBottom,
			float extraRight, float extraTop) {
		return setAttribute(BACKGROUND, new Object[] { color,
				new float[] { extraLeft, extraBottom, extraRight, extraTop } });
	}

	/**
	 * Sets an horizontal line that can be an underline or a strikethrough.
	 * Actually, the line can be anywhere vertically and has always the <CODE>
	 * Chunk</CODE> width. Multiple call to this method will produce multiple
	 * lines.
	 * 
	 * @param thickness
	 *            the absolute thickness of the line
	 * @param yPosition
	 *            the absolute y position relative to the baseline
	 * @return this <CODE>Chunk</CODE>
	 */
	public Chunk setUnderline(float thickness, float yPosition) {
		return setUnderline(null, thickness, 0f, yPosition, 0f,
				PdfContentByte.LINE_CAP_BUTT);
	}

	/**
	 * Sets an horizontal line that can be an underline or a strikethrough.
	 * Actually, the line can be anywhere vertically and has always the <CODE>
	 * Chunk</CODE> width. Multiple call to this method will produce multiple
	 * lines.
	 * 
	 * @param color
	 *            the color of the line or <CODE>null</CODE> to follow the
	 *            text color
	 * @param thickness
	 *            the absolute thickness of the line
	 * @param thicknessMul
	 *            the thickness multiplication factor with the font size
	 * @param yPosition
	 *            the absolute y position relative to the baseline
	 * @param yPositionMul
	 *            the position multiplication factor with the font size
	 * @param cap
	 *            the end line cap. Allowed values are
	 *            PdfContentByte.LINE_CAP_BUTT, PdfContentByte.LINE_CAP_ROUND
	 *            and PdfContentByte.LINE_CAP_PROJECTING_SQUARE
	 * @return this <CODE>Chunk</CODE>
	 */
	public Chunk setUnderline(Color color, float thickness, float thicknessMul,
			float yPosition, float yPositionMul, int cap) {
		if (attributes == null)
			attributes = new HashMap();
		Object obj[] = {
				color,
				new float[] { thickness, thicknessMul, yPosition, yPositionMul, cap } };
		Object unders[][] = addToArray((Object[][]) attributes.get(UNDERLINE),
				obj);
		return setAttribute(UNDERLINE, unders);
	}

	/**
	 * Utility method to extend an array.
	 * 
	 * @param original
	 *            the original array or <CODE>null</CODE>
	 * @param item
	 *            the item to be added to the array
	 * @return a new array with the item appended
	 */
	public static Object[][] addToArray(Object original[][], Object item[]) {
		if (original == null) {
			original = new Object[1][];
			original[0] = item;
			return original;
		} else {
			Object original2[][] = new Object[original.length + 1][];
			System.arraycopy(original, 0, original2, 0, original.length);
			original2[original.length] = item;
			return original2;
		}
	}

	/**
	 * Sets a generic annotation to this <CODE>Chunk</CODE>.
	 * 
	 * @param annotation
	 *            the annotation
	 * @return this <CODE>Chunk</CODE>
	 */
	public Chunk setAnnotation(PdfAnnotation annotation) {
		return setAttribute(PDFANNOTATION, annotation);
	}

	/**
	 * sets the hyphenation engine to this <CODE>Chunk</CODE>.
	 * 
	 * @param hyphenation
	 *            the hyphenation engine
	 * @return this <CODE>Chunk</CODE>
	 */
	public Chunk setHyphenation(HyphenationEvent hyphenation) {
		return setAttribute(HYPHENATION, hyphenation);
	}

	/**
	 * Sets a goto for a remote destination for this <CODE>Chunk</CODE>.
	 * 
	 * @param filename
	 *            the file name of the destination document
	 * @param name
	 *            the name of the destination to go to
	 * @return this <CODE>Chunk</CODE>
	 */

	public Chunk setRemoteGoto(String filename, String name) {
		return setAttribute(REMOTEGOTO, new Object[] { filename, name });
	}

	/**
	 * Sets a goto for a remote destination for this <CODE>Chunk</CODE>.
	 * 
	 * @param filename
	 *            the file name of the destination document
	 * @param page
	 *            the page of the destination to go to. First page is 1
	 * @return this <CODE>Chunk</CODE>
	 */

	public Chunk setRemoteGoto(String filename, int page) {
		return setAttribute(REMOTEGOTO, new Object[] { filename,
				new Integer(page) });
	}

	/**
	 * Sets a local destination for this <CODE>Chunk</CODE>.
	 * 
	 * @param name
	 *            the name for this destination
	 * @return this <CODE>Chunk</CODE>
	 */
	public Chunk setLocalDestination(String name) {
		return setAttribute(LOCALDESTINATION, name);
	}

	/**
	 * Sets the generic tag <CODE>Chunk</CODE>.
	 * <P>
	 * The text for this tag can be retrieved with <CODE>PdfPageEvent</CODE>.
	 * 
	 * @param text
	 *            the text for the tag
	 * @return this <CODE>Chunk</CODE>
	 */

	public Chunk setGenericTag(String text) {
		return setAttribute(GENERICTAG, text);
	}

	/**
	 * Sets the split characters.
	 * 
	 * @param splitCharacter
	 *            the <CODE>SplitCharacter</CODE> interface
	 * @return this <CODE>Chunk</CODE>
	 */
	public Chunk setSplitCharacter(SplitCharacter splitCharacter) {
		return setAttribute(SPLITCHARACTER, splitCharacter);
	}

	/**
	 * Sets a new page tag..
	 * 
	 * @return this <CODE>Chunk</CODE>
	 */

	public Chunk setNewPage() {
		return setAttribute(NEWPAGE, null);
	}

	/**
	 * Sets an arbitrary attribute.
	 * 
	 * @param name
	 *            the key for the attribute
	 * @param obj
	 *            the value of the attribute
	 * @return this <CODE>Chunk</CODE>
	 */

	private Chunk setAttribute(String name, Object obj) {
		if (attributes == null)
			attributes = new HashMap();
		attributes.put(name, obj);
		return this;
	}

	/**
	 * Gets the attributes for this <CODE>Chunk</CODE>.
	 * <P>
	 * It may be null.
	 * 
	 * @return the attributes for this <CODE>Chunk</CODE>
	 */

	public HashMap getAttributes() {
		return attributes;
	}

	/**
	 * Checks the attributes of this <CODE>Chunk</CODE>.
	 * 
	 * @return false if there aren't any.
	 */

	public boolean hasAttributes() {
		return attributes != null;
	}

	/**
	 * Returns the image.
	 * 
	 * @return the image
	 */
    /* ssteward: dropped in 1.44
	public Image getImage() {
		if (attributes == null)
			return null;
		Object obj[] = (Object[]) attributes.get(Chunk.IMAGE);
		if (obj == null)
			return null;
		else {
			return (Image) obj[0];
		}
	}
    */
	/**
	 * Checks if a given tag corresponds with this object.
	 * 
	 * @param tag
	 *            the given tag
	 * @return true if the tag corresponds
	 */

	public static boolean isTag(String tag) {
		return ElementTags.CHUNK.equals(tag);
	}

	/**
	 * @see pdftk.com.lowagie.text.MarkupAttributes#setMarkupAttribute(java.lang.String,
	 *      java.lang.String)
	 */
	public void setMarkupAttribute(String name, String value) {
		if (markupAttributes == null)
			markupAttributes = new Properties();
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
		return getKeySet(markupAttributes);
	}

	/**
	 * @see pdftk.com.lowagie.text.MarkupAttributes#getMarkupAttributes()
	 */
	public Properties getMarkupAttributes() {
		return markupAttributes;
	}

	/**
	 * Gets the keys of a Hashtable
	 * 
	 * @param table
	 *            a Hashtable
	 * @return the keyset of a Hashtable (or an empty set if table is null)
	 */
	public static Set getKeySet(Hashtable table) {
		return (table == null) ? Collections.EMPTY_SET : table.keySet();
	}
}