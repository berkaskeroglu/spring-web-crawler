package com.example.Crawler.controller;
import java.util.List;
import java.util.Map;

//import org.springframework.data.mongodb.core.aggregation.VariableOperators.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Crawler.model.Link;
import com.example.Crawler.service.CrawlerService;

@RestController
@RequestMapping("/api/crawler")
public class CrawlerController {

    private final CrawlerService crawlerService;

    public CrawlerController(CrawlerService crawlerService) {
        this.crawlerService = crawlerService;
    }

    @PostMapping("/start")
    public void startCrawl(@RequestParam String url, @RequestParam int depth, @RequestParam String keyword) {
        crawlerService.startNewCrawl(url, depth, keyword);
    }

    @PostMapping("/purge")
    public void purge(@RequestParam(required = false) String collectionName) {
        if (collectionName == null) {
            crawlerService.purgeDatabase();
        } else {
            crawlerService.purgeCollection(collectionName);
        }
    }

    // @GetMapping("/displayTree")
    // public ResponseEntity<Map<String, List<Link>>> displayTree(@RequestParam String collectionName) {
    //     Map<String, List<Link>> tree = crawlerService.displayTree(collectionName);
    //     return ResponseEntity.ok(tree);
    // }
    @GetMapping("/displayTree")
    public ResponseEntity<Map<String, List<Link>>> displayTree(@RequestParam String parentId) {
        Map<String, List<Link>> tree = crawlerService.displayTree(parentId);
        return ResponseEntity.ok(tree);
    }

    @PostMapping("/purgeAllRelated")
    public ResponseEntity<String> purgeAllRelated(@RequestParam String parentId) {
        crawlerService.deleteAllRelated(parentId);
        return ResponseEntity.ok("Related records deleted successfully.");
    }







}