package com.enflash.mobile.storeapp.ordenes.data;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


@Keep
public class OrderNewRequest implements Serializable {

    @SerializedName("companyId")
    @Expose
    private Long companyId;

    @SerializedName("orderId")
    @Expose
    private String orderId;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

}