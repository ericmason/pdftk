package pdftk.org.bouncycastle.asn1.oiw;

import java.math.BigInteger;
import java.util.Enumeration;

import pdftk.org.bouncycastle.asn1.ASN1EncodableVector;
import pdftk.org.bouncycastle.asn1.ASN1Integer;
import pdftk.org.bouncycastle.asn1.ASN1Object;
import pdftk.org.bouncycastle.asn1.ASN1Primitive;
import pdftk.org.bouncycastle.asn1.ASN1Sequence;
import pdftk.org.bouncycastle.asn1.DERSequence;

public class ElGamalParameter
    extends ASN1Object
{
    ASN1Integer      p, g;

    public ElGamalParameter(
        BigInteger  p,
        BigInteger  g)
    {
        this.p = new ASN1Integer(p);
        this.g = new ASN1Integer(g);
    }

    public ElGamalParameter(
        ASN1Sequence  seq)
    {
        Enumeration     e = seq.getObjects();

        p = (ASN1Integer)e.nextElement();
        g = (ASN1Integer)e.nextElement();
    }

    public BigInteger getP()
    {
        return p.getPositiveValue();
    }

    public BigInteger getG()
    {
        return g.getPositiveValue();
    }

    public ASN1Primitive toASN1Primitive()
    {
        ASN1EncodableVector  v = new ASN1EncodableVector();

        v.add(p);
        v.add(g);

        return new DERSequence(v);
    }
}
