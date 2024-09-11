package com.ecommerce.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "cart_items")
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne // Birden fazla CartItem, bir Product'a ait olabilir. Bu ilişki, CartItem ile Product arasında bir "çoktan bire" ilişkiyi ifade eder.
    @JoinColumn(name = "product_id") // Bu ilişkinin veritabanındaki 'product_id' adlı bir sütun ile temsil edileceğini belirtir.
    private Product product; // CartItem'da bulunan Product nesnesi.

    private Integer quantity; // Bu CartItem'ın sepet içindeki miktarı.
    private double discount; // Bu CartItem için uygulanacak indirim miktarı.
    private double productPrice; // Bu CartItem'da bulunan ürünün birim fiyatı.



}
