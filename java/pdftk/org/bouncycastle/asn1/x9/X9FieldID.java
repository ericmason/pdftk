package pdftk.org.bouncycastle.asn1.x9;

import java.math.BigInteger;

import pdftk.org.bouncycastle.asn1.ASN1EncodableVector;
import pdftk.org.bouncycastle.asn1.ASN1Integer;
import pdftk.org.bouncycastle.asn1.ASN1Object;
import pdftk.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import pdftk.org.bouncycastle.asn1.ASN1Primitive;
import pdftk.org.bouncycastle.asn1.ASN1Sequence;
import pdftk.org.bouncycastle.asn1.DERSequence;

/**
 * ASN.1 def for Elliptic-Curve Field ID structure. See
 * X9.62, for further details.
 */
public class X9FieldID
    extends ASN1Object
    implements X9ObjectIdentifiers
{
    private ASN1ObjectIdentifier     id;
    private ASN1Primitive parameters;

    /**
     * Constructor for elliptic curves over prime fields
     * <code>F<sub>2</sub></code>.
     * @param primeP The prime <code>p</code> defining the prime field.
     */
    public X9FieldID(BigInteger primeP)
    {
        this.id = prime_field;
        this.parameters = new ASN1Integer(primeP);
    }

    /**
     * Constructor for elliptic curves over binary fields
     * <code>F<sub>2<sup>m</sup></sub></code>.
     * @param m  The exponent <code>m</code> of
     * <code>F<sub>2<sup>m</sup></sub></code>.
     * @param k1 The integer <code>k1</code> where <code>x<sup>m</sup> +
     * x<sup>k3</sup> + x<sup>k2</sup> + x<sup>k1</sup> + 1</code>
     * represents the reduction polynomial <code>f(z)</code>.
     * @param k2 The integer <code>k2</code> where <code>x<sup>m</sup> +
     * x<sup>k3</sup> + x<sup>k2</sup> + x<sup>k1</sup> + 1</code>
     * represents the reduction polynomial <code>f(z)</code>.
     * @param k3 The integer <code>k3</code> where <code>x<sup>m</sup> +
     * x<sup>k3</sup> + x<sup>k2</sup> + x<sup>k1</sup> + 1</code>
     * represents the reduction polynomial <code>f(z)</code>..
     */
    public X9FieldID(int m, int k1, int k2, int k3)
    {
        this.id = characteristic_two_field;
        ASN1EncodableVector fieldIdParams = new ASN1EncodableVector();
        fieldIdParams.add(new ASN1Integer(m));
        
        if (k2 == 0) 
        {
            fieldIdParams.add(tpBasis);
            fieldIdParams.add(new ASN1Integer(k1));
        } 
        else 
        {
            fieldIdParams.add(ppBasis);
            ASN1EncodableVector pentanomialParams = new ASN1EncodableVector();
            pentanomialParams.add(new ASN1Integer(k1));
            pentanomialParams.add(new ASN1Integer(k2));
            pentanomialParams.add(new ASN1Integer(k3));
            fieldIdParams.add(new DERSequence(pentanomialParams));
        }
        
        this.parameters = new DERSequence(fieldIdParams);
    }

    public X9FieldID(
        ASN1Sequence  seq)
    {
        this.id = (ASN1ObjectIdentifier)seq.getObjectAt(0);
        this.parameters = (ASN1Primitive)seq.getObjectAt(1);
    }

    public ASN1ObjectIdentifier getIdentifier()
    {
        return id;
    }

    public ASN1Primitive getParameters()
    {
        return parameters;
    }

    /**
     * Produce a DER encoding of the following structure.
     * <pre>
     *  FieldID ::= SEQUENCE {
     *      fieldType       FIELD-ID.&amp;id({IOSet}),
     *      parameters      FIELD-ID.&amp;Type({IOSet}{&#64;fieldType})
     *  }
     * </pre>
     */
    public ASN1Primitive toASN1Primitive()
    {
        ASN1EncodableVector v = new ASN1EncodableVector();

        v.add(this.id);
        v.add(this.parameters);

        return new DERSequence(v);
    }
}
