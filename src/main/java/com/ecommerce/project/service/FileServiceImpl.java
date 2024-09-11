package com.ecommerce.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service // Bu sınıf bir spring bileşeni olduğunu gösterir. Spring tarafından yonetilmesini sağlar.
public class FileServiceImpl implements FileService {


    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        //Kullanıcının yüklediği dosyanın orijinal adını alır.
        String originalFilename = file.getOriginalFilename();

        //Benzersiz bir dosya adı oluşturmak için UUID(Küresel Benzersiz Tanımlayıcı) kullanır
        //UUID, dosya adının çakışmasını önler.
        String randomId = UUID.randomUUID().toString();

        //Dosyanın uzantısını almak için orijinal dosya adını sonundaki '.' karkaterinden sonrasını alır
        //Yeni dosya adını, UUID ile birleştirerek oluşturur.
        String fileName = randomId.concat(originalFilename.substring(originalFilename.lastIndexOf(".")));

        //Dosyanın kaydedileceği tam yolu oluşturur.
        String filePath = path + File.separator + fileName;

        //Dosyanın kaydedileceği klasörü oluşturur, eğer klasör mevcut değilse
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdir(); //Klasör mevcut değilse oluştur.

        }
        //Kullanıcının yüklediği dosyayı belirtilen yola kopyalar.
        Files.copy(file.getInputStream(), Paths.get(filePath));
        return fileName;

    }
}

/*
*uploadImage Metodu:

Amaç: Kullanıcı tarafından yüklenen bir dosyayı belirtilen bir klasöre kaydeder.
Parametreler:
path: Dosyanın kaydedileceği klasörün yolu.
file: Yüklenen dosya (MultipartFile türünde).
İşlem Adımları:
Dosya Adını Almak: file.getOriginalFilename() ile yüklenen dosyanın orijinal adını alır.
Benzersiz Dosya Adı Oluşturmak: UUID.randomUUID().toString() ile benzersiz bir tanımlayıcı oluşturur. Bu tanımlayıcı, dosya adının başına eklenir. Böylece dosya adının çakışmasını önler.
Dosya Uzantısını Almak: Dosyanın uzantısını (.jpg, .png vb.) almak için orijinal dosya adındaki son . karakterinden sonrasını alır.
Dosya Yolunu Oluşturmak: path ile dosya adını birleştirerek dosyanın tam kaydedileceği yolu oluşturur.
Klasörü Oluşturmak: Eğer belirtilen klasör mevcut değilse, folder.mkdir() ile oluşturur.
Dosyayı Kaydetmek: Files.copy(file.getInputStream(), Paths.get(filePath)) ile yüklenen dosyayı belirlenen yola kopyalar.
Yeni Dosya Adını Döndürmek: Dosyanın yeni adını döndürür.
* */