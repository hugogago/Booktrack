package es.colegiocalasanz.booktrack.service;

import es.colegiocalasanz.booktrack.dto.CatalogBookResponse;
import es.colegiocalasanz.booktrack.entity.Book;
import es.colegiocalasanz.booktrack.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class CatalogService {

    @Autowired
    private BookRepository bookRepository;

    public List<CatalogBookResponse> getCatalog(String search, String genre) {
        return bookRepository.findAll().stream()
                .filter(book -> matchesSearch(book, search))
                .filter(book -> matchesField(book.getGenre(), genre))
                .sorted(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER))
                .map(book -> new CatalogBookResponse(
                        book.getTitle(),
                        book.getAuthor(),
                        book.getGenre(),
                        book.getPublisher(),
                        book.getTotalPages()))
                .distinct()
                .toList();
    }

    private boolean matchesSearch(Book book, String search) {
        if (search == null || search.isBlank()) {
            return true;
        }
        String term = search.toLowerCase(Locale.ROOT).trim();
        return contains(book.getTitle(), term)
                || contains(book.getAuthor(), term)
                || contains(book.getGenre(), term);
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
}
