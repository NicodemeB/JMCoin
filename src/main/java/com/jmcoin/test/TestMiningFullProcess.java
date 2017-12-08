package com.jmcoin.test;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.ExecutionException;

import com.jmcoin.crypto.AES.InvalidAESStreamException;
import com.jmcoin.crypto.AES.InvalidPasswordException;
import com.jmcoin.crypto.AES.StrongEncryptionNotAvailableException;
import com.jmcoin.model.Block;
import com.jmcoin.network.MinerJMProtocolImpl;
import com.jmcoin.network.MinerNode;

public class TestMiningFullProcess {
	
	/**
	 * To run this routine, keys have to exist. If it's not the case, please open TestWallet, uncomment "before w.createKeys("a");"
	 * and run TestWallet to create keys.
	 * To pass appropriate arguments:
	 * Run As > Run configurations > Arguments > Program Arguments and write 'a a' (without quotes) > Run 
	 * @param args
	 */
	public static void main(String[] args){
		try {
			TestMasterNode.runMaster();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		try {
			TestRelay.run();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		MinerNode minerHard;
		try {
			minerHard = new MinerNode(args[0]);
			MinerJMProtocolImpl minerJMProtocolImpl = new MinerJMProtocolImpl(minerHard);
			Block block = minerHard.buildBlock(minerJMProtocolImpl);
			block.setDifficulty(25);
			minerJMProtocolImpl.getMining().mine(block, minerJMProtocolImpl);
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException | IOException
				| InvalidPasswordException | InvalidAESStreamException | StrongEncryptionNotAvailableException | InvalidKeyException | ClassNotFoundException | SignatureException | InterruptedException | ExecutionException e1) {
			e1.printStackTrace();
			System.out.println("TestMiningFullProcess: Cannot create Miner/Wallet");
			return;
		}
		MinerNode miner;
		try {
			miner = new MinerNode(args[0]);
			MinerJMProtocolImpl minerJMProtocolImpl = new MinerJMProtocolImpl(miner);
			minerJMProtocolImpl.getMining().mine(miner.buildBlock(minerJMProtocolImpl), minerJMProtocolImpl);
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException | IOException
				| InvalidPasswordException | InvalidAESStreamException | StrongEncryptionNotAvailableException | InvalidKeyException | ClassNotFoundException | SignatureException | InterruptedException | ExecutionException e1) {
			e1.printStackTrace();
			System.out.println("TestMiningFullProcess: Cannot create Miner/Wallet");
			return;
		}
		//TODO relaunch if the miner is stopped
	}

}