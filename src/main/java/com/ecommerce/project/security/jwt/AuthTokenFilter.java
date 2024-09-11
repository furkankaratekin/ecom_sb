package com.ecommerce.project.security.jwt;

import com.ecommerce.project.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    // jwtUtils ve userDetailsService nesneleri otomatik olarak dependency injection ile yüklenir.
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    // Logger, sınıftaki olayları ve hataları loglamak için kullanılır.
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    // doFilterInternal, her HTTP isteğinde çağrılan filtreleme metodudur.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Gelen isteğin URI'sini loglar, debug amaçlıdır.
        logger.debug("AuthTokenFilter called for URI: {}", request.getRequestURI());

        try {
            // HTTP isteğinden JWT token'ını alır.
            String jwt = parseJwt(request);

            // Eğer JWT token mevcut ve geçerliyse, kullanıcı doğrulama işlemleri yapılır.
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {

                // JWT'den kullanıcı adını alır.
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                // Kullanıcı adını kullanarak veritabanından kullanıcı bilgilerini yükler.
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Kullanıcının kimlik doğrulamasını temsil eden bir nesne oluşturur.
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                null, // Parola null olarak belirtilir çünkü JWT kullanılıyor.
                                userDetails.getAuthorities()); // Kullanıcının yetkilerini ekler.

                // Kullanıcının rollerini loglar, debug amaçlıdır.
                logger.debug("Roles from JWT: {}", userDetails.getAuthorities());

                // İstekle ilgili ek detayları doğrulama nesnesine ekler (IP adresi, tarayıcı bilgisi vs.).
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Kimlik doğrulama bilgisini güvenlik bağlamına yerleştirir.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Herhangi bir hata olursa, hata mesajını loglar.
            logger.error("Cannot set user authentication: {}", e);
        }

        // Filtreleme zincirini devam ettirir, diğer filtreler ve işlemler çağrılır.
        filterChain.doFilter(request, response);
    }

    // HTTP isteğinden JWT token'ı parse eden (çözen) metod.
    private String parseJwt(HttpServletRequest request) {
        // İstekten JWT cookie'sini alır.
        String jwt = jwtUtils.getJwtFromCookies(request);

        // JWT'nin değerini loglar, debug amaçlıdır.
        logger.debug("AuthTokenFilter.java: {}", jwt);

        // JWT'yi geri döner.
        return jwt;
    }
}
