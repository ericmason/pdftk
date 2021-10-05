/*
 * $Id: PdfDate.java,v 1.63 2005/09/04 16:20:01 psoares33 Exp $
 * $Name:  $
 *
 * Copyright 1999, 2000, 2001, 2002 Bruno Lowagie
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

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.SimpleTimeZone;

import gnu.java.locale.*;

/**
 * <CODE>PdfDate</CODE> is the PDF date object.
 * <P>
 * PDF defines a standard date format. The PDF date format closely follows the format
 * defined by the international standard ASN.1 (Abstract Syntax Notation One, defined
 * in CCITT X.208 or ISO/IEC 8824). A date is a <CODE>PdfString</CODE> of the form:
 * <P><BLOCKQUOTE>
 * (D: YYYYMMDDHHmmSSOHH'mm')
 * </BLOCKQUOTE><P>
 * This object is described in the 'Portable Document Format Reference Manual version 1.3'
 * section 7.2 (page 183-184)
 *
 * @see		PdfString
 * @see		java.util.GregorianCalendar
 */

public class PdfDate extends PdfString {
    
    /* we now have code in pdftk.cc that pulls it in for static (win32) build
    // ssteward; static builds of pdftk (Windows, gcc 3.3.1) would
    // omit this class because of its reference by reflection;
    // this treatment ensures that ld will include it; we also init it in pdftk.cc;
    private static Class c1= java.util.Calendar.class;
    */

    private static final int dateSpace[] = {Calendar.YEAR, 4, 0, Calendar.MONTH, 2, -1, Calendar.DAY_OF_MONTH, 2, 0,
        Calendar.HOUR_OF_DAY, 2, 0, Calendar.MINUTE, 2, 0, Calendar.SECOND, 2, 0};
    
    // constructors
    
/**
 * Constructs a <CODE>PdfDate</CODE>-object.
 *
 * @param		d			the date that has to be turned into a <CODE>PdfDate</CODE>-object
 */
    
    public PdfDate(Calendar d) {
        super();
        StringBuffer date = new StringBuffer("D:");
        date.append(setLength(d.get(Calendar.YEAR), 4));
        date.append(setLength(d.get(Calendar.MONTH) + 1, 2));
        date.append(setLength(d.get(Calendar.DATE), 2));
        date.append(setLength(d.get(Calendar.HOUR_OF_DAY), 2));
        date.append(setLength(d.get(Calendar.MINUTE), 2));
        date.append(setLength(d.get(Calendar.SECOND), 2));
        int timezone = (d.get(Calendar.ZONE_OFFSET) + d.get(Calendar.DST_OFFSET)) / (60 * 60 * 1000);
        if (timezone == 0) {
            date.append("Z");
        }
        else if (timezone < 0) {
            date.append("-");
            timezone = -timezone;
        }
        else {
            date.append("+");
        }
        if (timezone != 0) {
            date.append(setLength(timezone, 2)).append("'");
            int zone = Math.abs((d.get(Calendar.ZONE_OFFSET) + d.get(Calendar.DST_OFFSET)) / (60 * 1000)) - (timezone * 60);
            date.append(setLength(zone, 2)).append("'");
        }
        value = date.toString();
    }
    
/**
 * Constructs a <CODE>PdfDate</CODE>-object, representing the current day and time.
 */
    
    public PdfDate() {
        this(new GregorianCalendar());
    }
    
/**
 * Adds a number of leading zeros to a given <CODE>String</CODE> in order to get a <CODE>String</CODE>
 * of a certain length.
 *
 * @param		i   		a given number
 * @param		length		the length of the resulting <CODE>String</CODE>
 * @return		the resulting <CODE>String</CODE>
 */
    
    private String setLength(int i, int length) { // 1.3-1.4 problem fixed by Finn Bock
        StringBuffer tmp = new StringBuffer();
        tmp.append(i);
        while (tmp.length() < length) {
            tmp.insert(0, "0");
        }
        tmp.setLength(length);
        return tmp.toString();
    }
    
    /**
     * Gives the W3C format of the PdfDate.
     * @return a formatted date
     */
    public String getW3CDate() {
        return getW3CDate(value);
    }
    
    /**
     * Gives the W3C format of the PdfDate.
     * @param d
     * @return a formatted date
     */
    public static String getW3CDate(String d) {
    	SimpleDateFormat w3c = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    	Calendar c = decode(d);
		return w3c.format(c.getTime());
    }
    
    /**
     * Converts a PDF string representing a date into a Calendar.
     * @param s the PDF string representing a date
     * @return a <CODE>Calendar</CODE> representing the date or <CODE>null</CODE> if the string
     * was not a date
     */    
    public static Calendar decode(String s) {
        try {
            if (s.startsWith("D:"))
                s = s.substring(2);
            GregorianCalendar calendar;
            int slen = s.length();
            int idx = s.indexOf('Z');
            if (idx >= 0) {
                slen = idx;
                calendar = new GregorianCalendar(new SimpleTimeZone(0, "ZPDF"));
            }
            else {
                int sign = 1;
                idx = s.indexOf('+');
                if (idx < 0) {
                    idx = s.indexOf('-');
                    if (idx >= 0)
                        sign = -1;
                }
                if (idx < 0)
                    calendar = new GregorianCalendar();
                else {
                    int offset = Integer.parseInt(s.substring(idx + 1, idx + 3)) * 60;
                    if (idx + 5 < s.length())
                        offset += Integer.parseInt(s.substring(idx + 4, idx + 6));
                    calendar = new GregorianCalendar(new SimpleTimeZone(offset * sign * 60000, "ZPDF"));
                    slen = idx;
                }
            }
            calendar.clear();
            idx = 0;
            for (int k = 0; k < dateSpace.length; k += 3) {
                if (idx >= slen)
                    break;
                calendar.set(dateSpace[k], Integer.parseInt(s.substring(idx, idx + dateSpace[k + 1])) + dateSpace[k + 2]);
                idx += dateSpace[k + 1];
            }
            return calendar;
        }
        catch (Exception e) {
            return null;
        }
    }
}
