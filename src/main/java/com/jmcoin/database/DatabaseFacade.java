package com.jmcoin.database;

import com.jmcoin.model.Block;
import com.jmcoin.model.Chain;
import com.jmcoin.model.Transaction;

import java.util.Arrays;
import java.util.List;

public class DatabaseFacade {

    private DatabaseFacade(){}

    public static void storeBlockChain(Chain chain){
        Connection.getTransaction().begin();
        Connection.getManager().persist(chain);
        Connection.getTransaction().commit();
    }

    public static Chain getStoredChain(){
        Connection.getTransaction().begin();
        Chain chain = (Chain) Connection.getManager().createNamedQuery("Chain.findAll").getSingleResult();
        Connection.getTransaction().commit();
        return chain;
    }

    public static void updateChain(Chain chain){
        Connection.getTransaction().begin();
        Connection.getManager().merge(chain);
        Connection.getTransaction().commit();
    }

    public static void removeBlockChain(Chain chain){
        Connection.getTransaction().begin();
        Connection.getManager().remove(chain);
        Connection.getTransaction().commit();
    }

    public static List<Transaction> getAllTransactionsWithAddress(String... addresses){
        List<String> addr = Arrays.asList(addresses);
        Connection.getTransaction().begin();
        List<Transaction> transactions = Connection.getManager().createQuery("SELECT t from Transaction t where t.outputOut.address IN :addr1 or t.outputBack.address IN :addr2")
                .setParameter("addr1", addr)
                .setParameter("addr2", addr).getResultList();
        Connection.getTransaction().commit();
        return transactions;
    }

    public static Block getBlockWithHash(String finalHash){
        Connection.getTransaction().begin();
        Block b = (Block) Connection.getManager().createQuery("SELECT b from Block b where b.finalHash = :finalHash").setParameter("finalHash", finalHash).getSingleResult();
        Connection.getTransaction().commit();
        return b;
    }

    public static Block getLastBlock() {
        Connection.getTransaction().begin();
        Block b = (Block) Connection.getManager().createQuery("SELECT b from Block b where b.id = (SELECT max(b2.id) from Block b2)").getSingleResult();
        Connection.getTransaction().commit();
        return b;
    }

}
