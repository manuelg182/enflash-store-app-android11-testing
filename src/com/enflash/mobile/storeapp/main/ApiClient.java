package com.enflash.mobile.storeapp.main;

import android.content.Context;
import android.util.Log;

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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    Context context;
    Retrofit retrofit;

    public ApiClient(Context context) {
        this.context = context;

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_BASE_ENFLASH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public Call<List<Section>> getSections(Long id) {
        return retrofit.create(Requests.class).getCategorias(id);
    }

    public Call<List<Product>> getProducts(Long id) {
        return retrofit.create(Requests.class).getProductos(id);
    }

    public Call<ResponseOperationalActions> getOperationalActions(Long id) {
        return retrofit.create(Requests.class).getOperationalActions(id);
    }

    public Call<ResponsePost> updateStatus(Long id, Long productId, RequestPost dto) {
        Log.i("ApiClient", String.format("id: %s productId: %s, enabled: %s", id, productId, dto.getEnabled()));
        return retrofit.create(Requests.class).updateStatus(id, productId, dto);
    }

    public Call<ResponsePost> updateStatusSection(Long id, Long sectionId, RequestPost dto) {
        Log.i("ApiClient", String.format("id: %s sectionId: %s, enabled: %s", id, sectionId, dto.getEnabled()));
        return retrofit.create(Requests.class).updateStatusSection(id, sectionId, dto);
    }

    public Call<Boolean> registerPushToken(RequestPushTokenPost dto) {
        Log.i("ApiClient", String.format("id %s, token: %s", dto.getId(), dto.getToken()));
        return retrofit.create(Requests.class).registerPushToken(dto);
    }

    public Call<ResponseStoreStatusChange> storeChangeConfig(StoreStatusChange dto) {
        return retrofit.create(Requests.class).storeChangeConfig(dto);
    }

    public Call<ResponseStoreAutoAccept> changeAcceptanceConfig(StoreSaturationChange dto) {
        return retrofit.create(Requests.class).changeAcceptanceConfig(dto);
    }

    public Call<ResponseStoreSaturated> changeSaturationConfig(StoreSaturationChange dto) {
        return retrofit.create(Requests.class).changeSaturationConfig(dto);
    }

    public Call<ResponseOrderStatus> acceptNewOrderRequest(ResponseOrderStatus dto) {
        return retrofit.create(Requests.class).acceptNewOrderRequest(dto);
    }

    public Call<ResponseOrderStatus> orderCollected(ResponseStore dto) {
        return retrofit.create(Requests.class).orderCollected(dto);
    }

    public Call<ResponseOrderStatus> orderDelivered(ResponseStore dto) {
        return retrofit.create(Requests.class).orderDelivered(dto);
    }

    public Call<ResponseOrderStatus> orderMarkAsArriving(ResponseStore dto) {
        return retrofit.create(Requests.class).orderMarkAsArriving(dto);
    }

    public Call<ResponseOrderStatus> orderReadyToCollect(ResponseStore dto) {
        return retrofit.create(Requests.class).orderReadyToCollect(dto);
    }

    public Call<ResponseOrderStatus> orderRejectNewRequest(ResponseStore dto) {
        return retrofit.create(Requests.class).orderRejectNewRequest(dto);
    }

    public Call<ResponseNewOrder> orderGetNewRequest(Long companyId, String orderId) {
        return retrofit.create(Requests.class).orderGetNewRequest(companyId, orderId);
    }
}