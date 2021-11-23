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

@Service
public class ProductService {

    @Autowired
    private ReactiveElasticsearchOperations elasticsearchOperations;

    public Flux<SearchHit<Product>> getProductByCategory(String category) {
        Criteria criteria = new Criteria("category").contains(category);
        Query query = new CriteriaQuery(criteria);
        return elasticsearchOperations.search(query, Product.class);
    }
}
