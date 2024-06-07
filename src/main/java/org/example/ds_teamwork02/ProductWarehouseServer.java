package org.example.ds_teamwork02;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ProductWarehouseServer {
    private ServerSocket serverSocket;
    private Map<String, Integer> productStock;

    public ProductWarehouseServer() {
        initializeServer();
        initializeStock();
    }

    private void initializeServer() {
        try {
            serverSocket = new ServerSocket(8001);
            System.out.println("Warehouse Server is listening on port 8001");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }

    private void initializeStock() {
        productStock = new HashMap<>();
        productStock.put("1001", 50);
        productStock.put("1002", 30);
        productStock.put("1003", 20);
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private DataInputStream input;
        private DataOutputStream output;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            try {
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                System.out.println("Error setting up data streams: " + e.getMessage());
            }
        }

        public void run() {
            try {
                while (true) {
                    String productId = input.readUTF();
                    int quantity = input.readInt();
                    processOrder(productId, quantity);
                }
            } catch (IOException e) {
                System.out.println("Error processing client request: " + e.getMessage());
            } finally {
                try {
                    input.close();
                    output.close();
                    clientSocket.close();
                } catch (IOException e) {
                    System.out.println("Error closing resources: " + e.getMessage());
                }
            }
        }

        private void processOrder(String productId, int quantity) throws IOException {
            if (productStock.containsKey(productId) && productStock.get(productId) >= quantity) {
                productStock.put(productId, productStock.get(productId) - quantity);
                output.writeUTF("ORDER_CONFIRMED");
                output.writeInt(productStock.get(productId));
            } else {
                output.writeUTF("INSUFFICIENT_STOCK");
                output.writeInt(productStock.getOrDefault(productId, 0));
            }
            output.flush();
        }
    }

    public static void main(String[] args) {
        new ProductWarehouseServer();
    }
};