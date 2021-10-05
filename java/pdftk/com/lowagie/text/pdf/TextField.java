/*
 * Copyright 2003-2005 by Paulo Soares.
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

import java.awt.Color;
import pdftk.com.lowagie.text.Element;
import pdftk.com.lowagie.text.DocumentException;
import pdftk.com.lowagie.text.Rectangle;
import java.io.IOException;
import java.util.ArrayList;

/** Supports text, combo and list fields generating the correct appearances.
 * All the option in the Acrobat GUI are supported in an easy to use API.
 * @author Paulo Soares (psoares@consiste.pt)
 */
public class TextField extends BaseField {
    
    /** Holds value of property defaultText. */
    private String defaultText;
    
    /** Holds value of property choices. */
    private String[] choices;
    
    /** Holds value of property choiceExports. */
    private String[] choiceExports;
    
    /** Holds value of property choiceSelection. */
    private int choiceSelection;
    
    private int topFirst;
    
    private float extraMarginLeft;
    private float extraMarginTop;
    
    /** Creates a new <CODE>TextField</CODE>.
     * @param writer the document <CODE>PdfWriter</CODE>
     * @param box the field location and dimensions
     * @param fieldName the field name. If <CODE>null</CODE> only the widget keys
     * will be included in the field allowing it to be used as a kid field.
     */
    public TextField(PdfWriter writer, Rectangle box, String fieldName) {
        super(writer, box, fieldName);
    }
    
