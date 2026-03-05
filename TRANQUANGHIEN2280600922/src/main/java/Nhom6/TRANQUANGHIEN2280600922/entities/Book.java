package Nhom6.TRANQUANGHIEN2280600922.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @Entity @Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên sách không được để trống")
    private String title;

    @NotBlank(message = "Tác giả không được để trống")
    private String author;

    @NotNull(message = "Giá tiền không được để trống")
    private Double price;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0)
    private Integer quantity;

    private String image;

    @Column(name = "is_deleted")
    private boolean isDeleted = false; // Mặc định là false (chưa xóa)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}