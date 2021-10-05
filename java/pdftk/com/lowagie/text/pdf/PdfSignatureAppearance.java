/*
 * Copyright 2004 by Paulo Soares.
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

import pdftk.com.lowagie.text.Rectangle;
import pdftk.com.lowagie.text.ExceptionConverter;
import pdftk.com.lowagie.text.Phrase;
import pdftk.com.lowagie.text.Font;
import pdftk.com.lowagie.text.Element;
// import pdftk.com.lowagie.text.Image; ssteward: dropped in 1.44
import pdftk.com.lowagie.text.DocumentException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.text.SimpleDateFormat;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.cert.CRL;
import java.security.PrivateKey;
import java.io.OutputStream;
import java.io.IOException;
import java.io.EOFException;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.InputStream;

/**
 * This class takes care of the cryptographic options and appearances that form a signature.
 */
public class PdfSignatureAppearance {
    
    /**
     * The self signed filter.
     */
    public static final PdfName SELF_SIGNED = PdfName.ADOBE_PPKLITE;
    /**
     * The VeriSign filter.
     */
    public static final PdfName VERISIGN_SIGNED = PdfName.VERISIGN_PPKVS;
    /**
     * The Windows Certificate Security.
     */
    public static final PdfName WINCER_SIGNED = PdfName.ADOBE_PPKMS;

    private static final float topSection = 0.3f;
    private static final float margin = 2;
    private Rectangle rect;
    private Rectangle pageRect;
    private PdfTemplate app[] = new PdfTemplate[5];
    private PdfTemplate frm; 
    private PdfStamperImp writer;
    private String layer2Text;
    private String reason;
    private String location;
    private Calendar signDate;
    private String provider;
    private int page = 1;
    private String fieldName;
    private PrivateKey privKey;
    private Certificate[] certChain;
    private CRL[] crlList;
    private PdfName filter;
    private boolean newField;
    private ByteBuffer sigout;
    private OutputStream originalout;
    private File tempFile;
    private PdfDictionary cryptoDictionary;
    private PdfStamper stamper;
    private boolean preClosed = false;
    private PdfSigGenericPKCS sigStandard;
    private int range[];
    private RandomAccessFile raf;
    // ssteward omit: private int rangePosition = 0;
    private byte bout[];
    private int boutLen;
    private byte externalDigest[];
    private byte externalRSAdata[];
    private String digestEncryptionAlgorithm;
    private HashMap exclusionLocations;
        
    PdfSignatureAppearance(PdfStamperImp writer) {
        this.writer = writer;
        signDate = new GregorianCalendar();
        fieldName = getNewSigName();
    }
    
    /**
     * Sets the signature text identifying the signer.
     * @param text the signature text identifying the signer. If <CODE>null</CODE> or not set
     * a standard description will be used
     */    
    public void setLayer2Text(String text) {
        layer2Text = text;
    }
    
    /**
     * Gets the signature text identifying the signer if set by setLayer2Text().
     * @return the signature text identifying the signer
     */    
    public String getLayer2Text() {
        return layer2Text;
    }
    
    /**
     * Sets the text identifying the signature status.
     * @param text the text identifying the signature status. If <CODE>null</CODE> or not set
     * the description "Signature Not Verified" will be used
     */    
    public void setLayer4Text(String text) {
        layer4Text = text;
    }
    
    /**
     * Gets the text identifying the signature status if set by setLayer4Text().
     * @return the text identifying the signature status
     */    
    public String getLayer4Text() {
        return layer4Text;
    }
    
    /**
     * Gets the rectangle representing the signature dimensions.
     * @return the rectangle representing the signature dimensions. It may be <CODE>null</CODE>
     * or have zero width or height for invisible signatures
     */    
    public Rectangle getRect() {
        return rect;
    }
    
    /**
     * Gets the visibility status of the signature.
     * @return the visibility status of the signature
     */    
    public boolean isInvisible() {
        return (rect == null || rect.width() == 0 || rect.height() == 0);
    }
    
    /**
     * Sets the cryptographic parameters.
     * @param privKey the private key
     * @param certChain the certificate chain
     * @param crlList the certificate revocation list. It may be <CODE>null</CODE>
     * @param filter the crytographic filter type. It can be SELF_SIGNED, VERISIGN_SIGNED or WINCER_SIGNED
     */    
    public void setCrypto(PrivateKey privKey, Certificate[] certChain, CRL[] crlList, PdfName filter) {
        this.privKey = privKey;
        this.certChain = certChain;
        this.crlList = crlList;
        this.filter = filter;
    }
    
    /**
     * Sets the signature to be visible. It creates a new visible signature field.
     * @param pageRect the position and dimension of the field in the page
     * @param page the page to place the field. The fist page is 1
     * @param fieldName the field name or <CODE>null</CODE> to generate automatically a new field name
     */    
    public void setVisibleSignature(Rectangle pageRect, int page, String fieldName) {
        if (fieldName != null) {
            AcroFields af = writer.getAcroFields();
            AcroFields.Item item = af.getFieldItem(fieldName);
            if (item != null)
                throw new IllegalArgumentException("The field " + fieldName + " already exists.");
            this.fieldName = fieldName;
        }
        if (page < 1 || page > writer.reader.getNumberOfPages())
            throw new IllegalArgumentException("Invalid page number: " + page);
        this.pageRect = new Rectangle(pageRect);
        this.pageRect.normalize();
        rect = new Rectangle(this.pageRect.width(), this.pageRect.height());
        this.page = page;
        newField = true;
    }
    
