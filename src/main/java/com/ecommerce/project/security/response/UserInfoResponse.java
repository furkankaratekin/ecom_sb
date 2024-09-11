package com.ecommerce.project.security.response;

import java.util.List;

public class UserInfoResponse {
    private Long id;
    private String jwtToken;
    private String username;
    private List<String> roles;

    public UserInfoResponse(Long id, String username, List<String> roles, String jwtToken) {
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.jwtToken = jwtToken;
    }

    //Burada özellikle 2 tane kurucu metot tanımlıyoruz.(overloading) //Eğer 4 parametre alırsa cookie ile giriş yapar.Yani daha önceden var
    //Eğer cookie yok ise daha yeni yapacak gibi bir anlamı var
    public UserInfoResponse(Long id, String username, List<String> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}


