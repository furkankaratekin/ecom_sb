package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.repositories.AddressRepository;
import com.ecommerce.project.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService{

    // AddressRepository, veritabanı işlemlerini yönetir (adres CRUD işlemleri).
    @Autowired
    private AddressRepository addressRepository;

    // ModelMapper, Entity ve DTO'lar arasında otomatik dönüşümler sağlar.
    @Autowired
    private ModelMapper modelMapper;

    // UserRepository, kullanıcıya ait veritabanı işlemleri için kullanılır.
    @Autowired
    UserRepository userRepository;


    //Yeni bir adres ekle ve bu adresi kullanıcıya ekle
    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        //AddressDTO nesnesini entity'ye dönüştür
        Address address = modelMapper.map(addressDTO, Address.class);

        //Adresin kullanıcı bilgisi
        address.setUser(user);

        //Kullanıcının mevcut adres listesini alır ve yeni adres ekler
        List<Address> addressesList = user.getAddresses();
        addressesList.add(address);

        //Güncellenen adres listesini kullanıcıya atar
        user.setAddresses(addressesList);

        //Adresi db'ye kaydet
        Address savedAddress = addressRepository.save(address);

        //Kaydedilen adresi DTO'ya dönüştür ve postala usta
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    // Tüm adresleri getirir ve AddressDTO listesi olarak döner.
    @Override
    public List<AddressDTO> getAddresses() {
        // Veritabanından tüm adresleri getirir.
        List<Address> addresses = addressRepository.findAll();

        // Her bir adresi DTO'ya dönüştürür ve listeye ekler.
        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();
    }

    // Belirtilen ID'ye sahip adresi getirir, eğer adres bulunamazsa hata fırlatır.
    @Override
    public AddressDTO getAddressesById(Long addressId) {
        // Veritabanından adresi bulur, bulunamazsa ResourceNotFoundException fırlatır.
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        // Bulunan adresi DTO'ya dönüştürür ve geri döner.
        return modelMapper.map(address, AddressDTO.class);
    }

    // Verilen kullanıcının sahip olduğu tüm adresleri getirir ve DTO listesi olarak döner.
    @Override
    public List<AddressDTO> getUserAddresses(User user) {
        // Kullanıcının adreslerini alır.
        List<Address> addresses = user.getAddresses();

        // Her bir adresi DTO'ya dönüştürür ve listeye ekler.
        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();
    }

    // Belirtilen ID'ye sahip adresi günceller.
    @Override
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        // Veritabanında adresi bulur, eğer adres yoksa hata fırlatır.
        Address addressFromDatabase = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        // Adresin alanlarını DTO'daki yeni verilerle günceller.
        addressFromDatabase.setCity(addressDTO.getCity());
        addressFromDatabase.setPincode(addressDTO.getPincode());
        addressFromDatabase.setState(addressDTO.getState());
        addressFromDatabase.setCountry(addressDTO.getCountry());
        addressFromDatabase.setStreet(addressDTO.getStreet());
        addressFromDatabase.setBuildingName(addressDTO.getBuildingName());

        // Güncellenen adresi veritabanına kaydeder.
        Address updatedAddress = addressRepository.save(addressFromDatabase);

        // Kullanıcının adres listesinden eski adresi kaldırır ve yeni adresi ekler.
        User user = addressFromDatabase.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        user.getAddresses().add(updatedAddress);

        // Güncellenmiş kullanıcıyı veritabanına kaydeder.
        userRepository.save(user);

        // Güncellenen adresi DTO'ya dönüştürür ve geri döner.
        return modelMapper.map(updatedAddress, AddressDTO.class);
    }

    // Belirtilen ID'ye sahip adresi siler.
    @Override
    public String deleteAddress(Long addressId) {
        // Veritabanında adresi bulur, eğer adres yoksa hata fırlatır.
        Address addressFromDatabase = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        // Kullanıcının adres listesinden bu adresi kaldırır.
        User user = addressFromDatabase.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));

        // Güncellenen kullanıcıyı veritabanına kaydeder.
        userRepository.save(user);

        // Adresi veritabanından siler.
        addressRepository.delete(addressFromDatabase);

        // Başarılı silme mesajını döner.
        return "ID'si verilen adres başarılı bir şekilde silindi: " + addressId;
    }
}

