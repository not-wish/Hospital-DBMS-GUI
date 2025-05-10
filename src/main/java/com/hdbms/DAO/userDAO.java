package com.hdbms.DAO;

public interface userDAO {
    boolean saveUser(String hashId, String username, String password, String role);
    // void updateUser(User user);
    // void deleteUser(User user);
    boolean getUserById(String hashId);
    boolean getUserByUsername(String username);
    boolean validateCredentials(String username, String password);
    String getUserRole(String username);
    String getUserId(String username);
    // List<User> getUsers();
}