package Nhom6.TRANQUANGHIEN2280600922.repositories;

import Nhom6.TRANQUANGHIEN2280600922.entities.Order;
import Nhom6.TRANQUANGHIEN2280600922.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IOrderRepository extends JpaRepository<Order, Long> {
    // Giữ cái cũ nếu bạn có dùng ở đâu đó
    List<Order> findByUser(User user);
    
    List<Order> findByUserId(Long userId);
}