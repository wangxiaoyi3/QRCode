package com.wangyit.qrcodeapp.ui.login;

import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
class LoginResult {
    @Nullable
    private String token;
    @Nullable
    private String message;

    public LoginResult(@Nullable String token, @Nullable String message) {
        this.token = token;
        this.message = message;
    }

    @Nullable
    public String getToken() {
        return token;
    }

    @Nullable
    String getMessage() {
        return message;
    }
}