package space.gavinklfong.supermarket.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import space.gavinklfong.supermarket.models.Product;
import space.gavinklfong.supermarket.repositories.ProductRepository;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class ProductRepoService {

    @Autowired
    private ProductRepository productRepository;

    public Flux<Product> getProductsByCategory(String category) {
        if (isNull(category) || category.isBlank()) return Flux.empty();
        return productRepository.findByCategory(category);
    }

//    public Flux<Product> getProductsByKeyword(String queryString) {
//        if (isNull(queryString) || queryString.isBlank()) return Flux.empty();
//    }

    public Flux<Product> getRelevantProducts(String productId) {
        if (isNull(productId) || productId.isBlank()) return Flux.empty();
        return productRepository.findById(productId)
                .flatMapMany(product ->
                        productRepository.findByRelevantProducts(product.getName(), product.getCategory()))
                .filter(product -> !productId.equalsIgnoreCase(product.getId()));
    }
}
