package com.ecommerce.project.config;

//Bu sınıf,uygulama genelinde kullanılacak sabit değerleri gösterir(constants)


public class AppConstants {
    // Sayfa numarası için varsayılan değer. Örneğin, sayfalama işlemlerinde ilk sayfa için kullanılır.
    public static final String PAGE_NUMBER = "0";

    // Sayfa boyutu için varsayılan değer. Bir sayfada kaç adet öğe gösterileceğini belirler.
    public static final String PAGE_SIZE = "50";

    // Kategorileri sıralamak için kullanılacak alan. Kategoriler, "categoryId" alanına göre sıralanır.
    public static final String SORT_CATEGORIES_BY = "categoryId";

    // Ürünleri sıralamak için kullanılacak alan. Ürünler, "productId" alanına göre sıralanır.
    public static final String SORT_PRODUCTS_BY = "productId";

    // Sıralamanın hangi yönde yapılacağını belirler. "asc" değeri, artan (ascending) sırayı temsil eder.
    public static final String SORT_DIR = "asc";
}
