package es.colegiocalasanz.booktrack.dto;

import java.util.List;

public class StatisticsResponse {

    private final List<MonthlyReadsPoint> monthlyReads;
    private final List<GenreBreakdownPoint> genreBreakdown;

    public StatisticsResponse(List<MonthlyReadsPoint> monthlyReads, List<GenreBreakdownPoint> genreBreakdown) {
        this.monthlyReads = monthlyReads;
        this.genreBreakdown = genreBreakdown;
    }

    public List<MonthlyReadsPoint> getMonthlyReads() {
        return monthlyReads;
    }

    public List<GenreBreakdownPoint> getGenreBreakdown() {
        return genreBreakdown;
    }

    public static class MonthlyReadsPoint {
        private final String label;
        private final int value;

        public MonthlyReadsPoint(String label, int value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public int getValue() {
            return value;
        }
    }

    public static class GenreBreakdownPoint {
        private final String genre;
        private final long count;
        private final double percentage;

        public GenreBreakdownPoint(String genre, long count, double percentage) {
            this.genre = genre;
            this.count = count;
            this.percentage = percentage;
        }

        public String getGenre() {
            return genre;
        }

        public long getCount() {
            return count;
        }

        public double getPercentage() {
            return percentage;
        }
    }
}