    /**
     * Sets the signature to be visible. An empty signature field with the same name must already exist.
     * @param fieldName the existing empty signature field name
     */    
    public void setVisibleSignature(String fieldName) {
        AcroFields af = writer.getAcroFields();
        AcroFields.Item item = af.getFieldItem(fieldName);
        if (item == null)
            throw new IllegalArgumentException("The field " + fieldName + " does not exist.");
        PdfDictionary merged = (PdfDictionary)item.merged.get(0);
        if (!PdfName.SIG.equals(PdfReader.getPdfObject(merged.get(PdfName.FT))))
            throw new IllegalArgumentException("The field " + fieldName + " is not a signature field.");
        this.fieldName = fieldName;
        PdfArray r = (PdfArray)PdfReader.getPdfObject(merged.get(PdfName.RECT));
        ArrayList ar = r.getArrayList();
        float llx = ((PdfNumber)PdfReader.getPdfObject((PdfObject)ar.get(0))).floatValue();
        float lly = ((PdfNumber)PdfReader.getPdfObject((PdfObject)ar.get(1))).floatValue();
        float urx = ((PdfNumber)PdfReader.getPdfObject((PdfObject)ar.get(2))).floatValue();
        float ury = ((PdfNumber)PdfReader.getPdfObject((PdfObject)ar.get(3))).floatValue();
        pageRect = new Rectangle(llx, lly, urx, ury);
        pageRect.normalize();
        page = ((Integer)item.page.get(0)).intValue();
        int rotation = writer.reader.getPageRotation(page);
        Rectangle pageSize = writer.reader.getPageSizeWithRotation(page);
        switch (rotation) {
            case 90:
                pageRect = new Rectangle(
                pageRect.bottom(),
                pageSize.top() - pageRect.left(),
                pageRect.top(),
                pageSize.top() - pageRect.right());
                break;
            case 180:
                pageRect = new Rectangle(
                pageSize.right() - pageRect.left(),
                pageSize.top() - pageRect.bottom(),
                pageSize.right() - pageRect.right(),
                pageSize.top() - pageRect.top());
                break;
            case 270:
                pageRect = new Rectangle(
                pageSize.right() - pageRect.bottom(),
                pageRect.left(),
                pageSize.right() - pageRect.top(),
                pageRect.right());
                break;
        }
        if (rotation != 0)
            pageRect.normalize();
        rect = new Rectangle(this.pageRect.width(), this.pageRect.height());
    }
    
    /**
     * Gets a template layer to create a signature appearance. The layers can go from 0 to 4.
     * <p>
     * Consult <A HREF="http://partners.adobe.com/asn/developer/pdfs/tn/PPKAppearances.pdf">PPKAppearances.pdf</A>
     * for further details.
     * @param layer the layer
     * @return a template
     */    
    public PdfTemplate getLayer(int layer) {
        if (layer < 0 || layer >= app.length)
            return null;
        PdfTemplate t = app[layer];
        if (t == null) {
            t = app[layer] = new PdfTemplate(writer);
            t.setBoundingBox(rect);
            writer.addDirectTemplateSimple(t, new PdfName("n" + layer));
        }
        return t;
    }
    
    /**
     * Gets the template that aggregates all appearance layers. This corresponds to the /FRM resource.
     * <p>
     * Consult <A HREF="http://partners.adobe.com/asn/developer/pdfs/tn/PPKAppearances.pdf">PPKAppearances.pdf</A>
     * for further details.
     * @return the template that aggregates all appearance layers
     */    
    public PdfTemplate getTopLayer() {
        if (frm == null) {
            frm = new PdfTemplate(writer);
            frm.setBoundingBox(rect);
            writer.addDirectTemplateSimple(frm, new PdfName("FRM"));
        }
        return frm;
    }
    
