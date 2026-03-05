package Nhom6.TRANQUANGHIEN2280600922.controllers;

import Nhom6.TRANQUANGHIEN2280600922.entities.Order;
import Nhom6.TRANQUANGHIEN2280600922.entities.User;
import Nhom6.TRANQUANGHIEN2280600922.services.OrderService;
import Nhom6.TRANQUANGHIEN2280600922.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final OrderService orderService;

    @GetMapping("/login")
    public String login() { return "user/login"; }

    @GetMapping("/register")
    public String register(@NotNull Model model) {
        model.addAttribute("user", new User());
        return "user/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult bindingResult, 
                               Model model) {
        // 1. Kiểm tra lỗi form (validation)
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "user/register";
        }

        try {
            // 2. Thử tìm user. Vì Service quăng lỗi nếu không có, nên nếu lệnh này 
            // chạy qua được có nghĩa là username ĐÃ TỒN TẠI.
            userService.findByUsername(user.getUsername());
            
            model.addAttribute("errors", new String[]{"Tên đăng nhập này đã tồn tại!"});
            return "user/register";

        } catch (UsernameNotFoundException e) {
            // 3. Nếu rơi vào catch này nghĩa là username CHƯA TỒN TẠI (Tốt)
            try {
                userService.save(user);
                userService.setDefaultRole(user.getUsername());
                return "redirect:/login?success=registered";
            } catch (Exception ex) {
                model.addAttribute("errors", new String[]{"Lỗi lưu dữ liệu: " + ex.getMessage()});
                return "user/register";
            }
        }
    }

    @GetMapping("/user/orders")
    public String myOrders(Model model, Principal principal,
                           @RequestParam(required = false) String success,
                           @RequestParam(required = false) String error) {
        if (principal == null) return "redirect:/login";
        
        // Vì Service quăng lỗi, không cần check null ở đây
        User user = userService.findByUsername(principal.getName());
        List<Order> orders = orderService.getOrdersByUser(user.getId());
        model.addAttribute("orders", orders);

        if (success != null) model.addAttribute("success", "Thao tác thành công!");
        if (error != null) model.addAttribute("error", error);

        return "user/my-orders";
    }

    @PostMapping("/user/orders/cancel/{id}")
    public String cancelOrder(@PathVariable Long id, Principal principal) {
        if (principal == null) return "redirect:/login";

        User currentUser = userService.findByUsername(principal.getName());
        Order order = orderService.getOrderById(id).orElse(null);

        if (order != null && Objects.equals(order.getUser().getId(), currentUser.getId())) {
            try {
                orderService.cancelOrder(id);
                return "redirect:/user/orders?success=deleted";
            } catch (Exception e) {
                return "redirect:/user/orders?error=" + e.getMessage();
            }
        }
        return "redirect:/user/orders?error=access_denied";
    }
}