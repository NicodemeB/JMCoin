package com.jmcoin.crypto;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import com.jmcoin.model.Transaction;
import com.jmcoin.util.BytesUtil;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.util.encoders.Hex;

public abstract class SignaturesVerification {
	
	
	public static byte[] signTransaction(Transaction tr, PrivateKey privKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, FileNotFoundException, IOException, SignatureException
    {
        Signature dsa = Signature.getInstance("SHA1withDSA", "SUN"); 
        dsa.initSign(privKey);
        byte[] bytesTr = BytesUtil.toByteArray(tr);
        BufferedInputStream bufIn = new BufferedInputStream(new ByteArrayInputStream(bytesTr));
        byte[] buffer = new byte[1024];
        int len;
        while ((len = bufIn.read(buffer)) >= 0) {
            dsa.update(buffer, 0, len);
        }
        bufIn.close();
        return dsa.sign();
    }
	
    public static boolean verifyTransaction(byte[] signature, Transaction tr, PublicKey pubKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IOException, SignatureException
    {
        boolean verifies = false;
        if(signature == null || tr == null || pubKey == null){
        	return false;
        }
        else{
            Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
            sig.initVerify(pubKey);
            byte[] bytesTr = BytesUtil.toByteArray(tr);
            BufferedInputStream bufIn = new BufferedInputStream(new ByteArrayInputStream(bytesTr));
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bufIn.read(buffer)) >= 0) {
                sig.update(buffer, 0, len);
            };
            bufIn.close();
            verifies = sig.verify(signature);
        }
        return verifies; 
    }
    
    public static String DeriveJMAddressFromPubKey(PublicKey pubKey)
    {
        RIPEMD160Digest dgst = new RIPEMD160Digest();
        byte[] key = pubKey.getEncoded();
        dgst.update(key, 0, key.length);
        byte[] bytes = new byte[dgst.getDigestSize()];
        dgst.doFinal(bytes, 0);
        return new String(Hex.encode(bytes));
    }
   
}
