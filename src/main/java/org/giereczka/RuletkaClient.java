package org.giereczka;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class RuletkaClient {
        private Socket s = null;
        private DataInputStream in = null;
        private DataOutputStream out = null;

        public RuletkaClient(String addr, int port) {
            try {
                s = new Socket(addr, port);
                System.out.println("Connected!");

                in = new DataInputStream(System.in);
                out = new DataOutputStream(s.getOutputStream());
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }
}


