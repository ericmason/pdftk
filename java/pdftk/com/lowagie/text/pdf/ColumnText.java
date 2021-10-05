/*
 * $Id: ColumnText.java,v 1.58 2005/01/11 14:14:58 blowagie Exp $
 * $Name:  $
 *
 * Copyright 2001, 2002 by Paulo Soares.
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

package pdftk.com.lowagie.text.pdf;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;
import java.util.Iterator;
import pdftk.com.lowagie.text.Phrase;
import pdftk.com.lowagie.text.Chunk;
import pdftk.com.lowagie.text.Paragraph;
// import pdftk.com.lowagie.text.Graphic; ssteward: dropped in 1.44
import pdftk.com.lowagie.text.ListItem;
import pdftk.com.lowagie.text.Element;
import pdftk.com.lowagie.text.DocumentException;
import pdftk.com.lowagie.text.ExceptionConverter;
// import pdftk.com.lowagie.text.Image; ssteward: dropped in 1.44

/**
 * Formats text in a columnwise form. The text is bound
 * on the left and on the right by a sequence of lines. This allows the column
 * to have any shape, not only rectangular.
 * <P>
 * Several parameters can be set like the first paragraph line indent and
 * extra space between paragraphs.
 * <P>
 * A call to the method <CODE>go</CODE> will return one of the following
 * situations: the column ended or the text ended.
 * <P>
 * I the column ended, a new column definition can be loaded with the method
 * <CODE>setColumns</CODE> and the method <CODE>go</CODE> can be called again.
 * <P>
 * If the text ended, more text can be loaded with <CODE>addText</CODE>
 * and the method <CODE>go</CODE> can be called again.<BR>
 * The only limitation is that one or more complete paragraphs must be loaded
 * each time.
 * <P>
 * Full bidirectional reordering is supported. If the run direction is
 * <CODE>PdfWriter.RUN_DIRECTION_RTL</CODE> the meaning of the horizontal
 * alignments and margins is mirrored.
 * @author Paulo Soares (psoares@consiste.pt)
 */

public class ColumnText {
    /** Eliminate the arabic vowels */    
    public static final int AR_NOVOWEL = ArabicLigaturizer.ar_novowel;
    /** Compose the tashkeel in the ligatures. */    
    public static final int AR_COMPOSEDTASHKEEL = ArabicLigaturizer.ar_composedtashkeel;
    /** Do some extra double ligatures. */    
    public static final int AR_LIG = ArabicLigaturizer.ar_lig;
    /**
     * Digit shaping option: Replace European digits (U+0030...U+0039) by Arabic-Indic digits.
     */
    public static final int DIGITS_EN2AN = ArabicLigaturizer.DIGITS_EN2AN;
    
    /**
     * Digit shaping option: Replace Arabic-Indic digits by European digits (U+0030...U+0039).
     */
    public static final int DIGITS_AN2EN = ArabicLigaturizer.DIGITS_AN2EN;
    
    /**
     * Digit shaping option:
     * Replace European digits (U+0030...U+0039) by Arabic-Indic digits
     * if the most recent strongly directional character
     * is an Arabic letter (its Bidi direction value is RIGHT_TO_LEFT_ARABIC).
     * The initial state at the start of the text is assumed to be not an Arabic,
     * letter, so European digits at the start of the text will not change.
     * Compare to DIGITS_ALEN2AN_INIT_AL.
     */
    public static final int DIGITS_EN2AN_INIT_LR = ArabicLigaturizer.DIGITS_EN2AN_INIT_LR;
    
    /**
     * Digit shaping option:
     * Replace European digits (U+0030...U+0039) by Arabic-Indic digits
     * if the most recent strongly directional character
     * is an Arabic letter (its Bidi direction value is RIGHT_TO_LEFT_ARABIC).
     * The initial state at the start of the text is assumed to be an Arabic,
     * letter, so European digits at the start of the text will change.
     * Compare to DIGITS_ALEN2AN_INT_LR.
     */
    public static final int DIGITS_EN2AN_INIT_AL = ArabicLigaturizer.DIGITS_EN2AN_INIT_AL;
    
    /**
     * Digit type option: Use Arabic-Indic digits (U+0660...U+0669).
     */
    public static final int DIGIT_TYPE_AN = ArabicLigaturizer.DIGIT_TYPE_AN;
    
    /**
     * Digit type option: Use Eastern (Extended) Arabic-Indic digits (U+06f0...U+06f9).
     */
    public static final int DIGIT_TYPE_AN_EXTENDED = ArabicLigaturizer.DIGIT_TYPE_AN_EXTENDED;
    
    protected int runDirection = PdfWriter.RUN_DIRECTION_DEFAULT;
    
    /** the space char ratio */
    public static final float GLOBAL_SPACE_CHAR_RATIO = 0;
    
    /** Signals that there is no more text available. */
    public static final int NO_MORE_TEXT = 1;
    
    /** Signals that there is no more column. */
    public static final int NO_MORE_COLUMN = 2;
    
    /** The column is valid. */
    protected static final int LINE_STATUS_OK = 0;
    
    /** The line is out the column limits. */
    protected static final int LINE_STATUS_OFFLIMITS = 1;
    
    /** The line cannot fit this column position. */
    protected static final int LINE_STATUS_NOLINE = 2;
    
    /** Upper bound of the column. */
    protected float maxY;
    
    /** Lower bound of the column. */
    protected float minY;
    
    protected float leftX;
    
    protected float rightX;
    
    /** The column alignment. Default is left alignment. */
    protected int alignment = Element.ALIGN_LEFT;
    
    /** The left column bound. */
    protected ArrayList leftWall;
    
    /** The right column bound. */
    protected ArrayList rightWall;
    
    /** The chunks that form the text. */
//    protected ArrayList chunks = new ArrayList();
    protected BidiLine bidiLine;
    
    /** The current y line location. Text will be written at this line minus the leading. */
    protected float yLine;
    
    /** The leading for the current line. */
    protected float currentLeading = 16;
    
    /** The fixed text leading. */
    protected float fixedLeading = 16;
    
