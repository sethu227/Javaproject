package com.example.appmanager.model;

import jakarta.persistence.*;

@Entity
public class ApplicationFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String path;
    private String hash;
    private long size;
    private String fileType; // e.g., extension or MIME type
    private double entropy; // Shannon entropy for file content
    private String ssdeepHash;
    private double similarityScore; // Percentage similarity (0-100)

    @ManyToOne
    private Category category;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getHash() { return hash; }
    public void setHash(String hash) { this.hash = hash; }
    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public double getEntropy() { return entropy; }
    public void setEntropy(double entropy) { this.entropy = entropy; }
    public String getSsdeepHash() { return ssdeepHash; }
    public void setSsdeepHash(String ssdeepHash) { this.ssdeepHash = ssdeepHash; }
    public double getSimilarityScore() { return similarityScore; }
    public void setSimilarityScore(double similarityScore) { this.similarityScore = similarityScore; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}