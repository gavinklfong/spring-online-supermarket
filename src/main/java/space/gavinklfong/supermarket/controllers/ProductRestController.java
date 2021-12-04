package space.gavinklfong.supermarket.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import space.gavinklfong.supermarket.dtos.SearchResult;
import space.gavinklfong.supermarket.models.Product;
import space.gavinklfong.supermarket.services.ProductService;

@Slf4j
@RestController
@RequestMapping("/products")
public class ProductRestController {

    static private final String DEFAULT_PAGE_NUM = "0";
    static private final String DEFAULT_PAGE_SIZE = "5";

    @Autowired
    private ProductService productService;

    @GetMapping(path = {"/by-category"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<SearchResult<Product>> searchByCategory(@RequestParam(required = true) String category,
                                                        @RequestParam(defaultValue = DEFAULT_PAGE_NUM) Integer pageNum,
                                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) Integer pageSize) {
        return productService.getProductsByCategory(category, pageNum, pageSize);
    }

    @GetMapping(path = {"/by-keyword"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<SearchResult<Product>> searchByKeyword(@RequestParam(required = true) String keyword,
                                                        @RequestParam(defaultValue = DEFAULT_PAGE_NUM) Integer pageNum,
                                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) Integer pageSize) {
        return productService.getProductsByQuerystring(keyword, pageNum, pageSize);
    }

    @GetMapping(path = {"/{productId}/relevant-products"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<SearchResult<Product>> searchRelevantProducts(@PathVariable(required = true) String productId,
                                                       @RequestParam(defaultValue = DEFAULT_PAGE_NUM) Integer pageNum,
                                                       @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) Integer pageSize) {
        return productService.getRelevantProducts(productId, pageNum, pageSize);
    }
}