    /**
     * Gets the appearance for this TextField.
     * @return the appearance object for this TextField
     * @throws IOException
     * @throws DocumentException
     */
    public PdfAppearance getAppearance() throws IOException, DocumentException {
        PdfAppearance app = getBorderAppearance();
        app.beginVariableText();
        if (text == null || text.length() == 0) {
            app.endVariableText();
            return app;
        }
        BaseFont ufont = getRealFont();
        boolean borderExtra = borderStyle == PdfBorderDictionary.STYLE_BEVELED || borderStyle == PdfBorderDictionary.STYLE_INSET;
        float h = box.height() - borderWidth * 2;
        float bw2 = borderWidth;
        if (borderExtra) {
            h -= borderWidth * 2;
            bw2 *= 2;
        }
        h -= extraMarginTop;
        float offsetX = (borderExtra ? 2 * borderWidth : borderWidth);
        offsetX = Math.max(offsetX, 1);
        float offX = Math.min(bw2, offsetX);
        app.saveState();
        app.rectangle(offX, offX, box.width() - 2 * offX, box.height() - 2 * offX);
        app.clip();
        app.newPath();
        if (textColor == null)
            app.setGrayFill(0);
        else
            app.setColorFill(textColor);
        app.beginText();
        String ptext = text; //fixed by Kazuya Ujihara (ujihara.jp)
        if ((options & PASSWORD) != 0) { 
            char[] pchar = new char[text.length()];
            for (int i = 0; i < text.length(); i++)
                pchar[i] = '*';
            ptext = new String(pchar);
        }
        if ((options & MULTILINE) != 0) {
            float usize = fontSize;
            float width = box.width() - 3 * offsetX - extraMarginLeft;
            ArrayList breaks = getHardBreaks(ptext);
            ArrayList lines = breaks;
            float factor = ufont.getFontDescriptor(BaseFont.BBOXURY, 1) - ufont.getFontDescriptor(BaseFont.BBOXLLY, 1);
            if (usize == 0) {
                usize = h / breaks.size() / factor;
                if (usize > 4) {
                    if (usize > 12)
                        usize = 12;
                    float step = Math.max((usize - 4) / 10, 0.2f);
                    for (; usize > 4; usize -= step) {
                        lines = breakLines(breaks, ufont, usize, width);
                        if (lines.size() * usize * factor <= h)
                            break;
                    }
                }
                if (usize <= 4) {
                    usize = 4;
                    lines = breakLines(breaks, ufont, usize, width);
                }
            }
            else
                lines = breakLines(breaks, ufont, usize, width);
            app.setFontAndSize(ufont, usize);
            app.setLeading(usize * factor);
            float offsetY = offsetX + h - ufont.getFontDescriptor(BaseFont.BBOXURY, usize);
            String nt = (String)lines.get(0);
            if (alignment == Element.ALIGN_RIGHT) {
                float wd = ufont.getWidthPoint(nt, usize);
                app.moveText(extraMarginLeft + box.width() - 2 * offsetX - wd, offsetY);
            }
            else if (alignment == Element.ALIGN_CENTER) {
                nt = nt.trim();
                float wd = ufont.getWidthPoint(nt, usize);
                app.moveText(extraMarginLeft + box.width() / 2  - wd / 2, offsetY);
            }
            else
                app.moveText(extraMarginLeft + 2 * offsetX, offsetY);
            app.showText(nt);
            int maxline = (int)(h / usize / factor) + 1;
            maxline = Math.min(maxline, lines.size());
            for (int k = 1; k < maxline; ++k) {
                nt = (String)lines.get(k);
                if (alignment == Element.ALIGN_RIGHT) {
                    float wd = ufont.getWidthPoint(nt, usize);
                    app.moveText(extraMarginLeft + box.width() - 2 * offsetX - wd - app.getXTLM(), 0);
                }
                else if (alignment == Element.ALIGN_CENTER) {
                    nt = nt.trim();
                    float wd = ufont.getWidthPoint(nt, usize);
                    app.moveText(extraMarginLeft + box.width() / 2  - wd / 2 - app.getXTLM(), 0);
                }
                app.newlineShowText(nt);
            }
        }
        else {
            float usize = fontSize;
            if (usize == 0) {
                float maxCalculatedSize = h / (ufont.getFontDescriptor(BaseFont.BBOXURX, 1) - ufont.getFontDescriptor(BaseFont.BBOXLLY, 1));
                float wd = ufont.getWidthPoint(ptext, 1);
                if (wd == 0)
                    usize = maxCalculatedSize;
                else
                    usize = (box.width() - extraMarginLeft - 2 * offsetX) / wd;
                if (usize > maxCalculatedSize)
                    usize = maxCalculatedSize;
                if (usize < 4)
                    usize = 4;
            }
            app.setFontAndSize(ufont, usize);
            float offsetY = offX + ((box.height() - 2*offX) - ufont.getFontDescriptor(BaseFont.ASCENT, usize)) / 2;
            if (offsetY < offX)
                offsetY = offX;
            if (offsetY - offX < -ufont.getFontDescriptor(BaseFont.DESCENT, usize)) {
                float ny = -ufont.getFontDescriptor(BaseFont.DESCENT, usize) + offX;
                float dy = box.height() - offX - ufont.getFontDescriptor(BaseFont.ASCENT, usize);
                offsetY = Math.min(ny, Math.max(offsetY, dy));
            }
            if ((options & COMB) != 0 && maxCharacterLength > 0) {
                int textLen = Math.min(maxCharacterLength, ptext.length());
                int position = 0;
                if (alignment == Element.ALIGN_RIGHT) {
                    position = maxCharacterLength - textLen;
                }
                else if (alignment == Element.ALIGN_CENTER) {
                    position = (maxCharacterLength - textLen) / 2;
                }
                float step = (box.width() - extraMarginLeft) / maxCharacterLength;
                float start = step / 2 + position * step;
                for (int k = 0; k < textLen; ++k) {
                    String c = ptext.substring(k, k + 1);
                    float wd = ufont.getWidthPoint(c, usize);
                    app.setTextMatrix(extraMarginLeft + start - wd / 2, offsetY - extraMarginTop);
                    app.showText(c);
                    start += step;
                }
            }
            else {
                if (alignment == Element.ALIGN_RIGHT) {
                    float wd = ufont.getWidthPoint(ptext, usize);
                    app.moveText(extraMarginLeft + box.width() - 2 * offsetX - wd, offsetY - extraMarginTop);
                }
                else if (alignment == Element.ALIGN_CENTER) {
                    float wd = ufont.getWidthPoint(ptext, usize);
                    app.moveText(extraMarginLeft + box.width() / 2  - wd / 2, offsetY - extraMarginTop);
                }
                else
                    app.moveText(extraMarginLeft + 2 * offsetX, offsetY - extraMarginTop);
                app.showText(ptext);
            }
        }
        app.endText();
        app.restoreState();
        app.endVariableText();
        return app;
    }

