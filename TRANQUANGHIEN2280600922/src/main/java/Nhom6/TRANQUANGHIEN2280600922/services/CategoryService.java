package Nhom6.TRANQUANGHIEN2280600922.services;

import Nhom6.TRANQUANGHIEN2280600922.entities.Book;
import Nhom6.TRANQUANGHIEN2280600922.entities.Category;
import Nhom6.TRANQUANGHIEN2280600922.repositories.IBookRepository;
import Nhom6.TRANQUANGHIEN2280600922.repositories.ICategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final ICategoryRepository categoryRepository;
    private final IBookRepository bookRepository;

    // Lấy danh sách tất cả các thể loại
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Lấy thông tin thể loại theo ID
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    // Thêm thể loại mới
    public void addCategory(Category category) {
        categoryRepository.save(category);
    }

    // Cập nhật thông tin thể loại
    public void updateCategory(Category category) {
        categoryRepository.save(category);
    }

    // Chức năng xóa thể loại với logic chuyển sách sang "Chưa biết"
    @Transactional
    public void deleteCategory(Long id) {
        Category categoryToDelete = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Thể loại không tồn tại"));

        // Ngăn chặn xóa chính thể loại mặc định "Chưa biết"
        if ("Chưa biết".equalsIgnoreCase(categoryToDelete.getName())) {
            throw new RuntimeException("Không thể xóa thể loại mặc định này!");
        }

        // 1. Tìm hoặc tạo thể loại "Chưa biết" (Unknown) nếu chưa tồn tại
        Category unknownCategory = categoryRepository.findAll().stream()
                .filter(c -> "Chưa biết".equalsIgnoreCase(c.getName()))
                .findFirst()
                .orElseGet(() -> {
                    Category newCat = new Category();
                    newCat.setName("Chưa biết");
                    return categoryRepository.save(newCat);
                });

        // 2. Chuyển toàn bộ sách thuộc thể loại sắp xóa sang thể loại "Chưa biết"
        // Tìm tất cả sách có category_id trùng với ID cần xóa
        List<Book> books = bookRepository.findAll().stream()
                .filter(b -> b.getCategory() != null && b.getCategory().getId().equals(id))
                .toList();

        for (Book book : books) {
            book.setCategory(unknownCategory);
        }
        bookRepository.saveAll(books);

        // 3. Thực hiện xóa thể loại cũ khỏi cơ sở dữ liệu
        categoryRepository.delete(categoryToDelete);
    }

    /**
* Đếm số lượng sách thực tế của một thể loại.
     * Chỉ tính những sách chưa bị đánh dấu xóa mềm (isDeleted = false).
     */
    public long countActiveBooks(Long categoryId) {
        return bookRepository.findAll().stream()
                .filter(b -> b.getCategory() != null 
                             && b.getCategory().getId().equals(categoryId) 
                             && !b.isDeleted()) // Chỉ đếm sách chưa xóa mềm
                .count();
    }
}