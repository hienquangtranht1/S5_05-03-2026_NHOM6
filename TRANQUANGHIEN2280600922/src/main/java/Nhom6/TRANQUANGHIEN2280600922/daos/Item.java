package Nhom6.TRANQUANGHIEN2280600922.daos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    private Long bookId;
    private String bookName;
    private Double price;
    private int quantity;
    private String image;           // Hình ảnh sách
    private int maxQuantity;        // Số lượng tối đa trong kho
    private boolean isAvailable;    // Còn hàng hay không
}