package com.webbazar.service;

import com.webbazar.model.Product;
import com.webbazar.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    private ProductService productService;
    private ProductRepository productRepository;

    @BeforeEach
    public void setup() {
        productRepository = mock(ProductRepository.class);
        productService = new ProductService(productRepository);
    }

    @Test
    public void testGetAllProducts() {
        List<Product> products = Arrays.asList(new Product(), new Product());
        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.getAllProducts();
        assertEquals(2, result.size());
    }

    @Test
    public void testGetProductById_found() {
        Product product = new Product();
        product.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Optional<Product> result = productService.getProductById(1L);
        assertTrue(result.isPresent());
    }

    @Test
    public void testGetProductById_notFound() {
        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Product> result = productService.getProductById(2L);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testSaveProduct() {
        Product product = Product.builder().title("Test Product").build();
        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.saveProduct(product);
        assertEquals("Test Product", result.getTitle());
    }

    @Test
    public void testDeleteProduct() {
        Long id = 3L;
        productService.deleteProduct(id);
        verify(productRepository, times(1)).deleteById(id);
    }
}
