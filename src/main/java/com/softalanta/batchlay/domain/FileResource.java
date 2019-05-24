package com.softalanta.batchlay.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class FileResource {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long uid = System.currentTimeMillis();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
    private FileResourceDomain domain;
    private Long contentLength;
    private String contentType;
    private String contentMD5;

    public FileResource(String name, FileResourceDomain domain, Long contentLength, String contentType, String contentMD5) {
        this.name = name;
        this.domain = domain;
        this.contentLength = contentLength;
        this.contentType = contentType;
        this.contentMD5 = contentMD5;
    }

    public FileResource() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public FileResourceDomain getDomain() {
        return domain;
    }

    public void setDomain(FileResourceDomain domain) {
        this.domain = domain;
    }

    public Long getContentLength() {
        return contentLength;
    }

    public void setContentLength(Long contentLength) {
        this.contentLength = contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentMD5() {
        return contentMD5;
    }

    public void setContentMD5(String contentMD5) {
        this.contentMD5 = contentMD5;
    }
}
