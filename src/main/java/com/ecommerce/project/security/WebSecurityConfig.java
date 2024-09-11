package com.ecommerce.project.security;

import com.ecommerce.project.model.AppRole;
import com.ecommerce.project.model.Role;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repositories.RoleRepository;
import com.ecommerce.project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ecommerce.project.security.jwt.AuthEntryPointJwt;
import com.ecommerce.project.security.jwt.AuthTokenFilter;
import com.ecommerce.project.security.services.UserDetailsServiceImpl;

import java.util.Set;

@Configuration  // Bu sınıfın bir konfigürasyon sınıfı olduğunu belirtir.
@EnableWebSecurity  // Spring Security için web güvenliğini etkinleştirir.
public class WebSecurityConfig {

    // Kullanıcı bilgilerini almak için UserDetailsServiceImpl sınıfını kullanır.
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    // Yetkisiz erişimler için özel bir giriş noktası olan AuthEntryPointJwt nesnesi kullanılır.
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    // JWT doğrulaması yapacak filtreyi tanımlar. Bu filtre HTTP isteklerini JWT'ye göre kontrol eder.
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    // Kimlik doğrulama sağlayıcısıdır. Kullanıcı bilgileri ve şifre doğrulama işlemlerini gerçekleştirir.
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        // Kullanıcı bilgilerini almak için userDetailsService kullanılır.
        authProvider.setUserDetailsService(userDetailsService);

        // Şifreleri şifrelemek ve karşılaştırmak için passwordEncoder kullanılır.
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    // AuthenticationManager, kimlik doğrulama işlemlerini yönetir. AuthenticationConfiguration kullanarak oluşturulur.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // BCrypt şifreleyici kullanarak şifrelerin şifrelenmesini sağlar.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // HTTP güvenlik ayarlarını yapar. SecurityFilterChain ile güvenlik politikaları belirlenir.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())  // CSRF korumasını devre dışı bırakır. (Genellikle REST API'lerde kullanılır.)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))  // Yetkisiz erişim denemelerini yönetir.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Oturumsuz bir güvenlik modeli kullanılır.
                .authorizeHttpRequests(auth ->  // HTTP isteklerinin yetkilendirme kurallarını belirler.
                        auth.requestMatchers("/api/auth/**").permitAll()  // `/api/auth/**` URL'lerine herkes erişebilir.
                                .requestMatchers("/v3/api-docs/**").permitAll()  // Swagger API dokümantasyonu için izin verir.
                                .requestMatchers("/h2-console/**").permitAll()  // H2 veritabanı konsolu erişime açık.
                                //.requestMatchers("/api/admin/**").permitAll()  // (Yorum satırında, istenirse izin verilebilir.)
                                //.requestMatchers("/api/public/**").permitAll()  // (Yorum satırında, istenirse izin verilebilir.)
                                .requestMatchers("/swagger-ui/**").permitAll()  // Swagger UI erişime açık.
                                .requestMatchers("/api/test/**").permitAll()  // Test API'lerine erişime izin verilir.
                                .requestMatchers("/images/**").permitAll()  // Görsellerin herkes tarafından erişilebilir olmasını sağlar.
                                .anyRequest().authenticated()  // Diğer tüm istekler kimlik doğrulaması gerektirir.
                );

        // authenticationProvider() ile kimlik doğrulama sağlayıcısını ekler.
        http.authenticationProvider(authenticationProvider());

        // JWT doğrulama filtresini, UsernamePasswordAuthenticationFilter'dan önce ekler.
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        // H2 veritabanı konsoluna erişimde tarayıcı çerçevelerine izin verir.
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();  // HTTP güvenlik yapılandırmasını tamamlar ve uygular.
    }

    // Belirli yolların güvenlik filtrelerinden hariç tutulmasını sağlar. Swagger ve dokümantasyon dosyaları hariç tutulur.
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.ignoring().requestMatchers("/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**"));
    }

    // Uygulama başlatıldığında çalışan bir veri yükleyici. Kullanıcı ve rol verilerini başlatır.
    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Kullanıcı rolü olup olmadığını kontrol eder, yoksa yeni rol oluşturur.
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseGet(() -> {
                        Role newUserRole = new Role(AppRole.ROLE_USER);
                        return roleRepository.save(newUserRole);  // Yeni kullanıcı rolünü veri tabanına kaydeder.
                    });

            // Satıcı rolünü kontrol eder, yoksa yeni satıcı rolü oluşturur.
            Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                    .orElseGet(() -> {
                        Role newSellerRole = new Role(AppRole.ROLE_SELLER);
                        return roleRepository.save(newSellerRole);  // Yeni satıcı rolünü veri tabanına kaydeder.
                    });

            // Yönetici rolünü kontrol eder, yoksa yeni yönetici rolü oluşturur.
            Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                    .orElseGet(() -> {
                        Role newAdminRole = new Role(AppRole.ROLE_ADMIN);
                        return roleRepository.save(newAdminRole);  // Yeni yönetici rolünü veri tabanına kaydeder.
                    });

            // Kullanıcılar için roller seti oluşturulur.
            Set<Role> userRoles = Set.of(userRole);  // Sadece `ROLE_USER` içeren set.
            Set<Role> sellerRoles = Set.of(sellerRole);  // Sadece `ROLE_SELLER` içeren set.
            Set<Role> adminRoles = Set.of(userRole, sellerRole, adminRole);  // Kullanıcı, satıcı ve yönetici rollerini içeren set.

            // Eğer `user1` adında bir kullanıcı yoksa, yeni bir kullanıcı oluşturur.
            if (!userRepository.existsByUserName("user1")) {
                User user1 = new User("user1", "user1@example.com", passwordEncoder.encode("password1"));
                userRepository.save(user1);  // Yeni kullanıcıyı veri tabanına kaydeder.
            }

            // Eğer `seller1` adında bir satıcı yoksa, yeni bir satıcı oluşturur.
            if (!userRepository.existsByUserName("seller1")) {
                User seller1 = new User("seller1", "seller1@example.com", passwordEncoder.encode("password2"));
                userRepository.save(seller1);  // Yeni satıcıyı veri tabanına kaydeder.
            }

            // Eğer `admin` adında bir yönetici yoksa, yeni bir yönetici oluşturur.
            if (!userRepository.existsByUserName("admin")) {
                User admin = new User("admin", "admin@example.com", passwordEncoder.encode("adminPass"));
                userRepository.save(admin);  // Yeni yöneticiyi veri tabanına kaydeder.
            }

            // `user1` adındaki kullanıcının rollerini günceller.
            userRepository.findByUserName("user1").ifPresent(user -> {
                user.setRoles(userRoles);  // Kullanıcıya sadece `ROLE_USER` atanır.
                userRepository.save(user);  // Güncellenmiş kullanıcı veri tabanına kaydedilir.
            });

            // `seller1` adındaki satıcının rollerini günceller.
            userRepository.findByUserName("seller1").ifPresent(seller -> {
                seller.setRoles(sellerRoles);  // Satıcıya sadece `ROLE_SELLER` atanır.
                userRepository.save(seller);  // Güncellenmiş satıcı veri tabanına kaydedilir.
            });

            // `admin` adındaki kullanıcının rollerini günceller.
            userRepository.findByUserName("admin").ifPresent(admin -> {
                admin.setRoles(adminRoles);  // Yöneticiye tüm roller atanır (`ROLE_USER`, `ROLE_SELLER`, `ROLE_ADMIN`).
                userRepository.save(admin);  // Güncellenmiş yönetici veri tabanına kaydedilir.
            });
        };
    }
}
