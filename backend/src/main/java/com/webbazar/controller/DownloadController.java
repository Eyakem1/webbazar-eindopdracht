package com.webbazar.controller;

import com.webbazar.entity.OrderItem;
import com.webbazar.entity.Rental;
import com.webbazar.repo.OrderItemRepository;
import com.webbazar.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/downloads")
@RequiredArgsConstructor
public class DownloadController {

    private final OrderItemRepository orderItemRepository;
    private final FileService fileService;

    @GetMapping("/{orderItemId}")
    @PreAuthorize("@ownershipGuard.canAccessOrderItem(#orderItemId, authentication) or hasRole('ADMIN')")
    public ResponseEntity<byte[]> download(@PathVariable Long orderItemId) {

        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new NoSuchElementException("OrderItem " + orderItemId + " niet gevonden"));

        // Huurcontrole
        Rental rental = item.getRental();
        if (rental != null) {
            Instant now = Instant.now();
            if (now.isBefore(rental.getStartDate())) return ResponseEntity.status(HttpStatus.LOCKED).build(); // 423
            if (now.isAfter(rental.getEndDate())) return ResponseEntity.status(HttpStatus.GONE).build();       // 410
        }

        String storageKey = item.getProduct().getFilePath();
        if (storageKey == null || storageKey.isBlank()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        byte[] data = fileService.read(storageKey);
        String contentType = fileService.contentType(storageKey);


        String filename = sanitizeFilename(item.getProduct().getTitle()) + getExt(storageKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(data.length);


        headers.add("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    private String getExt(String key) {
        int i = key.lastIndexOf('.');
        return i == -1 ? "" : key.substring(i);
    }

    private String sanitizeFilename(String s) {
        return s == null ? "ebook" : s.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
