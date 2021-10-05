package pdftk.org.bouncycastle.asn1.x509;

import pdftk.org.bouncycastle.asn1.ASN1Object;
import pdftk.org.bouncycastle.asn1.ASN1Primitive;
import pdftk.org.bouncycastle.asn1.ASN1Sequence;
import pdftk.org.bouncycastle.asn1.ASN1TaggedObject;
import pdftk.org.bouncycastle.asn1.DERSequence;

public class CertificatePolicies
    extends ASN1Object
{
    private final PolicyInformation[] policyInformation;

    public static CertificatePolicies getInstance(
        Object  obj)
    {
        if (obj instanceof CertificatePolicies)
        {
            return (CertificatePolicies)obj;
        }

        if (obj != null)
        {
            return new CertificatePolicies(ASN1Sequence.getInstance(obj));
        }

        return null;
    }

    public static CertificatePolicies getInstance(
        ASN1TaggedObject obj,
        boolean          explicit)
    {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    /**
     * Construct a CertificatePolicies object containing one PolicyInformation.
     * 
     * @param name the name to be contained.
     */
    public CertificatePolicies(
        PolicyInformation  name)
    {
        this.policyInformation = new PolicyInformation[] { name };
    }

    public CertificatePolicies(
        PolicyInformation[] policyInformation)
    {
        this.policyInformation = policyInformation;
    }

    private CertificatePolicies(
        ASN1Sequence  seq)
    {
        this.policyInformation = new PolicyInformation[seq.size()];

        for (int i = 0; i != seq.size(); i++)
        {
            policyInformation[i] = PolicyInformation.getInstance(seq.getObjectAt(i));
        }
    }

    public PolicyInformation[] getPolicyInformation()
    {
        PolicyInformation[] tmp = new PolicyInformation[policyInformation.length];

        System.arraycopy(policyInformation, 0, tmp, 0, policyInformation.length);

        return tmp;
    }

    /**
     * Produce an object suitable for an ASN1OutputStream.
     * <pre>
     * CertificatePolicies ::= SEQUENCE SIZE {1..MAX} OF PolicyInformation
     * </pre>
     */
    public ASN1Primitive toASN1Primitive()
    {
        return new DERSequence(policyInformation);
    }

    public String toString()
    {
        String p = null;
        for (int i = 0; i < policyInformation.length; i++)
        {
            if (p != null)
            {
                p += ", ";
            }
            p += policyInformation[i];
        }

        return "CertificatePolicies: " + p;
    }
}
