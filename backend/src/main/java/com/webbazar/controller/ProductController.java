package com.webbazar.controller;

import com.webbazar.model.Product;
import com.webbazar.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        List<Product> products = productRepository.findAll();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return productRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body("Product niet gevonden"));
    }

    // 🔐 Alleen admins mogen producten toevoegen
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> addProduct(
            @RequestPart("product") Product product,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        try {
            if (product.getTitle() == null || product.getTitle().isBlank()) {
                return ResponseEntity.badRequest().body("Titel is verplicht");
            }

            if (file != null && !file.isEmpty()) {
                String fileName = storeFile(file);
                product.setFilePath(uploadDir + "/" + fileName);
                product.setFileType(getExtension(fileName));
            }

            Product saved = productRepository.save(product);
            return ResponseEntity.ok(saved);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Bestand uploaden mislukt: " + e.getMessage());
        }
    }

    // 🔐 Alleen admins mogen producten bijwerken
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestPart("product") Product updatedProduct,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        return productRepository.findById(id)
                .map(product -> {
                    try {
                        product.setTitle(updatedProduct.getTitle());
                        product.setAuthor(updatedProduct.getAuthor());
                        product.setDescription(updatedProduct.getDescription());
                        product.setPrice(updatedProduct.getPrice());
                        product.setRentalPrice(updatedProduct.getRentalPrice());

                        if (file != null && !file.isEmpty()) {
                            String fileName = storeFile(file);
                            product.setFilePath(uploadDir + "/" + fileName);
                            product.setFileType(getExtension(fileName));
                        }

                        return ResponseEntity.ok(productRepository.save(product));
                    } catch (IOException e) {
                        return ResponseEntity.internalServerError().body("Bijwerken mislukt: " + e.getMessage());
                    }
                })
                .orElseGet(() -> ResponseEntity.status(404).body("Product niet gevonden"));
    }

    // 🔐 Alleen admins mogen producten verwijderen
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Product niet gevonden");
        }
        productRepository.deleteById(id);
        return ResponseEntity.ok("Product verwijderd");
    }

    // 👇 Helpers

    private String storeFile(MultipartFile file) throws IOException {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(originalFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return originalFileName;
    }

    private String getExtension(String filename) {
        int index = filename.lastIndexOf('.');
        return (index > 0) ? filename.substring(index + 1).toLowerCase() : "";
    }
}
