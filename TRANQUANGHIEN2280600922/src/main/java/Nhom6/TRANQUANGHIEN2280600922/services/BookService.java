package Nhom6.TRANQUANGHIEN2280600922.services;

import Nhom6.TRANQUANGHIEN2280600922.entities.Book;
import Nhom6.TRANQUANGHIEN2280600922.repositories.IBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private IBookRepository bookRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        return optionalBook.orElse(null);
    }

    public void addBook(Book book) {
        bookRepository.save(book);
    }

    public void updateBook(Book book) {
        bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        Optional<Book> bookOptional = bookRepository.findById(id);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            
            book.setQuantity(0);
            
            book.setDeleted(false); 
            
            bookRepository.save(book);
        }
    }

    public List<Book> searchBooks(String keyword) {
        return bookRepository.searchBooks(keyword, null, null, null);
    }

    public List<Book> searchBooksFull(String keyword, Long categoryId, Double minPrice, Double maxPrice) {
        return bookRepository.searchBooks(keyword, categoryId, minPrice, maxPrice);
    }
}