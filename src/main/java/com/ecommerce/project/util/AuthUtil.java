package com.ecommerce.project.util;


import com.ecommerce.project.model.User;
import com.ecommerce.project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component // Bu anotasyon, bu sınıfın bir Spring bileşeni olduğunu belirtir ve Spring tarafından yönetilmesini sağlar.
// Bu sınıf, uygulamanın herhangi bir yerinde bağımlılık enjeksiyonu ile kullanılabilir.
public class AuthUtil {

    @Autowired
    UserRepository userRepository; //Kullanıcı verilerini db'den çekmeyi sağlar

    //Oturum açmış kullanıcının e-posta adresini alır
    public String loggedInEmail() {
        //Authentication nesnesi şu anda oturum açmış kullanıcıyı temsil eder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //Kullanıcının adını (username) kullanarak veritabanından arama yapılır
        User user = userRepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User Not Found")); //Kullanıcı bululnamazsa hata fırlatır

        return user.getEmail();
    }

    //Oturum açmış kullanıcının ID'sini alır
    public Long loggedInUserId() {
        //Şu anda oturum açmış kullanıcıyı alır
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //DB'den kullanıcıyı bul
        User user = userRepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        return user.getUserId();
    }

    //Oturum açmış kullanıcının User nesnesini döner (Bütün veriler)
    public User loggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //Kullanıcı db'den geldi
        User user = userRepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        return user;
    }

}
