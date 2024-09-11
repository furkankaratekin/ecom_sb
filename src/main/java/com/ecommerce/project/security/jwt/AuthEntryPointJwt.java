package com.ecommerce.project.security.jwt;

//Yetkilendirilmemiş bir kullanıcı uygulamaya erişmeye çalışıtığında
//, onlara uygun bir hata mesajı döndürür.

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    //Loglama için bir Logger oluşturur.
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    //Bu metot, yetkilendirme hatası meydana geldiğiinde çalışır ve uygun bir yanıt döndürür.
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        //Yetkisiz erişim hatasını sorguluyoruz.
        logger.error("Unauthorized error: {}", authException.getMessage());

        //Yanıtın JSON formatında ve "unauthorized" durumda olmasını sağlıyoruz.
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        //Yanıtın gövdesi için bir map luşştur ve gerekli bilgileri ekle
        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED); // HTTP status kodu
        body.put("error", "Unauthorized"); // Hata türü
        body.put("message", authException.getMessage()); // Hata mesajı
        body.put("path", request.getServletPath()); // Hatanın meydana geldiği URL yolu    }

        //Yanıt gövdesi JSON formatına çevrilip , HTTP yanıt akışını yazdır
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
}}

