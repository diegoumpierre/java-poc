package br.dev.guereguere.tools;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;



/**
 * Em Criptografia, o Advanced Encryption Standard (AES, ou Padrão de Criptografia Avançada, em português),
 * também conhecida pelo seu nome original Rijndael, é uma especificação para criptografia de dados eletrônicos
 * estabelecida pelo Instituto Nacional de Padrões e Tecnologia (NIST) dos EUA em 2001.[1]
 *
 * https://pt.wikipedia.org/wiki/Advanced_Encryption_Standard
 */


/**
 * final String secretKey = "sera_que_muda";
 *
 *             //String originalString = "howtodoinjava.com";
 *             //String encryptedString = AES.encrypt(originalString, secretKey) ;
 *             String decryptedString = AdvancedEncryptionStandardUtil.decrypt("V1EilOpc6UIu1ZFUC+l8LdOYGrvfsQAlcJdGS4B8BcM=", secretKey) ;
 *
 * //            System.out.println(originalString);
 * //            System.out.println(encryptedString);
 *             System.out.println(decryptedString);
 */
@Component
public class EncryptionStandardUtil {

    public EncryptionStandardUtil(){}



    @Value("${encryption-key}")
    private String myKey;

    private SecretKeySpec secretKey;

    private byte[] key;

    public void setKey()
    {
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public String encrypt(String strToEncrypt)
    {
        try
        {
            setKey();
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public String decrypt(String strToDecrypt)
    {
        try
        {
            setKey();
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        }
        catch (Exception e)
        {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

}