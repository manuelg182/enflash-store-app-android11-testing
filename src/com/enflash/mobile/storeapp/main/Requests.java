package com.enflash.mobile.storeapp.main;


import com.enflash.mobile.storeapp.main.model.RequestPushTokenPost;
import com.enflash.mobile.storeapp.main.model.ResponseOperationalActions;
import com.enflash.mobile.storeapp.main.model.ResponseStoreAutoAccept;
import com.enflash.mobile.storeapp.main.config.models.Section;
import com.enflash.mobile.storeapp.main.config.models.Product;
import com.enflash.mobile.storeapp.main.model.RequestPost;
import com.enflash.mobile.storeapp.main.model.ResponsePost;
import com.enflash.mobile.storeapp.main.model.ResponseOrderStatus;
import com.enflash.mobile.storeapp.main.model.ResponseStore;
import com.enflash.mobile.storeapp.main.model.ResponseStoreSaturated;
import com.enflash.mobile.storeapp.main.model.ResponseStoreStatusChange;
import com.enflash.mobile.storeapp.main.model.StoreSaturationChange;
import com.enflash.mobile.storeapp.main.model.StoreStatusChange;
import com.enflash.mobile.storeapp.main.model.ResponseNewOrder;
import com.enflash.mobile.storeapp.utils.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

interface Requests {

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "x-api-key: " + Constants.X_API_KEY_SERVICES
    })
    @GET("store/{store-id}/products")
    Call<List<Product>> getProductos(@Path("store-id") Long storeId);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "x-api-key: " + Constants.X_API_KEY_SERVICES
    })
    @GET("/store/{store-id}/sections")
    Call<List<Section>> getCategorias(@Path("store-id") Long storeId);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "x-api-key: " + Constants.X_API_KEY_SERVICES
    })
    @POST("store/{store-id}/products/{product-id}")
    Call<ResponsePost> updateStatus(@Path("store-id") Long storeId, @Path("product-id") Long productId, @Body RequestPost dto);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "x-api-key: " + Constants.X_API_KEY_SERVICES
    })
    @POST("/store/{store-id}/sections/{section-id}")
    Call<ResponsePost> updateStatusSection(@Path("store-id") Long storeId, @Path("section-id") Long sectionId, @Body RequestPost dto);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "x-api-key: " + Constants.X_API_KEY_SERVICES
    })
    @POST("/sns/register-push-token")
    Call<Boolean> registerPushToken(@Body RequestPushTokenPost dto);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "x-api-key: " + Constants.X_API_KEY_SERVICES
    })
    @GET("/store/{store-id}/operational-actions")
    Call<ResponseOperationalActions> getOperationalActions(@Path("store-id") Long companyId);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "x-api-key: " + Constants.X_API_KEY_SERVICES
    })
    @POST("/orders/v1/api/orders/accept-new-request")
    Call<ResponseOrderStatus> acceptNewOrderRequest(@Body ResponseOrderStatus dto);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "x-api-key: " + Constants.X_API_KEY_SERVICES
    })
    @POST("/store/status-operations")
    Call<ResponseStoreStatusChange> storeChangeConfig(@Body StoreStatusChange dto);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "x-api-key: " + Constants.X_API_KEY_SERVICES
    })
    @POST("/store/change-acceptance-config")
    Call<ResponseStoreAutoAccept> changeAcceptanceConfig(@Body StoreSaturationChange dto);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "x-api-key: " + Constants.X_API_KEY_SERVICES
    })
    @POST("/store/change-saturation-config")
    Call<ResponseStoreSaturated> changeSaturationConfig(@Body StoreSaturationChange dto);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "x-api-key: " + Constants.X_API_KEY_SERVICES
    })
    @POST("/orders/v1/api/orders/collected")
    Call<ResponseOrderStatus> orderCollected(@Body ResponseStore dto);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "x-api-key: " + Constants.X_API_KEY_SERVICES
    })
    @POST("/orders/v1/api/orders/delivered")
    Call<ResponseOrderStatus> orderDelivered(@Body ResponseStore dto);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "x-api-key: " + Constants.X_API_KEY_SERVICES
    })
    @POST("/orders/v1/api/orders/mark-as-arriving")
    Call<ResponseOrderStatus> orderMarkAsArriving(@Body ResponseStore dto);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "x-api-key: " + Constants.X_API_KEY_SERVICES
    })
    @POST("/orders/v1/api/orders/ready-to-collect")
    Call<ResponseOrderStatus> orderReadyToCollect(@Body ResponseStore dto);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "x-api-key: " + Constants.X_API_KEY_SERVICES
    })
    @POST("/orders/v1/api/orders/reject-new-request")
    Call<ResponseOrderStatus> orderRejectNewRequest(@Body ResponseStore dto);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "x-api-key: " + Constants.X_API_KEY_SERVICES
    })
    @GET("/orders/v1/api/orders/{store-id}/{order-id}")
    Call<ResponseNewOrder> orderGetNewRequest(@Path("store-id") Long companyId, @Path("order-id") String orderId);

}
