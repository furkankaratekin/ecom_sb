package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.*;
import com.ecommerce.project.payload.OrderDTO;
import com.ecommerce.project.payload.OrderItemDTO;
import com.ecommerce.project.repositories.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired // Bağımlılıkların otomatik olarak inject edilmesi sağlanıyor.
    CartRepository cartRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    CartService cartService;

    @Autowired
    ModelMapper modelMapper; // ModelMapper, nesneler arası dönüşüm işlemleri için kullanılıyor.

    @Autowired
    ProductRepository productRepository;


    @Override
    @Transactional // Bir işlemin bir bütün olarak çalışmasını sağlar.Örneğin ödeme işlem ibaşladıysa bitinceye kadar devam eder eğer arada bir hata olursa ise işlem tamamen iptal edilir
    public OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage) {
        //Kullanıcının emailine göre sepetini (cart) bul
        Cart cart = cartRepository.findCartByEmail(emailId);
        if(cart == null) {
            //Sepet bulunmazsa hata fırlat
            throw new ResourceNotFoundException("Cart", "email",emailId);
        }
        //Verilen adres ID'sine göre adresi buluyoruz.Eğer adres yoksa hata fırlat
        Address address = addressRepository.findById(addressId)
                .orElseThrow(()-> new ResourceNotFoundException("Address", "id", addressId));

        //Yeni bir Order (Sipariş) nesnesi oluştur
        Order order = new Order();
        order.setEmail(emailId); // Sipariş verenin e-posta adresi atanıyor.
        order.setOrderDate(LocalDate.now()); // Sipariş tarihi şu anki tarih olarak atanıyor.
        order.setTotalAmount(cart.getTotalPrice()); // Sepetin toplam fiyatı siparişin toplam tutarı olarak belirleniyor.
        order.setOrderStatus("Order Accepted !"); // Siparişin durumu "Kabul Edildi" olarak ayarlanıyor.
        order.setAddress(address); // Sipariş adresi atanıyor.

        // Yeni bir Payment (Ödeme) nesnesi oluşturuluyor.
        Payment payment = new Payment(paymentMethod, pgPaymentId, pgStatus, pgResponseMessage, pgName);
        payment.setOrder(order); // Ödeme nesnesine sipariş atanıyor.
        payment = paymentRepository.save(payment); // Ödeme kaydediliyor ve ödeme siparişe ekleniyor.
        order.setPayment(payment); // Siparişin ödeme bilgileri ayarlanıyor.

        //Siparişi db'ye kaydet
        Order savedOrder = orderRepository.save(order);

        //Sepetteki ürünler (cartItems) alınıyor.
        List<CartItem> cartItems = cart.getCartItems();
        if(cartItems.isEmpty()) {
            throw new APIException("Sepet Boş");
        }

        //Siparişe ait ürünlerin tutulacağı bir liste oluştur
        List<OrderItem> orderItems = new ArrayList<>();

        //Sepetteki her bir ürün için bir sipariş öğesi (OrderItem) oluştur
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct()); // Ürün atanıyor.
            orderItem.setQuantity(cartItem.getQuantity()); // Ürün miktarı atanıyor.
            orderItem.setDiscount(cartItem.getDiscount()); // İndirim bilgisi atanıyor.
            orderItem.setOrderedProductPrice(cartItem.getProductPrice()); // Ürün fiyatı atanıyor.
            orderItem.setOrder(savedOrder); // Sipariş atanıyor.
            orderItems.add(orderItem); // Listeye ekleniyor.
        }

        //Sipariş ürünlerini db'ye toplu olarak kaydet
        orderItems = orderItemRepository.saveAll(orderItems);

        // Sepetteki ürünlerin her biri için işlemler yapılır.
                cart.getCartItems().forEach(item -> {
                    int quantity = item.getQuantity(); // Ürünün miktarı alınıyor.
                    Product product = item.getProduct(); // Ürün nesnesi alınıyor.

                    // Ürünün stoğundan satın alınan miktar düşülüyor.
                    product.setQuantity(product.getQuantity() - quantity);

                    // Ürün güncellenmiş haliyle veri tabanına kaydediliyor.
                    productRepository.save(product);

                    // Sepetten ürünleri kaldırıyoruz.
                    cartService.deleteProductFromCart(cart.getCartId(), item.getProduct().getProductId());
                });

        // Order nesnesini OrderDTO'ya dönüştürüyoruz.
        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);

        // Sipariş ürünlerini DTO'ya ekliyoruz.
        orderItems.forEach(item -> orderDTO.getOrderItems().add(modelMapper.map(item, OrderItemDTO.class)));

        // Adres ID'si sipariş DTO'suna ekleniyor.
        orderDTO.setAddressId(addressId);

        // Son olarak oluşturulan sipariş DTO'sunu döndürüyoruz.
        return orderDTO;
    }
}
