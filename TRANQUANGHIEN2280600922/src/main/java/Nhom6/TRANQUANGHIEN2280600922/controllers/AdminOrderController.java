package Nhom6.TRANQUANGHIEN2280600922.controllers;

import Nhom6.TRANQUANGHIEN2280600922.entities.Order;
import Nhom6.TRANQUANGHIEN2280600922.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    // 1. Hiển thị danh sách toàn bộ đơn hàng
    @GetMapping
    public String listOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "admin/order-list"; 
    }

    // 2. Xem chi tiết một đơn hàng
    @GetMapping("/details/{id}")
    public String viewOrderDetails(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));
        model.addAttribute("order", order);
        return "admin/order-detail";
    }

    // 3. Xử lý xác nhận giao hàng
    @PostMapping("/confirm-delivery/{id}")
    public String confirmDelivery(@PathVariable Long id, RedirectAttributes ra) {
        try {
            orderService.markAsDelivered(id);
            ra.addFlashAttribute("success", "Xác nhận đã giao hàng thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/orders";
    }

    // 4. BỔ SUNG: Xử lý hủy đơn hàng (Admin có quyền hủy đơn)
    @PostMapping("/cancel/{id}")
    public String cancelOrder(@PathVariable Long id, RedirectAttributes ra) {
        try {
            // Gọi Service để thực hiện logic hủy (Hoàn tồn kho, cập nhật trạng thái)
            orderService.cancelOrder(id);
            ra.addFlashAttribute("success", "Đã hủy đơn hàng và hoàn lại số lượng sách vào kho!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không thể hủy đơn hàng: " + e.getMessage());
        }
        return "redirect:/admin/orders";
    }
}