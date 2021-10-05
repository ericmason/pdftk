package pdftk.org.bouncycastle.crypto.agreement.kdf;

import pdftk.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import pdftk.org.bouncycastle.asn1.DERObjectIdentifier;
import pdftk.org.bouncycastle.crypto.DerivationParameters;

public class DHKDFParameters
    implements DerivationParameters
{
    private ASN1ObjectIdentifier algorithm;
    private int keySize;
    private byte[] z;
    private byte[] extraInfo;

    public DHKDFParameters(
        DERObjectIdentifier algorithm,
        int keySize,
        byte[] z)
    {
        this(algorithm, keySize, z, null);
    }

    public DHKDFParameters(
        DERObjectIdentifier algorithm,
        int keySize,
        byte[] z,
        byte[] extraInfo)
    {
        this.algorithm = new ASN1ObjectIdentifier(algorithm.getId());
        this.keySize = keySize;
        this.z = z;
        this.extraInfo = extraInfo;
    }

    public ASN1ObjectIdentifier getAlgorithm()
    {
        return algorithm;
    }

    public int getKeySize()
    {
        return keySize;
    }

    public byte[] getZ()
    {
        return z;
    }

    public byte[] getExtraInfo()
    {
        return extraInfo;
    }
}
