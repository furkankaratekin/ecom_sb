package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    //application.properties  dosyasından 'project.image'değerini alıyoruz.Bu ürün resimlerinin yolu olabilir.
    @Value("${project.image}")
    private String path;

    // Ürün ekleme işlemini gerçekleştirir.
    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        // Belirtilen kategori ID'sine sahip kategoriyi veritabanından buluyoruz.
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category", "categoryId", categoryId));

        // Ürünün zaten mevcut olup olmadığını kontrol etmek için bir bayrak (flag) oluşturuyoruz.
        boolean isProductNotPresent = true;

        // Kategorinin içindeki tüm ürünleri alıyoruz.
        List<Product> products = category.getProducts();
        for (Product value : products) {
            // Eğer ürün ismi mevcut ürün isimlerinden biriyle eşleşirse, ürün zaten var demektir.
            if (value.getProductName().equals(productDTO.getProductName())) {
                isProductNotPresent = false; // Ürün mevcut
                break;
            }
        }

        // Ürün mevcut değilse, yeni ürünü ekliyoruz.
        if (isProductNotPresent) {
            // ProductDTO'yu Product modeline dönüştürüyoruz.
            Product product = modelMapper.map(productDTO, Product.class);
            // Ürün için varsayılan bir resim belirliyoruz.
            product.setImage("default.png");
            // Ürünün kategorisini ayarlıyoruz.
            product.setCategory(category);
            // Ürünün özel fiyatını, indirim uygulayarak hesaplıyoruz.
            double specialPrice = product.getPrice() -
                    ((product.getDiscount() * 0.01) * product.getPrice());
            product.setSpecialPrice(specialPrice);
            // Ürünü veritabanına kaydediyoruz.
            Product savedProduct = productRepository.save(product);
            // Kaydedilen ürünü ProductDTO'ya dönüştürerek geri döndürüyoruz.
            return modelMapper.map(savedProduct, ProductDTO.class);
        } else {
            // Eğer ürün zaten mevcutsa, bir hata fırlatıyoruz.
            throw new APIException("Ürün zaten bulunmaktadır.");
        }
    }

    //Tüm ürünleri sayfalı olarka getir.
    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
    //sortBy ve sortOrder parametrelerine göre sıralama yapar
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending() //Eğer sortOrder "asc" ise sıralama artan şekilde
                :Sort.by(sortBy).descending(); //Aksi durumda azalan

        //Sayfalama detayları => sayfa numarası bouyu sıralama
        Pageable pageDetails = PageRequest.of(pageNumber , pageSize, sortByAndOrder);

        //prdocutRepository kullanılarak tüm ürünler sayfalama ile veritabanından alınıyor.
        Page<Product> pageProducts = productRepository.findAll(pageDetails);

        //Sayfadaki ürünleri liste olarak al
        List<Product> products = pageProducts.getContent();

        //Product nesneleri ProductDTO nesnelerine dönüştürülüyor.
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class)) //Her bir Product nesnesini DTO'ya çevirir
                .toList(); //Sonuçları listeye çevirdi

        //Response oluştur.
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);  // DTO'ların içeriği ekleniyor
        productResponse.setPageNumber(pageProducts.getNumber());  // Sayfa numarası ayarlanıyor
        productResponse.setPageSize(pageProducts.getSize());  // Sayfa boyutu ayarlanıyor
        productResponse.setTotalElements(pageProducts.getTotalElements());  // Toplam eleman sayısı
        productResponse.setTotalPages(pageProducts.getTotalPages());  // Toplam sayfa sayısı
        productResponse.setLastPage(pageProducts.isLast());  // Son sayfa olup olmadığını kontrol ediyor

        return productResponse;  // Sonuç olarak ProductResponse dönülüyor
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        //Kategori id'sine göre kategori bulunuyor,eğer bulamaz ise hata fırlatıyor.
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()->
                        new ResourceNotFoundException("Category", "categoryId", categoryId));

        // Sıralama artan ya da azalan olarak belirleniyor
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        // Sayfa numarası, boyutu ve sıralamaya göre sayfalama ayarları
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        // Belirli bir kategoriye göre ürünler sıralanarak veritabanından alınıyor
        Page<Product> pageProducts = productRepository.findByCategoryOrderByPriceAsc(category, pageDetails);
        // Sayfa içindeki ürünlerin liste olarak alınması
        List<Product> products = pageProducts.getContent();

        //Eğer kategoriye ait ürün yoksa hata fırlatır
        if(products.isEmpty()) {
            throw new APIException(category.getCategoryName() + "adındaki kategori bulunamadı");
        }

        //Product nesneleri ProductDTO nesnelerine dönüştür
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))  //Her bir Prodcut nesnesini DTO'ya çeviriyoruz.
                .toList(); //Liste haline getir.

        //Response oluştur.
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);  // DTO'ların içeriği ayarlanıyor
        productResponse.setPageNumber(pageProducts.getNumber());  // Sayfa numarası ekleniyor
        productResponse.setPageSize(pageProducts.getSize());  // Sayfa boyutu ayarlanıyor
        productResponse.setTotalElements(pageProducts.getTotalElements());  // Toplam eleman sayısı
        productResponse.setTotalPages(pageProducts.getTotalPages());  // Toplam sayfa sayısı
        productResponse.setLastPage(pageProducts.isLast());  // Son sayfa mı kontrolü
        return productResponse;  // Sonuç olarak ProductResponse dönülüyor

    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        // Sıralama artan ya da azalan olarak belirleniyor
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // Sayfa numarası, boyutu ve sıralamaya göre sayfalama ayarları yapılıyor
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        // Veritabanından ürün adı içinde anahtar kelime geçen ürünler aranıyor, büyük/küçük harf duyarsız
        Page<Product> pageProducts = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%', pageDetails);

        // Sayfadaki ürünler listeye dönüştürülüyor
        List<Product> products = pageProducts.getContent();

        // Her bir Product nesnesi ProductDTO'ya dönüştürülüyor
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        //Eğer hiçbir ürün bulunamazsa APIException firlatılıyor.
        if(products.isEmpty()) {
            throw new APIException("Products not found with keyword" + keyword);
        }

        // ProductResponse oluşturuluyor ve DTO'lar ile diğer sayfalama bilgileri ekleniyor
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());

        return productResponse;  // Sonuç olarak ProductResponse dönülüyor
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        //Veritabanından güncellenecek ürün bulunuyor, bulunamazsa hata fırlatıyor.
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // DTO'dan gelen bilgileri Product nesnesine dönüştürüyoruz
        Product product = modelMapper.map(productDTO, Product.class);

        productFromDb.setProductName(product.getProductName());
        productFromDb.setDescription(product.getDescription());
        productFromDb.setQuantity(product.getQuantity());
        productFromDb.setDiscount(product.getDiscount());
        productFromDb.setPrice(product.getPrice());
        productFromDb.setSpecialPrice(product.getSpecialPrice());

        //Güncellenmiş ürün kaydediliyor.
        Product savedProduct = productRepository.save(productFromDb);

        //Güncellenen ürününü sepetlerdeki halini bul
        List<Cart> carts = cartRepository.findCartsByProductId(productId);

        // Her bir sepet için ürün bilgilerini DTO'ya dönüştürüyoruz
        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

            // Sepet içerisindeki ürünleri DTO'ya çeviriyoruz
            List<ProductDTO> products = cart.getCartItems().stream()
                    .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).collect(Collectors.toList());

            cartDTO.setProducts(products);

            return cartDTO;

        }).collect(Collectors.toList());

        // Sepetlerdeki ürün bilgilerini güncelliyoruz
        cartDTOs.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(), productId));

        // Sonuç olarak güncellenmiş ürün bilgisi DTO olarak dönülüyor
        return modelMapper.map(savedProduct, ProductDTO.class);

    }


    @Override
    public ProductDTO deleteProduct(Long productId) {
        //Silinecek ürün veritabanından bulunuyor, bulunamazsa hata fırlatır
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        //Ürün ile ilgili tüm  sepetler bulunuyor ve sepetlerden ürün siliniyor.
        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(), productId));

        //Ürün veritabanından silimiyor.
        productRepository.delete(product);

        //Silinen ürün bilgisi DTO olarak dönülüyor
        return modelMapper.map(product, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        String fileName = fileService.uploadImage(path, image);
        productFromDb.setImage(fileName);

        Product updatedProduct = productRepository.save(productFromDb);
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }
}
