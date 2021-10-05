package pdftk.org.bouncycastle.asn1.pkcs;

import pdftk.org.bouncycastle.asn1.ASN1EncodableVector;
import pdftk.org.bouncycastle.asn1.ASN1Integer;
import pdftk.org.bouncycastle.asn1.ASN1Object;
import pdftk.org.bouncycastle.asn1.ASN1Primitive;
import pdftk.org.bouncycastle.asn1.ASN1Sequence;
import pdftk.org.bouncycastle.asn1.ASN1Set;
import pdftk.org.bouncycastle.asn1.DERSequence;
import pdftk.org.bouncycastle.asn1.DERTaggedObject;
import pdftk.org.bouncycastle.asn1.x500.X500Name;
import pdftk.org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import pdftk.org.bouncycastle.asn1.x509.X509Name;

/**
 * PKCS10 CertificationRequestInfo object.
 * <pre>
 *  CertificationRequestInfo ::= SEQUENCE {
 *   version             INTEGER { v1(0) } (v1,...),
 *   subject             Name,
 *   subjectPKInfo   SubjectPublicKeyInfo{{ PKInfoAlgorithms }},
 *   attributes          [0] Attributes{{ CRIAttributes }}
 *  }
 *
 *  Attributes { ATTRIBUTE:IOSet } ::= SET OF Attribute{{ IOSet }}
 *
 *  Attribute { ATTRIBUTE:IOSet } ::= SEQUENCE {
 *    type    ATTRIBUTE.&id({IOSet}),
 *    values  SET SIZE(1..MAX) OF ATTRIBUTE.&Type({IOSet}{\@type})
 *  }
 * </pre>
 */
public class CertificationRequestInfo
    extends ASN1Object
{
    ASN1Integer              version = new ASN1Integer(0);
    X500Name                subject;
    SubjectPublicKeyInfo    subjectPKInfo;
    ASN1Set                 attributes = null;

    public static CertificationRequestInfo getInstance(
        Object  obj)
    {
        if (obj instanceof CertificationRequestInfo)
        {
            return (CertificationRequestInfo)obj;
        }
        else if (obj != null)
        {
            return new CertificationRequestInfo(ASN1Sequence.getInstance(obj));
        }

        return null;
    }

    public CertificationRequestInfo(
        X500Name subject,
        SubjectPublicKeyInfo    pkInfo,
        ASN1Set                 attributes)
    {
        this.subject = subject;
        this.subjectPKInfo = pkInfo;
        this.attributes = attributes;

        if ((subject == null) || (version == null) || (subjectPKInfo == null))
        {
            throw new IllegalArgumentException("Not all mandatory fields set in CertificationRequestInfo generator.");
        }
    }

    /**
     * @deprecated use X500Name method.
     */
    public CertificationRequestInfo(
        X509Name                subject,
        SubjectPublicKeyInfo    pkInfo,
        ASN1Set                 attributes)
    {
        this.subject = X500Name.getInstance(subject.toASN1Primitive());
        this.subjectPKInfo = pkInfo;
        this.attributes = attributes;

        if ((subject == null) || (version == null) || (subjectPKInfo == null))
        {
            throw new IllegalArgumentException("Not all mandatory fields set in CertificationRequestInfo generator.");
        }
    }

    public CertificationRequestInfo(
        ASN1Sequence  seq)
    {
        version = (ASN1Integer)seq.getObjectAt(0);

        subject = X500Name.getInstance(seq.getObjectAt(1));
        subjectPKInfo = SubjectPublicKeyInfo.getInstance(seq.getObjectAt(2));

        //
        // some CertificationRequestInfo objects seem to treat this field
        // as optional.
        //
        if (seq.size() > 3)
        {
            DERTaggedObject tagobj = (DERTaggedObject)seq.getObjectAt(3);
            attributes = ASN1Set.getInstance(tagobj, false);
        }

        if ((subject == null) || (version == null) || (subjectPKInfo == null))
        {
            throw new IllegalArgumentException("Not all mandatory fields set in CertificationRequestInfo generator.");
        }
    }

    public ASN1Integer getVersion()
    {
        return version;
    }

    public X500Name getSubject()
    {
        return subject;
    }

    public SubjectPublicKeyInfo getSubjectPublicKeyInfo()
    {
        return subjectPKInfo;
    }

    public ASN1Set getAttributes()
    {
        return attributes;
    }

    public ASN1Primitive toASN1Primitive()
    {
        ASN1EncodableVector  v = new ASN1EncodableVector();

        v.add(version);
        v.add(subject);
        v.add(subjectPKInfo);

        if (attributes != null)
        {
            v.add(new DERTaggedObject(false, 0, attributes));
        }

        return new DERSequence(v);
    }
}