    /**
     * Gets the main appearance layer.
     * <p>
     * Consult <A HREF="http://partners.adobe.com/asn/developer/pdfs/tn/PPKAppearances.pdf">PPKAppearances.pdf</A>
     * for further details.
     * @return the main appearance layer
     * @throws DocumentException on error
     * @throws IOException on error
     */    
    public PdfTemplate getAppearance() throws DocumentException, IOException {
        if (app[0] == null) {
            PdfTemplate t = app[0] = new PdfTemplate(writer);
            t.setBoundingBox(new Rectangle(100, 100));
            writer.addDirectTemplateSimple(t, new PdfName("n0"));
            t.setLiteral("% DSBlank\n");
        }
        if (app[1] == null && !acro6Layers) {
            PdfTemplate t = app[1] = new PdfTemplate(writer);
            t.setBoundingBox(new Rectangle(100, 100));
            writer.addDirectTemplateSimple(t, new PdfName("n1"));
            t.setLiteral(questionMark);
        }
        if (app[2] == null) {
            String text;
            if (layer2Text == null) {
                StringBuffer buf = new StringBuffer();
                buf.append("Digitally signed by ").append(PdfPKCS7.getSubjectFields((X509Certificate)certChain[0]).getField("CN")).append("\n");
                SimpleDateFormat sd = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
                buf.append("Date: ").append(sd.format(signDate.getTime()));
                if (reason != null)
                    buf.append("\n").append("Reason: ").append(reason);
                if (location != null)
                    buf.append("\n").append("Location: ").append(location);
                text = buf.toString();
            }
            else
                text = layer2Text;
            PdfTemplate t = app[2] = new PdfTemplate(writer);
            t.setBoundingBox(rect);
            writer.addDirectTemplateSimple(t, new PdfName("n2"));
	    /* ssteward: dropped in 1.44
            if (image != null) {
                if (imageScale == 0) {
                    t.addImage(image, rect.width(), 0, 0, rect.height(), 0, 0);
                }
                else {
                    float usableScale = imageScale;
                    if (imageScale < 0)
                        usableScale = Math.min(rect.width() / image.width(), rect.height() / image.height());
                    float w = image.width() * usableScale;
                    float h = image.height() * usableScale;
                    float x = (rect.width() - w) / 2;
                    float y = (rect.height() - h) / 2;
                    t.addImage(image, w, 0, 0, h, x, y);
                }
            }
	    */
            Font font;
            if (layer2Font == null)
                font = new Font();
            else
                font = new Font(layer2Font);
            float size = font.size();
            if (size <= 0) {
                Rectangle sr = new Rectangle(rect.width() - 2 * margin, rect.height() * (1 - topSection) - 2 * margin);
                size = fitText(font, text, sr, 12, runDirection);
            }
            ColumnText ct = new ColumnText(t);
            ct.setRunDirection(runDirection);
            ct.setSimpleColumn(new Phrase(text, font), margin, 0, rect.width() - margin, rect.height() * (1 - topSection) - margin, size, Element.ALIGN_LEFT);
            ct.go();
        }
        if (app[3] == null && !acro6Layers) {
            PdfTemplate t = app[3] = new PdfTemplate(writer);
            t.setBoundingBox(new Rectangle(100, 100));
            writer.addDirectTemplateSimple(t, new PdfName("n3"));
            t.setLiteral("% DSBlank\n");
        }
        if (app[4] == null && !acro6Layers) {
            PdfTemplate t = app[4] = new PdfTemplate(writer);
            t.setBoundingBox(new Rectangle(0, rect.height() * (1 - topSection), rect.right(), rect.top()));
            writer.addDirectTemplateSimple(t, new PdfName("n4"));
            Font font;
            if (layer2Font == null)
                font = new Font();
            else
                font = new Font(layer2Font);
            float size = font.size();
            String text = "Signature Not Verified";
            if (layer4Text != null)
                text = layer4Text;
            Rectangle sr = new Rectangle(rect.width() - 2 * margin, rect.height() * topSection - 2 * margin);
            size = fitText(font, text, sr, 15, runDirection);
            ColumnText ct = new ColumnText(t);
            ct.setRunDirection(runDirection);
            ct.setSimpleColumn(new Phrase(text, font), margin, 0, rect.width() - margin, rect.height() - margin, size, Element.ALIGN_LEFT);
            ct.go();
        }
        int rotation = writer.reader.getPageRotation(page);
        Rectangle rotated = new Rectangle(rect);
        int n = rotation;
        while (n > 0) {
            rotated = rotated.rotate();
            n -= 90;
        }
        if (frm == null) {
            frm = new PdfTemplate(writer);
            frm.setBoundingBox(rotated);
            writer.addDirectTemplateSimple(frm, new PdfName("FRM"));
            float scale = Math.min(rect.width(), rect.height()) * 0.9f;
            float x = (rect.width() - scale) / 2;
            float y = (rect.height() - scale) / 2;
            scale /= 100;
            if (rotation == 90)
                frm.concatCTM(0, 1, -1, 0, rect.height(), 0);
            else if (rotation == 180)
                frm.concatCTM(-1, 0, 0, -1, rect.width(), rect.height());
            else if (rotation == 270)
                frm.concatCTM(0, -1, 1, 0, 0, rect.width());
            frm.addTemplate(app[0], 0, 0);
            if (!acro6Layers)
                frm.addTemplate(app[1], scale, 0, 0, scale, x, y);
            frm.addTemplate(app[2], 0, 0);
            if (!acro6Layers) {
                frm.addTemplate(app[3], scale, 0, 0, scale, x, y);
                frm.addTemplate(app[4], 0, 0);
            }
        }
        PdfTemplate napp = new PdfTemplate(writer);
        napp.setBoundingBox(rotated);
        writer.addDirectTemplateSimple(napp, null);
        napp.addTemplate(frm, 0, 0);
        return napp;
    }
    
