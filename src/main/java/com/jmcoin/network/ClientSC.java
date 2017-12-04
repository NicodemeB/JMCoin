package com.jmcoin.network;

import java.io.IOException;

public class ClientSC extends Client{	
    //INFO - THIS IS THE CLIENT SIDE OF THE RELAY BETWEEN THE RELAY AND MASTER NODE

    private MultiThreadedServerClient server;

    public MultiThreadedServerClient getServer() {
        return server;
    }
    
    public ClientSC(int port, String host, JMProtocolImpl<? extends Peer> protocol, MultiThreadedServerClient srv) throws IOException {
        super(port, host, protocol);
        this.server = srv;
        new Thread(new ReceiverThread<ClientSC>(this)).start();
    }

    @Override
    public void receiveAndHandleMessage() throws InterruptedException {
        try {
            do {
                if (getToSend() != null) {
                    System.out.println("Thread #"+Thread.currentThread().getId() +" ClientSC - to send : " + getToSend().toString());
                    sendMessage(getToSend());
                }
                Thread.sleep(100);
            } while (true);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                close();
                System.out.println("close");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    protected void handleMessage(Object msg) {
        switch (msg.toString()) {
            case NetConst.CONNECTED :
                break;
            case NetConst.CONNECTION_REQUEST:
                break;
            case NetConst.STOP_MINING_REQ :
                System.out.println("server.not()");
                server.not();
                break;
            default:
                //Send what RelayNodeJMProtocolImpl return to MASTER NODE
            	this.server.getAwaitingAnswers().firstElement().setToSend(msg);
            	this.server.getAwaitingAnswers().remove(this.server.getAwaitingAnswers().firstElement());
                break;
        }
    }
}