    PdfAppearance getListAppearance() throws IOException, DocumentException {
        PdfAppearance app = getBorderAppearance();
        app.beginVariableText();
        if (choices == null || choices.length == 0) {
            app.endVariableText();
            return app;
        }
        int topChoice = choiceSelection;
        if (topChoice >= choices.length) {
            topChoice = choices.length - 1;
        }
        if (topChoice < 0)
            topChoice = 0;
        BaseFont ufont = getRealFont();
        float usize = fontSize;
        if (usize == 0)
            usize = 12;
        boolean borderExtra = borderStyle == PdfBorderDictionary.STYLE_BEVELED || borderStyle == PdfBorderDictionary.STYLE_INSET;
        float h = box.height() - borderWidth * 2;
        if (borderExtra)
            h -= borderWidth * 2;
        float offsetX = (borderExtra ? 2 * borderWidth : borderWidth);
        float leading = ufont.getFontDescriptor(BaseFont.BBOXURY, usize) - ufont.getFontDescriptor(BaseFont.BBOXLLY, usize);
        int maxFit = (int)(h / leading) + 1;
        int first = 0;
        int last = 0;
        last = topChoice + maxFit / 2 + 1;
        first = last - maxFit;
        if (first < 0) {
            last += first;
            first = 0;
        }
//        first = topChoice;
        last = first + maxFit;
        if (last > choices.length)
            last = choices.length;
        topFirst = first;
        app.saveState();
        app.rectangle(offsetX, offsetX, box.width() - 2 * offsetX, box.height() - 2 * offsetX);
        app.clip();
        app.newPath();
        Color mColor;
        if (textColor == null)
            mColor = new GrayColor(0);
        else
            mColor = textColor;
        app.setColorFill(new Color(10, 36, 106));
        app.rectangle(offsetX, offsetX + h - (topChoice - first + 1) * leading, box.width() - 2 * offsetX, leading);
        app.fill();
        app.beginText();
        app.setFontAndSize(ufont, usize);
        app.setLeading(leading);
        app.moveText(offsetX * 2, offsetX + h - ufont.getFontDescriptor(BaseFont.BBOXURY, usize) + leading);
        app.setColorFill(mColor);
        for (int idx = first; idx < last; ++idx) {
            if (idx == topChoice) {
                app.setGrayFill(1);
                app.newlineShowText(choices[idx]);
                app.setColorFill(mColor);
            }
            else
                app.newlineShowText(choices[idx]);
        }
        app.endText();
        app.restoreState();
        app.endVariableText();
        return app;
    }

