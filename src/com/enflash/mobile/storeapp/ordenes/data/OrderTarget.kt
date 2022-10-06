package com.enflash.mobile.storeapp.ordenes.data

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal


@Keep
data class OrderTarget (
        @SerializedName("id")
        @Expose
        var id: String? = null,
        @SerializedName("name")
        @Expose
        var name: String? = null,
        @SerializedName("address")
        @Expose
        var address: OrderAddress? = null

)