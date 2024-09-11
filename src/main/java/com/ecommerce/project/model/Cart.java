package com.ecommerce.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "carts")
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    @Id // Bu alanın tablodaki birincil anahtar (primary key) olduğunu belirtir.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Birincil anahtarın otomatik olarak artan bir değer ile oluşturulacağını belirtir.
    private Long cartId; // Cart için benzersiz kimlik (id) alanı.

    @OneToOne //Cart ile User arasında bire bir (one-to-one) ilişkiyi ifade eder.Her kullanıcı yalnızca bir sepete sahip olabilir.
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "cart",cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REMOVE}, orphanRemoval = true)
    //Cart ile CartItem arasında bire bir (one-to-many) ilişkiyi ifade eder.
    //mappedBy parametresi, bu ilişkinin CartItem sınıfındaki "Cart" alanı ile haritalandığını gösterir.
    //"cascade" işlemi, CartItem'ların otomatik olarak eklenmesini, güncellenmesini ve silinmesini sağlar
    //orphanRemovaleğer true ise , ilişkissi kaldırılan CartItem'lar otomatik olarak silinir.
    private List<CartItem> cartItems = new ArrayList<>(); // Bu sepetin içerdiği ürünlerin listesi.

    private Double totalPrice = 0.0;
}
