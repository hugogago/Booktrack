package es.colegiocalasanz.booktrack.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import es.colegiocalasanz.booktrack.entity.Book;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class BookRepository {

    private static final Logger LOGGER = Logger.getLogger(BookRepository.class.getName());

    private final ObjectMapper objectMapper;
    private final Path booksFile = resolveDataFile("books.json");
    private List<Book> books = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public BookRepository() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        loadBooks();
    }

    private void loadBooks() {
        if (Files.exists(booksFile)) {
            try {
                books = objectMapper.readValue(booksFile.toFile(), new TypeReference<List<Book>>() {});
                if (!books.isEmpty()) {
                    idGenerator.set(books.stream().mapToLong(Book::getId).max().orElse(0) + 1);
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "No se pudieron cargar los libros", e);
            }
        }
    }

    private void saveBooks() {
        try {
            Files.createDirectories(booksFile.getParent());
            objectMapper.writeValue(booksFile.toFile(), books);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "No se pudieron guardar los libros", e);
        }
    }

    private Path resolveDataFile(String fileName) {
        Path[] candidates = new Path[] {
                Path.of("src", "main", "resources", fileName),
                Path.of("booktrack", "src", "main", "resources", fileName)
        };

        for (Path candidate : candidates) {
            if (Files.exists(candidate) || Files.exists(candidate.getParent())) {
                return candidate;
            }
        }

        return candidates[0];
    }

    public List<Book> findByOwnerUsername(String username) {
        return books.stream()
                .filter(book -> username.equals(book.getOwnerUsername()))
                .toList();
    }

    public Optional<Book> findByIdAndOwnerUsername(Long id, String username) {
        return books.stream()
                .filter(book -> id.equals(book.getId()) && username.equals(book.getOwnerUsername()))
                .findFirst();
    }

    public Book save(Book book) {
        if (book.getId() == null) {
            book.setId(idGenerator.getAndIncrement());
            books.add(book);
        } else {
            books.removeIf(existing -> existing.getId().equals(book.getId()));
            books.add(book);
        }

        saveBooks();
        return book;
    }

    public void delete(Book book) {
        books.removeIf(existing -> existing.getId().equals(book.getId()));
        saveBooks();
    }
}