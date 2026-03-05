package Nhom6.TRANQUANGHIEN2280600922.services;

import Nhom6.TRANQUANGHIEN2280600922.daos.Cart;
import Nhom6.TRANQUANGHIEN2280600922.daos.Item;
import Nhom6.TRANQUANGHIEN2280600922.entities.Book;
import Nhom6.TRANQUANGHIEN2280600922.entities.Order;
import Nhom6.TRANQUANGHIEN2280600922.entities.OrderDetail;
import Nhom6.TRANQUANGHIEN2280600922.entities.User;
import Nhom6.TRANQUANGHIEN2280600922.repositories.IBookRepository;
import Nhom6.TRANQUANGHIEN2280600922.repositories.IOrderDetailRepository;
import Nhom6.TRANQUANGHIEN2280600922.repositories.IOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final IOrderRepository orderRepository;
    private final IOrderDetailRepository orderDetailRepository;
    private final IBookRepository bookRepository;

    // ==================== 1. CHỨC NĂNG MUA HÀNG ====================
    @Transactional
    public Order createOrder(User user, Cart cart, String address, String phone, String paymentMethod) {
        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice(cart.getTotalPrice());
        order.setShippingAddress(address);
        order.setPhoneNumber(phone);
        order.setPaymentMethod(paymentMethod);
        order.setStatus("UNPAID"); // Mặc định chưa thanh toán
        
        Order savedOrder = orderRepository.save(order);

        for (Item item : cart.getCartItems()) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(savedOrder);
            
            Book book = bookRepository.findById(item.getBookId())
                    .orElseThrow(() -> new RuntimeException("Book not found"));
            
            detail.setBook(book);
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getPrice());
            
            // TRỪ KHO KHI MUA
            book.setQuantity(book.getQuantity() - item.getQuantity());
            bookRepository.save(book);
            
            orderDetailRepository.save(detail);
        }
        return savedOrder;
    }

    // ==================== 2. CÁC HÀM GET DỮ LIỆU ====================
    
    // Hàm tìm đơn theo ID
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    // Hàm lấy danh sách đơn của 1 User (Dùng cho UserController)
    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    // --- 👇 HÀM BẠN ĐANG THIẾU (Dùng cho AdminController) 👇 ---
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    // -----------------------------------------------------------


    // ==================== 3. CÁC HÀM XỬ LÝ TRẠNG THÁI ====================

    // Cập nhật trạng thái (Dùng cho VNPay)
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus(status);
        orderRepository.save(order);
    }

    // --- 👇 HÀM BẠN ĐANG THIẾU (Admin xác nhận giao hàng) 👇 ---
    public void markAsDelivered(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus("DELIVERED");
        orderRepository.save(order);
    }
    // -----------------------------------------------------------

    // HỦY ĐƠN HÀNG -> XÓA VÀ HOÀN KHO
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));

        if (!"UNPAID".equalsIgnoreCase(order.getStatus())) {
            throw new RuntimeException("Không thể hủy đơn hàng đã thanh toán hoặc đã giao!");
        }

        // HOÀN TRẢ SỐ LƯỢNG SÁCH VÀO KHO
        List<OrderDetail> details = orderDetailRepository.findByOrderId(orderId);
        for (OrderDetail detail : details) {
            Book book = detail.getBook();
            if (book != null) {
                book.setQuantity(book.getQuantity() + detail.getQuantity());
                bookRepository.save(book);
            }
        }

        // XÓA ĐƠN HÀNG
        orderRepository.delete(order);
    }
}