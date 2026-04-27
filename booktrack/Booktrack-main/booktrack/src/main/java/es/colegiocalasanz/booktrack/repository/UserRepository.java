package es.colegiocalasanz.booktrack.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.colegiocalasanz.booktrack.entity.User;
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
public class UserRepository {

    private static final Logger LOGGER = Logger.getLogger(UserRepository.class.getName());

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Path usersFile = resolveDataFile("users.json");
    private List<User> users = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public UserRepository() {
        loadUsers();
    }

    private void loadUsers() {
        if (Files.exists(usersFile)) {
            try {
                users = objectMapper.readValue(usersFile.toFile(), new TypeReference<List<User>>() {});
                if (!users.isEmpty()) {
                    idGenerator.set(users.stream().mapToLong(User::getId).max().orElse(0) + 1);
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "No se pudieron cargar los usuarios", e);
            }
        }
    }

    private void saveUsers() {
        try {
            Files.createDirectories(usersFile.getParent());
            objectMapper.writeValue(usersFile.toFile(), users);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "No se pudieron guardar los usuarios", e);
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

    public Optional<User> findByUsername(String username) {
        return users.stream().filter(u -> u.getUsername().equals(username)).findFirst();
    }

    public Optional<User> findByEmail(String email) {
        return users.stream().filter(u -> u.getEmail().equals(email)).findFirst();
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
            users.add(user);
        } else {
            // Update existing
            users.removeIf(u -> u.getId().equals(user.getId()));
            users.add(user);
        }
        saveUsers();
        return user;
    }

    public long count() {
        return users.size();
    }
}
