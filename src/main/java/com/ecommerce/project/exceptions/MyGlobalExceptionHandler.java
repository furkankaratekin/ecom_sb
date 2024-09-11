package com.ecommerce.project.exceptions;

/*
Bu sınıf, uygulama genelinde meydana gelen belirli türdeki
istisnaları yakalamak için kullanılır.İstisnalar yakalanır ve
uygun bir HTTP durumu ve yanıt mesajı ile geri gönderilir
* */

import com.ecommerce.project.payload.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice //Bu sınıfın global bir istisna (exception) işleyicisi olduğunu belirtir.Bu uygulamanın herhangi bir yerdeki belrili sitisnaları yakalar ve işler
public class MyGlobalExceptionHandler {

    //Bu metod, form verileriyle ilgili validasyon hatalarını yakalamak için kullanılır
    //Örneğin bir formda zorunlu bir alan boş bırakıldı veya geçersiz bir değer girildiğinde bu metot çalışır
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> myMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        //Hataları depolamak için bir Map oluşturuluyor
        Map<String,String> response = new HashMap<>();

        //Tüm hataları döngü ile gezip, ilgili alan adını hatayı Map'e ekliyoruz.
        e.getBindingResult().getAllErrors().forEach(err -> {
            //Hata alan form alanını adını alıyoruz.
            String fieldName = ((FieldError) err).getField();
            //Bu alandaki hata mesajını alıyoruz (örnepin bu lana zorunludur)
            String message = err.getDefaultMessage();
            //Hata mesajını ilgili alan adıyla birlikte Map'e ekliyoruz.
            response.put(fieldName, message);
        });

        //BAD_REQUEST, 400 anlamına gelir, yani istemciden (kullanıcıdan) gelen bir hata olduğunu gösterir.
        //Bu hatalar Map olarak istemciye geri gönderilir.
        return new ResponseEntity<Map<String,String>>(response, HttpStatus.BAD_REQUEST);
    }

    //Bu metod, eğer istenilen kaynak (örneğin veri tabanında aranan kayıt) bulunamazsa devreye girer.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> myResourceNotFoundException(ResourceNotFoundException ilayda) {
        //Hata mesajını,istisnadan (exception) alıyoruz.
        String message = ilayda.getMessage();
        //Bu mesajı ve başarısız olduğunu belirten bir APIResponse nesnesi oluşturur.
        APIResponse apiResponse = new APIResponse(message,false);

        //NOT_FOUND, 404 anlamına gelir, yani istenen şey bulunamadı
        //Bu mesaj istemciye geri döndürülür
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    //Bu metod, genel API hatalarını yakalamak için kullanılır.
    //Örneğin, bir işlem sırasında beklenmeyen bir hata meydana geldiğinde bu metod devreye girer.
    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> myAPIException(APIException ilayda) {
        //Hata mesajını, istisnadan(exception) alıyoruz.
        String message = ilayda.getMessage();
        //Bu mesajı ve başarısız olduğunu belirten bir APIResponse nesnesi oluşturuyoruz.
        APIResponse apiResponse = new APIResponse(message,false);

        //BAD_REQUEST, 400 anlamına gelir, yani istemciden (kullanıcıdan) gelen bir hata olduğnu gösterir.
        //Bu mesaj istemciye geri gönderilir.
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

/*
@ExceptionHandler: Bu özel metodlar, uygulamanızda belirli türde hatalar meydana geldiğinde ne yapılacağını belirtir.

Form Validasyon Hataları: Bir formda bir hata olduğunda, bu hatalar bir liste olarak geri gönderilir.

Kaynak Bulunamadığında: İstenilen şey (örneğin, veri) bulunamazsa, "404 Not Found" hatası döndürülür.

Genel API Hataları: API'de genel bir hata olduğunda, "400 Bad Request" hatası döndürülür.

* */


}



























