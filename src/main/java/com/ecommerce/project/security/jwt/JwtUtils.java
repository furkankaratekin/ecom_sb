package com.ecommerce.project.security.jwt;

import com.ecommerce.project.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    // Logger: Bu sınıf içerisindeki hataları veya mesajları loglamak için kullanılır.
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // jwtSecret: JWT'nin imzalanmasında kullanılan gizli anahtar, application.properties dosyasından okunur.
    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    // jwtExpirationMs: JWT'nin geçerlilik süresi (milisaniye cinsinden), application.properties dosyasından okunur.
    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    // jwtCookie: JWT'nin saklandığı cookie'nin adı, application.properties dosyasından okunur.
    @Value("${spring.ecom.app.jwtCookieName}")
    private String jwtCookie;

    // JWT token'ını cookie'den alan metod
    public String getJwtFromCookies(HttpServletRequest request) {
        // İstekten ilgili JWT cookie'sini bulur
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);

        // Eğer cookie varsa, token değerini döndürür
        if (cookie != null) {
            return cookie.getValue();
        } else {
            // Eğer cookie yoksa null döner
            return null;
        }
    }

    // Kullanıcıya JWT token içeren bir cookie oluşturur
    public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {
        // Kullanıcının kullanıcı adına göre JWT token oluşturur
        String jwt = generateTokenFromUsername(userPrincipal.getUsername());

        // JWT token'ı içeren bir cookie oluşturur
        ResponseCookie cookie = ResponseCookie.from(jwtCookie, jwt)
                .path("/api") // Cookie'nin geçerli olacağı yol (path)
                .maxAge(24 * 60 * 60) // Cookie'nin 24 saat geçerli olması sağlanır
                .httpOnly(false) // Cookie sadece HTTP istekleriyle erişilebilir (güvenlik amaçlı)
                .build();
        return cookie;
    }

    // JWT token içermeyen bir cookie oluşturur (JWT'yi temizlemek için kullanılır)
    public ResponseCookie getCleanJwtCookie() {
        // Boş bir cookie döner, JWT'yi temizlemek için kullanılır
        ResponseCookie cookie = ResponseCookie.from(jwtCookie, null)
                .path("/api")
                .build();
        return cookie;
    }

    // Kullanıcı adından JWT token oluşturur
    public String generateTokenFromUsername(String username) {
        // JWT token'ı oluşturur ve imzalar
        return Jwts.builder()
                .subject(username) // Kullanıcı adı (subject) JWT'nin payload'ına eklenir
                .issuedAt(new Date()) // Token'ın oluşturulma zamanı
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Token'ın geçerlilik süresi eklenir
                .signWith(key()) // Token'ı gizli anahtar ile imzalar
                .compact(); // JWT token'ı string hale getirir
    }

    // JWT token'ından kullanıcı adını alır
    public String getUserNameFromJwtToken(String token) {
        // JWT token'ı doğrular ve içindeki bilgileri alır
        return Jwts.parser()
                .verifyWith((SecretKey) key()) // Gizli anahtarı kullanarak token'ı doğrular
                .build().parseSignedClaims(token) // Token'ı ayrıştırır
                .getPayload().getSubject(); // Token'ın içindeki kullanıcı adını (subject) döner
    }

    // Gizli anahtarı base64 ile şifreleyerek elde eden metod
    private Key key() {
        // jwtSecret'i base64 ile çözer ve HMACSHA ile bir anahtar oluşturur
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // JWT token'ını doğrulayan metod
    public boolean validateJwtToken(String authToken) {
        try {
            // JWT token'ı doğrular
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            // Token formatı geçersizse hata mesajı loglanır
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            // Token süresi dolmuşsa hata mesajı loglanır
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            // Token desteklenmiyorsa hata mesajı loglanır
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            // Token'da gerekli bilgiler eksikse hata mesajı loglanır
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false; // Token geçersizse false döner
    }
}
