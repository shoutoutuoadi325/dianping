package org.com.dianping.controller;

import org.com.dianping.entity.SearchHistory;
import org.com.dianping.service.SearchHistoryService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:8081",
        allowCredentials = "true",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE})
@RestController
@RequestMapping("/api/search")
public class SearchController {
    private final SearchHistoryService service;

    public SearchController(SearchHistoryService service) {
        this.service = service;
    }

    @PostMapping
    public SearchHistory saveSearch(@RequestParam String keyword,
                                    @RequestHeader("UserId") Long userId) {
        return service.saveSearch(userId, keyword);
    }

    @GetMapping
    public List<SearchHistory> getHistory(@RequestHeader("UserId") Long userId) {
        return service.getHistory(userId);
    }

    @DeleteMapping("/{id}")
    public void deleteHistory(@PathVariable Long id,
                              @RequestHeader("UserId") Long userId) {
        System.out.println("Deleting history - userId: " + userId + ", id: " + id);
        service.deleteHistory(userId, id);
    }

    @DeleteMapping
    public void clearAll(@RequestHeader("UserId") Long userId) {
        System.out.println("Clearing all history - userId: " + userId);
        service.clearAll(userId);
    }
}