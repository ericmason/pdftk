package pdftk.org.bouncycastle.crypto.tls;

import java.security.SecureRandom;

import pdftk.org.bouncycastle.crypto.CryptoException;
import pdftk.org.bouncycastle.crypto.Signer;
import pdftk.org.bouncycastle.crypto.params.AsymmetricKeyParameter;

interface TlsSigner
{
    byte[] calculateRawSignature(SecureRandom random, AsymmetricKeyParameter privateKey, byte[] md5andsha1)
        throws CryptoException;

    Signer createVerifyer(AsymmetricKeyParameter publicKey);

    boolean isValidPublicKey(AsymmetricKeyParameter publicKey);
}
