package com.enflash.mobile.storeapp.main.model

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


@Keep
class ResponseOperationalActions {
    @SerializedName("banner")
    @Expose
    var banner: String? = null

    @SerializedName("averageTime")
    @Expose
    var averageTime: Long? = 0L

    @SerializedName("enabled")
    @Expose
    var enabled: Boolean? = false

    @SerializedName("saturated")
    @Expose
    var saturated: Boolean? = false

    @SerializedName("autoAcceptance")
    @Expose
    var autoAcceptance: Boolean? = false

    @SerializedName("timeToReject")
    @Expose
    var timeToReject: Long? = 5L

    override fun toString(): String {
        return "{\n" +
                "\tbanner: $banner,\n" +
                "\taverageTime: $averageTime,\n" +
                "\tenabled: $enabled,\n" +
                "\tsaturated: $saturated,\n" +
                "\tautoAcceptance: $autoAcceptance\n" +
                "\ttimeToReject: $timeToReject\n" +
                "}"
    }
}