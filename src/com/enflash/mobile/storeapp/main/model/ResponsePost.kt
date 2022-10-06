package com.enflash.mobile.storeapp.main.model

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


@Keep
class ResponsePost {

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("enabled")
    @Expose
    var enabled: Boolean? = null

    @SerializedName("error")
    @Expose
    var error: Any? = null

}