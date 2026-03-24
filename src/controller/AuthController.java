package controller;

import dao.UserDAO;

public class AuthController {
    UserDAO dao = new UserDAO();

    public boolean login(String username, String password) {
        return dao.login(username, password);
    }

    public boolean register(String username, String password) {
        return dao.register(username, password);
    }
}