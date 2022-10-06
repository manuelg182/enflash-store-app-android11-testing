package com.enflash.mobile.storeapp.main.model

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
class StoreSaturationChange {

    @SerializedName("companyId")
    @Expose
    var companyId: Long? = null

    @SerializedName("status")
    @Expose
    var status: Boolean? = null
}