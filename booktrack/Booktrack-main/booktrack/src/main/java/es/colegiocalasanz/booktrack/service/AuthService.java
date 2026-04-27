package es.colegiocalasanz.booktrack.service;

import es.colegiocalasanz.booktrack.dto.AuthResponse;
import es.colegiocalasanz.booktrack.dto.LoginRequest;
import es.colegiocalasanz.booktrack.dto.RegisterRequest;
import es.colegiocalasanz.booktrack.entity.User;
import es.colegiocalasanz.booktrack.repository.UserRepository;
import es.colegiocalasanz.booktrack.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public AuthResponse login(LoginRequest request) throws Exception {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new Exception("Contraseña incorrecta");
        }

        String token = jwtTokenProvider.generateToken(user.getUsername());
        return new AuthResponse(token, user.getUsername(), user.getEmail());
    }

    public AuthResponse register(RegisterRequest request) throws Exception {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new Exception("Las contraseñas no coinciden");
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new Exception("El usuario ya existe");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new Exception("El email ya está registrado");
        }

        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getEmail()
        );

        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getUsername());
        return new AuthResponse(token, user.getUsername(), user.getEmail());
    }
}
