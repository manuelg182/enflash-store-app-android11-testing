package com.enflash.mobile.storeapp.ordenes.data

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal


@Keep
data class OrderItems(
        @SerializedName("uuid")
        @Expose
        var uuid: String,

        @SerializedName("productId")
        @Expose
        var productId: Long? = null,

        @SerializedName("thumbnail")
        @Expose
        var thumbnail: String? = null,

        @SerializedName("banner")
        @Expose
        var banner: String? = null,

        @SerializedName("productPrice")
        @Expose
        var productPrice: Double? = null,

        @SerializedName("quantity")
        @Expose
        var quantity: Int? = null,
        @SerializedName("description")
        @Expose
        var description: String? = null,
        @SerializedName("price")
        @Expose
        var price: Double? = null,
        @SerializedName("discount")
        @Expose
        var discount: Double? = null,
        @SerializedName("total")
        @Expose
        var total: Double? = null,
        @SerializedName("modifiers")
        @Expose
        var modifiers: List<OrderItemModifiers>? = ArrayList()

)