package space.gavinklfong.supermarket.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import space.gavinklfong.supermarket.models.Product;
import space.gavinklfong.supermarket.repositories.ProductRepository;

@RestController
@RequestMapping("products")
public class ProductController {

    @Autowired
    private ProductRepository productRepo;

//    @GetMapping("/query")
//    public Flux<Product> findByStringQuery(@RequestParam String query, @RequestParam Integer pageNumber) {
//        return productRepo.findByStringQuery(query);
//    }

    @GetMapping("/category")
    public Flux<Product> findProductsByCategory(@RequestParam String category, @RequestParam Integer pageNumber) {
        return productRepo.findByCategoryStartingWith(category);
    }
}