    /**
     * Fits the text to some rectangle adjusting the font size as needed.
     * @param font the font to use
     * @param text the text
     * @param rect the rectangle where the text must fit
     * @param maxFontSize the maximum font size
     * @param runDirection the run direction
     * @return the calculated font size that makes the text fit
     */    
    public static float fitText(Font font, String text, Rectangle rect, float maxFontSize, int runDirection) {
        try {
            ColumnText ct = null;
            int status = 0;
            if (maxFontSize <= 0) {
                int cr = 0;
                int lf = 0;
                char t[] = text.toCharArray();
                for (int k = 0; k < t.length; ++k) {
                    if (t[k] == '\n')
                        ++lf;
                    else if (t[k] == '\r')
                        ++cr;
                }
                int minLines = Math.max(cr, lf) + 1;
                maxFontSize = Math.abs(rect.height()) / minLines - 0.001f;
            }
            font.setSize(maxFontSize);
            Phrase ph = new Phrase(text, font);
            ct = new ColumnText(null);
            ct.setSimpleColumn(ph, rect.left(), rect.bottom(), rect.right(), rect.top(), maxFontSize, Element.ALIGN_LEFT);
            ct.setRunDirection(runDirection);
            status = ct.go(true);
            if ((status & ColumnText.NO_MORE_TEXT) != 0)
                return maxFontSize;
            float precision = 0.1f;
            float min = 0;
            float max = maxFontSize;
            float size = maxFontSize;
            for (int k = 0; k < 50; ++k) { //just in case it doesn't converge
                size = (min + max) / 2;
                ct = new ColumnText(null);
                font.setSize(size);
                ct.setSimpleColumn(new Phrase(text, font), rect.left(), rect.bottom(), rect.right(), rect.top(), size, Element.ALIGN_LEFT);
                ct.setRunDirection(runDirection);
                status = ct.go(true);
                if ((status & ColumnText.NO_MORE_TEXT) != 0) {
                    if (max - min < size * precision)
                        return size;
                    min = size;
                }
                else
                    max = size;
            }
            return size;
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    /**
     * Sets the digest/signature to an external calculated value.
     * @param digest the digest. This is the actual signature
     * @param RSAdata the extra data that goes into the data tag in PKCS#7
     * @param digestEncryptionAlgorithm the encryption algorithm. It may must be <CODE>null</CODE> if the <CODE>digest</CODE>
     * is also <CODE>null</CODE>. If the <CODE>digest</CODE> is not <CODE>null</CODE>
     * then it may be "RSA" or "DSA"
     */    
    public void setExternalDigest(byte digest[], byte RSAdata[], String digestEncryptionAlgorithm) {
        externalDigest = digest;
        externalRSAdata = RSAdata;
        this.digestEncryptionAlgorithm = digestEncryptionAlgorithm;
    }

    /**
     * Gets the signing reason.
     * @return the signing reason
     */    
    public String getReason() {
        return this.reason;
    }
    
    /**
     * Sets the signing reason.
     * @param reason the signing reason
     */    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    /**
     * Gets the signing location.
     * @return the signing location
     */    
    public String getLocation() {
        return this.location;
    }
    
    /**
     * Sets the signing location.
     * @param location the signing location
     */    
    public void setLocation(String location) {
        this.location = location;
    }
        
    /**
     * Returns the Cryptographic Service Provider that will sign the document.
     * @return provider the name of the provider, for example "SUN",
     * or <code>null</code> to use the default provider.
     */
    public String getProvider() {
        return this.provider;
    }
    
    /**
     * Sets the Cryptographic Service Provider that will sign the document.
     *
     * @param provider the name of the provider, for example "SUN", or
     * <code>null</code> to use the default provider.
     */
    public void setProvider(String provider) {
        this.provider = provider;
    }
    
    /**
     * Gets the private key.
     * @return the private key
     */    
    public java.security.PrivateKey getPrivKey() {
        return privKey;
    }
    
    /**
     * Gets the certificate chain.
     * @return the certificate chain
     */    
    public java.security.cert.Certificate[] getCertChain() {
        return this.certChain;
    }
    
    /**
     * Gets the certificate revocation list.
     * @return the certificate revocation list
     */    
    public java.security.cert.CRL[] getCrlList() {
        return this.crlList;
    }
    
    /**
     * Gets the filter used to sign the document.
     * @return the filter used to sign the document
     */    
    public pdftk.com.lowagie.text.pdf.PdfName getFilter() {
        return filter;
    }
    
    /**
     * Checks if a new field was created.
     * @return <CODE>true</CODE> if a new field was created, <CODE>false</CODE> if signing
     * an existing field or if the signature is invisible
     */    
    public boolean isNewField() {
        return this.newField;
    }
    
    /**
     * Gets the page number of the field.
     * @return the page number of the field
     */    
    public int getPage() {
        return page;
    }
    
    /**
     * Gets the field name.
     * @return the field name
     */    
    public java.lang.String getFieldName() {
        return fieldName;
    }
    
    /**
     * Gets the rectangle that represent the position and dimension of the signature in the page.
     * @return the rectangle that represent the position and dimension of the signature in the page
     */    
    public pdftk.com.lowagie.text.Rectangle getPageRect() {
        return pageRect;
    }
    
    /**
     * Gets the signature date.
     * @return the signature date
     */    
    public java.util.Calendar getSignDate() {
        return signDate;
    }
    
    /**
     * Sets the signature date.
     * @param signDate the signature date
     */    
    public void setSignDate(java.util.Calendar signDate) {
        this.signDate = signDate;
    }
    
    pdftk.com.lowagie.text.pdf.ByteBuffer getSigout() {
        return sigout;
    }
    
    void setSigout(pdftk.com.lowagie.text.pdf.ByteBuffer sigout) {
        this.sigout = sigout;
    }
    
    java.io.OutputStream getOriginalout() {
        return originalout;
    }
    
    void setOriginalout(java.io.OutputStream originalout) {
        this.originalout = originalout;
    }
    
    /**
     * Gets the temporary file.
     * @return the temporary file or <CODE>null</CODE> is the document is created in memory
     */    
    public java.io.File getTempFile() {
        return tempFile;
    }
    
    void setTempFile(java.io.File tempFile) {
        this.tempFile = tempFile;
    }

    /**
     * Gets a new signature fied name that doesn't clash with any existing name.
     * @return a new signature fied name
     */    
    public String getNewSigName() {
        AcroFields af = writer.getAcroFields();
        String name = "Signature";
        int step = 0;
        boolean found = false;
        while (!found) {
            ++step;
            String n1 = name + step;
            if (af.getFieldItem(n1) != null)
                continue;
            n1 += ".";
            found = true;
            for (Iterator it = af.getFields().keySet().iterator(); it.hasNext();) {
                String fn = (String)it.next();
                if (fn.startsWith(n1)) {
                    found = false;
                    break;
                }
            }
        }
        name += step;
        return name;
    }
    
    /**
     * This is the first method to be called when using external signatures. The general sequence is:
     * preClose(), getDocumentBytes() and close().
     * <p>
     * If calling preClose() <B>dont't</B> call PdfStamper.close().
     * <p>
     * No external signatures are allowed if this methos is called.
     * @throws IOException on error
     * @throws DocumentException on error
     */    
    public void preClose() throws IOException, DocumentException {
        preClose(null);
    }
    /**
     * This is the first method to be called when using external signatures. The general sequence is:
     * preClose(), getDocumentBytes() and close().
     * <p>
     * If calling preClose() <B>dont't</B> call PdfStamper.close().
     * <p>
     * If using an external signature <CODE>exclusionSizes</CODE> must contain at least
     * the <CODE>PdfName.CONTENTS</CODE> key with the size that it will take in the
     * document. Note that due to the hex string coding this size should be
     * byte_size*2+2.
     * @param exclusionSizes a <CODE>HashMap</CODE> with names and sizes to be excluded in the signature
     * calculation. The key is a <CODE>PdfName</CODE> and the value an
     * <CODE>Integer</CODE>. At least the <CODE>PdfName.CONTENTS</CODE> must be present
     * @throws IOException on error
     * @throws DocumentException on error
     */    
    public void preClose(HashMap exclusionSizes) throws IOException, DocumentException {
        if (preClosed)
            throw new DocumentException("Document already pre closed.");
        preClosed = true;
        AcroFields af = writer.getAcroFields();
        String name = getFieldName();
        boolean fieldExists = !(isInvisible() || isNewField());
        int flags = 132;
        if (fieldExists) {
            flags = 0;
            ArrayList merged = af.getFieldItem(name).merged;
            PdfObject obj = PdfReader.getPdfObjectRelease(((PdfDictionary)merged.get(0)).get(PdfName.F));
            if (obj != null && obj.isNumber())
                flags = ((PdfNumber)obj).intValue();
            af.removeField(name);
        }
        writer.setSigFlags(3);
        PdfFormField sigField = PdfFormField.createSignature(writer);
        sigField.setFieldName(name);
        PdfIndirectReference refSig = writer.getPdfIndirectReference();
        sigField.put(PdfName.V, refSig);
        sigField.setFlags(flags);

        int pagen = getPage();
        // PdfReader reader = writer.reader;
        if (!isInvisible()) {
            sigField.setWidget(getPageRect(), null);
            sigField.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, getAppearance());
        }
        else
            sigField.setWidget(new Rectangle(0, 0), null);
        sigField.setPage(pagen);
        writer.addAnnotation(sigField, pagen);

        exclusionLocations = new HashMap();
        if (cryptoDictionary == null) {
            if (PdfName.ADOBE_PPKLITE.equals(getFilter()))
                sigStandard = new PdfSigGenericPKCS.PPKLite(getProvider());
            else if (PdfName.ADOBE_PPKMS.equals(getFilter()))
                sigStandard = new PdfSigGenericPKCS.PPKMS(getProvider());
            else if (PdfName.VERISIGN_PPKVS.equals(getFilter()))
                sigStandard = new PdfSigGenericPKCS.VeriSign(getProvider());
            else
                throw new IllegalArgumentException("Unknown filter: " + getFilter());
            sigStandard.setExternalDigest(externalDigest, externalRSAdata, digestEncryptionAlgorithm);
            if (getReason() != null)
                sigStandard.setReason(getReason());
            if (getLocation() != null)
                sigStandard.setLocation(getLocation());
            if (getContact() != null)
                sigStandard.setContact(getContact());
            sigStandard.put(PdfName.M, new PdfDate(getSignDate()));
            sigStandard.setSignInfo(getPrivKey(), getCertChain(), getCrlList());
            PdfString contents = (PdfString)sigStandard.get(PdfName.CONTENTS);
            PdfLiteral lit = new PdfLiteral((contents.toString().length() + (PdfName.ADOBE_PPKLITE.equals(getFilter())?0:64)) * 2 + 2);
            exclusionLocations.put(PdfName.CONTENTS, lit);
            sigStandard.put(PdfName.CONTENTS, lit);
            lit = new PdfLiteral(80);
            exclusionLocations.put(PdfName.BYTERANGE, lit);
            sigStandard.put(PdfName.BYTERANGE, lit);
            if (signatureEvent != null)
                signatureEvent.getSignatureDictionary(sigStandard);
            writer.addToBody(sigStandard, refSig, false);
        }
        else {
            PdfLiteral lit = new PdfLiteral(80);
            exclusionLocations.put(PdfName.BYTERANGE, lit);
            cryptoDictionary.put(PdfName.BYTERANGE, lit);
            for (Iterator it = exclusionSizes.entrySet().iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry)it.next();
                PdfName key = (PdfName)entry.getKey();
                Integer v = (Integer)entry.getValue();
                lit = new PdfLiteral(v.intValue());
                exclusionLocations.put(key, lit);
                cryptoDictionary.put(key, lit);
            }
            if (signatureEvent != null)
                signatureEvent.getSignatureDictionary(cryptoDictionary);
            writer.addToBody(cryptoDictionary, refSig, false);
        }
        writer.close(stamper.getMoreInfo());
        
        range = new int[exclusionLocations.size() * 2];
        int byteRangePosition = ((PdfLiteral)exclusionLocations.get(PdfName.BYTERANGE)).getPosition();
        exclusionLocations.remove(PdfName.BYTERANGE);
        int idx = 1;
        for (Iterator it = exclusionLocations.values().iterator(); it.hasNext();) {
            PdfLiteral lit = (PdfLiteral)it.next();
            int n = lit.getPosition();
            range[idx++] = n;
            range[idx++] = lit.getPosLength() + n;
        }
        Arrays.sort(range, 1, range.length - 1);
        for (int k = 3; k < range.length - 2; k += 2)
            range[k] -= range[k - 1];
        
        if (tempFile == null) {
            bout = sigout.getBuffer();
            boutLen = sigout.size();
            range[range.length - 1] = boutLen - range[range.length - 2];
            ByteBuffer bf = new ByteBuffer();
            bf.append('[');
            for (int k = 0; k < range.length; ++k)
                bf.append(range[k]).append(' ');
            bf.append(']');
            System.arraycopy(bf.getBuffer(), 0, bout, byteRangePosition, bf.size());
        }
        else {
            try {
                raf = new RandomAccessFile(tempFile, "rw");
                int boutLen = (int)raf.length();
                range[range.length - 1] = boutLen - range[range.length - 2];
                ByteBuffer bf = new ByteBuffer();
                bf.append('[');
                for (int k = 0; k < range.length; ++k)
                    bf.append(range[k]).append(' ');
                bf.append(']');
                raf.seek(byteRangePosition);
                raf.write(bf.getBuffer(), 0, bf.size());
            }
            catch (IOException e) {
                try{raf.close();}catch(Exception ee){}
                try{tempFile.delete();}catch(Exception ee){}
                throw e;
            }
        }
    }
    
