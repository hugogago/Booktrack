package es.colegiocalasanz.booktrack.entity;

public class User {

    private Long id;
    private String username;
    private String password;
    private String email;
    private String role = "ROLE_USER";
    private int annualGoal = 12;

    // Constructores
    public User() {
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getAnnualGoal() {
        return annualGoal;
    }

    public void setAnnualGoal(int annualGoal) {
        this.annualGoal = annualGoal;
    }
}
