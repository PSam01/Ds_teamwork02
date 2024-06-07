package org.example.ds_teamwork02;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UserManagementInterface extends Remote {
    String login(String username, String password) throws RemoteException;
    String searchProduct(String productQuery) throws RemoteException;
    String buyProduct(String productId, int quantity) throws RemoteException;
}