    /**
     * This is the last method to be called when using external signatures. The general sequence is:
     * preClose(), getDocumentBytes() and close().
     * <p>
     * <CODE>update</CODE> is a <CODE>PdfDictionary</CODE> that must have exactly the
     * same keys as the ones provided in {@link #preClose(HashMap)}.
     * @param update a <CODE>PdfDictionary</CODE> with the key/value that will fill the holes defined
     * in {@link #preClose(HashMap)}
     * @throws DocumentException on error
     * @throws IOException on error
     */    
    public void close(PdfDictionary update) throws IOException, DocumentException {
        try {
            if (!preClosed)
                throw new DocumentException("preClose() must be called first.");
            ByteBuffer bf = new ByteBuffer();
            for (Iterator it = update.getKeys().iterator(); it.hasNext();) {
                PdfName key = (PdfName)it.next();
                PdfObject obj = update.get(key);
                PdfLiteral lit = (PdfLiteral)exclusionLocations.get(key);
                if (lit == null)
                    throw new IllegalArgumentException("The key " + key.toString() + " didn't reserve space in preClose().");
                bf.reset();
                obj.toPdf(null, bf);
                if (bf.size() > lit.getPosLength())
                    throw new IllegalArgumentException("The key " + key.toString() + " is too big. Is " + bf.size() + ", reserved " + lit.getPosLength());
                if (tempFile == null)
                    System.arraycopy(bf.getBuffer(), 0, bout, lit.getPosition(), bf.size());
                else {
                    raf.seek(lit.getPosition());
                    raf.write(bf.getBuffer(), 0, bf.size());
                }
            }
            if (update.size() != exclusionLocations.size())
                throw new IllegalArgumentException("The update dictionary has less keys than required.");
            if (tempFile == null) {
                originalout.write(bout, 0, boutLen);
            }
            else {
                if (originalout != null) {
                    raf.seek(0);
                    int length = (int)raf.length();
                    byte buf[] = new byte[8192];
                    while (length > 0) {
                        int r = raf.read(buf, 0, Math.min(buf.length, length));
                        if (r < 0)
                            throw new EOFException("Unexpected EOF");
                        originalout.write(buf, 0, r);
                        length -= r;
                    }
                }
            }
        }
        finally {
            if (tempFile != null) {
                try{raf.close();}catch(Exception ee){}
                if (originalout != null)
                    try{tempFile.delete();}catch(Exception ee){}
            }
            if (originalout != null)
                try{originalout.close();}catch(Exception e){}
        }
    }
    /* ssteward omit: 
    private static int indexArray(byte bout[], int position, String search) {
        byte ss[] = PdfEncodings.convertToBytes(search, null);
        while (true) {
            int k;
            for (k = 0; k < ss.length; ++k) {
                if (ss[k] != bout[position + k])
                    break;
            }
            if (k == ss.length)
                return position;
            ++position;
        }
    }
    
    private static int indexFile(RandomAccessFile raf, int position, String search) throws IOException {
        byte ss[] = PdfEncodings.convertToBytes(search, null);
        while (true) {
            raf.seek(position);
            int k;
            for (k = 0; k < ss.length; ++k) {
                int b = raf.read();
                if (b < 0)
                    throw new EOFException("Unexpected EOF");
                if (ss[k] != (byte)b)
                    break;
            }
            if (k == ss.length)
                return position;
            ++position;
        }
    }
    */
    
