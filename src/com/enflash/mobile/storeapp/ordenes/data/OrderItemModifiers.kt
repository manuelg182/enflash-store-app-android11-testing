package com.enflash.mobile.storeapp.ordenes.data

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal


@Keep
data class OrderItemModifiers (
        @SerializedName("id")
        @Expose
        var id: String? = null,
        @SerializedName("name")
        @Expose
        var name: String? = null,
        @SerializedName("selection")
        @Expose
        var selection: List<OrderItemModifierSelection> = ArrayList()

)