import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class ProductStockRequestDto
    @JsonCreator
    constructor(
        @JsonProperty("productList") val productList: List<ProductStock>,
    )

data class ProductStock
    @JsonCreator
    constructor(
        @JsonProperty("productId") val productId: Long,
        @JsonProperty("quantity") val quantity: Int,
    )
