package org.example.ds_teamwork02;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class UserManagementServer extends JFrame {
    private ServerSocket serverSocket;
    private Map<String, String> userCredentials = new HashMap<>();
    private JTextArea logArea;

    public UserManagementServer() {
        // Initialize server
        createUI();
        initializeServer();
        setupDummyUsers();
    }

    private void createUI() {
        setTitle("User Management Server");
        setSize(500, 300);
        setLayout(new BorderLayout());

        logArea = new JTextArea();
        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void initializeServer() {
        try {
            serverSocket = new ServerSocket(8000);
            logArea.append("Server listening on port 8000\n");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            logArea.append("Server failed to start: " + e.getMessage() + "\n");
        }
    }

    private void setupDummyUsers() {
        userCredentials.put("admin", "admin");  // Dummy admin user
        userCredentials.put("user1", "user1");  // Dummy regular user
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private DataInputStream input;
        private DataOutputStream output;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            try {
                input = new DataInputStream(clientSocket.getInputStream());
                output = new DataOutputStream(clientSocket.getOutputStream());
            } catch (IOException e) {
                logArea.append("Failed to create data streams: " + e.getMessage() + "\n");
            }
        }

        public void run() {
            try {
                while (true) {
                    String command = input.readUTF();
                    switch (command) {
                        case "LOGIN":
                            handleLogin();
                            break;
                        case "SEARCH_PRODUCT":
                            handleSearchProduct();
                            break;
                        case "BUY_PRODUCT":
                            handleBuyProduct();
                            break;
                        default:
                            logArea.append("Received unknown command: " + command + "\n");
                            break;
                    }
                }
            } catch (IOException e) {
                logArea.append("Client disconnected: " + e.getMessage() + "\n");
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    logArea.append("Failed to close client socket: " + e.getMessage() + "\n");
                }
            }
        }

        private void handleLogin() throws IOException {
            String username = input.readUTF();
            String password = input.readUTF();
            if (userCredentials.containsKey(username) && userCredentials.get(username).equals(password)) {
                output.writeUTF("SUCCESS");
                logArea.append("User " + username + " authenticated successfully.\n");
            } else {
                output.writeUTF("FAILURE");
                logArea.append("Failed to authenticate user " + username + ".\n");
            }
        }



        private void handleSearchProduct() throws IOException {
            // Logic to search products
        }

        private void handleBuyProduct() throws IOException {
            // Logic to buy products
        }
    }

    public static void main(String[] args) {
        new UserManagementServer();
    }
}