    /** The text leading that is multiplied by the biggest font size in the line. */
    protected float multipliedLeading = 0;
    
    /** The <CODE>PdfContent</CODE> where the text will be written to. */
    protected PdfContentByte canvas;
    
    /** The line status when trying to fit a line to a column. */
    protected int lineStatus;
    
    /** The first paragraph line indent. */
    protected float indent = 0;
    
    /** The following paragraph lines indent. */
    protected float followingIndent = 0;
    
    /** The right paragraph lines indent. */
    protected float rightIndent = 0;
    
    /** The extra space between paragraphs. */
    protected float extraParagraphSpace = 0;
    
    /** The width of the line when the column is defined as a simple rectangle. */
    protected float rectangularWidth = -1;
    
    protected boolean rectangularMode = false;
    /** Holds value of property spaceCharRatio. */
    private float spaceCharRatio = GLOBAL_SPACE_CHAR_RATIO;

    private boolean lastWasNewline = true;
    
    /** Holds value of property linesWritten. */
    private int linesWritten;
    
    private float firstLineY;
    private boolean firstLineYDone = false;
    
    /** Holds value of property arabicOptions. */
    private int arabicOptions = 0;
    
    protected float descender;
    
    protected boolean composite = false;
    
    protected ColumnText compositeColumn;
    
    protected LinkedList compositeElements;
    
    protected int listIdx = 0;
    
    private boolean splittedRow;
    
    protected Phrase waitPhrase;
    
    /** if true, first line height is adjusted so that the max ascender touches the top */
    private boolean useAscender = false;

    /**
     * Creates a <CODE>ColumnText</CODE>.
     * @param canvas the place where the text will be written to. Can
     * be a template.
     */
    public ColumnText(PdfContentByte canvas) {
        this.canvas = canvas;
    }
    
    /** Creates an independent duplicated of the instance <CODE>org</CODE>.
     * @param org the original <CODE>ColumnText</CODE>
     * @return the duplicated
     */    
    public static ColumnText duplicate(ColumnText org) {
        ColumnText ct = new ColumnText(null);
        ct.setACopy(org);
        return ct;
    }
    
    /** Makes this instance an independent copy of <CODE>org</CODE>.
     * @param org the original <CODE>ColumnText</CODE>
     * @return itself
     */    
    public ColumnText setACopy(ColumnText org) {
        setSimpleVars(org);
        if (org.bidiLine != null)
            bidiLine = new BidiLine(org.bidiLine);
        return this;
    }
    
    protected void setSimpleVars(ColumnText org) {
        maxY = org.maxY;
        minY = org.minY;
        alignment = org.alignment;
        leftWall = null;
        if (org.leftWall != null)
            leftWall = new ArrayList(org.leftWall);
        rightWall = null;
        if (org.rightWall != null)
            rightWall = new ArrayList(org.rightWall);
        yLine = org.yLine;
        currentLeading = org.currentLeading;
        fixedLeading = org.fixedLeading;
        multipliedLeading = org.multipliedLeading;
        canvas = org.canvas;
        lineStatus = org.lineStatus;
        indent = org.indent;
        followingIndent = org.followingIndent;
        rightIndent = org.rightIndent;
        extraParagraphSpace = org.extraParagraphSpace;
        rectangularWidth = org.rectangularWidth;
        rectangularMode = org.rectangularMode;
        spaceCharRatio = org.spaceCharRatio;
        lastWasNewline = org.lastWasNewline;
        linesWritten = org.linesWritten;
        arabicOptions = org.arabicOptions;
        runDirection = org.runDirection;
        descender = org.descender;
        composite = org.composite;
        splittedRow = org.splittedRow;
        if (org.composite) {
            compositeElements = new LinkedList(org.compositeElements);
	    /* ssteward: dropped in 1.44 
            if (splittedRow) {
                PdfPTable table = (PdfPTable)compositeElements.getFirst();
                compositeElements.set(0, new PdfPTable(table));
            }
	    */
            if (org.compositeColumn != null)
                compositeColumn = duplicate(org.compositeColumn);
        }
        listIdx = org.listIdx;
        firstLineY = org.firstLineY;
        leftX = org.leftX;
        rightX = org.rightX;
        firstLineYDone = org.firstLineYDone;
        waitPhrase = org.waitPhrase;
        useAscender = org.useAscender;
    }
    
    private void addWaitingPhrase() {
        if (bidiLine == null && waitPhrase != null) {
            bidiLine = new BidiLine();
            for (Iterator j = waitPhrase.getChunks().iterator(); j.hasNext();) {
                bidiLine.addChunk(new PdfChunk((Chunk)j.next(), null));
            }
            waitPhrase = null;
        }
    }
    
    /**
     * Adds a <CODE>Phrase</CODE> to the current text array.
     * Will not have any effect if addElement() was called before.
     * @param phrase the text
     */
    public void addText(Phrase phrase) {
        if (phrase == null || composite)
            return;
        addWaitingPhrase();
        if (bidiLine == null) {
            waitPhrase = phrase;
            return;
        }
        for (Iterator j = phrase.getChunks().iterator(); j.hasNext();) {
            bidiLine.addChunk(new PdfChunk((Chunk)j.next(), null));
        }
    }
    
    /**
     * Replaces the current text array with this <CODE>Phrase</CODE>.
     * Anything added previously with addElement() is lost.
     * @param phrase the text
     */
    public void setText(Phrase phrase) {
        bidiLine = null;
        composite = false;
        compositeColumn = null;
        compositeElements = null;
        listIdx = 0;
        splittedRow = false;
        waitPhrase = phrase;
    }
    
    /**
     * Adds a <CODE>Chunk</CODE> to the current text array.
     * Will not have any effect if addElement() was called before.
     * @param chunk the text
     */
    public void addText(Chunk chunk) {
        if (chunk == null || composite)
            return;
        addText(new Phrase(chunk));
    }
    
