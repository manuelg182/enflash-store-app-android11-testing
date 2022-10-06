package com.enflash.mobile.storeapp.main.model

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


@Keep
class RequestPost {

    @SerializedName("enabled")
    @Expose
    var enabled: Boolean? = null

}