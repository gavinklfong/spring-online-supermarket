package space.gavinklfong.supermarket.repositories;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import space.gavinklfong.supermarket.models.Product;

@Repository
public interface ProductRepository extends ReactiveSortingRepository<Product, String> {

    @Query("{\"bool\": {\"must\": [{\"query_string\": " +
            "{\"query\": \"?0\", " +
            "\"analyze_wildcard\": true," +
            "\"default_field\": \"*\"} " +
            "}] } }")
    public Flux<Product> findByStringQuery(String query);

    public Flux<Product> findByCategory(String category);

}
