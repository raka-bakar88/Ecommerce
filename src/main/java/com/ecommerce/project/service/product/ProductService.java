package com.ecommerce.project.service.product;

import com.ecommerce.project.payload.product.ProductDTO;
import com.ecommerce.project.payload.product.ProductResponse;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductDTO addProduct( ProductDTO productDTO, Long categoryId);

    ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductResponse searchByCategory(Long categoryId,
                                     Integer pageNumber,
                                     Integer pageSize,
                                     String sortBy,
                                     String sortOrder);

    ProductResponse searchByKeyword(String keyword,
                                    Integer pageNumber,
                                    Integer pageSize,
                                    String sortBy,
                                    String sortOrder);

    ProductDTO deleteProduct(Long productId);

    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;

    ProductDTO updateProduct(Long productId, ProductDTO productDTO);
}
