package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"), //'username' kolonunun veritabanında benzersiz (unique olduğunu gösterir)
        @UniqueConstraint(columnNames = "email") //'email' kolonunun veritabanında benzersiz (unique) olduğunu gösterir.
})

public class User {
    @Id // Bu alanın tablodaki birincil anahtar (primary key) olduğunu belirtir.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Birincil anahtarın otomatik olarak artan bir değer ile oluşturulacağını belirtir.
    @Column(name = "user_id") // Bu alanın veritabanındaki kolon adını 'user_id' olarak belirtir.
    private Long userId; // Kullanıcı için benzersiz kimlik (id) alanı.

    @NotBlank
    @Size(max = 20, message = "Kullanici adi 20 karakterden fazla olamaz.")
    @Column(name = "username")
    private String userName;


    @NotBlank
    @Size(max = 50)
    @Email //Email formatında olacağını gösterir.
    @Column(name="email")
    private String email;

    @NotBlank
    @Size(max = 120)
    @Column(name = "password")
    private String password;

    //Kullanıcının kullanıcı adı,e-posta adresi ve şifre bilgilerini alarak bir User nesnesi oluşturmak için kullanılan yapıcı metod
    public User(String username, String email, String password) {
        this.userName = username;
        this.email = email;
        this.password = password;
    }

    @Setter
    @Getter
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE} , fetch = FetchType.EAGER)  //Kullanıcı ve roller arasında çoktan çoğa ilişki varıdr.Roller hemen yüklenir (EAGER getch type)
    @JoinTable(name = "user_role",//ilişkili tablo adı "user_role" olarka göster
            joinColumns = @JoinColumn(name = "user_id"), // 'user_id' alanı ile ilişki kurulacağını belirtir.
            inverseJoinColumns = @JoinColumn(name = "role_id")) // 'role_id' alanı ile ilişki kurulacağını belirtir.
    private Set<Role> roles = new HashSet<>();

    @Getter
    @Setter
    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST,CascadeType.MERGE}, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    @ToString.Exclude
    @OneToOne(mappedBy = "user", cascade = { CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private Cart cart;

    @ToString.Exclude
    @OneToMany(mappedBy = "user",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true)
    private Set<Product> products;




    }
