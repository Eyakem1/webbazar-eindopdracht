package com.webbazar.service;

import com.webbazar.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    // voor  ProductController
    List<ProductDTO> getAllProducts();

    // gebruikt in  ProductServiceImpl
    Page<ProductDTO> list(Pageable pageable);

    ProductDTO get(Long id);

    ProductDTO create(ProductDTO dto, MultipartFile file);

    ProductDTO update(Long id, ProductDTO dto);

    void delete(Long id);
}
