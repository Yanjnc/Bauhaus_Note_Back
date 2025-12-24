package com.note.back.pojo;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "note", indexes = {
        @Index(name = "idx_note_author", columnList = "author"),
        @Index(name = "idx_note_category", columnList = "category"),
        @Index(name = "idx_note_created_time", columnList = "created_time"),
        @Index(name = "idx_note_last_modified_time", columnList = "last_modified_time"),
        @Index(name = "idx_note_name_abs_content", columnList = "name, abs, content_md")
})
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "name", length = 255)
    private String name;
    
    @Column(name = "abs", length = 500)
    private String abs;

    @Column(name="content_html", columnDefinition = "TEXT")
    private String contentHtml;
    
    @Column(name="content_md", columnDefinition = "TEXT")
    private String contentMd;

    @ManyToOne
    @JoinColumn(name="author")
    private User author;

    @ManyToOne
    @JoinColumn(name="category")
    private Category category;

    @Column(name="created_time")
    private Timestamp createdTime;

    @Column(name="last_modified_time")
    private Timestamp lastModifiedTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbs() {
        return abs;
    }

    public void setAbs(String abs) {
        this.abs = abs;
    }

    public String getContentHtml() {
        return contentHtml;
    }

    public void setContentHtml(String contentHtml) {
        this.contentHtml = contentHtml;
    }

    public String getContentMd() {
        return contentMd;
    }

    public void setContentMd(String contentMd) {
        this.contentMd = contentMd;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public Timestamp getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Timestamp lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }
}