    /**
     * Gets the document bytes that are hashable when using external signatures. The general sequence is:
     * preClose(), getRangeStream() and close().
     * <p>
     * @return the document bytes that are hashable
     */    
    public InputStream getRangeStream() {
        return new PdfSignatureAppearance.RangeStream(raf, bout, range);
    }
    
    /**
     * Gets the user made signature dictionary. This is the dictionary at the /V key.
     * @return the user made signature dictionary
     */    
    public pdftk.com.lowagie.text.pdf.PdfDictionary getCryptoDictionary() {
        return cryptoDictionary;
    }
    
    /**
     * Sets a user made signature dictionary. This is the dictionary at the /V key.
     * @param cryptoDictionary a user made signature dictionary
     */    
    public void setCryptoDictionary(pdftk.com.lowagie.text.pdf.PdfDictionary cryptoDictionary) {
        this.cryptoDictionary = cryptoDictionary;
    }
    
    /**
     * Gets the <CODE>PdfStamper</CODE> associated with this instance.
     * @return the <CODE>PdfStamper</CODE> associated with this instance
     */    
    public pdftk.com.lowagie.text.pdf.PdfStamper getStamper() {
        return stamper;
    }
    
    void setStamper(pdftk.com.lowagie.text.pdf.PdfStamper stamper) {
        this.stamper = stamper;
    }
    
