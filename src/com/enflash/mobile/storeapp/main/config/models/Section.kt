package com.enflash.mobile.storeapp.main.config.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


@Keep
class Section {
    @SerializedName("id")
    @Expose
    var id: Long? = null

    @SerializedName("sequence")
    @Expose
    var sequence: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("enabled")
    @Expose
    var enabled: Boolean? = null

    @SerializedName("product_ids")
    @Expose
    var productIds: List<Long>? = null

}