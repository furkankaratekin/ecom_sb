package com.ecommerce.project.config;


import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration //Konfigurasyon yapılandırma class
//Bu sınıfta tanımlanan bileşenler (Bean'ler) Spring konteynırına eklenir
//ve uygulamanın diğer kısımlarında da kullanılır
public class AppConfig {
    @Bean //Bu metot bir Bean tanımlar.
    //Yani , bu metodun dönüş değeri olan ''ModelMapper', Spring konteynırına bir bileşen olarak eklenir.
    public ModelMapper modelMapper() {
        //ModelMapper,nesneler arasında veri transferini kolaylaştıran bir kütüphanedir.
        //Örneğin bir DTO'yu (Data Transfer Object) bir Entity'ye veya tam tersine dönüştürmek için kullanılır.
        return new ModelMapper();
        //Bu metod her çağırıldığında yeni bir 'ModelMapper' nesnesi oluşturur ve bunu Spring konteynırına ekler.
        //Diğer sınıflar bu 'ModelMapper' nesnesini otomatik olarak oluşturur.
    }
}
