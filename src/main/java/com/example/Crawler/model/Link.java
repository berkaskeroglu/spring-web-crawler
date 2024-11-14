package com.example.Crawler.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "urls")
public class Link {
    @Id
    private String id;
    private String parentId;
    private String url;
    private String label;
    private List<String> childLinks;

    public Link(String id, String parentId, String url, String label, List<String> childLinks) {
        this.id = id;
        this.parentId = parentId;
        this.url = url;
        this.label = label;
        this.childLinks = childLinks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<String> getChildLinks() {
        return childLinks;
    }

    public void setChildLinks(List<String> childLinks) {
        this.childLinks = childLinks;
    }

    @Override
    public String toString() {
        return "Link{" +
                "id='" + id + '\'' +
                ", parentId='" + parentId + '\'' +
                ", url='" + url + '\'' +
                ", label='" + label + '\'' +
                ", childLinks=" + childLinks +
                '}';
    }
}