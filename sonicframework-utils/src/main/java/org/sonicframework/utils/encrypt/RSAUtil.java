package org.sonicframework.utils.encrypt;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.sonicframework.utils.StreamUtil;

import org.sonicframework.context.exception.EncryptException;

/**
* @author lujunyi
*/
public class RSAUtil {

	private RSAUtil() {}
	
	/**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;
    /**
     * 编码
     */
    private static String charset = "utf-8";
    
    /**
     * 获取密钥对
     *
     * @return 密钥对
     */
    public static KeyPair getKeyPair() {
        KeyPairGenerator generator;
		try {
			generator = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			throw new EncryptException("获取秘钥对失败", e);
		}
        generator.initialize(1024);
        return generator.generateKeyPair();
    }
    
    /**
     * 获取私钥
     *
     * @param PRIVATE_KEY 私钥字符串
     * @return
     */
    public static PrivateKey getPrivateKey(String privateKey) {
        try {
        	KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] decodedKey = Base64.decodeBase64(privateKey.getBytes(charset));
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
			return keyFactory.generatePrivate(keySpec);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
			throw new EncryptException("转换私钥失败", e);
		}
    }

    /**
     * 获取公钥
     *
     * @param PUBLIC_KEY 公钥字符串
     * @return
     */
    public static PublicKey getPublicKey(String publicKey) {
    	try {
    		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] decodedKey = Base64.decodeBase64(publicKey.getBytes(charset));
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
            return keyFactory.generatePublic(keySpec);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
			throw new EncryptException("转换私钥失败", e);
		}
    }
    
    /**
     * RSA加密
     *
     * @param data 待加密数据
     * @param publicKey 公钥
     * @return
     */
    public static String encrypt(String data, Key publicKey) {
    	ByteArrayOutputStream out = null;
    	try {
    		byte[] bytes = data.getBytes(charset);
	        return encrypt(bytes, publicKey);
		} catch (UnsupportedEncodingException e) {
			throw new EncryptException("加密失败", e);
		} finally {
			StreamUtil.close(out);
		}
        
    }
    public static String encrypt(byte[] data, Key publicKey) {
    	ByteArrayOutputStream out = null;
    	try {
    		Cipher cipher = Cipher.getInstance("RSA");
    		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
    		int inputLen = data.length;
    		out = new ByteArrayOutputStream();
    		int offset = 0;
    		byte[] cache;
    		int i = 0;
    		// 对数据分段加密
    		while (inputLen - offset > 0) {
    			if (inputLen - offset > MAX_ENCRYPT_BLOCK) {
    				cache = cipher.doFinal(data, offset, MAX_ENCRYPT_BLOCK);
    			} else {
    				cache = cipher.doFinal(data, offset, inputLen - offset);
    			}
    			out.write(cache, 0, cache.length);
    			i++;
    			offset = i * MAX_ENCRYPT_BLOCK;
    		}
    		byte[] encryptedData = out.toByteArray();
    		// 获取加密内容使用base64进行编码,并以UTF-8为标准转化成字符串
    		// 加密后的字符串
    		return Base64.encodeBase64String(encryptedData);
    	} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
    		throw new EncryptException("加密失败", e);
    	} finally {
    		StreamUtil.close(out);
    	}
    	
    }
    
    /**
     * RSA解密
     *
     * @param data 待解密数据
     * @param privateKey 私钥
     * @return
     */
    public static String decrypt(String data, Key privateKey) {
    	return decrypt(Base64.decodeBase64(data), privateKey);
    }
    public static byte[] decryptReturnBytes(String data, Key privateKey) {
    	return decryptReturnBytes(Base64.decodeBase64(data), privateKey);
    }
    public static byte[] decryptReturnBytes(byte[] dataBytes, Key privateKey) {
    	ByteArrayOutputStream out = null;
    	try {
    		Cipher cipher = Cipher.getInstance("RSA");
    		cipher.init(Cipher.DECRYPT_MODE, privateKey);
    		int inputLen = dataBytes.length;
    		out = new ByteArrayOutputStream();
    		int offset = 0;
    		byte[] cache;
    		int i = 0;
    		// 对数据分段解密
    		while (inputLen - offset > 0) {
    			if (inputLen - offset > MAX_DECRYPT_BLOCK) {
    				cache = cipher.doFinal(dataBytes, offset, MAX_DECRYPT_BLOCK);
    			} else {
    				cache = cipher.doFinal(dataBytes, offset, inputLen - offset);
    			}
    			out.write(cache, 0, cache.length);
    			i++;
    			offset = i * MAX_DECRYPT_BLOCK;
    		}
    		byte[] decryptedData = out.toByteArray();
    		return decryptedData;
    	} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
    		throw new EncryptException("解密失败", e);
    	} finally {
    		StreamUtil.close(out);
    	}
    	
    }
    public static String decrypt(byte[] dataBytes, Key privateKey) {
    	
    	try {
    		byte[] decryptedData = decryptReturnBytes(dataBytes, privateKey);
    		// 解密后的内容
    		return new String(decryptedData, "UTF-8");
    	} catch (UnsupportedEncodingException e) {
    		throw new EncryptException("解密失败", e);
    	} 
    	
    }
    
    /**
     * 签名
     *
     * @param data 待签名数据
     * @param privateKey 私钥
     * @return 签名
     */
    public static String sign(String data, PrivateKey privateKey) {
    	try {
    		byte[] keyBytes = privateKey.getEncoded();
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey key = keyFactory.generatePrivate(keySpec);
            Signature signature = Signature.getInstance("MD5withRSA");
            signature.initSign(key);
            signature.update(data.getBytes(charset));
            return new String(Base64.encodeBase64(signature.sign()),charset);
		} catch (Exception e) {
			throw new EncryptException("签名失败", e);
		}
        
    }

    /**
     * 验签
     *
     * @param srcData 原始字符串
     * @param publicKey 公钥
     * @param sign 签名
     * @return 是否验签通过
     */
    public static boolean verify(String srcData, PublicKey publicKey, String sign){
        try {
        	byte[] keyBytes = publicKey.getEncoded();
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey key = keyFactory.generatePublic(keySpec);
            Signature signature = Signature.getInstance("MD5withRSA");
            signature.initVerify(key);
            signature.update(srcData.getBytes(charset));
            return signature.verify(Base64.decodeBase64(sign.getBytes(charset)));
		} catch (Exception e) {
			throw new EncryptException("验签失败", e);
		}
    }

}
