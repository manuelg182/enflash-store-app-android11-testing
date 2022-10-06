package com.enflash.mobile.storeapp.main.model

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


@Keep
class ResponseStoreSaturated {

    @SerializedName("companyId")
    @Expose
    var companyId: Long? = null

    @SerializedName("saturated")
    @Expose
    var saturated: Boolean? = null

    @SerializedName("avgtime")
    @Expose
    var avgtime: Long? = null

}