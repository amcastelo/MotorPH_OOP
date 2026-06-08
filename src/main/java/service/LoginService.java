package service;

import service.AuthService;

public class LoginService {

    private final AuthService authService;

    public LoginService(AuthService authService) {
        this.authService = authService;
    }

    public LoginResult authenticate(String username, String password) {

        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            return LoginResult.fail(
                    "Please enter both username and password."
            );
        }

        return authService.authenticate(
                username.trim(),
                password.trim()
        );
    }
}