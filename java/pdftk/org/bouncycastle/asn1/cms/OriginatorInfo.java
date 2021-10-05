package pdftk.org.bouncycastle.asn1.cms;

import pdftk.org.bouncycastle.asn1.ASN1EncodableVector;
import pdftk.org.bouncycastle.asn1.ASN1Object;
import pdftk.org.bouncycastle.asn1.ASN1Primitive;
import pdftk.org.bouncycastle.asn1.ASN1Sequence;
import pdftk.org.bouncycastle.asn1.ASN1Set;
import pdftk.org.bouncycastle.asn1.ASN1TaggedObject;
import pdftk.org.bouncycastle.asn1.DERSequence;
import pdftk.org.bouncycastle.asn1.DERTaggedObject;

public class OriginatorInfo
    extends ASN1Object
{
    private ASN1Set certs;
    private ASN1Set crls;
    
    public OriginatorInfo(
        ASN1Set certs,
        ASN1Set crls)
    {
        this.certs = certs;
        this.crls = crls;
    }
    
    private OriginatorInfo(
        ASN1Sequence seq)
    {
        switch (seq.size())
        {
        case 0:     // empty
            break;
        case 1:
            ASN1TaggedObject o = (ASN1TaggedObject)seq.getObjectAt(0);
            switch (o.getTagNo())
            {
            case 0 :
                certs = ASN1Set.getInstance(o, false);
                break;
            case 1 :
                crls = ASN1Set.getInstance(o, false);
                break;
            default:
                throw new IllegalArgumentException("Bad tag in OriginatorInfo: " + o.getTagNo());
            }
            break;
        case 2:
            certs = ASN1Set.getInstance((ASN1TaggedObject)seq.getObjectAt(0), false);
            crls  = ASN1Set.getInstance((ASN1TaggedObject)seq.getObjectAt(1), false);
            break;
        default:
            throw new IllegalArgumentException("OriginatorInfo too big");
        }
    }
    
    /**
     * return an OriginatorInfo object from a tagged object.
     *
     * @param obj the tagged object holding the object we want.
     * @param explicit true if the object is meant to be explicitly
     *              tagged false otherwise.
     * @exception IllegalArgumentException if the object held by the
     *          tagged object cannot be converted.
     */
    public static OriginatorInfo getInstance(
        ASN1TaggedObject    obj,
        boolean             explicit)
    {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }
    
    /**
     * return an OriginatorInfo object from the given object.
     *
     * @param obj the object we want converted.
     * @exception IllegalArgumentException if the object cannot be converted.
     */
    public static OriginatorInfo getInstance(
        Object obj)
    {
        if (obj instanceof OriginatorInfo)
        {
            return (OriginatorInfo)obj;
        }
        else if (obj != null)
        {
            return new OriginatorInfo(ASN1Sequence.getInstance(obj));
        }
        
        return null;
    }
    
    public ASN1Set getCertificates()
    {
        return certs;
    }

    public ASN1Set getCRLs()
    {
        return crls;
    }

    /** 
     * Produce an object suitable for an ASN1OutputStream.
     * <pre>
     * OriginatorInfo ::= SEQUENCE {
     *     certs [0] IMPLICIT CertificateSet OPTIONAL,
     *     crls [1] IMPLICIT CertificateRevocationLists OPTIONAL 
     * }
     * </pre>
     */
    public ASN1Primitive toASN1Primitive()
    {
        ASN1EncodableVector  v = new ASN1EncodableVector();

        if (certs != null)
        {
            v.add(new DERTaggedObject(false, 0, certs));
        }
        
        if (crls != null)
        {
            v.add(new DERTaggedObject(false, 1, crls));
        }
        
        return new DERSequence(v);
    }
}
