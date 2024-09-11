package com.ecommerce.project.security.services;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ecommerce.project.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

@NoArgsConstructor
@Data
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;// Serileştirme sırasında kullanılacak olan sürüm numarası.


    private Long id;

    private String username;

    private String email;

    @JsonIgnore// Şifrenin JSON çıktısında yer almaması için kullanılır, böylece şifre güvenliği sağlanır.
    private String password;

    private Collection<? extends GrantedAuthority> authorities;// Kullanıcının sahip olduğu yetkiler (roller).

    public UserDetailsImpl(Long id, String username, String email, String password,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    //Statik bir metot :User objesinden UserDetailsImpl objesi oluşturur.
    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.getPassword(),
                authorities);
    }

    //Kullanıcının yetkilerini göre döner
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // Hesabın süresinin dolup dolmadığını belirtir. Şu an hep 'true' döner, yani hesap süresiz aktif.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Hesabın kilitli olup olmadığını belirtir. Şu an hep 'true' döner, yani hesap kilitli değil.
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Kimlik bilgilerinin süresinin dolup dolmadığını belirtir. Şu an hep 'true' döner, yani şifre geçerli.
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Kullanıcının aktif olup olmadığını belirtir. Şu an hep 'true' döner, yani hesap aktif.
    @Override
    public boolean isEnabled() {
        return true;
    }

    //İki UserDetailsImpl nesnesinin eşit olup olmadığını kontrol eder.(id bazında)
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }

}