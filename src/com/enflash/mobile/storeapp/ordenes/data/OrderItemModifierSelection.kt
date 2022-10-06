package com.enflash.mobile.storeapp.ordenes.data

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal


@Keep
data class OrderItemModifierSelection (
        @SerializedName("uuid")
        @Expose
        var uuid: String,
        @SerializedName("id")
        @Expose
        var id: String? = null,
        @SerializedName("name")
        @Expose
        var name: String? = null,
        @SerializedName("quantity")
        @Expose
        var quantity: Int? = null,
        @SerializedName("price")
        @Expose
        var price: BigDecimal? = null
)