    /**
     * Checks if the document is in the process of closing.
     * @return <CODE>true</CODE> if the document is in the process of closing,
     * <CODE>false</CODE> otherwise
     */    
    public boolean isPreClosed() {
        return preClosed;
    }
    
    /**
     * Gets the instance of the standard signature dictionary. This instance
     * is only available after pre close.
     * <p>
     * The main use is to insert external signatures.
     * @return the instance of the standard signature dictionary
     */    
    public pdftk.com.lowagie.text.pdf.PdfSigGenericPKCS getSigStandard() {
        return sigStandard;
    }
    
    /**
     * Gets the signing contact.
     * @return the signing contact
     */
    public String getContact() {
        return this.contact;
    }
    
    /**
     * Sets the signing contact.
     * @param contact the signing contact
     */
    public void setContact(String contact) {
        this.contact = contact;
    }
    
    /**
     * Gets the n2 and n4 layer font.
     * @return the n2 and n4 layer font
     */
    public Font getLayer2Font() {
        return this.layer2Font;
    }
    
    /**
     * Sets the n2 and n4 layer font. If the font size is zero, auto-fit will be used.
     * @param layer2Font the n2 and n4 font
     */
    public void setLayer2Font(Font layer2Font) {
        this.layer2Font = layer2Font;
    }
    
    /**
     * Gets the Acrobat 6.0 layer mode.
     * @return the Acrobat 6.0 layer mode
     */
    public boolean isAcro6Layers() {
        return this.acro6Layers;
    }
    
    /**
     * Acrobat 6.0 and higher recomends that only layer n2 and n4 be present. This method sets that mode.
     * @param acro6Layers if <code>true</code> only the layers n2 and n4 will be present
     */
    public void setAcro6Layers(boolean acro6Layers) {
        this.acro6Layers = acro6Layers;
    }
    
    /** Sets the run direction in the n2 and n4 layer. 
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
    
    /**
     * Getter for property signatureEvent.
     * @return Value of property signatureEvent.
     */
    public SignatureEvent getSignatureEvent() {
        return this.signatureEvent;
    }
    
    /**
     * Sets the signature event to allow modification of the signature dictionary.
     * @param signatureEvent the signature event
     */
    public void setSignatureEvent(SignatureEvent signatureEvent) {
        this.signatureEvent = signatureEvent;
    }
    
    /**
     * Gets the background image for the layer 2.
     * @return the background image for the layer 2
     */
    /* ssteward: dropped in 1.44
    public Image getImage() {
        return this.image;
    }
    */
    
