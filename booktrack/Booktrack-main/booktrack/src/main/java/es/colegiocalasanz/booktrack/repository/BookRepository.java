package es.colegiocalasanz.booktrack.repository;

import es.colegiocalasanz.booktrack.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByOwnerUsername(String ownerUsername);

    Optional<Book> findByIdAndOwnerUsername(Long id, String ownerUsername);
}