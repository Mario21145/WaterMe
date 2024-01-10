package com.example.waterme.network


import com.example.waterme.model.PlantResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

const val BASE_URL = "https://raw.githubusercontent.com/Mario21145/PlantsRepository/main/"

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface PlantApiService{
    @GET("plants.json")
    suspend fun getPlantList() : PlantResponse
}

object PlantApi {
    val retrofitService: PlantApiService by lazy {
        retrofit.create(PlantApiService::class.java)
    }
}