    /**
     * Adds an element. Elements supported are <CODE>Paragraph</CODE>,
     * <CODE>List</CODE>, <CODE>PdfPTable</CODE>, <CODE>Image</CODE> and
     * <CODE>Graphic</CODE>.
     * <p>
     * It removes all the text placed with <CODE>addText()</CODE>.
     * @param element the <CODE>Element</CODE>
     */    
    public void addElement(Element element) {
        if (element == null)
            return;
	/* ssteward: dropped in 1.44
        if (element instanceof Image) {
            Image img = (Image)element;
            PdfPTable t = new PdfPTable(1);
            float w = img.getWidthPercentage();
            if (w == 0) {
                t.setTotalWidth(img.scaledWidth());
                t.setLockedWidth(true);
            }
            else
                t.setWidthPercentage(w);
            t.setSpacingAfter(img.spacingAfter());
            t.setSpacingBefore(img.spacingBefore());
            switch (img.alignment()) {
                case Image.LEFT:
                    t.setHorizontalAlignment(Element.ALIGN_LEFT);
                    break;
                case Image.RIGHT:
                    t.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    break;
                default:
                    t.setHorizontalAlignment(Element.ALIGN_CENTER);
                    break;
            }
            PdfPCell c = new PdfPCell(img, true);
            c.setPadding(0);
            c.setBorder(img.border());
            c.setBorderColor(img.borderColor());
            c.setBorderWidth(img.borderWidth());
            c.setBackgroundColor(img.backgroundColor());
            c.setGrayFill(img.grayFill());
            t.addCell(c);
            element = t;
        }
	*/
        if (element.type() == Element.CHUNK) {
        	element = new Paragraph((Chunk)element);
        }
        else if (element.type() == Element.PHRASE) {
        	element = new Paragraph((Phrase)element);
        }
        if (element.type() != Element.PARAGRAPH && element.type() != Element.LIST && element.type() != Element.PTABLE && element.type() != Element.GRAPHIC)
            throw new IllegalArgumentException("Element not allowed.");
        if (!composite) {
            composite = true;
            compositeElements = new LinkedList();
            bidiLine = null;
            waitPhrase = null;
        }
        compositeElements.add(element);
    }
    
    /**
     * Converts a sequence of lines representing one of the column bounds into
     * an internal format.
     * <p>
     * Each array element will contain a <CODE>float[4]</CODE> representing
     * the line x = ax + b.
     * @param cLine the column array
     * @return the converted array
     */
    protected ArrayList convertColumn(float cLine[]) {
        if (cLine.length < 4)
            throw new RuntimeException("No valid column line found.");
        ArrayList cc = new ArrayList();
        for (int k = 0; k < cLine.length - 2; k += 2) {
            float x1 = cLine[k];
            float y1 = cLine[k + 1];
            float x2 = cLine[k + 2];
            float y2 = cLine[k + 3];
            if (y1 == y2)
                continue;
            // x = ay + b
            float a = (x1 - x2) / (y1 - y2);
            float b = x1 - a * y1;
            float r[] = new float[4];
            r[0] = Math.min(y1, y2);
            r[1] = Math.max(y1, y2);
            r[2] = a;
            r[3] = b;
            cc.add(r);
            maxY = Math.max(maxY, r[1]);
            minY = Math.min(minY, r[0]);
        }
        if (cc.size() == 0)
            throw new RuntimeException("No valid column line found.");
        return cc;
    }
    
    /**
     * Finds the intersection between the <CODE>yLine</CODE> and the column. It will
     * set the <CODE>lineStatus</CODE> apropriatly.
     * @param wall the column to intersect
     * @return the x coordinate of the intersection
     */
    protected float findLimitsPoint(ArrayList wall) {
        lineStatus = LINE_STATUS_OK;
        if (yLine < minY || yLine > maxY) {
            lineStatus = LINE_STATUS_OFFLIMITS;
            return 0;
        }
        for (int k = 0; k < wall.size(); ++k) {
            float r[] = (float[])wall.get(k);
            if (yLine < r[0] || yLine > r[1])
                continue;
            return r[2] * yLine + r[3];
        }
        lineStatus = LINE_STATUS_NOLINE;
        return 0;
    }
    
    /**
     * Finds the intersection between the <CODE>yLine</CODE> and the two
     * column bounds. It will set the <CODE>lineStatus</CODE> apropriatly.
     * @return a <CODE>float[2]</CODE>with the x coordinates of the intersection
     */
    protected float[] findLimitsOneLine() {
        float x1 = findLimitsPoint(leftWall);
        if (lineStatus == LINE_STATUS_OFFLIMITS || lineStatus == LINE_STATUS_NOLINE)
            return null;
        float x2 = findLimitsPoint(rightWall);
        if (lineStatus == LINE_STATUS_NOLINE)
            return null;
        return new float[]{x1, x2};
    }
    
    /**
     * Finds the intersection between the <CODE>yLine</CODE>,
     * the <CODE>yLine-leading</CODE>and the two
     * column bounds. It will set the <CODE>lineStatus</CODE> apropriatly.
     * @return a <CODE>float[4]</CODE>with the x coordinates of the intersection
     */
    protected float[] findLimitsTwoLines() {
        boolean repeat = false;
        for (;;) {
            if (repeat && currentLeading == 0)
                return null;
            repeat = true;
            float x1[] = findLimitsOneLine();
            if (lineStatus == LINE_STATUS_OFFLIMITS)
                return null;
            yLine -= currentLeading;
            if (lineStatus == LINE_STATUS_NOLINE) {
                continue;
            }
            float x2[] = findLimitsOneLine();
            if (lineStatus == LINE_STATUS_OFFLIMITS)
                return null;
            if (lineStatus == LINE_STATUS_NOLINE) {
                yLine -= currentLeading;
                continue;
            }
            if (x1[0] >= x2[1] || x2[0] >= x1[1])
                continue;
            return new float[]{x1[0], x1[1], x2[0], x2[1]};
        }
    }
    
    /**
     * Sets the columns bounds. Each column bound is described by a
     * <CODE>float[]</CODE> with the line points [x1,y1,x2,y2,...].
     * The array must have at least 4 elements.
     * @param leftLine the left column bound
     * @param rightLine the right column bound
     */
    public void setColumns(float leftLine[], float rightLine[]) {
        maxY = -10e20f;
        minY = 10e20f;
        rightWall = convertColumn(rightLine);
        leftWall = convertColumn(leftLine);
        rectangularWidth = -1;
        rectangularMode = false;
    }
    
