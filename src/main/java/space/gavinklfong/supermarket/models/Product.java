package space.gavinklfong.supermarket.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName="products", createIndex = true)
//@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @Id
    String id;
    @Field(type = FieldType.Text)
    String name;
    @Field(type = FieldType.Text)
    String category;
    @Field(type = FieldType.Text)
    Boolean inStock;
    @Field(type = FieldType.Text)
    String brand;
    @Field(type = FieldType.Double)
    Double price;
    @Field(type = FieldType.Double)
    Double updatedPrice;
}
