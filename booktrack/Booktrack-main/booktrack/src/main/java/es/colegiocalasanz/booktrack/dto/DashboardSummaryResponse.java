package es.colegiocalasanz.booktrack.dto;

public class DashboardSummaryResponse {

    private final int currentMonthReadCount;
    private final int currentYearReadCount;
    private final int inProgressCount;
    private final int pendingCount;
    private final int totalBooks;
    private final int annualGoal;
    private final double annualGoalProgress;

    public DashboardSummaryResponse(int currentMonthReadCount, int currentYearReadCount, int inProgressCount,
                                    int pendingCount, int totalBooks, int annualGoal, double annualGoalProgress) {
        this.currentMonthReadCount = currentMonthReadCount;
        this.currentYearReadCount = currentYearReadCount;
        this.inProgressCount = inProgressCount;
        this.pendingCount = pendingCount;
        this.totalBooks = totalBooks;
        this.annualGoal = annualGoal;
        this.annualGoalProgress = annualGoalProgress;
    }

    public int getCurrentMonthReadCount() {
        return currentMonthReadCount;
    }

    public int getCurrentYearReadCount() {
        return currentYearReadCount;
    }

    public int getInProgressCount() {
        return inProgressCount;
    }

    public int getPendingCount() {
        return pendingCount;
    }

    public int getTotalBooks() {
        return totalBooks;
    }

    public int getAnnualGoal() {
        return annualGoal;
    }

    public double getAnnualGoalProgress() {
        return annualGoalProgress;
    }
}