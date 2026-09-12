package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProdutoResponse(
        Integer id,
        @JsonProperty("title") String nome,
        @JsonProperty("price") BigDecimal preco,
        @JsonProperty("description") String descricao,
        @JsonProperty("stock") Integer quantidade
) {
}
