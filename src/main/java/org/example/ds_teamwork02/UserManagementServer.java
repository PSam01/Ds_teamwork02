package org.example.ds_teamwork02;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class UserManagementServer extends UnicastRemoteObject implements UserManagementInterface {
    private Map<String, String> userCredentials = new HashMap<>();
    private Map<String, Integer> productStock;

    protected UserManagementServer() throws RemoteException {
        super();
        setupDummyUsers();
        initializeStock();
    }

    private void setupDummyUsers() {
        userCredentials.put("admin", "adminpass");
        userCredentials.put("user1", "password1");
    }

    private void initializeStock() {
        productStock = new HashMap<>();
        productStock.put("1001", 50);
        productStock.put("1002", 30);
        productStock.put("1003", 20);
    }

    @Override
    public String login(String username, String password) throws RemoteException {
        String storedPassword = userCredentials.get(username);
        if (storedPassword != null && storedPassword.equals(password)) {
            return "SUCCESS";
        }
        return "FAILURE";
    }

    @Override
    public String searchProduct(String productQuery) throws RemoteException {
        StringBuilder response = new StringBuilder();
        for (Map.Entry<String, Integer> entry : productStock.entrySet()) {
            if (entry.getKey().contains(productQuery) || productQuery.isEmpty()) {
                response.append("Product ID: ").append(entry.getKey()).append(", Stock: ").append(entry.getValue()).append("\n");
            }
        }
        if (response.length() == 0) {
            response.append("No products found.");
        }
        return response.toString();
    }

    @Override
    public String buyProduct(String productId, int quantity) throws RemoteException {
        if (productStock.containsKey(productId) && productStock.get(productId) >= quantity) {
            productStock.put(productId, productStock.get(productId) - quantity);
            return "ORDER_CONFIRMED";
        }
        return "INSUFFICIENT_STOCK";
    }

    public static void main(String[] args) {
        try {
            java.rmi.registry.LocateRegistry.createRegistry(1099);
            UserManagementServer obj = new UserManagementServer();
            java.rmi.Naming.rebind("UserManagementServer", obj);
            System.out.println("UserManagementServer is ready.");
        } catch (Exception e) {
            System.out.println("UserManagementServer failed: " + e.getMessage());
        }
    }
}
