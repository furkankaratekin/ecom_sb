package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Bu sınıf, REST API isteğini işleyen bir kontrolcü (controller) sınıfıdır.
@RestController
@RequestMapping("/api") // Bu sınıfın işlediği tüm endpoint'lerin temel URL yolunu belirler.
public class CategoryController {

    @Autowired
    private CategoryService categoryService; // Kategori işlemlerini gerçekleştiren servis sınıfının bir örneği.

    // Tüm kategorileri listeleyen GET isteği işleyici metod.
    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {

        // İstemciden gelen parametrelere göre kategorileri getiren servis metodunu çağırır.
        CategoryResponse categoryResponse = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);

        // Servisten dönen kategori verisini ve HTTP durum kodunu içeren yanıtı oluşturur.
        return new ResponseEntity<>(categoryResponse, HttpStatus.OK); // HTTP 200 OK ile yanıt verir.
    }

    // Yeni bir kategori oluşturmak için POST isteği işleyici metod.
    @PostMapping("/public/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO){
        // İstek gövdesinden alınan JSON verisini CategoryDTO nesnesine dönüştürür ve doğrular.
        // Ardından yeni kategoriyi oluşturan servis metodunu çağırır.
        CategoryDTO savedCategoryDTO = categoryService.createCategory(categoryDTO);

        // Oluşturulan kategoriyi ve HTTP durum kodunu içeren yanıtı oluşturur.
        return new ResponseEntity<>(savedCategoryDTO, HttpStatus.CREATED); // HTTP 201 Created ile yanıt verir.
    }

    // Belirli bir kategori ID'sine sahip kategoriyi silmek için DELETE isteği işleyici metod.
    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId){
        // URL yolundan alınan kategori ID'sine göre kategoriyi bulan ve silen servis metodunu çağırır.
        CategoryDTO deletedCategory = categoryService.deleteCategory(categoryId);

        // Silinen kategoriyi ve HTTP durum kodunu içeren yanıtı oluşturur.
        return new ResponseEntity<>(deletedCategory, HttpStatus.OK); // HTTP 200 OK ile yanıt verir.
    }

    // Mevcut bir kategoriyi güncellemek için PUT isteği işleyici metod.
    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO,
                                                      @PathVariable Long categoryId){
        // İstek gövdesinden alınan JSON verisini CategoryDTO nesnesine dönüştürür ve doğrular.
        // URL yolundan alınan kategori ID'si ile mevcut kategoriyi bulur ve güncelleyen servis metodunu çağırır.
        CategoryDTO savedCategoryDTO = categoryService.updateCategory(categoryDTO, categoryId);

        // Güncellenmiş kategoriyi ve HTTP durum kodunu içeren yanıtı oluşturur.
        return new ResponseEntity<>(savedCategoryDTO, HttpStatus.OK); // HTTP 200 OK ile yanıt verir.
    }
}
