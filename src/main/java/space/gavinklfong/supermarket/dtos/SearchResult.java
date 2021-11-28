package space.gavinklfong.supermarket.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.With;
import space.gavinklfong.supermarket.models.Product;

import java.util.List;

@Data
@Builder
public class SearchResult<T> {
    @With
    private List<T> itemList;
    private Integer nextPageNum;
    private Integer nextPageSize;
    private Integer totalPageNum;
    private Boolean hasNextPage;
}
