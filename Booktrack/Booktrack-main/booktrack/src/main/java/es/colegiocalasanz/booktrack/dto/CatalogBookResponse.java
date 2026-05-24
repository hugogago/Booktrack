package es.colegiocalasanz.booktrack.dto;

public class CatalogBookResponse {

    private String title;
    private String author;
    private String genre;
    private String publisher;
    private int totalPages;

    public CatalogBookResponse(String title, String author, String genre, String publisher, int totalPages) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.publisher = publisher;
        this.totalPages = totalPages;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }
    public String getPublisher() { return publisher; }
    public int getTotalPages() { return totalPages; }
}
