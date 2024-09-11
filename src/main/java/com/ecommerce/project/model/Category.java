package com.ecommerce.project.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name = "categories") //bu sınıf bir JPA varlığı olduğunu bildirir ve veritabanında categories adında bir tabloya denk gelir
@Data //getter,setter,toString,equals,hashCode
@NoArgsConstructor //Parametresiz constructor
@AllArgsConstructor // Parametreli constructor

public class Category {

    @Id //Bu alan tabloda birincil anahtar(primary key)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @NotBlank //Bu alan boş bırakılamaz.
    @Size(min = 5, message = "Kategori adı en az 5 karakter olmalıdır.")//Bu alan 5 karakterli olduğunu gösteriyor.
    private String categoryName;

    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL)// Bir kategoriye birden fazla ürünün ait olabileceğini belirtir.'mappedBy' parametresi, bu ilişkinin 'Product' sınıfındaki 'category' alanı ile haritalandığını gösterir.
    private List<Product> products; //Bu kategoriye ait ürünlerin listesi.
}
