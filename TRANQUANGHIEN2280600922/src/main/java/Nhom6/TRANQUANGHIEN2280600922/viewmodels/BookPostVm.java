package Nhom6.TRANQUANGHIEN2280600922.viewmodels;

import Nhom6.TRANQUANGHIEN2280600922.entities.Book;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BookPostVm(String title, String author, Double price, Long categoryId) {

    public static BookPostVm from(@NotNull Book book) {
        return new BookPostVm(
                book.getTitle(),
                book.getAuthor(),
                book.getPrice(),
                book.getCategory() != null ? book.getCategory().getId() : null
        );
    }

    public Book toBook() {
        Book book = new Book();
        book.setTitle(this.title());
        book.setAuthor(this.author());
        book.setPrice(this.price());
        return book;
    }
}