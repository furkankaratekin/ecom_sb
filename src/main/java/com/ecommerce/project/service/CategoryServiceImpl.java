package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // Bu anotasyon, bu sınıfın bir Spring bileşeni olduğunu belirtir ve Spring tarafından yönetilmesini sağlar.
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository; // Kategorilerle ilgili veri tabanı işlemlerini gerçekleştiren repository.

    @Autowired
    private ModelMapper modelMapper; // DTO'ları ve model nesnelerini dönüştürmek için kullanılan ModelMapper.

    // Kategorileri sayfalı olarak alır ve yanıtı yapılandırır.
    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        // Sıralama yönünü belirler (artan veya azalan).
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // Sayfalama ve sıralama bilgilerini ayarlar.
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        // Kategorileri veri tabanından alır.
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);

        // Kategorileri listeye dönüştürür.
        List<Category> categories = categoryPage.getContent();
        if (categories.isEmpty())
            throw new APIException("No category created till now."); // Kategori bulunamazsa hata fırlatır.

        // Kategori listelerini DTO'lara dönüştürür.
        List<CategoryDTO> categoryDTOS = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();

        // Yanıt nesnesini oluşturur ve verileri ayarlar.
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());
        return categoryResponse;
    }

    // Yeni bir kategori oluşturur ve geri döndürür.
    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        // DTO'yu model nesnesine dönüştürür.
        Category category = modelMapper.map(categoryDTO, Category.class);

        // Aynı isimde bir kategori olup olmadığını kontrol eder.
        Category categoryFromDb = categoryRepository.findByCategoryName(category.getCategoryName());
        if (categoryFromDb != null)
            throw new APIException("Category with the name " + category.getCategoryName() + " already exists !!!"); // Kategori varsa hata fırlatır.

        // Kategoriyi veri tabanına kaydeder.
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class); // Kaydedilen kategoriyi DTO'ya dönüştürür.
    }

    // Belirli bir kategoriyi siler ve geri döndürür.
    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        // Kategoriyi veri tabanında bulur.
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId)); // Kategori bulunamazsa hata fırlatır.

        // Kategoriyi siler.
        categoryRepository.delete(category);
        return modelMapper.map(category, CategoryDTO.class); // Silinen kategoriyi DTO'ya dönüştürür.
    }

    // Belirli bir kategoriyi günceller ve geri döndürür.
    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        // Kategoriyi veri tabanında bulur.
        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId)); // Kategori bulunamazsa hata fırlatır.

        // DTO'yu model nesnesine dönüştürür ve günceller.
        Category category = modelMapper.map(categoryDTO, Category.class);
        category.setCategoryId(categoryId);
        savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class); // Güncellenen kategoriyi DTO'ya dönüştürür.
    }
}
