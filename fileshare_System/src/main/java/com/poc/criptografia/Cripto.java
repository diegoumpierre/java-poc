package com.poc.criptografia;

//import org.apache.commons.configuration2.FileBasedConfiguration;
//import org.apache.commons.configuration2.PropertiesConfiguration;
//import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
//import org.apache.commons.configuration2.builder.fluent.Parameters;
//import org.apache.commons.configuration2.ex.ConfigurationException;

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

public class Cripto {

    private static SecretKeySpec secretKey;

    private static byte[] key;

    public static void setKey(String myKey)
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

    public static String encrypt(String strToEncrypt, String secret)
    {
        try
        {
            setKey(secret);
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

    public static String decrypt(String strToDecrypt, String secret)
    {
        try
        {
            setKey(secret);
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


    public static String loadKeySecret() {

        String key = "";
//        Parameters params = new Parameters();
//        FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
//                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
//                        .configure(params.properties()
//                                .setFileName("application.yml"));
//        try {
//            org.apache.commons.configuration2.Configuration config = builder.getConfiguration();
//
//            key =  config.getString("encryption-key");
//
//        } catch (ConfigurationException cex) {
//            System.out.println(cex.getMessage());
//
//        }

        return key;
    }

}