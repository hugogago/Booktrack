package es.colegiocalasanz.booktrack.controller;

import es.colegiocalasanz.booktrack.dto.CatalogBookResponse;
import es.colegiocalasanz.booktrack.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    @Autowired
    private CatalogService catalogService;

    @GetMapping
    public List<CatalogBookResponse> getCatalog(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String genre) {
        return catalogService.getCatalog(search, genre);
    }
}