    /** Gets a new text field.
     * @throws IOException on error
     * @throws DocumentException on error
     * @return a new text field
     */    
    public PdfFormField getTextField() throws IOException, DocumentException {
        if (maxCharacterLength <= 0)
            options &= ~COMB;
        if ((options & COMB) != 0)
            options &= ~MULTILINE;
        PdfFormField field = PdfFormField.createTextField(writer, false, false, maxCharacterLength);
        field.setWidget(box, PdfAnnotation.HIGHLIGHT_INVERT);
        switch (alignment) {
            case Element.ALIGN_CENTER:
                field.setQuadding(PdfFormField.Q_CENTER);
                break;
            case Element.ALIGN_RIGHT:
                field.setQuadding(PdfFormField.Q_RIGHT);
                break;
        }
        if (rotation != 0)
            field.setMKRotation(rotation);
        if (fieldName != null) {
            field.setFieldName(fieldName);
            field.setValueAsString(text);
            if (defaultText != null)
                field.setDefaultValueAsString(defaultText);
            if ((options & READ_ONLY) != 0)
                field.setFieldFlags(PdfFormField.FF_READ_ONLY);
            if ((options & REQUIRED) != 0)
                field.setFieldFlags(PdfFormField.FF_REQUIRED);
            if ((options & MULTILINE) != 0)
                field.setFieldFlags(PdfFormField.FF_MULTILINE);
            if ((options & DO_NOT_SCROLL) != 0)
                field.setFieldFlags(PdfFormField.FF_DONOTSCROLL);
            if ((options & PASSWORD) != 0)
                field.setFieldFlags(PdfFormField.FF_PASSWORD);
            if ((options & FILE_SELECTION) != 0)
                field.setFieldFlags(PdfFormField.FF_FILESELECT);
            if ((options & DO_NOT_SPELL_CHECK) != 0)
                field.setFieldFlags(PdfFormField.FF_DONOTSPELLCHECK);
            if ((options & COMB) != 0)
                field.setFieldFlags(PdfFormField.FF_COMB);
        }
        field.setBorderStyle(new PdfBorderDictionary(borderWidth, borderStyle, new PdfDashPattern(3)));
        PdfAppearance tp = getAppearance();
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, tp);
        PdfAppearance da = (PdfAppearance)tp.getDuplicate();
        da.setFontAndSize(getRealFont(), fontSize);
        if (textColor == null)
            da.setGrayFill(0);
        else
            da.setColorFill(textColor);
        field.setDefaultAppearanceString(da);
        if (borderColor != null)
            field.setMKBorderColor(borderColor);
        if (backgroundColor != null)
            field.setMKBackgroundColor(backgroundColor);
        switch (visibility) {
            case HIDDEN:
                field.setFlags(PdfAnnotation.FLAGS_PRINT | PdfAnnotation.FLAGS_HIDDEN);
                break;
            case VISIBLE_BUT_DOES_NOT_PRINT:
                break;
            case HIDDEN_BUT_PRINTABLE:
                field.setFlags(PdfAnnotation.FLAGS_PRINT | PdfAnnotation.FLAGS_NOVIEW);
                break;
            default:
                field.setFlags(PdfAnnotation.FLAGS_PRINT);
                break;
        }
        return field;
    }
    
    /** Gets a new combo field.
     * @throws IOException on error
     * @throws DocumentException on error
     * @return a new combo field
     */    
    public PdfFormField getComboField() throws IOException, DocumentException {
        return getChoiceField(false);
    }
    
    /** Gets a new list field.
     * @throws IOException on error
     * @throws DocumentException on error
     * @return a new list field
     */    
    public PdfFormField getListField() throws IOException, DocumentException {
        return getChoiceField(true);
    }

    protected PdfFormField getChoiceField(boolean isList) throws IOException, DocumentException {
        options &= (~MULTILINE) & (~COMB);
        String uchoices[] = choices;
        if (uchoices == null)
            uchoices = new String[0];
        int topChoice = choiceSelection;
        if (topChoice >= uchoices.length)
            topChoice = uchoices.length - 1;
        if (text == null) text = ""; //fixed by Kazuya Ujihara (ujihara.jp)
        if (topChoice >= 0)
            text = uchoices[topChoice];
        if (topChoice < 0)
            topChoice = 0;
        PdfFormField field = null;
        String mix[][] = null;
        if (choiceExports == null) {
            if (isList)
                field = PdfFormField.createList(writer, uchoices, topChoice);
            else
                field = PdfFormField.createCombo(writer, (options & EDIT) != 0, uchoices, topChoice);
        }
        else {
            mix = new String[uchoices.length][2];
            for (int k = 0; k < mix.length; ++k)
                mix[k][0] = mix[k][1] = uchoices[k];
            int top = Math.min(uchoices.length, choiceExports.length);
            for (int k = 0; k < top; ++k) {
                if (choiceExports[k] != null)
                    mix[k][0] = choiceExports[k];
            }
            if (isList)
                field = PdfFormField.createList(writer, mix, topChoice);
            else
                field = PdfFormField.createCombo(writer, (options & EDIT) != 0, mix, topChoice);
        }
        field.setWidget(box, PdfAnnotation.HIGHLIGHT_INVERT);
        if (rotation != 0)
            field.setMKRotation(rotation);
        if (fieldName != null) {
            field.setFieldName(fieldName);
            if (uchoices.length > 0) {
                if (mix != null) {
                    field.setValueAsString(mix[topChoice][0]);
                    field.setDefaultValueAsString(mix[topChoice][0]);
                }
                else {
                    field.setValueAsString(text);
                    field.setDefaultValueAsString(text);
                }
            }
            if ((options & READ_ONLY) != 0)
                field.setFieldFlags(PdfFormField.FF_READ_ONLY);
            if ((options & REQUIRED) != 0)
                field.setFieldFlags(PdfFormField.FF_REQUIRED);
            if ((options & DO_NOT_SPELL_CHECK) != 0)
                field.setFieldFlags(PdfFormField.FF_DONOTSPELLCHECK);
        }
        field.setBorderStyle(new PdfBorderDictionary(borderWidth, borderStyle, new PdfDashPattern(3)));
        PdfAppearance tp;
        if (isList) {
            tp = getListAppearance();
            if (topFirst > 0)
                field.put(PdfName.TI, new PdfNumber(topFirst));
        }
        else
            tp = getAppearance();
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, tp);
        PdfAppearance da = (PdfAppearance)tp.getDuplicate();
        da.setFontAndSize(getRealFont(), fontSize);
        if (textColor == null)
            da.setGrayFill(0);
        else
            da.setColorFill(textColor);
        field.setDefaultAppearanceString(da);
        if (borderColor != null)
            field.setMKBorderColor(borderColor);
        if (backgroundColor != null)
            field.setMKBackgroundColor(backgroundColor);
        switch (visibility) {
            case HIDDEN:
                field.setFlags(PdfAnnotation.FLAGS_PRINT | PdfAnnotation.FLAGS_HIDDEN);
                break;
            case VISIBLE_BUT_DOES_NOT_PRINT:
                break;
            case HIDDEN_BUT_PRINTABLE:
                field.setFlags(PdfAnnotation.FLAGS_PRINT | PdfAnnotation.FLAGS_NOVIEW);
                break;
            default:
                field.setFlags(PdfAnnotation.FLAGS_PRINT);
                break;
        }
        return field;
    }
    
    /** Gets the default text.
     * @return the default text
     */
    public String getDefaultText() {
        return this.defaultText;
    }
    
    /** Sets the default text. It is only meaningful for text fields.
     * @param defaultText the default text
     */
    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
    }
    
    /** Gets the choices to be presented to the user in list/combo
     * fields.
     * @return the choices to be presented to the user
     */
    public String[] getChoices() {
        return this.choices;
    }
    
    /** Sets the choices to be presented to the user in list/combo
     * fields.
     * @param choices the choices to be presented to the user
     */
    public void setChoices(String[] choices) {
        this.choices = choices;
    }
    
    /** Gets the export values in list/combo fields.
     * @return the export values in list/combo fields
     */
    public String[] getChoiceExports() {
        return this.choiceExports;
    }
    
    /** Sets the export values in list/combo fields. If this array
     * is <CODE>null</CODE> then the choice values will also be used
     * as the export values.
     * @param choiceExports the export values in list/combo fields
     */
    public void setChoiceExports(String[] choiceExports) {
        this.choiceExports = choiceExports;
    }
    
    /** Gets the zero based index of the selected item.
     * @return the zero based index of the selected item
     */
    public int getChoiceSelection() {
        return this.choiceSelection;
    }
    
    /** Sets the zero based index of the selected item.
     * @param choiceSelection the zero based index of the selected item
     */
    public void setChoiceSelection(int choiceSelection) {
        this.choiceSelection = choiceSelection;
    }
    
    int getTopFirst() {
        return topFirst;
    }
    
    /**
     * Sets extra margins in text fields to better mimic the Acrobat layout.
     * @param extraMarginLeft the extra marging left
     * @param extraMarginTop the extra margin top
     */    
    public void setExtraMargin(float extraMarginLeft, float extraMarginTop) {
        this.extraMarginLeft = extraMarginLeft;
        this.extraMarginTop = extraMarginTop;
    }
}