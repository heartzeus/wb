package com.tuhanbao.io.impl.encipherUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.tuhanbao.util.exception.MyException;

public class AESEncipherTool implements IEncipherTool
{
    private static final String ALG = "AES/ECB/PKCS5Padding";
    
    private static final String KEY = "AES";
    
    private SecretKeySpec key;
    
    private Cipher decodeCipher, encodeCipher;
    
	private AESEncipherTool(byte[] password)
    {
		key = new SecretKeySpec(password, KEY);
		try {
			decodeCipher = Cipher.getInstance(ALG);
			decodeCipher.init(Cipher.DECRYPT_MODE, key);
			encodeCipher = Cipher.getInstance(ALG);
			encodeCipher.init(Cipher.ENCRYPT_MODE, key);
		} catch (Exception e) {
			throw new MyException(-1, "init aes encipher fail");
		}
    }
    
    public static AESEncipherTool getAESEncipherTool(byte[] password) {
    	return new AESEncipherTool(password);
    }
    
    @Override
    public byte[] decrypt(byte[] bytes)
    {
        try {
			return decodeCipher.doFinal(bytes);
		} catch (Exception e) {
			throw MyException.getMyException(e);
		}
    }

    @Override
    public byte[] encrypt(byte[] bytes)
    {
    	try {
			return encodeCipher.doFinal(bytes);
		} catch (Exception e) {
			throw MyException.getMyException(e);
		}
    }
}
