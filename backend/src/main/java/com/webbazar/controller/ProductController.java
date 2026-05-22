    package com.webbazar.controller;

    import com.webbazar.dto.ProductDTO;
    import com.webbazar.service.ProductService;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/api/products")
    public class ProductController {

        private final ProductService productService;

        public ProductController(final ProductService productService){
            this.productService = productService;
        }

        @GetMapping
        public ResponseEntity<List<ProductDTO>> list() {
            return ResponseEntity.ok(productService.getAllProducts()); //
        }

        @GetMapping("/{id}")
        public ResponseEntity<ProductDTO> get(@PathVariable Long id) {
            return ResponseEntity.ok(productService.get(id));
        }
    }
