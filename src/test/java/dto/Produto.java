package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record Produto(
        @JsonProperty("title") String nome,
        @JsonProperty("price") BigDecimal preco,
        @JsonProperty("description") String descricao,
        @JsonProperty("stock") Integer quantidade
) {
}
