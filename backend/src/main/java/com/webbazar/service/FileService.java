package com.webbazar.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Service
public class FileService {

    // Alleen PDF toestaan
    private static final Set<String> EXT_WHITELIST = Set.of("pdf");
    private static final long MAX_BYTES = 25L * 1024 * 1024; // 25MB

    private final Path uploadRoot;
    private final Path booksRoot;

    public FileService(@Value("${webbazar.upload-dir:uploads}") String uploadDir) {
        this.uploadRoot = Paths.get(uploadDir).normalize().toAbsolutePath();
        this.booksRoot = this.uploadRoot.resolve("books").normalize();

        try {
            // Uploadmappen aanmaken
            Files.createDirectories(this.uploadRoot);
            Files.createDirectories(this.booksRoot);
        } catch (IOException e) {
            throw new RuntimeException("Kan upload mappen niet aanmaken: " + this.uploadRoot, e);
        }
    }



    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Geen bestand ontvangen (leeg).");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException("Bestand is te groot (max 25MB).");
        }

        String original = file.getOriginalFilename() == null ? "upload" : file.getOriginalFilename();
        String safeName = Paths.get(original).getFileName().toString();
        String ext = getExtension(safeName).toLowerCase();

        if (!EXT_WHITELIST.contains(ext)) {
            throw new IllegalArgumentException("Bestandstype niet toegestaan. Alleen: " + EXT_WHITELIST);
        }

        String newName = UUID.randomUUID() + ".pdf";

        // Nieuwe uploads altijd onder /uploads/books
        Path target = booksRoot.resolve(newName).normalize();

        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Opslaan van bestand mislukt", e);
        }

        // relatieve key voor in de DB: "uploads/books/uuid.pdf"
        String uploadsDirName = uploadRoot.getFileName().toString(); // "uploads"
        return uploadsDirName + "/books/" + newName;
    }

    // Bestand lezen voor download
    public byte[] read(String storageKey) {
        try {
            Path p = resolve(storageKey);
            return Files.readAllBytes(p);
        } catch (IOException e) {
            throw new RuntimeException("Bestand niet gevonden of leesfout: " + storageKey, e);
        }
    }

    // Content type bepalen
    public String contentType(String storageKey) {
        Path p = resolve(storageKey);
        String filename = p.getFileName().toString().toLowerCase();

        if (filename.endsWith(".pdf")) {
            return MediaType.APPLICATION_PDF_VALUE;
        }

        if (filename.endsWith(".xml")) {
            return MediaType.APPLICATION_XML_VALUE;
        }

        return MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }


     //  Storage key omzetten naar veilig pad

    private Path resolve(String storageKey) {
        if (storageKey == null || storageKey.isBlank()) {
            throw new IllegalArgumentException("Lege storage key.");
        }
        String key = storageKey.replace('\\', '/').trim();
        if (key.contains("..")) {
            throw new IllegalArgumentException("Ongeldige storage key.");
        }

        // uploads-prefix uit storage key verwijderen
        String uploadsDirName = uploadRoot.getFileName().toString(); // "uploads"
        if (key.startsWith(uploadsDirName + "/")) {
            key = key.substring(uploadsDirName.length() + 1);
        }

        Path abs = uploadRoot.resolve(key).normalize();
        if (!abs.startsWith(uploadRoot)) {
            throw new IllegalArgumentException("Pad valt buiten toegestane map.");
        }
        return abs;
    }

    private String getExtension(String filename) {
        int i = filename.lastIndexOf('.');
        return i == -1 ? "" : filename.substring(i + 1);
    }
}