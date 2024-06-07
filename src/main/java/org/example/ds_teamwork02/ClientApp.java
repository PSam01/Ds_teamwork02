package org.example.ds_teamwork02;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ClientApp extends JFrame {
    private JTextField userField;
    private JPasswordField passwordField;
    private JButton loginButton, searchButton, buyButton;
    private JTextArea productArea, logArea;
    private JTextField searchField, quantityField;
    private Socket socket;
    private DataOutputStream toServer;
    private DataInputStream fromServer;

    public ClientApp() {
        createUI();
        setupConnection();
    }

    private void createUI() {
        setTitle("Client Application");
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Login Panel
        JPanel northPanel = new JPanel();
        northPanel.add(new JLabel("Username:"));
        userField = new JTextField(10);
        northPanel.add(userField);
        northPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(10);
        northPanel.add(passwordField);
        loginButton = new JButton("Login");
        northPanel.add(loginButton);
        add(northPanel, BorderLayout.NORTH);

        // Product Panel
        JPanel centerPanel = new JPanel(new GridLayout(2, 1));
        productArea = new JTextArea(10, 30);
        productArea.setEditable(false);
        centerPanel.add(new JScrollPane(productArea));

        JPanel searchPanel = new JPanel();
        searchField = new JTextField(10);
        searchButton = new JButton("Search");
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        centerPanel.add(searchPanel);

        add(centerPanel, BorderLayout.CENTER);

        // Action Panel
        JPanel southPanel = new JPanel();
        quantityField = new JTextField(5);
        buyButton = new JButton("Buy");
        logArea = new JTextArea(5, 30);
        logArea.setEditable(false);
        southPanel.add(new JLabel("Quantity:"));
        southPanel.add(quantityField);
        southPanel.add(buyButton);
        southPanel.add(new JScrollPane(logArea));
        add(southPanel, BorderLayout.SOUTH);

        // Action Listeners
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchProducts();
            }
        });
        buyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buyProduct();
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void setupConnection() {
        try {
            socket = new Socket("localhost", 8000);
            toServer = new DataOutputStream(socket.getOutputStream());
            fromServer = new DataInputStream(socket.getInputStream());
        } catch (Exception e) {
            logArea.setText("Error connecting to server: " + e.getMessage());
        }
    }

    private void performLogin() {
        try {
            toServer.writeUTF("LOGIN"); // Εντολή για σύνδεση
            toServer.writeUTF(userField.getText()); // Αποστολή username
            toServer.writeUTF(new String(passwordField.getPassword())); // Αποστολή password
            toServer.flush(); // Ολοκλήρωση εκπομπής δεδομένων

            String response = fromServer.readUTF(); // Λήψη απόκρισης από τον server
            if (response.equals("SUCCESS")) {
                logArea.setText("Login successful.");
            } else {
                logArea.setText("Login failed.");
            }
        } catch (Exception e) {
            logArea.setText("Error during login: " + e.getMessage());
        }
    }


    private void searchProducts() {
        try {
            toServer.writeUTF("SEARCH_PRODUCT");
            toServer.writeUTF(searchField.getText());
            toServer.flush();

            String products = fromServer.readUTF();
            productArea.setText(products);
        } catch (Exception e) {
            logArea.setText("Error searching products: " + e.getMessage());
        }
    }

    private void buyProduct() {
        try {
            toServer.writeUTF("BUY_PRODUCT");
            toServer.writeUTF(searchField.getText());
            toServer.writeUTF(quantityField.getText());
            toServer.flush();

            String response = fromServer.readUTF();
            logArea.setText(response);
        } catch (Exception e) {
            logArea.setText("Error buying product: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new ClientApp();
    }
}

