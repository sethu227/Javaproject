package com.example.appmanager.service;

import com.example.appmanager.model.ApplicationFile;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DuplicateDetectorService {
    public Map<String, List<ApplicationFile>> findDuplicates(List<ApplicationFile> files) {
        Map<String, List<ApplicationFile>> groupedByHash = files.stream()
                .collect(Collectors.groupingBy(ApplicationFile::getHash));
        Map<String, List<ApplicationFile>> duplicates = groupedByHash.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        
        // Set 100% similarity for exact matches (SHA-256)
        for (List<ApplicationFile> group : duplicates.values()) {
            for (ApplicationFile file : group) {
                file.setSimilarityScore(100.0);
            }
        }

        // Hybrid: For files with unique hashes, check for further similarity
        List<ApplicationFile> nonDuplicateFiles = files.stream()
                .filter(f -> groupedByHash.get(f.getHash()).size() == 1)
                .collect(Collectors.toList());
        Map<String, List<ApplicationFile>> hybridDuplicates = new HashMap<>();
        boolean[] visited = new boolean[nonDuplicateFiles.size()];
        for (int i = 0; i < nonDuplicateFiles.size(); i++) {
            if (visited[i]) continue;
            ApplicationFile fileA = nonDuplicateFiles.get(i);
            List<ApplicationFile> group = new java.util.ArrayList<>();
            group.add(fileA);
            for (int j = i + 1; j < nonDuplicateFiles.size(); j++) {
                if (visited[j]) continue;
                ApplicationFile fileB = nonDuplicateFiles.get(j);
                double similarity = calculateSimilarity(fileA, fileB);
                if (similarity > getSimilarityThreshold(fileA.getFileType())) {
                    fileB.setSimilarityScore(similarity);
                    group.add(fileB);
                    visited[j] = true;
                }
            }
            if (group.size() > 1) {
                // Set similarity for the first file based on the group
                double avgSimilarity = group.stream()
                    .mapToDouble(ApplicationFile::getSimilarityScore)
                    .average()
                    .orElse(0.0);
                fileA.setSimilarityScore(avgSimilarity);
                
                // Use a synthetic key for hybrid groups
                String key = "hybrid-" + fileA.getName() + "-" + fileA.getSize();
                hybridDuplicates.put(key, group);
            }
        }
        // Merge SHA and hybrid duplicates
        duplicates.putAll(hybridDuplicates);
        return duplicates;
    }

    private double calculateSimilarity(ApplicationFile a, ApplicationFile b) {
        // Text file similarity
        if (a.getFileType().equals("txt") && b.getFileType().equals("txt")) {
            try {
                double jaccard = jaccardSimilarity(a.getPath(), b.getPath());
                return jaccard * 100.0; // Convert to percentage
            } catch (Exception e) { 
                System.err.println("Error calculating Jaccard similarity: " + e.getMessage());
                return 0.0;
            }
        }
        
        // Audio file similarity (wav, mp3, flac, etc.)
        if (isAudioFile(a.getFileType()) && isAudioFile(b.getFileType())) {
            // First try ssdeep comparison
            if (a.getSsdeepHash() != null && b.getSsdeepHash() != null && 
                !a.getSsdeepHash().isEmpty() && !b.getSsdeepHash().isEmpty()) {
                try {
                    int score = ssdeepCompare(a.getSsdeepHash(), b.getSsdeepHash());
                    if (score > 0) {
                        return (double) score; // ssdeep already returns percentage
                    }
                } catch (Exception e) { 
                    System.err.println("Error comparing ssdeep hashes: " + e.getMessage());
                }
            }
            
            // Fallback for audio files: check size and entropy similarity
            // Audio files with similar content should have similar entropy
            double sizeDiff = Math.abs(a.getSize() - b.getSize()) / Math.max(a.getSize(), b.getSize());
            double entropyDiff = Math.abs(a.getEntropy() - b.getEntropy());
            
            // If sizes are very similar and entropy is close, likely same audio content
            if (sizeDiff < 0.1 && entropyDiff < 0.1) {
                return 85.0; // High similarity for audio files
            } else if (sizeDiff < 0.2 && entropyDiff < 0.2) {
                return 70.0; // Medium similarity
            }
        }
        
        // Video file similarity (mp4, avi, mov, mkv, etc.)
        if (isVideoFile(a.getFileType()) && isVideoFile(b.getFileType())) {
            // First try ssdeep comparison for video files
            if (a.getSsdeepHash() != null && b.getSsdeepHash() != null && 
                !a.getSsdeepHash().isEmpty() && !b.getSsdeepHash().isEmpty()) {
                try {
                    int score = ssdeepCompare(a.getSsdeepHash(), b.getSsdeepHash());
                    if (score > 0) {
                        return (double) score; // ssdeep already returns percentage
                    }
                } catch (Exception e) { 
                    System.err.println("Error comparing ssdeep hashes for video: " + e.getMessage());
                }
            }
            
            // Video-specific similarity detection
            double sizeDiff = Math.abs(a.getSize() - b.getSize()) / Math.max(a.getSize(), b.getSize());
            double entropyDiff = Math.abs(a.getEntropy() - b.getEntropy());
            
            // Video files with same content but different quality/compression
            if (sizeDiff < 0.15 && entropyDiff < 0.15) {
                return 90.0; // High similarity for video files
            } else if (sizeDiff < 0.3 && entropyDiff < 0.2) {
                return 75.0; // Medium similarity (different quality/compression)
            } else if (sizeDiff < 0.5 && entropyDiff < 0.25) {
                return 60.0; // Low similarity (might be same video, different format/quality)
            }
        }
        
        // Binary file similarity (non-audio, non-video, non-text)
        if (!a.getFileType().equals("txt") && !b.getFileType().equals("txt") && 
            !isAudioFile(a.getFileType()) && !isAudioFile(b.getFileType()) &&
            !isVideoFile(a.getFileType()) && !isVideoFile(b.getFileType())) {
            // Fuzzy binary comparison using ssdeep
            if (a.getSsdeepHash() != null && b.getSsdeepHash() != null && 
                !a.getSsdeepHash().isEmpty() && !b.getSsdeepHash().isEmpty()) {
                try {
                    int score = ssdeepCompare(a.getSsdeepHash(), b.getSsdeepHash());
                    return (double) score; // ssdeep already returns percentage
                } catch (Exception e) { 
                    System.err.println("Error comparing ssdeep hashes: " + e.getMessage());
                    return 0.0;
                }
            }
        }
        
        // General fallback: check size, type, and entropy similarity
        if (!a.getFileType().equals(b.getFileType())) return 0.0;
        if (a.getSize() != b.getSize()) return 0.0;
        double entropyDiff = Math.abs(a.getEntropy() - b.getEntropy());
        if (entropyDiff < 0.01) return 95.0; // High similarity if entropy matches very closely
        return Math.max(0.0, 100.0 - (entropyDiff * 1000)); // Scale entropy difference
    }
    
    private boolean isAudioFile(String fileType) {
        return fileType.equals("wav") || fileType.equals("mp3") || fileType.equals("flac") || 
               fileType.equals("aac") || fileType.equals("ogg") || fileType.equals("m4a") ||
               fileType.equals("wma") || fileType.equals("aiff");
    }
    
    private boolean isVideoFile(String fileType) {
        return fileType.equals("mp4") || fileType.equals("avi") || fileType.equals("mov") || 
               fileType.equals("mkv") || fileType.equals("wmv") || fileType.equals("flv") ||
               fileType.equals("webm") || fileType.equals("m4v") || fileType.equals("3gp") ||
               fileType.equals("ogv") || fileType.equals("ts") || fileType.equals("mts");
    }

    private double getSimilarityThreshold(String fileType) {
        if (fileType.equals("txt")) return 80.0; // 80% for text files
        if (isAudioFile(fileType)) return 70.0; // 70% for audio files (more lenient)
        if (isVideoFile(fileType)) return 60.0; // 60% for video files (very lenient due to compression differences)
        return 90.0; // 90% for other binary files
    }

    // Hybrid similarity: size, type, and entropy must match closely
    private boolean isSimilar(ApplicationFile a, ApplicationFile b) {
        if (a.getFileType().equals("txt") && b.getFileType().equals("txt")) {
            try {
                double jaccard = jaccardSimilarity(a.getPath(), b.getPath());
                if (jaccard > 0.8) return true; // threshold for near-duplicate
            } catch (Exception e) { /* ignore */ }
        }
        if (!a.getFileType().equals("txt") && !b.getFileType().equals("txt")) {
            // Fuzzy binary comparison using ssdeep
            if (a.getSsdeepHash() != null && b.getSsdeepHash() != null && !a.getSsdeepHash().isEmpty() && !b.getSsdeepHash().isEmpty()) {
                try {
                    int score = ssdeepCompare(a.getSsdeepHash(), b.getSsdeepHash());
                    if (score > 90) return true; // threshold for near-duplicate binaries
                } catch (Exception e) { /* ignore */ }
            }
        }
        if (a.getSize() != b.getSize()) return false;
        if (!a.getFileType().equals(b.getFileType())) return false;
        return Math.abs(a.getEntropy() - b.getEntropy()) < 0.01;
    }

    private double jaccardSimilarity(String pathA, String pathB) throws java.io.IOException {
        java.util.Set<String> setA = new java.util.HashSet<>(java.util.Arrays.asList(
            new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(pathA)))
                .toLowerCase().replaceAll("[^a-z0-9 ]", " ").split("\\s+")
        ));
        java.util.Set<String> setB = new java.util.HashSet<>(java.util.Arrays.asList(
            new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(pathB)))
                .toLowerCase().replaceAll("[^a-z0-9 ]", " ").split("\\s+")
        ));
        java.util.Set<String> intersection = new java.util.HashSet<>(setA);
        intersection.retainAll(setB);
        java.util.Set<String> union = new java.util.HashSet<>(setA);
        union.addAll(setB);
        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }

    private int ssdeepCompare(String hashA, String hashB) throws java.io.IOException, InterruptedException {
        try {
            // Use ssdeep -v for comparison
            ProcessBuilder pb = new ProcessBuilder("ssdeep", "-v", hashA, hashB);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()));
            String line;
            int score = 0;
            while ((line = reader.readLine()) != null) {
                if (line.contains(":")) {
                    // Expected format: "hash1:hash2:score"
                    String[] parts = line.split(":");
                    if (parts.length == 3) {
                        try {
                            score = Integer.parseInt(parts[2].trim());
                            break;
                        } catch (NumberFormatException e) {
                            // Try alternative parsing
                            String scoreStr = parts[2].replaceAll("[^0-9]", "");
                            if (!scoreStr.isEmpty()) {
                                score = Integer.parseInt(scoreStr);
                                break;
                            }
                        }
                    }
                }
            }
            process.waitFor();
            return score;
        } catch (Exception e) {
            System.err.println("Error in ssdeep comparison: " + e.getMessage());
            return 0;
        }
    }
}