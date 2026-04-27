package es.colegiocalasanz.booktrack.dto;

public class AnnualGoalResponse {

    private final String username;
    private final int annualGoal;

    public AnnualGoalResponse(String username, int annualGoal) {
        this.username = username;
        this.annualGoal = annualGoal;
    }

    public String getUsername() {
        return username;
    }

    public int getAnnualGoal() {
        return annualGoal;
    }
}