    /**
     * Simplified method for rectangular columns.
     * @param phrase a <CODE>Phrase</CODE>
     * @param llx the lower left x corner
     * @param lly the lower left y corner
     * @param urx the upper right x corner
     * @param ury the upper right y corner
     * @param leading the leading
     * @param alignment the column alignment
     */
    public void setSimpleColumn(Phrase phrase, float llx, float lly, float urx, float ury, float leading, int alignment) {
        addText(phrase);
        setSimpleColumn(llx, lly, urx, ury, leading, alignment);
    }
    
    /**
     * Simplified method for rectangular columns.
     * @param llx the lower left x corner
     * @param lly the lower left y corner
     * @param urx the upper right x corner
     * @param ury the upper right y corner
     * @param leading the leading
     * @param alignment the column alignment
     */
    public void setSimpleColumn(float llx, float lly, float urx, float ury, float leading, int alignment) {
        setLeading(leading);
        this.alignment = alignment;
        setSimpleColumn(llx, lly, urx, ury);
    }
    
    /**
     * Simplified method for rectangular columns.
     * @param llx
     * @param lly
     * @param urx
     * @param ury
     */
    public void setSimpleColumn(float llx, float lly, float urx, float ury) {
        leftX = Math.min(llx, urx);
        maxY = Math.max(lly, ury);
        minY = Math.min(lly, ury);
        rightX = Math.max(llx, urx);
        yLine = maxY;
        rectangularWidth = rightX - leftX;
        if (rectangularWidth < 0)
            rectangularWidth = 0;
        rectangularMode = true;
    }
    /**
     * Sets the leading to fixed
     * @param leading the leading
     */
    public void setLeading(float leading) {
        fixedLeading = leading;
        multipliedLeading = 0;
    }
    
    /**
     * Sets the leading fixed and variable. The resultant leading will be
     * fixedLeading+multipliedLeading*maxFontSize where maxFontSize is the
     * size of the bigest font in the line.
     * @param fixedLeading the fixed leading
     * @param multipliedLeading the variable leading
     */
    public void setLeading(float fixedLeading, float multipliedLeading) {
        this.fixedLeading = fixedLeading;
        this.multipliedLeading = multipliedLeading;
    }
    
    /**
     * Gets the fixed leading
     * @return the leading
     */
    public float getLeading() {
        return fixedLeading;
    }
    
    /**
     * Gets the variable leading
     * @return the leading
     */
    public float getMultipliedLeading() {
        return multipliedLeading;
    }
    
    /**
     * Sets the yLine. The line will be written to yLine-leading.
     * @param yLine the yLine
     */
    public void setYLine(float yLine) {
        this.yLine = yLine;
    }
    
    /**
     * Gets the yLine.
     * @return the yLine
     */
    public float getYLine() {
        return yLine;
    }
    
