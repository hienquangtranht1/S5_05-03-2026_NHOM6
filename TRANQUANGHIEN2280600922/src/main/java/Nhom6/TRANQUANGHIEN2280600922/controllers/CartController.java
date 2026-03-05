package Nhom6.TRANQUANGHIEN2280600922.controllers;

import Nhom6.TRANQUANGHIEN2280600922.daos.Item;
import Nhom6.TRANQUANGHIEN2280600922.entities.Book;
import Nhom6.TRANQUANGHIEN2280600922.entities.Order;
import Nhom6.TRANQUANGHIEN2280600922.entities.User;
import Nhom6.TRANQUANGHIEN2280600922.services.BookService;
import Nhom6.TRANQUANGHIEN2280600922.services.CartService;
import Nhom6.TRANQUANGHIEN2280600922.services.OrderService;
import Nhom6.TRANQUANGHIEN2280600922.services.UserService;
import Nhom6.TRANQUANGHIEN2280600922.services.VNPayService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final BookService bookService;
    private final OrderService orderService;
    private final UserService userService;
    private final VNPayService vnPayService;

    @GetMapping("/add/{id}")
    public String addToCart(HttpSession session, @PathVariable Long id) {
        try {
            Book book = bookService.getBookById(id);
            if (book == null) return "redirect:/books?error=Sách không tồn tại";
            if (book.getQuantity() <= 0) return "redirect:/books?error=Sách đã hết hàng";

            Item item = new Item();
            item.setBookId(book.getId());
            item.setBookName(book.getTitle());
            item.setPrice(book.getPrice());
            item.setQuantity(1);
            item.setImage(book.getImage());
            item.setMaxQuantity(book.getQuantity()); 
            item.setAvailable(book.getQuantity() > 0); 

            var cart = cartService.getCart(session);
            cart.addItems(item);

            return "redirect:/books?success=added_to_cart"; 
        } catch (Exception e) {
            return "redirect:/books?error=" + e.getMessage();
        }
    }

    @GetMapping
    public String showCart(HttpSession session, @NotNull Model model) {
        double totalPrice = cartService.getSumPrice(session);
        
        model.addAttribute("cart", cartService.getCart(session));
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("totalQuantity", cartService.getSumQuantity(session));
        return "cart/cart";
    }

    @GetMapping("/updateCart/{id}/{quantity}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCart(HttpSession session, @PathVariable Long id, @PathVariable int quantity) {
        Map<String, Object> response = new java.util.HashMap<>();
        
        Book book = bookService.getBookById(id);
        if (book == null) {
            response.put("success", false);
response.put("message", "Sản phẩm không tồn tại");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (quantity > book.getQuantity()) {
            response.put("success", false);
            response.put("message", "Số lượng vượt quá kho hàng (còn lại: " + book.getQuantity() + ")");
            return ResponseEntity.badRequest().body(response);
        }
        
        var cart = cartService.getCart(session);
        cart.updateItems(id, quantity);
        cartService.updateCart(session, cart); 
        
        double totalPrice = cartService.getSumPrice(session);
        
        response.put("success", true);
        response.put("message", "Cập nhật thành công");
        response.put("totalPrice", totalPrice);
        response.put("updatedQuantity", quantity);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/removeFromCart/{id}")
    public String removeFromCart(HttpSession session, @PathVariable Long id) {
        var cart = cartService.getCart(session);
        cart.removeItems(id);
        return "redirect:/cart";
    }

    @GetMapping("/clearCart")
    public String clearCart(HttpSession session) {
        cartService.removeCart(session);
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkoutForm(HttpSession session, Model model) {
        var cart = cartService.getCart(session);
        if (cart.getCartItems().isEmpty()) return "redirect:/cart";
        
        double totalPrice = cartService.getSumPrice(session);
        
        model.addAttribute("cart", cart);
        model.addAttribute("totalPrice", totalPrice);
        return "cart/checkout";
    }

    @PostMapping("/checkout")
    public String submitCheckout(@RequestParam String address, @RequestParam String phone,
                                 @RequestParam String paymentMethod, HttpSession session, Principal principal) {
        if (principal == null) return "redirect:/login";
        try {
            User user = userService.findByUsername(principal.getName());
            
            cartService.getSumPrice(session);
            
            var currentCart = cartService.getCart(session);
            if (currentCart.getCartItems().isEmpty()) return "redirect:/cart";

            Order order = orderService.createOrder(user, currentCart, address, phone, paymentMethod);
            cartService.removeCart(session);

            if (paymentMethod.equals("VNPAY")) {
                int total = (int) order.getTotalPrice().doubleValue();
                String paymentUrl = vnPayService.createOrder(total, String.valueOf(order.getId()), "http://localhost:8080/cart/payment-callback");
                return "redirect:" + paymentUrl;
            }
            return "redirect:/user/orders?success=order_placed";
        } catch (Exception e) {
            return "redirect:/cart?error=" + e.getMessage();
        }
    }
@GetMapping("/payment-callback")
    public String paymentCallback(@RequestParam Map<String, String> queryParams) {
        String vnp_ResponseCode = queryParams.get("vnp_ResponseCode");
        String orderId = queryParams.get("vnp_TxnRef");
        if (orderId != null && "00".equals(vnp_ResponseCode)) {
            orderService.updateOrderStatus(Long.parseLong(orderId), "PAID");
            return "redirect:/user/orders?success=payment_successful";
        }
        return "redirect:/user/orders?error=payment_failed";
    }
    
    @GetMapping("/repay/{id}")
    public String repayOrder(@PathVariable Long id, Principal principal) {
        if (principal == null) return "redirect:/login";
        Order order = orderService.getOrderById(id).orElse(null);
        if (order == null || !order.getUser().getUsername().equals(principal.getName())) return "redirect:/user/orders?error=invalid_order";
        if (!"UNPAID".equalsIgnoreCase(order.getStatus())) return "redirect:/user/orders?error=order_already_paid";
        try {
            int total = (int) order.getTotalPrice().doubleValue();
            String paymentUrl = vnPayService.createOrder(total, String.valueOf(order.getId()), "http://localhost:8080/cart/payment-callback");
            return "redirect:" + paymentUrl;
        } catch (Exception e) {
            return "redirect:/user/orders?error=" + e.getMessage();
        }
    }
}
