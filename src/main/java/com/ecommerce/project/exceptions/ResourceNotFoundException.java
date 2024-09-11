package com.ecommerce.project.exceptions;

public class ResourceNotFoundException extends RuntimeException  {
    //Bu alanlar hata durumunda kaynağın adını , arama yapılan alanı ve aranan değeri saklamak için kullanılır
    String resourceName; // Kaynağın adı (örneğin, "User", "Product")
    String field; // Hangi alanda arama yapıldığı (örneğin, "id", "name")
    String fieldName; // Aranan değerin ismi (örneğin, "John Doe")
    Long fieldId; // Aranan değerin numarası (örneğin, 12345)

    //Parametresiz constructor .Gerekirse özel ibr mesaj olmadan hata fırlatabilir.
    public ResourceNotFoundException(){
    }

    //Bu kurucu, kaynak adı, alan adı ve aranan alan değeri ile hata mesajı oluşturur.
    //Örneğin "12345 id'li kullanıcı bulunamaı" vb mesaj gönderir.
    public ResourceNotFoundException(String resourceName, String field, String fieldName) {
        //super ile üst sınıf olan RuntimeException'a bir mesaj gönderiyoruz.
        //Bu mesaj kaynağın bulunamadığına belirten bir formatta olşuturuluyor
        super(String.format("%s not found with %s: %s", resourceName, field, fieldName));
        //Parametreler ile gelen değerler, sınıfın ilgili alanlaırna atanır.
        this.resourceName = resourceName;
        this.field = field;
        this.fieldName = fieldName;
    }

    //Bu kurucu da benzer şekilde çalışır, ancak aranan değer bir numara (ID) olduğunda kullanılır
    //Örneğin "Product not found with id: 12345" gibi bir mesaj döner
    public ResourceNotFoundException(String resourceName, String field, Long fieldId) {
        // Üst sınıfa (RuntimeException) kaynak bulunamadığına dair bir mesaj gönderiliyor.
        super(String.format("%s not found with %s: %d", resourceName, field, fieldId));
        // Parametreler ile gelen değerler, sınıfın ilgili alanlarına atanır.
        this.resourceName = resourceName;
        this.field = field;
        this.fieldId = fieldId;
    }

}

/*
   ResourceNotFoundException Sınıfı: Bu sınıf, bir kaynak
    bulunamadığında bu durumu ifade eden özel bir hata (istisna)
    oluşturmak için kullanılır. Örneğin, bir kullanıcı veri
    tabanında aranan bir kayıt bulunamadığında bu hata fırlatılabilir.

    Alanlar (Fields): resourceName, field, fieldName, ve
    fieldId gibi alanlar, hatayı daha anlamlı hale getirmek için
    kullanılır. Bu alanlar, hatanın hangi kaynakta, hangi alanda
    ve hangi değerle ilgili olduğunu belirtir.

    Kurucular (Constructors): İki farklı kurucu (constructor) vardır:

Birinci Kurucu: Aranan değerin bir metin (String) olduğu durumlar için kullanılır. Örneğin, "Kullanıcı ismi John Doe olan kullanıcı bulunamadı" gibi bir hata mesajı üretir.
İkinci Kurucu: Aranan değerin bir sayı (Long) olduğu durumlar için kullanılır. Örneğin, "ID'si 12345 olan ürün bulunamadı" gibi bir hata mesajı üretir.

* */

