package pdftk.org.bouncycastle.crypto.generators;

import pdftk.org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import pdftk.org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import pdftk.org.bouncycastle.crypto.EphemeralKeyPair;
import pdftk.org.bouncycastle.crypto.KeyEncoder;

public class EphemeralKeyPairGenerator
{
    private AsymmetricCipherKeyPairGenerator gen;
    private KeyEncoder keyEncoder;

    public EphemeralKeyPairGenerator(AsymmetricCipherKeyPairGenerator gen, KeyEncoder keyEncoder)
    {
        this.gen = gen;
        this.keyEncoder = keyEncoder;
    }

    public EphemeralKeyPair generate()
    {
        AsymmetricCipherKeyPair eph = gen.generateKeyPair();

        // Encode the ephemeral public key
     	return new EphemeralKeyPair(eph, keyEncoder);
    }
}