    /**
     * Sets the background image for the layer 2.
     * @param image the background image for the layer 2
     */
    /* ssteward: dropped in 1.44
    public void setImage(Image image) {
        this.image = image;
    }
    */
    
    /**
     * Gets the scaling to be applied to the background image.
     * @return the scaling to be applied to the background image
     */
    public float getImageScale() {
        return this.imageScale;
    }
    
    /**
     * Sets the scaling to be applied to the background image. If it's zero the image
     * will fully fill the rectangle. If it's less than zero the image will fill the rectangle but
     * will keep the proportions. If it's greater than zero that scaling will be applied.
     * In any of the cases the image will always be centered. It's zero by default.
     * @param imageScale the scaling to be applied to the background image
     */
    public void setImageScale(float imageScale) {
        this.imageScale = imageScale;
    }
    
    /**
     * Commands to draw a yellow question mark in a stream content
     */    
    public static final String questionMark = 
        "% DSUnknown\n" +
        "q\n" +
        "1 G\n" +
        "1 g\n" +
        "0.1 0 0 0.1 9 0 cm\n" +
        "0 J 0 j 4 M []0 d\n" +
        "1 i \n" +
        "0 g\n" +
        "313 292 m\n" +
        "313 404 325 453 432 529 c\n" +
        "478 561 504 597 504 645 c\n" +
        "504 736 440 760 391 760 c\n" +
        "286 760 271 681 265 626 c\n" +
        "265 625 l\n" +
        "100 625 l\n" +
        "100 828 253 898 381 898 c\n" +
        "451 898 679 878 679 650 c\n" +
        "679 555 628 499 538 435 c\n" +
        "488 399 467 376 467 292 c\n" +
        "313 292 l\n" +
        "h\n" +
        "308 214 170 -164 re\n" +
        "f\n" +
        "0.44 G\n" +
        "1.2 w\n" +
        "1 1 0.4 rg\n" +
        "287 318 m\n" +
        "287 430 299 479 406 555 c\n" +
        "451 587 478 623 478 671 c\n" +
        "478 762 414 786 365 786 c\n" +
        "260 786 245 707 239 652 c\n" +
        "239 651 l\n" +
        "74 651 l\n" +
        "74 854 227 924 355 924 c\n" +
        "425 924 653 904 653 676 c\n" +
        "653 581 602 525 512 461 c\n" +
        "462 425 441 402 441 318 c\n" +
        "287 318 l\n" +
        "h\n" +
        "282 240 170 -164 re\n" +
        "B\n" +
        "Q\n";
    
    /**
     * Holds value of property contact.
     */
    private String contact;
    
    /**
     * Holds value of property layer2Font.
     */
    private Font layer2Font;
    
    /**
     * Holds value of property layer4Text.
     */
    private String layer4Text;
    
    /**
     * Holds value of property acro6Layers.
     */
    private boolean acro6Layers;
    
    /**
     * Holds value of property runDirection.
     */
    private int runDirection = PdfWriter.RUN_DIRECTION_NO_BIDI;
    
    /**
     * Holds value of property signatureEvent.
     */
    private SignatureEvent signatureEvent;
    
    /**
     * Holds value of property image.
     */
    // private Image image; ssteward: dropped in 1.44
    
    /**
     * Holds value of property imageScale.
     */
    private float imageScale;
    
    /**
     *
     */    
    private static class RangeStream extends InputStream {
        private byte b[] = new byte[1];
        private RandomAccessFile raf;
        private byte bout[];
        private int range[];
        private int rangePosition = 0;
        
        private RangeStream(RandomAccessFile raf, byte bout[], int range[]) {
            this.raf = raf;
            this.bout = bout;
            this.range = range;
        }
        
        /**
         * @see java.io.InputStream#read()
         */
        public int read() throws IOException {
            int n = read(b);
            if (n != 1)
                return -1;
            return b[0] & 0xff;
        }
        
        /**
         * @see java.io.InputStream#read(byte[], int, int)
         */
        public int read(byte[] b, int off, int len) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            } else if ((off < 0) || (off > b.length) || (len < 0) ||
            ((off + len) > b.length) || ((off + len) < 0)) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return 0;
            }
            if (rangePosition >= range[range.length - 2] + range[range.length - 1]) {
                return -1;
            }
            for (int k = 0; k < range.length; k += 2) {
                int start = range[k];
                int end = start + range[k + 1];
                if (rangePosition < start)
                    rangePosition = start;
                if (rangePosition >= start && rangePosition < end) {
                    int lenf = Math.min(len, end - rangePosition);
                    if (raf == null)
                        System.arraycopy(bout, rangePosition, b, off, lenf);
                    else {
                        raf.seek(rangePosition);
                        raf.readFully(b, off, lenf);
                    }
                    rangePosition += lenf;
                    return lenf;
                }
            }
            return -1;
        }
    }
    
    /**
     * An interface to retrieve the signature dictionary for modification.
     */    
    public interface SignatureEvent {
        /**
         * Allows modification of the signature dictionary.
         * @param sig the signature dictionary
         */        
        public void getSignatureDictionary(PdfDictionary sig);
    }
}
