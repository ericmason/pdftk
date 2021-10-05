package pdftk.org.bouncycastle.asn1.x509;

import java.util.Enumeration;

import pdftk.org.bouncycastle.asn1.ASN1EncodableVector;
import pdftk.org.bouncycastle.asn1.ASN1Object;
import pdftk.org.bouncycastle.asn1.ASN1Primitive;
import pdftk.org.bouncycastle.asn1.ASN1Sequence;
import pdftk.org.bouncycastle.asn1.ASN1TaggedObject;
import pdftk.org.bouncycastle.asn1.DERGeneralizedTime;
import pdftk.org.bouncycastle.asn1.DERSequence;
import pdftk.org.bouncycastle.asn1.DERTaggedObject;

/**
 * <pre>
 *    PrivateKeyUsagePeriod ::= SEQUENCE {
 *      notBefore       [0]     GeneralizedTime OPTIONAL,
 *      notAfter        [1]     GeneralizedTime OPTIONAL }
 * </pre>
 */
public class PrivateKeyUsagePeriod
    extends ASN1Object
{
    public static PrivateKeyUsagePeriod getInstance(Object obj)
    {
        if (obj instanceof PrivateKeyUsagePeriod)
        {
            return (PrivateKeyUsagePeriod)obj;
        }

        if (obj != null)
        {
            return new PrivateKeyUsagePeriod(ASN1Sequence.getInstance(obj));
        }

        return null;
    }

    private DERGeneralizedTime _notBefore, _notAfter;

    private PrivateKeyUsagePeriod(ASN1Sequence seq)
    {
        Enumeration en = seq.getObjects();
        while (en.hasMoreElements())
        {
            ASN1TaggedObject tObj = (ASN1TaggedObject)en.nextElement();

            if (tObj.getTagNo() == 0)
            {
                _notBefore = DERGeneralizedTime.getInstance(tObj, false);
            }
            else if (tObj.getTagNo() == 1)
            {
                _notAfter = DERGeneralizedTime.getInstance(tObj, false);
            }
        }
    }

    public DERGeneralizedTime getNotBefore()
    {
        return _notBefore;
    }

    public DERGeneralizedTime getNotAfter()
    {
        return _notAfter;
    }

    public ASN1Primitive toASN1Primitive()
    {
        ASN1EncodableVector v = new ASN1EncodableVector();

        if (_notBefore != null)
        {
            v.add(new DERTaggedObject(false, 0, _notBefore));
        }
        if (_notAfter != null)
        {
            v.add(new DERTaggedObject(false, 1, _notAfter));
        }

        return new DERSequence(v);
    }
}