    /**
     * Sets the alignment.
     * @param alignment the alignment
     */
    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }
    
    /**
     * Gets the alignment.
     * @return the alignment
     */
    public int getAlignment() {
        return alignment;
    }
    
    /**
     * Sets the first paragraph line indent.
     * @param indent the indent
     */
    public void setIndent(float indent) {
        this.indent = indent;
        lastWasNewline = true;
    }
    
    /**
     * Gets the first paragraph line indent.
     * @return the indent
     */
    public float getIndent() {
        return indent;
    }
    
    /**
     * Sets the following paragraph lines indent.
     * @param indent the indent
     */
    public void setFollowingIndent(float indent) {
        this.followingIndent = indent;
        lastWasNewline = true;
    }
    
    /**
     * Gets the following paragraph lines indent.
     * @return the indent
     */
    public float getFollowingIndent() {
        return followingIndent;
    }
    
    /**
     * Sets the right paragraph lines indent.
     * @param indent the indent
     */
    public void setRightIndent(float indent) {
        this.rightIndent = indent;
        lastWasNewline = true;
    }
    
    /**
     * Gets the right paragraph lines indent.
     * @return the indent
     */
    public float getRightIndent() {
        return rightIndent;
    }
    
    /**
     * Outputs the lines to the document. It is equivalent to <CODE>go(false)</CODE>.
     * @return returns the result of the operation. It can be <CODE>NO_MORE_TEXT</CODE>
     * and/or <CODE>NO_MORE_COLUMN</CODE>
     * @throws DocumentException on error
     */
    public int go() throws DocumentException {
        return go(false);
    }
    
    /**
     * Outputs the lines to the document. The output can be simulated.
     * @param simulate <CODE>true</CODE> to simulate the writting to the document
     * @return returns the result of the operation. It can be <CODE>NO_MORE_TEXT</CODE>
     * and/or <CODE>NO_MORE_COLUMN</CODE>
     * @throws DocumentException on error
     */
    public int go(boolean simulate) throws DocumentException {
        if (composite)
            return goComposite(simulate);
        addWaitingPhrase();
        if (bidiLine == null)
            return NO_MORE_TEXT;
        descender = 0;
        linesWritten = 0;
        boolean dirty = false;
        float ratio = spaceCharRatio;
        Object currentValues[] = new Object[2];
        PdfFont currentFont = null;
        Float lastBaseFactor = new Float(0);
        currentValues[1] = lastBaseFactor;
        PdfDocument pdf = null;
        PdfContentByte graphics = null;
        PdfContentByte text = null;
        firstLineY = Float.NaN;
        int localRunDirection = PdfWriter.RUN_DIRECTION_NO_BIDI;
        if (runDirection != PdfWriter.RUN_DIRECTION_DEFAULT)
            localRunDirection = runDirection;
        if (canvas != null) {
            graphics = canvas;
            pdf = canvas.getPdfDocument();
            text = canvas.getDuplicate();
        }
        else if (!simulate)
            throw new NullPointerException("ColumnText.go with simulate==false and text==null.");
        if (!simulate) {
            if (ratio == GLOBAL_SPACE_CHAR_RATIO)
                ratio = text.getPdfWriter().getSpaceCharRatio();
            else if (ratio < 0.001f)
                ratio = 0.001f;
        }
        float firstIndent = 0;
        
        int status = 0;
        if (rectangularMode) {
            for (;;) {
                firstIndent = (lastWasNewline ? indent : followingIndent);
                if (rectangularWidth <= firstIndent + rightIndent) {
                    status = NO_MORE_COLUMN;
                    if (bidiLine.isEmpty())
                        status |= NO_MORE_TEXT;
                    break;
                }
                if (bidiLine.isEmpty()) {
                    status = NO_MORE_TEXT;
                    break;
                }
                PdfLine line = bidiLine.processLine(rectangularWidth - firstIndent - rightIndent, alignment, localRunDirection, arabicOptions);
                if (line == null) {
                    status = NO_MORE_TEXT;
                    break;
                }
                float maxSize = line.getMaxSizeSimple();
                if (isUseAscender() && Float.isNaN(firstLineY)) {
                    currentLeading = line.getAscender();
                }
                else {
                    currentLeading = fixedLeading + maxSize * multipliedLeading;
                }
                if (yLine > maxY || yLine - currentLeading < minY ) {
                    status = NO_MORE_COLUMN;
                    bidiLine.restore();
                    break;
                }
                yLine -= currentLeading;
                if (!simulate && !dirty) {
                    text.beginText();
                    dirty = true;
                }
                if (Float.isNaN(firstLineY)) {
                    firstLineY = yLine;
                }
                if (!simulate) {
                    currentValues[0] = currentFont;
                    text.setTextMatrix(leftX + (line.isRTL() ? rightIndent : firstIndent) + line.indentLeft(), yLine);
                    pdf.writeLineToContent(line, text, graphics, currentValues, ratio);
                    currentFont = (PdfFont)currentValues[0];
                }
                lastWasNewline = line.isNewlineSplit();
                yLine -= line.isNewlineSplit() ? extraParagraphSpace : 0;
                ++linesWritten;
                descender = line.getDescender();
            }
        }
        else {
            currentLeading = fixedLeading;
            for (;;) {
                firstIndent = (lastWasNewline ? indent : followingIndent);
                float yTemp = yLine;
                float xx[] = findLimitsTwoLines();
                if (xx == null) {
                    status = NO_MORE_COLUMN;
                    if (bidiLine.isEmpty())
                        status |= NO_MORE_TEXT;
                    yLine = yTemp;
                    break;
                }
                if (bidiLine.isEmpty()) {
                    status = NO_MORE_TEXT;
                    yLine = yTemp;
                    break;
                }
                float x1 = Math.max(xx[0], xx[2]);
                float x2 = Math.min(xx[1], xx[3]);
                if (x2 - x1 <= firstIndent + rightIndent)
                    continue;
                if (!simulate && !dirty) {
                    text.beginText();
                    dirty = true;
                }
                PdfLine line = bidiLine.processLine(x2 - x1 - firstIndent - rightIndent, alignment, localRunDirection, arabicOptions);
                if (line == null) {
                    status = NO_MORE_TEXT;
                    yLine = yTemp;
                    break;
                }
                if (!simulate) {
                    currentValues[0] = currentFont;
                    text.setTextMatrix(x1 + (line.isRTL() ? rightIndent : firstIndent) + line.indentLeft(), yLine);
                    pdf.writeLineToContent(line, text, graphics, currentValues, ratio);
                    currentFont = (PdfFont)currentValues[0];
                }
                lastWasNewline = line.isNewlineSplit();
                yLine -= line.isNewlineSplit() ? extraParagraphSpace : 0;
                ++linesWritten;
                descender = line.getDescender();
            }
        }
        if (dirty) {
            text.endText();
            canvas.add(text);
        }
        return status;
    }
    
    /**
     * Sets the extra space between paragraphs.
     * @return the extra space between paragraphs
     */
    public float getExtraParagraphSpace() {
        return extraParagraphSpace;
    }
    
    /**
     * Sets the extra space between paragraphs.
     * @param extraParagraphSpace the extra space between paragraphs
     */
    public void setExtraParagraphSpace(float extraParagraphSpace) {
        this.extraParagraphSpace = extraParagraphSpace;
    }
    
    /**
     * Clears the chunk array. A call to <CODE>go()</CODE> will always return
     * NO_MORE_TEXT.
     */
    public void clearChunks() {
        if (bidiLine != null)
            bidiLine.clearChunks();
    }
    
    /** Gets the space/character extra spacing ratio for
     * fully justified text.
     * @return the space/character extra spacing ratio
     */    
    public float getSpaceCharRatio() {
        return spaceCharRatio;
    }
    
    /** Sets the ratio between the extra word spacing and the extra character spacing
     * when the text is fully justified.
     * Extra word spacing will grow <CODE>spaceCharRatio</CODE> times more than extra character spacing.
     * If the ratio is <CODE>PdfWriter.NO_SPACE_CHAR_RATIO</CODE> then the extra character spacing
     * will be zero.
     * @param spaceCharRatio the ratio between the extra word spacing and the extra character spacing
     */
    public void setSpaceCharRatio(float spaceCharRatio) {
        this.spaceCharRatio = spaceCharRatio;
    }

    /** Sets the run direction. 
     * @param runDirection the run direction
     */    
    public void setRunDirection(int runDirection) {
        if (runDirection < PdfWriter.RUN_DIRECTION_DEFAULT || runDirection > PdfWriter.RUN_DIRECTION_RTL)
            throw new RuntimeException("Invalid run direction: " + runDirection);
        this.runDirection = runDirection;
    }
    
    /** Gets the run direction.
     * @return the run direction
     */    
    public int getRunDirection() {
        return runDirection;
    }
    
    /** Gets the number of lines written.
     * @return the number of lines written
     */
    public int getLinesWritten() {
        return this.linesWritten;
    }
    
    /** Gets the arabic shaping options.
     * @return the arabic shaping options
     */
    public int getArabicOptions() {
        return this.arabicOptions;
    }
    
    /** Sets the arabic shaping options. The option can be AR_NOVOWEL,
     * AR_COMPOSEDTASHKEEL and AR_LIG.
     * @param arabicOptions the arabic shaping options
     */
    public void setArabicOptions(int arabicOptions) {
        this.arabicOptions = arabicOptions;
    }
    
    /** Gets the biggest descender value of the last line written.
     * @return the biggest descender value of the last line written
     */    
    public float getDescender() {
        return descender;
    }
    
    /** Gets the width that the line will occupy after writing.
     * Only the width of the first line is returned.
     * @param phrase the <CODE>Phrase</CODE> containing the line
     * @param runDirection the run direction
     * @param arabicOptions the options for the arabic shaping
     * @return the width of the line
     */    
    public static float getWidth(Phrase phrase, int runDirection, int arabicOptions) {
        ColumnText ct = new ColumnText(null);
        ct.addText(phrase);
        ct.addWaitingPhrase();
        PdfLine line = ct.bidiLine.processLine(20000, Element.ALIGN_LEFT, runDirection, arabicOptions);
        if (line == null)
            return 0;
        else
            return 20000 - line.widthLeft();
    }
    
    /** Gets the width that the line will occupy after writing.
     * Only the width of the first line is returned.
     * @param phrase the <CODE>Phrase</CODE> containing the line
     * @return the width of the line
     */    
    public static float getWidth(Phrase phrase) {
        return getWidth(phrase, PdfWriter.RUN_DIRECTION_NO_BIDI, 0);
    }
    
    /** Shows a line of text. Only the first line is written.
     * @param canvas where the text is to be written to
     * @param alignment the alignment. It is not influenced by the run direction
     * @param phrase the <CODE>Phrase</CODE> with the text
     * @param x the x reference position
     * @param y the y reference position
     * @param rotation the rotation to be applied in degrees counterclockwise
     * @param runDirection the run direction
     * @param arabicOptions the options for the arabic shaping
     */    
    public static void showTextAligned(PdfContentByte canvas, int alignment, Phrase phrase, float x, float y, float rotation, int runDirection, int arabicOptions) {
        if (alignment != Element.ALIGN_LEFT && alignment != Element.ALIGN_CENTER
            && alignment != Element.ALIGN_RIGHT)
            alignment = Element.ALIGN_LEFT;
        canvas.saveState();
        ColumnText ct = new ColumnText(canvas);
        if (rotation == 0) {
            if (alignment == Element.ALIGN_LEFT)
                ct.setSimpleColumn(phrase, x, y - 1, 20000 + x, y + 2, 2, alignment);
            else if (alignment == Element.ALIGN_RIGHT)
                ct.setSimpleColumn(phrase, x-20000, y-1, x, y+2, 2, alignment);
            else
                ct.setSimpleColumn(phrase, x-20000, y-1, x+20000, y+2, 2, alignment);
        }
        else {
            double alpha = rotation * Math.PI / 180.0;
            float cos = (float)Math.cos(alpha);
            float sin = (float)Math.sin(alpha);
            canvas.concatCTM(cos, sin, -sin, cos, x, y);
            if (alignment == Element.ALIGN_LEFT)
                ct.setSimpleColumn(phrase, 0, -1, 20000, 2, 2, alignment);
            else if (alignment == Element.ALIGN_RIGHT)
                ct.setSimpleColumn(phrase, -20000, -1, 0, 2, 2, alignment);
            else
                ct.setSimpleColumn(phrase, -20000, -1, 20000, 2, 2, alignment);
        }
        if (runDirection == PdfWriter.RUN_DIRECTION_RTL) {
            if (alignment == Element.ALIGN_LEFT)
                alignment = Element.ALIGN_RIGHT;
            else if (alignment == Element.ALIGN_RIGHT)
                alignment = Element.ALIGN_LEFT;
        }
        ct.setAlignment(alignment);
        ct.setArabicOptions(arabicOptions);
        ct.setRunDirection(runDirection);
        try {
            ct.go();
        }
        catch (DocumentException e) {
            throw new ExceptionConverter(e);
        }
        canvas.restoreState();
    }

    /** Shows a line of text. Only the first line is written.
     * @param canvas where the text is to be written to
     * @param alignment the alignment
     * @param phrase the <CODE>Phrase</CODE> with the text
     * @param x the x reference position
     * @param y the y reference position
     * @param rotation the rotation to be applied in degrees counterclockwise
     */    
    public static void showTextAligned(PdfContentByte canvas, int alignment, Phrase phrase, float x, float y, float rotation) {
        showTextAligned(canvas, alignment, phrase, x, y, rotation, PdfWriter.RUN_DIRECTION_NO_BIDI, 0);
    }

    protected int goComposite(boolean simulate) throws DocumentException {
        if (!rectangularMode)
            throw new DocumentException("Irregular columns are not supported in composite mode.");
        linesWritten = 0;
        descender = 0;
        boolean firstPass = true;
        main_loop:
        while (true) {
            if (compositeElements.isEmpty())
                return NO_MORE_TEXT;
            Element element = (Element)compositeElements.getFirst();
            if (element.type() == Element.PARAGRAPH) {
                Paragraph para = (Paragraph)element;
                int status = 0;
                for (int keep = 0; keep < 2; ++keep) {
                    float lastY = yLine;
                    boolean createHere = false;
                    if (compositeColumn == null) {
                        compositeColumn = new ColumnText(canvas);
                        compositeColumn.setUseAscender(firstPass ? useAscender : false);
                        compositeColumn.setAlignment(para.alignment());
                        compositeColumn.setIndent(para.indentationLeft() + para.getFirstLineIndent());
                        compositeColumn.setExtraParagraphSpace(para.getExtraParagraphSpace());
                        compositeColumn.setFollowingIndent(para.indentationLeft());
                        compositeColumn.setRightIndent(para.indentationRight());
                        compositeColumn.setLeading(para.leading(), para.getMultipliedLeading());
                        compositeColumn.setRunDirection(runDirection);
                        compositeColumn.setArabicOptions(arabicOptions);
                        compositeColumn.setSpaceCharRatio(spaceCharRatio);
                        compositeColumn.addText(para);
                        if (!firstPass) {
                            yLine -= para.spacingBefore();
                        }
                        createHere = true;
                    }
                    compositeColumn.leftX = leftX;
                    compositeColumn.rightX = rightX;
                    compositeColumn.yLine = yLine;
                    compositeColumn.rectangularWidth = rectangularWidth;
                    compositeColumn.rectangularMode = rectangularMode;
                    compositeColumn.minY = minY;
                    compositeColumn.maxY = maxY;
                    boolean keepCandidate = (para.getKeepTogether() && createHere && !firstPass);
                    status = compositeColumn.go(simulate || (keepCandidate && keep == 0));
                    if ((status & NO_MORE_TEXT) == 0 && keepCandidate) {
                        compositeColumn = null;
                        yLine = lastY;
                        return NO_MORE_COLUMN;
                    }
                    if (simulate || !keepCandidate)
                        break;
                    if (keep == 0) {
                        compositeColumn = null;
                        yLine = lastY;
                    }
                }
                firstPass = false;
                yLine = compositeColumn.yLine;
                linesWritten += compositeColumn.linesWritten;
                descender = compositeColumn.descender;
                if ((status & NO_MORE_TEXT) != 0) {
                    compositeColumn = null;
                    compositeElements.removeFirst();
                    yLine -= para.spacingAfter();
                }
                if ((status & NO_MORE_COLUMN) != 0) {
                    return NO_MORE_COLUMN;
                }
            }
            else if (element.type() == Element.LIST) {
                pdftk.com.lowagie.text.List list = (pdftk.com.lowagie.text.List)element;
                ArrayList items = list.getItems();
                ListItem item = null;
                float listIndentation = list.indentationLeft();
                int count = 0;
                Stack stack = new Stack();
                for (int k = 0; k < items.size(); ++k) {
                    Object obj = items.get(k);
                    if (obj instanceof ListItem) {
                        if (count == listIdx) {
                            item = (ListItem)obj;
                            break;
                        }
                        else ++count;
                    }
                    else if (obj instanceof pdftk.com.lowagie.text.List) {
                        stack.push(new Object[]{list, new Integer(k), new Float(listIndentation)});
                        list = (pdftk.com.lowagie.text.List)obj;
                        items = list.getItems();
                        listIndentation += list.indentationLeft();
                        k = -1;
                        continue;
                    }
                    if (k == items.size() - 1) {
                        if (!stack.isEmpty()) {
                            Object objs[] = (Object[])stack.pop();
                            list = (pdftk.com.lowagie.text.List)objs[0];
                            items = list.getItems();
                            k = ((Integer)objs[1]).intValue();
                            listIndentation = ((Float)objs[2]).floatValue();
                        }
                    }
                }
                int status = 0;
                for (int keep = 0; keep < 2; ++keep) {
                    float lastY = yLine;
                    boolean createHere = false;
                    if (compositeColumn == null) {
                        if (item == null) {
                            listIdx = 0;
                            compositeElements.removeFirst();
                            continue main_loop;
                        }
                        compositeColumn = new ColumnText(canvas);
                        compositeColumn.setUseAscender(firstPass ? useAscender : false);
                        compositeColumn.setAlignment(item.alignment());
                        compositeColumn.setIndent(item.indentationLeft() + listIndentation + item.getFirstLineIndent());
                        compositeColumn.setExtraParagraphSpace(item.getExtraParagraphSpace());
                        compositeColumn.setFollowingIndent(compositeColumn.getIndent());
                        compositeColumn.setRightIndent(item.indentationRight() + list.indentationRight());
                        compositeColumn.setLeading(item.leading(), item.getMultipliedLeading());
                        compositeColumn.setRunDirection(runDirection);
                        compositeColumn.setArabicOptions(arabicOptions);
                        compositeColumn.setSpaceCharRatio(spaceCharRatio);
                        compositeColumn.addText(item);
                        if (!firstPass) {
                            yLine -= item.spacingBefore();
                        }
                        createHere = true;
                    }
                    compositeColumn.leftX = leftX;
                    compositeColumn.rightX = rightX;
                    compositeColumn.yLine = yLine;
                    compositeColumn.rectangularWidth = rectangularWidth;
                    compositeColumn.rectangularMode = rectangularMode;
                    compositeColumn.minY = minY;
                    compositeColumn.maxY = maxY;
                    boolean keepCandidate = (item.getKeepTogether() && createHere && !firstPass);
                    status = compositeColumn.go(simulate || (keepCandidate && keep == 0));
                    if ((status & NO_MORE_TEXT) == 0 && keepCandidate) {
                        compositeColumn = null;
                        yLine = lastY;
                        return NO_MORE_COLUMN;
                    }
                    if (simulate || !keepCandidate)
                        break;
                    if (keep == 0) {
                        compositeColumn = null;
                        yLine = lastY;
                    }
                }
                firstPass = false;
                yLine = compositeColumn.yLine;
                linesWritten += compositeColumn.linesWritten;
                descender = compositeColumn.descender;
                if (!Float.isNaN(compositeColumn.firstLineY) && !compositeColumn.firstLineYDone) {
                    if (!simulate)
                        showTextAligned(canvas, Element.ALIGN_LEFT, new Phrase(item.listSymbol()), compositeColumn.leftX + listIndentation, compositeColumn.firstLineY, 0);
                    compositeColumn.firstLineYDone = true;
                }
                if ((status & NO_MORE_TEXT) != 0) {
                    compositeColumn = null;
                    ++listIdx;
                    yLine -= item.spacingAfter();
                }
                if ((status & NO_MORE_COLUMN) != 0) {
                    return NO_MORE_COLUMN;
                }
            }
	    /* ssteward: dropped in 1.44 
            else if (element.type() == Element.PTABLE) {
                if (yLine < minY || yLine > maxY)
                    return NO_MORE_COLUMN;
                PdfPTable table = (PdfPTable)element;
                if (table.size() <= table.getHeaderRows()) {
                    compositeElements.removeFirst();
                    continue;
                }
                float yTemp = yLine;
                float yLineWrite = yLine;
                if (!firstPass && listIdx == 0) {
                    yTemp -= table.spacingBefore();
                    yLineWrite = yTemp;
                }
                currentLeading = 0;
                if (yTemp < minY || yTemp > maxY)
                    return NO_MORE_COLUMN;
                float x1 = leftX;
                float tableWidth;
                if (table.isLockedWidth())
                    tableWidth = table.getTotalWidth();
                else {
                    tableWidth = rectangularWidth * table.getWidthPercentage() / 100f;
                    table.setTotalWidth(tableWidth);
                }
                int k;
                boolean skipHeader = (!firstPass && table.isSkipFirstHeader() && listIdx <= table.getHeaderRows());
                if (!skipHeader) {
                    yTemp -= table.getHeaderHeight();
                    if (yTemp < minY || yTemp > maxY) {
                        if (firstPass) {
                            compositeElements.removeFirst();
                            continue;
                        }
                        return NO_MORE_COLUMN;
                    }
                }
                if (listIdx < table.getHeaderRows())
                    listIdx = table.getHeaderRows();
                for (k = listIdx; k < table.size(); ++k) {
                    float rowHeight = table.getRowHeight(k);
                    if (yTemp - rowHeight < minY)
                        break;
                    yTemp -= rowHeight;
                }
                if (k < table.size()) {
                    if (table.isSplitRows() && (!table.isSplitLate() || (k == listIdx && firstPass))) {
                        if (!splittedRow) {
                            splittedRow = true;
                            table = new PdfPTable(table);
                            compositeElements.set(0, table);
                            ArrayList rows = table.getRows();
                            for (int i = table.getHeaderRows(); i < listIdx; ++i)
                                rows.set(i, null);
                        }
                        float h = yTemp - minY;
                        PdfPRow newRow = table.getRow(k).splitRow(h);
                        if (newRow == null) {
                            if (k == listIdx)
                                return NO_MORE_COLUMN;
                        }
                        else {
                            yTemp = minY;
                            table.getRows().add(++k, newRow);
                        }
                    }
                    else if (!table.isSplitRows() && k == listIdx && firstPass) {
                        compositeElements.removeFirst();
                        splittedRow = false;
                        continue;
                    }
                    else if (k == listIdx && !firstPass && (!table.isSplitRows() || table.isSplitLate())) {
                            return NO_MORE_COLUMN;
                    }
                }
                firstPass = false;
                if (!simulate) {
                    switch (table.getHorizontalAlignment()) {
                        case Element.ALIGN_LEFT:
                            break;
                        case Element.ALIGN_RIGHT:
                            x1 += rectangularWidth - tableWidth;
                            break;
                        default:
                            x1 += (rectangularWidth - tableWidth) / 2f;
                    }
                    PdfPTable nt = PdfPTable.shallowCopy(table);
                    ArrayList rows = table.getRows();
                    ArrayList sub = nt.getRows();
                    if (!skipHeader) {
                        for (int j = 0; j < table.getHeaderRows(); ++j)
                            sub.add(rows.get(j));
                    }
                    else
                        nt.setHeaderRows(0);
                    for (int j = listIdx; j < k; ++j)
                        sub.add(rows.get(j));
                    float rowHeight = 0;
                    if (table.isExtendLastRow()) {
                        PdfPRow last = (PdfPRow)sub.get(sub.size() - 1);
                        rowHeight = last.getMaxHeights();
                        last.setMaxHeights(yTemp - minY + rowHeight);
                        yTemp = minY;
                    }
                    nt.writeSelectedRows(0, -1, x1, yLineWrite, canvas);
                    if (table.isExtendLastRow()) {
                        PdfPRow last = (PdfPRow)sub.get(sub.size() - 1);
                        last.setMaxHeights(rowHeight);
                    }
                }
                else if (table.isExtendLastRow() && minY > PdfPRow.BOTTOM_LIMIT)
                    yTemp = minY;
                yLine = yTemp;
                if (k >= table.size()) {
                    yLine -= table.spacingAfter();
                    compositeElements.removeFirst();
                    splittedRow = false;
                    listIdx = 0;
                }
                else {
                    if (splittedRow) {
                        ArrayList rows = table.getRows();
                        for (int i = listIdx; i < k; ++i)
                            rows.set(i, null);
                    }
                    listIdx = k;
                    return NO_MORE_COLUMN;
                }
            }
	    */
	    /* ssteward: dropped in 1.44
            else if (element.type() == Element.GRAPHIC) {
                if (!simulate) {
                    Graphic gr = (Graphic)element;
                    ByteBuffer bf = gr.getInternalBuffer();
                    ByteBuffer store = null;
                    if (bf.size() > 0) {
                        store = new ByteBuffer();
                        store.append(bf);
                        bf.reset();
                    }
                    gr.processAttributes(leftX, minY, rightX, maxY, yLine);
                    canvas.add(gr);
                    bf.reset();
                    if (store != null) {
                        bf.append(store);
                    }
                }
                compositeElements.removeFirst();
            }
	    */
            else
                compositeElements.removeFirst();
        }
    }
    
    /**
     * Gets the canvas.
     * @return a PdfContentByte.
     */
    public PdfContentByte getCanvas() {
        return canvas;
    }
    
    /**
     * Sets the canvas.
     * @param canvas
     */
    public void setCanvas(PdfContentByte canvas) {
        this.canvas = canvas;
        if (compositeColumn != null)
            compositeColumn.setCanvas(canvas);
    }
    
    /**
     * Checks if the element has a height of 0.
     * @return true or false
     */
    public boolean zeroHeightElement() {
        return composite == true && compositeElements.size() > 0 && ((Element)compositeElements.getFirst()).type() == Element.GRAPHIC;
    }

    /**
     * Checks if UseAscender is enabled/disabled.
     * @return true is the adjustment of the first line height is based on max ascender.
     */
    public boolean isUseAscender() {
        return useAscender;
    }

    /**
     * Enables/Disables adjustment of first line height based on max ascender.
     * @param use enable adjustment if true
     */
    public void setUseAscender(boolean use) {
        useAscender = use;
    }
}