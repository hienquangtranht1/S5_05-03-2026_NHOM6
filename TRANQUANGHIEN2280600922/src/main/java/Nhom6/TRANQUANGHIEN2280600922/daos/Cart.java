package Nhom6.TRANQUANGHIEN2280600922.daos;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class Cart {
    private List<Item> cartItems = new ArrayList<>();

    public void addItems(Item item) {
        boolean isExist = false;
        for (Item i : cartItems) {
            if (Objects.equals(i.getBookId(), item.getBookId())) {
                i.setQuantity(i.getQuantity() + item.getQuantity());
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            cartItems.add(item);
        }
    }

    public void removeItems(Long bookId) {
        cartItems.removeIf(item -> Objects.equals(item.getBookId(), bookId));
    }

    public void updateItems(Long bookId, int quantity) {
        for (Item item : cartItems) {
            if (Objects.equals(item.getBookId(), bookId)) {
                item.setQuantity(quantity);
                break;
            }
        }
    }

    public Double getTotalPrice() {
        return cartItems.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
    }
}