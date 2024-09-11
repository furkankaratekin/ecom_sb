package com.ecommerce.project.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    //Fotoğraf yükleme metodu
    String uploadImage(String path, MultipartFile file) throws IOException;

}
