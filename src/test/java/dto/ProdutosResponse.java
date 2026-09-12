package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProdutosResponse(
        List<ProdutoResponse> products,
        Integer total,
        Integer skip,
        Integer limit
) {
}
