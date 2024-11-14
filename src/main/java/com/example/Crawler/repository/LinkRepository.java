package com.example.Crawler.repository;

import java.util.Set;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.Crawler.model.Link;

public interface LinkRepository extends MongoRepository<Link, String> {
    void deleteAllById(Set<String> ids);
}

