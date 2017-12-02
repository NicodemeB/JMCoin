package com.jmcoin.test;

import com.jmcoin.network.*;

import java.io.IOException;

public class TestRelay {
    
	public static void run() throws IOException{
        MultiThreadedServerClient server = new MultiThreadedServerClient(NetConst.RELAY_NODE_LISTEN_PORT, new RelayNodeJMProtocolImpl());
        ClientSC cli = new ClientSC(NetConst.MASTER_NODE_LISTEN_PORT, NetConst.MASTER_HOST_NAME, new MasterJMProtocolImpl(), server);
        server.setClient(cli);

        new Thread(server).start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cli.sendMessage(NetConst.CONNECTION_REQUEST);
        Thread thread = new Thread(cli);
        thread.start();

    }
    
    public static void main(String args[]){

        try {
            run();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
