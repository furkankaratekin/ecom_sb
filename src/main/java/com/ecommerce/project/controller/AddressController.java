package com.ecommerce.project.controller;

import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.service.AddressService;
import com.ecommerce.project.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController //ReESTFul web servisi
@RequestMapping("/api")
public class AddressController {
    @Autowired
    AuthUtil authUtil; //Kullanıcı kimliğini doğrulamak ve oturum açanları görmek için

    @Autowired
    AddressService addressService;  // Adres işlemleri için gerekli servis sınıfı.

    // Yeni bir adres yaratır.
    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO) {
        // Oturum açmış kullanıcıyı alır.
        User user = authUtil.loggedInUser();
        // Adresi oluşturur ve kullanıcıya bağlar.
        AddressDTO savedAddressDTO = addressService.createAddress(addressDTO, user);
        // Adresin başarılı bir şekilde yaratıldığını bildirir ve 201 CREATED durumu döner.
        return new ResponseEntity<>(savedAddressDTO, HttpStatus.CREATED);
    }
    // Tüm adresleri getirir.
    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAddresses() {
        // Adres listesini çeker.
        List<AddressDTO> addressList = addressService.getAddresses();
        // Adres listesiyle birlikte 200 OK yanıtını döner.
        return new ResponseEntity<>(addressList, HttpStatus.OK);
    }

    // Belirli bir ID'ye sahip adresi getirir.
    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId) {
        // Verilen ID'ye göre adresi bulur.
        AddressDTO addressDTO = addressService.getAddressesById(addressId);
        // Adres bilgisiyle birlikte 200 OK yanıtını döner.
        return new ResponseEntity<>(addressDTO, HttpStatus.OK);
    }

    // Oturum açmış kullanıcının adreslerini getirir.
    @GetMapping("/users/addresses")
    public ResponseEntity<List<AddressDTO>> getUserAddresses() {
        // Oturum açmış kullanıcıyı alır.
        User user = authUtil.loggedInUser();
        // Kullanıcının adres listesini alır.
        List<AddressDTO> addressList = addressService.getUserAddresses(user);
        // Adres listesiyle birlikte 200 OK yanıtını döner.
        return new ResponseEntity<>(addressList, HttpStatus.OK);
    }

    // Belirtilen ID'ye sahip adresi günceller.
    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable Long addressId, @Valid @RequestBody AddressDTO addressDTO) {
        // Adresi günceller ve güncellenmiş adresi döner.
        AddressDTO updatedAddress = addressService.updateAddress(addressId, addressDTO);
        // Güncellenen adresle birlikte 200 OK yanıtını döner.
        return new ResponseEntity<>(updatedAddress, HttpStatus.OK);
    }

    // Belirtilen ID'ye sahip adresi siler.
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId) {
        // Adresi siler ve silme durumunu içeren bir mesaj döner.
        String status = addressService.deleteAddress(addressId);
        // Silme işlemi başarıyla tamamlandığında 200 OK yanıtını döner.
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

}

//1. @PathVariable
//Tanım: @PathVariable, bir URI'nin belirli bir segmentinden gelen dinamik veriyi bir parametreye bağlamak için kullanılır.
//Kullanım Amacı: URL'deki dinamik değerlere erişim sağlar. Örneğin, /addresses/{addressId} gibi bir rota tanımladığınızda, {addressId} kısmındaki değeri metot parametresi olarak alabilirsiniz.

//2. @Valid
//Tanım: @Valid anotasyonu, bir nesnenin alanlarının belirli doğrulama kurallarına uyup uymadığını kontrol etmek için kullanılır.
//Kullanım Amacı: Gelen verilerin (genellikle bir DTO'nun) doğruluğunu sağlamak için kullanılır. Doğrulama, sınıfın içinde tanımlanmış olan ek anotasyonlara göre yapılır (örneğin, @NotNull, @Size, @Email gibi).