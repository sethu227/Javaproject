package com.example.appmanager.service;

import com.example.appmanager.model.ApplicationFile;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class FileScannerService {
    public List<ApplicationFile> scanDirectory(String directoryPath) throws IOException, NoSuchAlgorithmException {
        List<ApplicationFile> applicationFiles = new ArrayList<>();
        Collection<File> files = FileUtils.listFiles(new File(directoryPath), null, true);
        for (File file : files) {
            ApplicationFile appFile = new ApplicationFile();
            appFile.setName(file.getName());
            appFile.setPath(file.getAbsolutePath());
            appFile.setSize(file.length());
            appFile.setFileType(getFileExtension(file));
            if (appFile.getFileType().equals("txt")) {
                appFile.setHash(computeNormalizedTextHash(file));
            } else {
                appFile.setHash(computeSHA256(file));
            }
            if (!appFile.getFileType().equals("txt")) {
                appFile.setSsdeepHash(computeSsdeepHash(file));
            }
            appFile.setEntropy(calculateEntropy(file));
            applicationFiles.add(appFile);
        }
        return applicationFiles;
    }

    private String computeSHA256(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] byteArray = new byte[1024];
            int bytesCount;
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
        }
        byte[] bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        return (lastDot == -1) ? "unknown" : name.substring(lastDot + 1).toLowerCase();
    }

    private double calculateEntropy(File file) throws IOException {
        int[] freq = new int[256];
        int total = 0;
        try (FileInputStream fis = new FileInputStream(file)) {
            int b;
            while ((b = fis.read()) != -1) {
                freq[b & 0xFF]++;
                total++;
            }
        }
        double entropy = 0.0;
        for (int f : freq) {
            if (f > 0) {
                double p = (double) f / total;
                entropy -= p * (Math.log(p) / Math.log(2));
            }
        }
        return entropy;
    }

    private String computeNormalizedTextHash(File file) throws IOException, NoSuchAlgorithmException {
        String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
        String[] words = content.toLowerCase().replaceAll("[^a-z0-9 ]", " ").split("\\s+");
        java.util.Arrays.sort(words);
        String normalized = String.join(" ", words).trim();
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(normalized.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private String computeSsdeepHash(File file) {
        try {
            ProcessBuilder pb = new ProcessBuilder("ssdeep", "-b", file.getAbsolutePath());
            pb.redirectErrorStream(true);
            Process process = pb.start();
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()));
            String line;
            String hash = null;
            while ((line = reader.readLine()) != null) {
                if (line.contains(",")) {
                    String[] parts = line.split(",");
                    if (parts.length >= 2) {
                        // ssdeep output format: "filename,hash"
                        hash = parts[1].trim();
                        break;
                    }
                }
            }
            process.waitFor();
            return hash != null ? hash : "";
        } catch (Exception e) {
            System.err.println("Error computing ssdeep hash for " + file.getName() + ": " + e.getMessage());
            return "";
        }
    }
}