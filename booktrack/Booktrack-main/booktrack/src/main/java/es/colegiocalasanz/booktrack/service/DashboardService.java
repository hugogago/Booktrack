package es.colegiocalasanz.booktrack.service;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import es.colegiocalasanz.booktrack.dto.AnnualGoalResponse;
import es.colegiocalasanz.booktrack.dto.DashboardSummaryResponse;
import es.colegiocalasanz.booktrack.dto.StatisticsResponse;
import es.colegiocalasanz.booktrack.entity.Book;
import es.colegiocalasanz.booktrack.entity.ReadingStatus;
import es.colegiocalasanz.booktrack.entity.User;
import es.colegiocalasanz.booktrack.repository.BookRepository;
import es.colegiocalasanz.booktrack.repository.UserRepository;

@Service
public class DashboardService {

        private static final Locale SPANISH_LOCALE = Locale.forLanguageTag("es-ES");

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    public DashboardSummaryResponse getSummary(String username) {
        List<Book> books = bookRepository.findByOwnerUsername(username);
        LocalDate today = LocalDate.now();

        int currentMonthReadCount = (int) books.stream()
                .filter(book -> book.getStatus() == ReadingStatus.READ)
                .filter(book -> book.getFinishedDate() != null)
                .filter(book -> book.getFinishedDate().getYear() == today.getYear())
                .filter(book -> book.getFinishedDate().getMonth() == today.getMonth())
                .count();

        int currentYearReadCount = (int) books.stream()
                .filter(book -> book.getStatus() == ReadingStatus.READ)
                .filter(book -> book.getFinishedDate() != null)
                .filter(book -> book.getFinishedDate().getYear() == today.getYear())
                .count();

        int inProgressCount = (int) books.stream()
                .filter(book -> book.getStatus() == ReadingStatus.IN_PROGRESS)
                .count();

        int pendingCount = (int) books.stream()
                .filter(book -> book.getStatus() == ReadingStatus.PENDING)
                .count();

        User user = getUser(username);
        double annualGoalProgress = user.getAnnualGoal() <= 0
                ? 0
                : Math.min(100.0, (currentYearReadCount * 100.0) / user.getAnnualGoal());

        return new DashboardSummaryResponse(
                currentMonthReadCount,
                currentYearReadCount,
                inProgressCount,
                pendingCount,
                books.size(),
                user.getAnnualGoal(),
                annualGoalProgress
        );
    }

    public StatisticsResponse getStatistics(String username) {
        List<Book> books = bookRepository.findByOwnerUsername(username);
        int currentYear = LocalDate.now().getYear();

        Map<Month, Long> monthlyReadCounts = books.stream()
                .filter(book -> book.getStatus() == ReadingStatus.READ)
                .filter(book -> book.getFinishedDate() != null)
                .filter(book -> book.getFinishedDate().getYear() == currentYear)
                .collect(Collectors.groupingBy(book -> book.getFinishedDate().getMonth(), Collectors.counting()));

        List<StatisticsResponse.MonthlyReadsPoint> monthlyReads = Arrays.stream(Month.values())
                .map(month -> new StatisticsResponse.MonthlyReadsPoint(
                        month.getDisplayName(TextStyle.SHORT, SPANISH_LOCALE),
                        monthlyReadCounts.getOrDefault(month, 0L).intValue()))
                .toList();

        List<Book> readBooks = books.stream()
                .filter(book -> book.getStatus() == ReadingStatus.READ)
                .filter(book -> book.getGenre() != null && !book.getGenre().isBlank())
                .toList();

        long totalReadWithGenre = readBooks.size();

        List<StatisticsResponse.GenreBreakdownPoint> genreBreakdown = readBooks.stream()
                .collect(Collectors.groupingBy(Book::getGenre, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(entry -> new StatisticsResponse.GenreBreakdownPoint(
                        entry.getKey(),
                        entry.getValue(),
                        totalReadWithGenre == 0 ? 0 : (entry.getValue() * 100.0) / totalReadWithGenre))
                .toList();

        return new StatisticsResponse(monthlyReads, genreBreakdown);
    }

        public AnnualGoalResponse updateAnnualGoal(String username, int annualGoal) {
        if (annualGoal < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El objetivo anual no puede ser negativo");
        }

        User user = getUser(username);
        user.setAnnualGoal(annualGoal);
                User updatedUser = userRepository.save(user);
                return new AnnualGoalResponse(updatedUser.getUsername(), updatedUser.getAnnualGoal());
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }
}