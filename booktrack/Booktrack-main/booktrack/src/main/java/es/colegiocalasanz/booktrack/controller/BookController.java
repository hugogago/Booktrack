package es.colegiocalasanz.booktrack.controller;

import es.colegiocalasanz.booktrack.dto.BookRequest;
import es.colegiocalasanz.booktrack.entity.Book;
import es.colegiocalasanz.booktrack.entity.ReadingStatus;
import es.colegiocalasanz.booktrack.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public List<Book> getBooks(Authentication authentication,
                               @RequestParam(required = false) String search,
                               @RequestParam(required = false) ReadingStatus status,
                               @RequestParam(required = false) String author,
                               @RequestParam(required = false) String genre,
                               @RequestParam(required = false) String publisher) {
        return bookService.getBooks(authentication.getName(), search, status, author, genre, publisher);
    }

    @GetMapping("/{id}")
    public Book getBook(Authentication authentication, @PathVariable Long id) {
        return bookService.getBook(authentication.getName(), id);
    }

    @PostMapping
    public Book createBook(Authentication authentication, @RequestBody BookRequest request) {
        return bookService.createBook(authentication.getName(), request);
    }

    @PutMapping("/{id}")
    public Book updateBook(Authentication authentication, @PathVariable Long id, @RequestBody BookRequest request) {
        return bookService.updateBook(authentication.getName(), id, request);
    }

    @PutMapping("/{id}/status")
    public Book updateStatus(Authentication authentication, @PathVariable Long id,
                             @RequestParam ReadingStatus status) {
        return bookService.updateStatus(authentication.getName(), id, status);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(Authentication authentication, @PathVariable Long id) {
        bookService.deleteBook(authentication.getName(), id);
    }
}