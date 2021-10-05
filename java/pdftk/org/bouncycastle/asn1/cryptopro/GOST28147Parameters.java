package pdftk.org.bouncycastle.asn1.cryptopro;

import java.util.Enumeration;

import pdftk.org.bouncycastle.asn1.ASN1EncodableVector;
import pdftk.org.bouncycastle.asn1.ASN1Object;
import pdftk.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import pdftk.org.bouncycastle.asn1.ASN1OctetString;
import pdftk.org.bouncycastle.asn1.ASN1Primitive;
import pdftk.org.bouncycastle.asn1.ASN1Sequence;
import pdftk.org.bouncycastle.asn1.ASN1TaggedObject;
import pdftk.org.bouncycastle.asn1.DERSequence;

public class GOST28147Parameters
    extends ASN1Object
{
    ASN1OctetString iv;
    ASN1ObjectIdentifier paramSet;

    public static GOST28147Parameters getInstance(
        ASN1TaggedObject obj,
        boolean          explicit)
    {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static GOST28147Parameters getInstance(
        Object obj)
    {
        if(obj == null || obj instanceof GOST28147Parameters)
        {
            return (GOST28147Parameters)obj;
        }

        if(obj instanceof ASN1Sequence)
        {
            return new GOST28147Parameters((ASN1Sequence)obj);
        }

        throw new IllegalArgumentException("Invalid GOST3410Parameter: " + obj.getClass().getName());
    }

    public GOST28147Parameters(
        ASN1Sequence  seq)
    {
        Enumeration     e = seq.getObjects();

        iv = (ASN1OctetString)e.nextElement();
        paramSet = (ASN1ObjectIdentifier)e.nextElement();
    }

    /**
     * <pre>
     * Gost28147-89-Parameters ::=
     *               SEQUENCE {
     *                       iv                   Gost28147-89-IV,
     *                       encryptionParamSet   OBJECT IDENTIFIER
     *                }
     *
     *   Gost28147-89-IV ::= OCTET STRING (SIZE (8))
     * </pre>
     */
    public ASN1Primitive toASN1Primitive()
    {
        ASN1EncodableVector  v = new ASN1EncodableVector();

        v.add(iv);
        v.add(paramSet);

        return new DERSequence(v);
    }
}
