package space.gavinklfong.supermarket.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.supermarket.dtos.SearchResult;
import space.gavinklfong.supermarket.models.Product;
import space.gavinklfong.supermarket.repositories.ProductRepository;

import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

@Service
public class ProductService {

    static private final Integer DEFAULT_PAGE_SIZE = 50;

    @Autowired
    private ProductRepository productRepository;

    public Mono<Product> getProductById(String productId) {
        if (isNull(productId) || productId.isBlank()) return Mono.empty();
        return productRepository.findById(productId);
    }

    public Mono<SearchResult<Product>> getProductsByQuerystring(String queryString, Integer pageNum, Integer pageSize) {
        if (isNull(queryString) || queryString.isBlank()) return Mono.empty();
        return productRepository.findByQueryString(queryString, pageNum, pageSize);
    }

    public Mono<SearchResult<Product>> getProductsByCategory(String category, Integer pageNum, Integer pageSize) {
        if (isNull(category) || category.isBlank()) return Mono.empty();
        return productRepository.findByCategory(category, pageNum, pageSize);
    }

    public Mono<SearchResult<Product>> getRelevantProducts(String productId, Integer pageNum, Integer pageSize) {
        if (isNull(productId) || productId.isBlank()) return Mono.empty();
        return productRepository.findById(productId)
                .flatMap(product ->
                        productRepository.findRelevantProducts(product.getName(), product.getCategory(), pageNum, pageSize))
                .map(searchResult ->
                        searchResult.withItemList(
                                searchResult.getItemList().stream()
                                        .filter(item -> !item.getId().equals(productId))
                                        .collect(toList())
                        ));
    }
}
