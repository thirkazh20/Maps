package com.thirafikaz.mapsintermediate.Network;

import com.thirafikaz.mapsintermediate.model.ResponseRoute;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("directions/json")
    Call<ResponseRoute> requestRoute(
            @Query("origin") String origin,
            @Query("destination") String destination,
            @Query("mode") String mode,
            @Query("key") String key

    );
}
