package space.gavinklfong.supermarket.controllers;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import space.gavinklfong.supermarket.dtos.SearchResult;
import space.gavinklfong.supermarket.models.Product;
import space.gavinklfong.supermarket.services.ProductService;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = {ProductRestController.class})
public class ProductRestControllerTest {

    private static final String CATEGORY = "Drinks/Fizzy Drinks";
    private static final String KEYWORD = "Drinks";
    private static final String PRODUCT_ID = "ae6b50ec-5f70-47fa-97ff-29bb319717c2";
    private static final Integer PAGE_NUM = 0;
    private static final Integer PAGE_SIZE = 5;

    private final Faker faker = new Faker();

    @MockBean
    private ProductService productService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testFindByCategory() {
        List<Product> expectedProductList = generateProduct(5);

        SearchResult<Product> expectedSearchResult = SearchResult.<Product>builder()
                .itemList(expectedProductList)
                .hasNextPage(true)
                .nextPageNum(1)
                .nextPageSize(5)
                .totalPageNum(4)
                .build();

        when(productService.getProductsByCategory(CATEGORY, PAGE_NUM, PAGE_SIZE))
                .thenReturn(Mono.just(expectedSearchResult));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path( "/products/by-category")
                                .queryParam("category", CATEGORY)
                                .queryParam("pageNum", PAGE_NUM)
                                .queryParam("pageSize", PAGE_SIZE)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .consumeWith(resp -> assertThat(resp.getResponseBody()).isNotNull())
                .jsonPath("$.itemList[0].id").isEqualTo(expectedProductList.get(0).getId())
                .jsonPath("$.itemList[1].id").isEqualTo(expectedProductList.get(1).getId())
                .jsonPath("$.itemList[2].id").isEqualTo(expectedProductList.get(2).getId())
                .jsonPath("$.itemList[3].id").isEqualTo(expectedProductList.get(3).getId())
                .jsonPath("$.itemList[4].id").isEqualTo(expectedProductList.get(4).getId())
                .jsonPath("$.hasNextPage").isEqualTo(expectedSearchResult.getHasNextPage())
                .jsonPath("$.nextPageNum").isEqualTo(expectedSearchResult.getNextPageNum())
                .jsonPath("$.nextPageSize").isEqualTo(expectedSearchResult.getNextPageSize())
                .jsonPath("$.totalPageNum").isEqualTo(expectedSearchResult.getTotalPageNum());
    }

    @Test
    void testFindByCategoryWithoutPagable() {

        List<Product> expectedProductList = generateProduct(5);

        SearchResult<Product> expectedSearchResult = SearchResult.<Product>builder()
                .itemList(expectedProductList)
                .hasNextPage(false)
                .totalPageNum(1)
                .build();

        when(productService.getProductsByCategory(CATEGORY, PAGE_NUM, PAGE_SIZE))
                .thenReturn(Mono.just(expectedSearchResult));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path( "/products/by-category")
                                .queryParam("category", CATEGORY)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .consumeWith(resp -> assertThat(resp.getResponseBody()).isNotNull())
                .jsonPath("$.itemList[0].id").isEqualTo(expectedProductList.get(0).getId())
                .jsonPath("$.itemList[1].id").isEqualTo(expectedProductList.get(1).getId())
                .jsonPath("$.itemList[2].id").isEqualTo(expectedProductList.get(2).getId())
                .jsonPath("$.itemList[3].id").isEqualTo(expectedProductList.get(3).getId())
                .jsonPath("$.itemList[4].id").isEqualTo(expectedProductList.get(4).getId())
                .jsonPath("$.hasNextPage").isEqualTo(expectedSearchResult.getHasNextPage())
                .jsonPath("$.nextPageNum").isEmpty()
                .jsonPath("$.nextPageSize").isEmpty()
                .jsonPath("$.totalPageNum").isEqualTo(expectedSearchResult.getTotalPageNum());
    }

