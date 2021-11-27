package space.gavinklfong.supermarket.services;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.supermarket.models.Product;

import static java.lang.String.format;

@Service
public class ProductService {

    @Autowired
    private ReactiveElasticsearchOperations elasticsearchOperations;

    public Mono<Product> save(Product product) {
        return elasticsearchOperations.save(product);
    }

    public Flux<Product> findByCategory(String category) {
//        Query query = new NativeSearchQueryBuilder()
//                .withQuery(QueryBuilders.queryStringQuery(category).field("category"))
//                .build();

        Query query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchPhrasePrefixQuery("category", category)).build();

//        Query query = new StringQuery(format("{" +
//                "        \"match\": { " +
//                "            \"category\": {" +
//                "                \"query\": \"%s\"" +
//                "            }" +
//                "        }" +
//                "    }", category));

//        Criteria criteria = new Criteria("category").contains(category);
//        Query query = new CriteriaQuery(criteria);


        Flux<SearchHit<Product>> searchHits = elasticsearchOperations.search(query, Product.class);
        return searchHits.map(hit -> hit.getContent());
    }

    public Mono<Product> findById(String productId) {
        Query query = new CriteriaQuery(new Criteria("id").is(productId));
        Flux<SearchHit<Product>> searchHits = elasticsearchOperations.search(query, Product.class);
        return searchHits.next().map(hit -> hit.getContent());
    }


}
