package Nhom6.TRANQUANGHIEN2280600922.controllers;

import Nhom6.TRANQUANGHIEN2280600922.entities.Book;
import Nhom6.TRANQUANGHIEN2280600922.services.BookService;
import Nhom6.TRANQUANGHIEN2280600922.services.CategoryService;
import Nhom6.TRANQUANGHIEN2280600922.viewmodels.BookGetVm;
import Nhom6.TRANQUANGHIEN2280600922.viewmodels.BookPostVm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ApiController {

    private final BookService bookService;
    private final CategoryService categoryService;

    @GetMapping("/books")
    public ResponseEntity<List<BookGetVm>> getAllBooks(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy) {
        
        return ResponseEntity.ok(bookService.getAllBooks()
                .stream()
                .map(BookGetVm::from)
                .toList());
    }

    @GetMapping("/books/id/{id}")
    public ResponseEntity<BookGetVm> getBookById(@PathVariable Long id) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(BookGetVm.from(book));
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBookById(@PathVariable Long id) {
        if (bookService.getBookById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        bookService.deleteBook(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/books/search")
    public ResponseEntity<List<BookGetVm>> searchBooks(@RequestParam String keyword) {
        return ResponseEntity.ok(bookService.searchBooks(keyword)
                .stream()
                .map(BookGetVm::from)
                .toList());
    }

    @PostMapping("/books")
    public ResponseEntity<BookGetVm> createBook(@RequestBody BookPostVm bookPostVm) {
        Book book = bookPostVm.toBook();
        categoryService.getCategoryById(bookPostVm.categoryId())
                .ifPresent(book::setCategory);
        bookService.addBook(book);
        return ResponseEntity.ok(BookGetVm.from(book));
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<BookGetVm> updateBook(@PathVariable Long id, @RequestBody BookPostVm bookPostVm) {
        Book existingBook = bookService.getBookById(id);
        if (existingBook == null) {
            return ResponseEntity.notFound().build();
        }
        existingBook.setTitle(bookPostVm.title());
        existingBook.setAuthor(bookPostVm.author());
        existingBook.setPrice(bookPostVm.price());
        categoryService.getCategoryById(bookPostVm.categoryId())
                .ifPresent(existingBook::setCategory);
        bookService.updateBook(existingBook);
        return ResponseEntity.ok(BookGetVm.from(existingBook));
    }
}