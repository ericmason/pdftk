package pdftk.org.bouncycastle.crypto.generators;

import java.math.BigInteger;

import pdftk.org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import pdftk.org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import pdftk.org.bouncycastle.crypto.KeyGenerationParameters;
import pdftk.org.bouncycastle.crypto.params.DHParameters;
import pdftk.org.bouncycastle.crypto.params.ElGamalKeyGenerationParameters;
import pdftk.org.bouncycastle.crypto.params.ElGamalParameters;
import pdftk.org.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
import pdftk.org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;

/**
 * a ElGamal key pair generator.
 * <p>
 * This generates keys consistent for use with ElGamal as described in
 * page 164 of "Handbook of Applied Cryptography".
 */
public class ElGamalKeyPairGenerator
    implements AsymmetricCipherKeyPairGenerator
{
    private ElGamalKeyGenerationParameters param;

    public void init(
        KeyGenerationParameters param)
    {
        this.param = (ElGamalKeyGenerationParameters)param;
    }

    public AsymmetricCipherKeyPair generateKeyPair()
    {
        DHKeyGeneratorHelper helper = DHKeyGeneratorHelper.INSTANCE;
        ElGamalParameters egp = param.getParameters();
        DHParameters dhp = new DHParameters(egp.getP(), egp.getG(), null, egp.getL());  

        BigInteger x = helper.calculatePrivate(dhp, param.getRandom()); 
        BigInteger y = helper.calculatePublic(dhp, x);

        return new AsymmetricCipherKeyPair(
            new ElGamalPublicKeyParameters(y, egp),
            new ElGamalPrivateKeyParameters(x, egp));
    }
}
