package org.example.ds_teamwork02;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;

public class ClientApp extends JFrame {
    private JTextField userField;
    private JPasswordField passwordField;
    private JButton loginButton, searchButton, buyButton;
    private JTextArea productArea, logArea;
    private JTextField searchField, quantityField;
    private UserManagementInterface server;

    public ClientApp() {
        createUI();
        setupConnection();
    }

    private void createUI() {
        setTitle("Client Application");
        setSize(600, 400);
        setLayout(new BorderLayout());

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

        enableActions(false);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void setupConnection() {
        try {
            server = (UserManagementInterface) Naming.lookup("//localhost/UserManagementServer");
        } catch (Exception e) {
            logArea.setText("Error connecting to server: " + e.getMessage());
        }
    }

    private void performLogin() {
        try {
            String response = server.login(userField.getText(), new String(passwordField.getPassword()));
            if (response.equals("SUCCESS")) {
                logArea.setText("Login successful.");
                enableActions(true);
            } else {
                logArea.setText("Login failed.");
                enableActions(false);
            }
        } catch (Exception e) {
            logArea.setText("Error during login: " + e.getMessage());
        }
    }

    private void enableActions(boolean enable) {
        searchButton.setEnabled(enable);
        buyButton.setEnabled(enable);
        searchField.setEnabled(enable);
        quantityField.setEnabled(enable);
    }

    private void searchProducts() {
        try {
            String products = server.searchProduct(searchField.getText());
            productArea.setText(products);
        } catch (Exception e) {
            logArea.setText("Error searching products: " + e.getMessage());
        }
    }

    private void buyProduct() {
        try {
            String response = server.buyProduct(searchField.getText(), Integer.parseInt(quantityField.getText()));
            logArea.setText(response);
        } catch (Exception e) {
            logArea.setText("Error buying product: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new ClientApp();
    }
}
