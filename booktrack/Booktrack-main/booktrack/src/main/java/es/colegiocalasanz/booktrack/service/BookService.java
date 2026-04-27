package es.colegiocalasanz.booktrack.service;

import es.colegiocalasanz.booktrack.dto.BookRequest;
import es.colegiocalasanz.booktrack.entity.Book;
import es.colegiocalasanz.booktrack.entity.ReadingStatus;
import es.colegiocalasanz.booktrack.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> getBooks(String username, String search, ReadingStatus status, String author,
                               String genre, String publisher) {
        return bookRepository.findByOwnerUsername(username).stream()
                .filter(book -> matchesSearch(book, search))
                .filter(book -> status == null || book.getStatus() == status)
                .filter(book -> matchesField(book.getAuthor(), author))
                .filter(book -> matchesField(book.getGenre(), genre))
                .filter(book -> matchesField(book.getPublisher(), publisher))
                .sorted(Comparator.comparing(Book::getAddedDate, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public Book getBook(String username, Long id) {
        return bookRepository.findByIdAndOwnerUsername(id, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Libro no encontrado"));
    }

    public Book createBook(String username, BookRequest request) {
        validateRequest(request);

        Book book = new Book();
        book.setOwnerUsername(username);
        book.setAddedDate(LocalDate.now());
        applyRequest(book, request);
        return bookRepository.save(book);
    }

    public Book updateBook(String username, Long id, BookRequest request) {
        validateRequest(request);

        Book book = getBook(username, id);
        applyRequest(book, request);
        return bookRepository.save(book);
    }

    public Book updateStatus(String username, Long id, ReadingStatus status) {
        Book book = getBook(username, id);
        book.setStatus(status);

        if (status == ReadingStatus.READ && book.getFinishedDate() == null) {
            book.setFinishedDate(LocalDate.now());
        }

        if (status == ReadingStatus.IN_PROGRESS && book.getStartedDate() == null) {
            book.setStartedDate(LocalDate.now());
        }

        if (status == ReadingStatus.PENDING) {
            book.setFinishedDate(null);
        }

        return bookRepository.save(book);
    }

    public void deleteBook(String username, Long id) {
        Book book = getBook(username, id);
        bookRepository.delete(book);
    }

    private void validateRequest(BookRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El titulo es obligatorio");
        }

        if (request.getAuthor() == null || request.getAuthor().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El autor es obligatorio");
        }
    }

    private void applyRequest(Book book, BookRequest request) {
        book.setTitle(request.getTitle().trim());
        book.setAuthor(request.getAuthor().trim());
        book.setGenre(normalizeNullable(request.getGenre()));
        book.setPublisher(normalizeNullable(request.getPublisher()));
        book.setTotalPages(Math.max(request.getTotalPages(), 0));
        book.setPersonalRating(request.getPersonalRating());
        book.setComments(normalizeNullable(request.getComments()));
        book.setStatus(request.getStatus() == null ? ReadingStatus.PENDING : request.getStatus());
        book.setStartedDate(request.getStartedDate());
        book.setFinishedDate(request.getFinishedDate());

        if (book.getStatus() == ReadingStatus.IN_PROGRESS && book.getStartedDate() == null) {
            book.setStartedDate(LocalDate.now());
        }

        if (book.getStatus() == ReadingStatus.READ && book.getFinishedDate() == null) {
            book.setFinishedDate(LocalDate.now());
        }

        if (book.getStatus() == ReadingStatus.PENDING) {
            book.setStartedDate(null);
            book.setFinishedDate(null);
        }
    }

    private boolean matchesSearch(Book book, String search) {
        if (search == null || search.isBlank()) {
            return true;
        }

        String normalizedSearch = search.toLowerCase(Locale.ROOT).trim();
        return contains(book.getTitle(), normalizedSearch)
                || contains(book.getAuthor(), normalizedSearch)
                || contains(book.getGenre(), normalizedSearch)
                || contains(book.getPublisher(), normalizedSearch);
    }

    private boolean matchesField(String value, String filter) {
        if (filter == null || filter.isBlank()) {
            return true;
        }

        return value != null && value.toLowerCase(Locale.ROOT).contains(filter.toLowerCase(Locale.ROOT).trim());
    }

    private boolean contains(String value, String term) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(term);
    }

    private String normalizeNullable(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}