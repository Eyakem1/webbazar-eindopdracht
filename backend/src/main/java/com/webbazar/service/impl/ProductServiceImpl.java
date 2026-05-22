package com.webbazar.service.impl;

import com.webbazar.dto.ProductDTO;
import com.webbazar.entity.Product;
import com.webbazar.repo.ProductRepository;
import com.webbazar.service.FileService;
import com.webbazar.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final FileService fileService;

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public Page<ProductDTO> list(Pageable pageable) {
        return productRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    public ProductDTO get(Long id) {
        return toDto(find(id));
    }

    @Override
    public ProductDTO create(ProductDTO dto, MultipartFile file) {
        Product p = toEntity(dto);
        if (file != null && !file.isEmpty()) {
            String path = fileService.store(file);
            p.setFilePath(path);
        }
        p = productRepository.save(p);
        return toDto(p);
    }

    @Override
    public ProductDTO update(Long id, ProductDTO dto) {
        Product p = find(id);
        p.setTitle(dto.getTitle());
        p.setAuthor(dto.getAuthor());
        p.setDescription(dto.getDescription());
        p.setPrice(dto.getPrice());
        p.setRentalPrice(dto.getRentalPrice());
        if (dto.getFilePath() != null && !Objects.equals(dto.getFilePath(), p.getFilePath())) {
            p.setFilePath(dto.getFilePath());
        }
        p = productRepository.save(p);
        return toDto(p);
    }

    @Override
    public void delete(Long id) {
        if (!productRepository.existsById(id)) throw new EntityNotFoundException("Product niet gevonden");
        productRepository.deleteById(id);
    }

    private Product find(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product niet gevonden"));
    }

    private ProductDTO toDto(Product p) {
        ProductDTO dto = new ProductDTO();
        dto.setId(p.getId());
        dto.setTitle(p.getTitle());
        dto.setAuthor(p.getAuthor());
        dto.setDescription(p.getDescription());
        dto.setPrice(p.getPrice());
        dto.setRentalPrice(p.getRentalPrice());
        dto.setFilePath(p.getFilePath());
        return dto;
    }

    private Product toEntity(ProductDTO dto) {
        return Product.builder()
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .rentalPrice(dto.getRentalPrice())
                .filePath(dto.getFilePath())
                .build();
    }
}
