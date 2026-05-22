package com.webbazar.service;

import com.webbazar.dto.ProductDTO;
import com.webbazar.entity.Product;
import com.webbazar.repo.ProductRepository;
import com.webbazar.service.impl.ProductServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock ProductRepository productRepository;
    @Mock FileService fileService;

    @InjectMocks ProductServiceImpl productService;

    @Test
    void getAllProducts_ok() {
        when(productRepository.findAll())
                .thenReturn(List.of(product(1L, "A"), product(2L, "B")));

        var list = productService.getAllProducts();

        assertThat(list).hasSize(2);
        assertThat(list.get(0).getTitle()).isEqualTo("A");
        assertThat(list.get(1).getRentalPrice()).isEqualByComparingTo("2.00");
    }

    @Test
    void list_returnsMappedPage() {
        var pageable = PageRequest.of(0, 10);

        when(productRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(product(1L, "A")), pageable, 1));

        var page = productService.list(pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getTitle()).isEqualTo("A");
    }

    @Test
    void get_existingProduct_returnsDto() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product(1L, "A")));

        var dto = productService.get(1L);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getAuthor()).isEqualTo("Author");
        assertThat(dto.getDescription()).isEqualTo("Description");
        assertThat(dto.getFilePath()).isEqualTo("uploads/books/a.pdf");
    }

    @Test
    void get_missingProduct_throwsEntityNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.get(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Product niet gevonden");
    }

    @Test
    void create_withFile_setsFilePath() {
        ProductDTO dto = dto(null, "X", "dto-path.pdf");
        MultipartFile file = mock(MultipartFile.class);

        when(file.isEmpty()).thenReturn(false);
        when(fileService.store(file)).thenReturn("uploads/x.pdf");
        when(productRepository.save(any())).thenAnswer(inv -> {
            Product p = inv.getArgument(0);
            p.setId(100L);
            return p;
        });

        var saved = productService.create(dto, file);

        assertThat(saved.getId()).isEqualTo(100L);
        assertThat(saved.getFilePath()).isEqualTo("uploads/x.pdf");
        verify(fileService).store(file);
    }

    @Test
    void create_withoutFile_usesDtoFilePath() {
        ProductDTO dto = dto(null, "X", "uploads/original.pdf");

        when(productRepository.save(any())).thenAnswer(inv -> {
            Product p = inv.getArgument(0);
            p.setId(101L);
            return p;
        });

        var saved = productService.create(dto, null);

        assertThat(saved.getFilePath()).isEqualTo("uploads/original.pdf");
        verify(fileService, never()).store(any());
    }

    @Test
    void create_withEmptyFile_doesNotStoreFile() {
        ProductDTO dto = dto(null, "X", "uploads/original.pdf");
        MultipartFile file = mock(MultipartFile.class);

        when(file.isEmpty()).thenReturn(true);
        when(productRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var saved = productService.create(dto, file);

        assertThat(saved.getFilePath()).isEqualTo("uploads/original.pdf");
        verify(fileService, never()).store(any());
    }

    @Test
    void update_existingProduct_updatesFieldsAndFilePath() {
        Product existing = product(1L, "Old");
        ProductDTO update = dto(1L, "New", "uploads/books/new.pdf");

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(existing)).thenReturn(existing);

        var dto = productService.update(1L, update);

        assertThat(dto.getTitle()).isEqualTo("New");
        assertThat(dto.getFilePath()).isEqualTo("uploads/books/new.pdf");
        assertThat(existing.getAuthor()).isEqualTo("Author DTO");
        assertThat(existing.getDescription()).isEqualTo("Description DTO");
    }

    @Test
    void update_withNullFilePath_keepsExistingFilePath() {
        Product existing = product(1L, "Old");
        ProductDTO update = dto(1L, "New", null);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(existing)).thenReturn(existing);

        var dto = productService.update(1L, update);

        assertThat(dto.getFilePath()).isEqualTo("uploads/books/a.pdf");
    }

    @Test
    void update_withSameFilePath_keepsExistingFilePath() {
        Product existing = product(1L, "Old");
        ProductDTO update = dto(1L, "New", "uploads/books/a.pdf");

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(existing)).thenReturn(existing);

        var dto = productService.update(1L, update);

        assertThat(dto.getFilePath()).isEqualTo("uploads/books/a.pdf");
    }

    @Test
    void update_missingProduct_throwsEntityNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(99L, dto(99L, "X", null)))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void delete_existingProduct_deletesById() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.delete(1L);

        verify(productRepository).deleteById(1L);
    }

    @Test
    void delete_notExisting_throws() {
        when(productRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> productService.delete(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    private Product product(Long id, String title) {
        return Product.builder()
                .id(id)
                .title(title)
                .author("Author")
                .description("Description")
                .price(new BigDecimal("20.00"))
                .rentalPrice(new BigDecimal("2.00"))
                .filePath("uploads/books/a.pdf")
                .build();
    }

    private ProductDTO dto(Long id, String title, String filePath) {
        ProductDTO dto = new ProductDTO();
        dto.setId(id);
        dto.setTitle(title);
        dto.setAuthor("Author DTO");
        dto.setDescription("Description DTO");
        dto.setPrice(new BigDecimal("10.00"));
        dto.setRentalPrice(new BigDecimal("1.00"));
        dto.setFilePath(filePath);
        return dto;
    }
}