    @Test
    void testFindByKeyword() {
        List<Product> expectedProductList = generateProduct(5);

        SearchResult<Product> expectedSearchResult = SearchResult.<Product>builder()
                .itemList(expectedProductList)
                .hasNextPage(true)
                .nextPageNum(1)
                .nextPageSize(5)
                .totalPageNum(4)
                .build();

        when(productService.getProductsByQuerystring(KEYWORD, PAGE_NUM, PAGE_SIZE))
                .thenReturn(Mono.just(expectedSearchResult));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path( "/products/by-keyword")
                                .queryParam("keyword", KEYWORD)
                                .queryParam("pageNum", PAGE_NUM)
                                .queryParam("pageSize", PAGE_SIZE)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .consumeWith(resp -> assertThat(resp.getResponseBody()).isNotNull())
                .jsonPath("$.itemList[0].id").isEqualTo(expectedProductList.get(0).getId())
                .jsonPath("$.itemList[1].id").isEqualTo(expectedProductList.get(1).getId())
                .jsonPath("$.itemList[2].id").isEqualTo(expectedProductList.get(2).getId())
                .jsonPath("$.itemList[3].id").isEqualTo(expectedProductList.get(3).getId())
                .jsonPath("$.itemList[4].id").isEqualTo(expectedProductList.get(4).getId())
                .jsonPath("$.hasNextPage").isEqualTo(expectedSearchResult.getHasNextPage())
                .jsonPath("$.nextPageNum").isEqualTo(expectedSearchResult.getNextPageNum())
                .jsonPath("$.nextPageSize").isEqualTo(expectedSearchResult.getNextPageSize())
                .jsonPath("$.totalPageNum").isEqualTo(expectedSearchResult.getTotalPageNum());
    }

    @Test
    void testFindBRelevantProducts() {
        List<Product> expectedProductList = generateProduct(5);

        SearchResult<Product> expectedSearchResult = SearchResult.<Product>builder()
                .itemList(expectedProductList)
                .hasNextPage(true)
                .nextPageNum(1)
                .nextPageSize(5)
                .totalPageNum(4)
                .build();

        when(productService.getRelevantProducts(PRODUCT_ID, PAGE_NUM, PAGE_SIZE))
                .thenReturn(Mono.just(expectedSearchResult));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path( String.format("/products/%s/relevant-products", PRODUCT_ID))
                                .queryParam("pageNum", PAGE_NUM)
                                .queryParam("pageSize", PAGE_SIZE)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .consumeWith(resp -> assertThat(resp.getResponseBody()).isNotNull())
                .jsonPath("$.itemList[0].id").isEqualTo(expectedProductList.get(0).getId())
                .jsonPath("$.itemList[1].id").isEqualTo(expectedProductList.get(1).getId())
                .jsonPath("$.itemList[2].id").isEqualTo(expectedProductList.get(2).getId())
                .jsonPath("$.itemList[3].id").isEqualTo(expectedProductList.get(3).getId())
                .jsonPath("$.itemList[4].id").isEqualTo(expectedProductList.get(4).getId())
                .jsonPath("$.hasNextPage").isEqualTo(expectedSearchResult.getHasNextPage())
                .jsonPath("$.nextPageNum").isEqualTo(expectedSearchResult.getNextPageNum())
                .jsonPath("$.nextPageSize").isEqualTo(expectedSearchResult.getNextPageSize())
                .jsonPath("$.totalPageNum").isEqualTo(expectedSearchResult.getTotalPageNum());
    }

    private List<Product> generateProduct(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> generateProduct())
                .collect(toList());
    }

    private Product generateProduct() {
        return Product.builder()
                .id(UUID.randomUUID().toString())
                .name(faker.name().name())
                .category(CATEGORY)
                .brand(faker.company().name())
                .inStock(true)
                .price(faker.number().randomDouble(2, 1, 5))
                .build();
    }

}
