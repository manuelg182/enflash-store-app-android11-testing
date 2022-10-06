package com.enflash.mobile.storeapp.main.config.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


@Keep
class Product {
    @SerializedName("id")
    @Expose
    var id: Long? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("enabled")
    @Expose
    var enabled: Boolean? = null

    @SerializedName("price")
    @Expose
    var price: Double? = null

    @SerializedName("thumbnail")
    @Expose
    var thumbnail: String? = null

}