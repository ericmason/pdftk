package pdftk.org.bouncycastle.asn1.cms;

import pdftk.org.bouncycastle.asn1.ASN1EncodableVector;
import pdftk.org.bouncycastle.asn1.ASN1Object;
import pdftk.org.bouncycastle.asn1.ASN1Primitive;
import pdftk.org.bouncycastle.asn1.ASN1Set;
import pdftk.org.bouncycastle.asn1.BERSet;

public class Attributes
    extends ASN1Object
{
    private ASN1Set attributes;

    private Attributes(ASN1Set set)
    {
        attributes = set;
    }

    public Attributes(ASN1EncodableVector v)
    {
        attributes = new BERSet(v);
    }

    public static Attributes getInstance(Object obj)
    {
        if (obj instanceof Attributes)
        {
            return (Attributes)obj;
        }
        else if (obj != null)
        {
            return new Attributes(ASN1Set.getInstance(obj));
        }

        return null;
    }

    /**
     * <pre>
     * Attributes ::=
     *   SET SIZE(1..MAX) OF Attribute -- according to RFC 5652
     * </pre>
     * @return
     */
    public ASN1Primitive toASN1Primitive()
    {
        return attributes;
    }
}
