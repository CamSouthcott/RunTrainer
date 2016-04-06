package com.camsouthcott.runtrainer.security;

import android.content.Context;
import android.util.Base64;

import com.camsouthcott.runtrainer.R;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by Cam Southcott on 3/25/2016.
 */
public class EncryptionProvider {

    private PublicKey serverPublicKey;
    private Cipher cipher;

    public EncryptionProvider(Context context) throws EncryptionProviderInitializationException{

        setServerPublicKey(context);

        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionProviderInitializationException();
        } catch (NoSuchPaddingException e) {
            throw new EncryptionProviderInitializationException();
        }
    }

    public byte[] encrypt(byte[] message) throws EncryptionFailureException{

        if(serverPublicKey != null && cipher != null){

            try {
                cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
                return cipher.doFinal(message);

            } catch (IllegalBlockSizeException e) {
                throw new EncryptionFailureException();
            } catch (BadPaddingException e) {
                throw new EncryptionFailureException();
            } catch (InvalidKeyException e) {
                throw new EncryptionFailureException();
            }
        }

        return null;
    }

    private void setServerPublicKey(Context context)throws EncryptionProviderInitializationException{

        try {
            String serverPublicKeyBase64 = context.getString(R.string.server_public_key);
            byte[] publicKeyEncodingBytes = Base64.decode(serverPublicKeyBase64.getBytes(),Base64.DEFAULT);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyEncodingBytes);

            serverPublicKey = (PublicKey) KeyFactory.getInstance("RSA").generatePublic(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new EncryptionProviderInitializationException();
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionProviderInitializationException();
        }
    }
}
