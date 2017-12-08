package com.jmcoin.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.Random;
import com.google.gson.Gson;
import com.jmcoin.crypto.AES;
import com.jmcoin.crypto.AES.InvalidAESStreamException;
import com.jmcoin.crypto.AES.InvalidKeyLengthException;
import com.jmcoin.crypto.AES.InvalidPasswordException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.crypto.SignaturesVerification;
import com.jmcoin.model.Block;
import com.jmcoin.model.Chain;
import com.jmcoin.model.Input;
import com.jmcoin.model.KeyGenerator;
import com.jmcoin.model.Output;
import com.jmcoin.model.Transaction;

public class TestBlockValidation {
	
	private static HashMap<PrivateKey, PublicKey> keys = new HashMap<>();
	
	/**
	 * Validates the {@link Transaction}
	 * Iterates over {@link Input} and {@link Output} and validates
	 * @param chain
	 * @param trans
	 * @return
	 * @throws IOException 
	 * @throws SignatureException 
	 * @throws NoSuchProviderException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws StrongEncryptionNotAvailableException 
	 * @throws InvalidAESStreamException 
	 * @throws InvalidPasswordException 
	 * @throws InvalidKeySpecException 
	 */
	
	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, IOException, InvalidKeyLengthException, StrongEncryptionNotAvailableException {
		createKeys("connard");
		createKeys("connasse");
		createKeys("test");
		
		PrivateKey keyConnard = keys.keySet().iterator().next();
		PrivateKey keyConnasse = keys.keySet().iterator().next();
		PrivateKey keyTest = keys.keySet().iterator().next();
		
		//Chain chain = new Chain();
		List<Block> blocks = new ArrayList<>();
		
				
		//genesis
		Block genesis = new Block();
		Input inGenesis = new Input();
		inGenesis.setPrevTransactionHash(null);
		Output outGenesis = new Output();
		outGenesis.setAmount(42);
		outGenesis.setAddress(SignaturesVerification.DeriveJMAddressFromPubKey(keys.get(keyConnard).getEncoded()));
		Output outGenesisBack = new Output();
		outGenesisBack.setAmount(0);
		outGenesisBack.setAddress(null);
		Transaction transGenesis = new Transaction();
		transGenesis.setOutputBack(outGenesisBack);
		transGenesis.setOutputOut(outGenesis);
		transGenesis.addInput(inGenesis);
		transGenesis.setPubKey(keys.get(keyConnasse).getEncoded());
		genesis.getTransactions().add(transGenesis);
//		transGenesis.setSignature(SignaturesVerification.);
		genesis.setPrevHash(null);

		Input input1 = new Input();
		input1.setAmount(outGenesis);
		Input input2 = new Input();
		input2.setAmount(outGenesis);
		Output outputOut = new Output();
		outputOut.setAmount(80);
		Output outputBack = new Output();
		outputBack.setAmount(15);
		Transaction transactionToverify = new Transaction();
		transactionToverify.addInput(input1);
		transactionToverify.addInput(input2);
		transactionToverify.setOutputOut(outputOut);
		transactionToverify.setOutputBack(outputBack);

		/*
		if(validateTrans(chain, transactionToverify)) {
			System.out.println("Trans OK");
		}else {
			System.out.println("Trans KO");
		}
		*/
		
		blocks.add(genesis);
		genesis.setPrevHash(null);		
		try {
			buildBlock(genesis, new PrivateKey[] {keyConnard, keyConnasse, keyTest});
		} catch (InterruptedException | ExecutionException | InvalidKeySpecException | InvalidPasswordException | InvalidAESStreamException e) {
			e.printStackTrace();
		}
		
//		for(Transaction transaction : block1.getTransactions()) {
//			if (!validateTrans(chain, transaction))return;
//		}
		System.out.println("It's alright");
	}
	
	private static Chain buildBlock(Block genesis, PrivateKey[] privKeys) throws NoSuchAlgorithmException, InterruptedException, ExecutionException, InvalidKeyException, NoSuchProviderException, FileNotFoundException, SignatureException, IOException, InvalidKeySpecException, InvalidPasswordException, InvalidAESStreamException, StrongEncryptionNotAvailableException {
		Random rand = new Random();
		Chain chain = new Chain();
		Block prevBlock = null;
		for(int i = 0; i < 10; i++) {
			Block block;
			if(i != 0) {
				block = new Block();
				block.setPrevHash(prevBlock.getFinalHash());
				prevBlock = block;
				int limit = rand.nextInt(5);
				PrivateKey privKey = privKeys[rand.nextInt(3)];
				for(int j = 0; j < limit; j++) {
					Transaction transaction = new Transaction();
					for(int k = 0; k < limit; k++) {
						Input in = new Input();
						in.setAmount(prevBlock.getTransactions().get(0).getOutputOut()); //FIXME
//						in.setPrevTransactionHash(//prevTransactionHash);
						transaction.getInputs().add(in);
					}
					Output outputOut = new Output();
					Output outputBack = new Output();
					transaction.setOutputOut(outputOut);
					transaction.setOutputBack(outputBack);
					transaction.setSignature(SignaturesVerification.signTransaction(transaction.getBytes(false), privKey));
					transaction.setPubKey(keys.get(privKey).getEncoded());
					block.getTransactions().add(transaction);
				}
			}
			else {
				block = genesis;
				prevBlock = genesis;
			}
			block.setDifficulty(4);
			block.setTimeCreation(System.currentTimeMillis());
			chain.getBlocks().put(block.getFinalHash(), block);
			System.out.println(new Gson().toJson(block));
		}
		return chain;
	}
	
	 public static void createKeys(String password) throws IOException, AES.InvalidKeyLengthException, AES.StrongEncryptionNotAvailableException, NoSuchAlgorithmException, NoSuchProviderException{
		KeyGenerator keyGen = new KeyGenerator(1024);
        keyGen.createKeys();
        PrivateKey privateKey = keyGen.getPrivateKey();
        PublicKey publicKey = keyGen.getPublicKey();
        char[] AESpw = password.toCharArray();
        ByteArrayInputStream inputPrivateKey = new ByteArrayInputStream(privateKey.getEncoded());
        ByteArrayOutputStream encryptedPrivateKey = new ByteArrayOutputStream();
        AES.encrypt(128, AESpw, inputPrivateKey , encryptedPrivateKey);
        keys.put(privateKey, publicKey);
     }
}