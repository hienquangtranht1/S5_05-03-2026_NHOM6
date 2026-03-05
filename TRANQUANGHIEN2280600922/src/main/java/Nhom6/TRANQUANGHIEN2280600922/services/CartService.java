package Nhom6.TRANQUANGHIEN2280600922.services;

import Nhom6.TRANQUANGHIEN2280600922.daos.Cart;
import Nhom6.TRANQUANGHIEN2280600922.daos.Item;
import Nhom6.TRANQUANGHIEN2280600922.entities.Invoice;
import Nhom6.TRANQUANGHIEN2280600922.entities.ItemInvoice;
import Nhom6.TRANQUANGHIEN2280600922.entities.Book;
import Nhom6.TRANQUANGHIEN2280600922.repositories.IBookRepository;
import Nhom6.TRANQUANGHIEN2280600922.repositories.IInvoiceRepository;
import Nhom6.TRANQUANGHIEN2280600922.repositories.IItemInvoiceRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class, Throwable.class})
public class CartService {
    private static final String CART_SESSION_KEY = "cart";
    private final IInvoiceRepository invoiceRepository;
    private final IItemInvoiceRepository itemInvoiceRepository;
    private final IBookRepository bookRepository;

    public Cart getCart(@NotNull HttpSession session) {
        return Optional.ofNullable((Cart) session.getAttribute(CART_SESSION_KEY))
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    session.setAttribute(CART_SESSION_KEY, cart);
                    return cart;
                });
    }

    public void updateCart(@NotNull HttpSession session, Cart cart) {
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public void removeCart(@NotNull HttpSession session) {
        session.removeAttribute(CART_SESSION_KEY);
    }

    public int getSumQuantity(@NotNull HttpSession session) {
        return getCart(session).getCartItems().stream().mapToInt(Item::getQuantity).sum();
    }

    /**
     * Cập nhật: Tự động đồng bộ giá từ Database cho từng sản phẩm trong giỏ hàng
     */
    public double getSumPrice(@NotNull HttpSession session) {
        Cart cart = getCart(session);
        double total = 0;
        
        for (Item item : cart.getCartItems()) {
            // Luôn lấy giá mới nhất từ DB để cập nhật vào giỏ hàng session
            Optional<Book> book = bookRepository.findById(item.getBookId());
            if (book.isPresent()) {
                item.setPrice(book.get().getPrice()); // Cập nhật giá mới nhất từ admin
            }
            total += item.getPrice() * item.getQuantity();
        }
        
        updateCart(session, cart); // Lưu lại trạng thái giỏ hàng đã cập nhật giá
        return total;
    }

    public void saveCart(@NotNull HttpSession session) {
        var cart = getCart(session);
        if (cart.getCartItems().isEmpty()) return;

        var invoice = new Invoice();
        invoice.setInvoiceDate(new Date());
        // Sử dụng phương thức getSumPrice để đảm bảo giá hóa đơn là giá mới nhất
        invoice.setPrice(getSumPrice(session));
        
        Invoice savedInvoice = invoiceRepository.save(invoice);

        cart.getCartItems().forEach(item -> {
            var itemInvoice = new ItemInvoice();
            itemInvoice.setInvoice(savedInvoice);
            itemInvoice.setQuantity(item.getQuantity());
            itemInvoice.setBook(bookRepository.findById(item.getBookId()).orElseThrow());
            itemInvoiceRepository.save(itemInvoice);
        });

        removeCart(session);
    }
}