package Nhom6.TRANQUANGHIEN2280600922.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_price")
    private Double totalPrice;

    @Column(name = "shipping_address")
    private String shippingAddress;

    @Column(name = "phone_number")
    private String phoneNumber;

    // --- THÊM DÒNG NÀY ĐỂ SỬA LỖI setPaymentMethod ---
    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "status")
    private String status; // UNPAID, PAID, CANCELLED, DELIVERED

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;
}