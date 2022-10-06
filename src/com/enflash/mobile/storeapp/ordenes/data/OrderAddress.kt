package com.enflash.mobile.storeapp.ordenes.data

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal


@Keep
data class OrderAddress (
        @SerializedName("id")
        @Expose
        var id: String? = null,
        @SerializedName("name")
        @Expose
        var name: String? = null,
        @SerializedName("street")
        @Expose
        var street: String? = null,
        @SerializedName("street2")
        @Expose
        var street2: String? = null,
        @SerializedName("zip")
        @Expose
        var zip: String? = null,
        @SerializedName("partner_latitude")
        @Expose
        var partner_latitude: BigDecimal? = null,
        @SerializedName("partner_longitude")
        @Expose
        var partner_longitude: BigDecimal? = null

)