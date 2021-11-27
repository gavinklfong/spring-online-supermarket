package space.gavinklfong.supermarket.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.supermarket.models.Product;

@Repository
public interface ProductRepository extends ReactiveSortingRepository<Product, String> {

    public Mono<Product> findById(String id);

    public Flux<Product> findByCategory(String category);

    public Flux<Product> findByCategoryStartingWith(String category);

    @Query("{" +
            "    \"query_string\" : {" +
            "      \"query\" : \"?0\"," +
            "      \"fields\" : [ \"name^2\", \"category\" ]" +
            "    }" +
            "  }")
    public Flux<Product> findByKeyword(String queryString);

    @Query("{" +
            "    \"bool\": {" +
            "        \"must\": [" +
            "            {" +
            "                \"query_string\": {" +
            "                    \"query\": \"?0\"," +
            "                    \"fields\": [" +
            "                        \"name^2\"" +
            "                    ]" +
            "                }" +
            "            }," +
            "            {" +
            "                \"match\": { " +
            "                    \"category\": {" +
            "                        \"query\": \"?1\"" +
            "                    }" +
            "                }" +
            "            }" +
            "        ]" +
            "    }" +
            "}")
    public Flux<Product> findByRelevantProducts(String name, String category);

}
