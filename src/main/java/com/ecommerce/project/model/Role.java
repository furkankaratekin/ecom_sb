package com.ecommerce.project.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "roles")
public class Role {
    @Id // Bu alanın tablodaki birincil anahtar (primary key) olduğunu belirtir.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Birincil anahtarın otomatik olarak artan bir değer ile oluşturulacağını belirtir.
    @Column(name = "role_id") // Bu alanın veritabanındaki kolon adını 'role_id' olarak belirtir.
    private Integer roleId; // Rol için benzersiz kimlik (id) alanı.

    @ToString.Exclude  //Lombokun toString metodunu oluştururuken bu alanı dahil etmesini sağlar
    @Enumerated(EnumType.STRING) //Bu alanın bir enum tipi olduğnun ve enum değerlerinin veritabanında string olarak saklandığını gösterir.
    @Column(length = 20, name = "role_name") // Bu alanın veritabanındaki kolon adını 'role_name' olarak belirtir ve maksimum uzunluğunu 20 karakter ile sınırlar.
    private AppRole roleName; // Rolün adı, enum türünde (`AppRole`) saklanır.

    //Parametre olarak sadec 'roleName' alanını alan bir yapıcı metod
    public Role(AppRole roleName) {
        this.roleName = roleName;
    }

}
