package com.ecommerce.project.exceptions;

//Bu sınıf apilerle ilgili bir problem olduğu zaman kullanılır
public class APIException extends RuntimeException {
    // serialVersionUID, bir sınıfın seri hale getirilmesi (serialization) sırasında kullanılacak olan benzersiz bir kimliktir.
    // Bu, genellikle hataların yönetimi sırasında versiyon uyumluluğunu sağlamak için eklenir.
    private static final long serialVersionUID = 1L;

    //Varsayılan kurucu (constructor).Parametre almaz ve herhangi bir özel mesaj iletmez.
    public APIException() {
    }

    //Bu kurucu, hatayı oluştururken bir mesaj iletilmesini sağlar.
    //Örneğin, Bilinmeyen bir API hatası oluştu" gibi bir mesajla hata fırlatabilir
    public APIException(String message) {
        //Üst sınıf olan RuntimeExcpetion'a mesaj gönderilir
        super(message);
    }

}
