package space.gavinklfong.supermarket.services;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.supermarket.models.Product;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static org.apache.logging.log4j.util.Strings.isBlank;

@Service
public class ProductService {

    @Autowired
    private ReactiveElasticsearchOperations elasticsearchOperations;

    public Mono<Product> save(Product product) {
        return elasticsearchOperations.save(product);
    }

    public Flux<Product> findByCategory(String category, Integer pageNum, Integer pageSize) {
        return findByCategory(category, null, null, pageNum, pageSize);
    }

    public Flux<Product> findByCategory(String category, String sortByField, Sort.Direction sortDirection, Integer pageNum, Integer pageSize) {

        Pageable pageable = constructPageable(pageNum, pageSize);
        SortBuilder sortBuilder = constructSortBuilder(sortByField, sortDirection);

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchPhrasePrefixQuery("category", category))
                .withSorts(sortBuilder);

        if (nonNull(pageable)) queryBuilder = queryBuilder.withPageable(pageable);

        Query query = queryBuilder.build();

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

    public Flux<Product> findByQueryString(String queryString, Integer pageNum, Integer pageSize) {
        return findByQueryString(queryString, null, null, pageNum, pageSize);
    }

    public Flux<Product> findByQueryString(String queryString, String sortByField, Sort.Direction sortDirection, Integer pageNum, Integer pageSize) {
        Pageable pageable = constructPageable(pageNum, pageSize);
        SortBuilder sortBuilder = constructSortBuilder(sortByField, sortDirection);

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.queryStringQuery(queryString))
                .withFields("name^2", "category", "brand")
                .withSorts(sortBuilder);

        if (nonNull(pageable)) queryBuilder = queryBuilder.withPageable(pageable);

        Query query = queryBuilder.build();

        Flux<SearchHit<Product>> searchHits = elasticsearchOperations.search(query, Product.class);
        return searchHits.map(hit -> hit.getContent());
    }

    public Flux<Product> findRelevantProducts(String name, String category, String sortByField, Sort.Direction sortDirection, Integer pageNum, Integer pageSize) {
        Pageable pageable = constructPageable(pageNum, pageSize);
        SortBuilder sortBuilder = constructSortBuilder(sortByField, sortDirection);

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(
                        QueryBuilders
                                .boolQuery()
                                .must(QueryBuilders.queryStringQuery(name).field("name").boost(2F))
                                .must(QueryBuilders.matchPhrasePrefixQuery("category", category))
                )
                .withSorts(sortBuilder);

        if (nonNull(pageable)) queryBuilder = queryBuilder.withPageable(pageable);

        Query query = queryBuilder.build();

        Flux<SearchHit<Product>> searchHits = elasticsearchOperations.search(query, Product.class);
        return searchHits.map(hit -> hit.getContent());
    }

    public Mono<Product> findById(String productId) {
//        Query query = new CriteriaQuery(new Criteria("id").is(productId));

        Query query = new NativeSearchQueryBuilder().withIds(productId).build();

        Flux<SearchHit<Product>> searchHits = elasticsearchOperations.search(query, Product.class);
        return searchHits.next().map(hit -> hit.getContent());
    }

    private SortBuilder constructSortBuilder(String sortByField, Sort.Direction sortDirection) {
        SortOrder sortOrder = SortOrder.DESC;
        if (nonNull(sortDirection)) sortOrder = (sortDirection.isAscending()) ? SortOrder.ASC : SortOrder.DESC;

        SortBuilder sortBuilder = SortBuilders.scoreSort().order(sortOrder);
        if (nonNull(sortByField) && !isBlank(sortByField))
            sortBuilder = SortBuilders.fieldSort(sortByField).order(sortOrder);

        return sortBuilder;
    }

    private Pageable constructPageable(Integer pageNum, Integer pageSize) {
        Pageable pageable = null;
        if (nonNull(pageNum) && nonNull(pageSize) && pageNum >= 0 && pageSize > 0)
            pageable = PageRequest.of(pageNum, pageSize);

        return pageable;
    }


}
