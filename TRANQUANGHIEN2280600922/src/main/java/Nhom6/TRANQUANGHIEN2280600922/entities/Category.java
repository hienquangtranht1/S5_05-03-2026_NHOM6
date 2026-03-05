package Nhom6.TRANQUANGHIEN2280600922.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "category")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 50, nullable = false)
    @NotBlank(message = "Tên thể loại không được để trống")
    @Size(max = 50, message = "Tên thể loại không quá 50 ký tự")
    private String name;

    // Quan hệ với sách: CascadeType.ALL ở đây có thể gây xóa sách khi xóa thể loại
    // Vì vậy ta sẽ xử lý logic chuyển sách về "Chưa biết" ở tầng Service
    @OneToMany(mappedBy = "category")
    private List<Book> books = new ArrayList<>();
}