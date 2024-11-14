package com.example.Crawler.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Crawler.model.Link;
import com.example.Crawler.repository.LinkRepository;


@Service
public class CrawlerService {

    @Autowired
    private LinkRepository linkRepository;
    private final GoogleSheetsService googleSheetsService;

    public CrawlerService() throws GeneralSecurityException, IOException {
        this.googleSheetsService = new GoogleSheetsService();
    }
    
    public void startNewCrawl(String url, int depth, String keyword) {
        try {
            googleSheetsService.addUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        crawl(url, depth, "", "", keyword);
    }

    private void crawl(String url, int depth, String parentId, String label, String keyword) {
        if (depth <= 0) return;

        try {
            Document document = Jsoup.connect(url).get();
            String currentId = generateMD5(url);
            String pageTitle = getLabelFromHtml(document);
            Elements links = document.select("a[href]");

            Link linkDocument = new Link(currentId, parentId, url, pageTitle, links.eachAttr("href"));
            linkRepository.save(linkDocument);

            links.forEach(link -> {
                String linkUrl = link.attr("abs:href");
                if (keyword.isEmpty() || linkUrl.contains(keyword)) {
                    crawl(linkUrl, depth - 1, currentId, pageTitle, keyword);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getLabelFromHtml(Document document) {
        return document.title();
    }

    private String generateMD5(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void purgeDatabase() {
        linkRepository.deleteAll();
    }

    public void purgeCollection(String collectionName) {
        linkRepository.deleteAll();
    }

    
    public Map<String, List<Link>> displayTree(String parentId) {
        List<Link> links = linkRepository.findAll();
        Map<String, List<Link>> tree = new HashMap<>();
        
        links.forEach(link -> {
            if (parentId.equals(link.getParentId())) {
                if (!tree.containsKey(parentId)) {
                    tree.put(parentId, new ArrayList<>());
                }
                tree.get(parentId).add(link);
            }
        });
        
        return tree;
    }
    
    
    
    

    private void displayTreeNode(Link node, int depth, List<Link> links, Set<String> processedNodes) {
        String indent = new String(new char[depth * 4]).replace("\0", " ");
        System.out.printf("%s - %s: %s (%s)%n", indent, node.getId(), node.getLabel(), node.getUrl());

        processedNodes.add(node.getId());

        links.stream()
            .filter(child -> child.getParentId().equals(node.getId()))
            .forEach(child -> displayTreeNode(child, depth + 1, links, processedNodes));
    }

    public void deleteAllRelated(String parentId) {
        List<Link> allLinks = linkRepository.findAll();
        Set<String> idsToDelete = new HashSet<>();
        collectAllRelatedIds(parentId, allLinks, idsToDelete);

        if (!idsToDelete.isEmpty()) {
            linkRepository.deleteAllById(idsToDelete);
        }
    }

    private void collectAllRelatedIds(String parentId, List<Link> allLinks, Set<String> idsToDelete) {
        Queue<String> queue = new LinkedList<>();
        queue.add(parentId);

        while (!queue.isEmpty()) {
            String currentId = queue.poll();
            idsToDelete.add(currentId);

            List<Link> relatedLinks = allLinks.stream()
                .filter(link -> currentId.equals(link.getParentId()))
                .collect(Collectors.toList());

            for (Link link : relatedLinks) {
                queue.add(link.getId());
            }
        }
    }
}
