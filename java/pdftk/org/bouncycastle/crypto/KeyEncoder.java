package pdftk.org.bouncycastle.crypto;

import pdftk.org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public interface KeyEncoder
{
    byte[] getEncoded(AsymmetricKeyParameter keyParameter);
}
