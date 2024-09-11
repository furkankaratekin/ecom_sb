package com.ecommerce.project.security.request;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {


    @NotBlank //Boş bırakılmasını engelleyen annatasyon
    private String username;

    @NotBlank
    private String password;

    //Yukarıdaki 2 tane veriyi gönderip aşağıdakileri geri alıyoruz.

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
