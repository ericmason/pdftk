package pdftk.org.bouncycastle.crypto.params;

import pdftk.org.bouncycastle.crypto.CipherParameters;

public class AsymmetricKeyParameter
    implements CipherParameters
{
    boolean privateKey;

    public AsymmetricKeyParameter(
        boolean privateKey)
    {
        this.privateKey = privateKey;
    }

    public boolean isPrivate()
    {
        return privateKey;
    }
}
