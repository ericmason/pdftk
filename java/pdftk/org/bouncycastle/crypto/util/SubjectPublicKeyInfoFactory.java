package pdftk.org.bouncycastle.crypto.util;

import java.io.IOException;

import pdftk.org.bouncycastle.asn1.ASN1Integer;
import pdftk.org.bouncycastle.asn1.DERNull;
import pdftk.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import pdftk.org.bouncycastle.asn1.pkcs.RSAPublicKey;
import pdftk.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import pdftk.org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import pdftk.org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import pdftk.org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import pdftk.org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import pdftk.org.bouncycastle.crypto.params.RSAKeyParameters;

/**
 * Factory to create ASN.1 subject public key info objects from lightweight public keys.
 */
public class SubjectPublicKeyInfoFactory
{
    /**
     * Create a SubjectPublicKeyInfo public key.
     *
     * @param publicKey the SubjectPublicKeyInfo encoding
     * @return the appropriate key parameter
     * @throws java.io.IOException on an error encoding the key
     */
    public static SubjectPublicKeyInfo createSubjectPublicKeyInfo(AsymmetricKeyParameter publicKey) throws IOException
    {
        if (publicKey instanceof RSAKeyParameters)
        {
            RSAKeyParameters pub = (RSAKeyParameters)publicKey;

            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE), new RSAPublicKey(pub.getModulus(), pub.getExponent()));
        }
        else if (publicKey instanceof DSAPublicKeyParameters)
        {
            DSAPublicKeyParameters pub = (DSAPublicKeyParameters)publicKey;

            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa), new ASN1Integer(pub.getY()));
        }
        else
        {
            throw new IOException("key parameters not recognised.");
        }
    }
}
