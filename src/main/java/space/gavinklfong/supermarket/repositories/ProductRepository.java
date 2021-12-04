package space.gavinklfong.supermarket.repositories;

import lombok.extern.slf4j.Slf4j;
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
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.supermarket.dtos.SearchResult;
import space.gavinklfong.supermarket.models.Product;

import java.util.function.Function;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.logging.log4j.util.Strings.isBlank;

@Slf4j
@Service
public class ProductRepository {

    @Autowired
    private ReactiveElasticsearchOperations elasticsearchOperations;

    public Mono<Product> save(Product product) {
        return elasticsearchOperations.save(product);
    }


    public Mono<SearchResult<Product>> findByCategory(String category, Integer pageNum, Integer pageSize) {
        return findByCategory(category, null, null, pageNum, pageSize);
    }

    public Mono<SearchResult<Product>> findByCategory(String category, String sortByField, Sort.Direction sortDirection, Integer pageNum, Integer pageSize) {

        Pageable pageable = constructPageable(pageNum, pageSize);
        SortBuilder sortBuilder = constructSortBuilder(sortByField, sortDirection);

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchPhrasePrefixQuery("category", category))
                .withSorts(sortBuilder);

        if (nonNull(pageable)) queryBuilder = queryBuilder.withPageable(pageable);

        Query query = queryBuilder.build();

        return elasticsearchOperations.searchForPage(query, Product.class)
                .map(mapToProductSearchResult());
    }

    public Mono<SearchResult<Product>>  findByQueryString(String queryString, Integer pageNum, Integer pageSize) {
        return findByQueryString(queryString, null, null, pageNum, pageSize);
    }

    public Mono<SearchResult<Product>>  findByQueryString(String queryString, String sortByField, Sort.Direction sortDirection, Integer pageNum, Integer pageSize) {
        Pageable pageable = constructPageable(pageNum, pageSize);
        SortBuilder sortBuilder = constructSortBuilder(sortByField, sortDirection);

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.queryStringQuery(queryString))
                .withFields("name^2", "category", "brand")
                .withSorts(sortBuilder);

        if (nonNull(pageable)) queryBuilder = queryBuilder.withPageable(pageable);

        Query query = queryBuilder.build();

       return elasticsearchOperations.searchForPage(query, Product.class)
               .map(mapToProductSearchResult());
    }

    public Mono<SearchResult<Product>> findRelevantProducts(String name, String category, Integer pageNum, Integer pageSize) {
        return findRelevantProducts(name, category, null, null, pageNum, pageSize);
    }

    public Mono<SearchResult<Product>> findRelevantProducts(String name, String category, String sortByField, Sort.Direction sortDirection, Integer pageNum, Integer pageSize) {
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

        return elasticsearchOperations.searchForPage(query, Product.class)
                .map(mapToProductSearchResult());
    }

    public Mono<Product> findById(String productId) {
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

    private Function<SearchPage<Product>, SearchResult<Product>> mapToProductSearchResult() {
        return (searchPage) -> {
            SearchResult.SearchResultBuilder <Product>builder = SearchResult.<Product>builder()
                    .hasNextPage(searchPage.hasNext())
                    .totalPageNum(searchPage.getTotalPages())
                    .itemList(searchPage.getSearchHits().stream().map(searchHit -> searchHit.getContent()).collect(toList()));

                    if (searchPage.hasNext()) {
                        builder.nextPageNum(searchPage.nextPageable().getPageNumber());
                        builder.nextPageSize(searchPage.nextPageable().getPageSize());
                    }

            return builder.build();
        };
    }
}
