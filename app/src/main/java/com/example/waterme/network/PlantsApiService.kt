package com.example.waterme.network

import com.example.waterme.model.Plant
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

const val BASE_URL = "https://github.com/Mario21145/PlantsRepository"

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface PlantApiService{
    @GET("/plants.json")
    suspend fun getPlantList() : Plant
}

object PlantApi {
    val retrofitService: PlantApiService by lazy {
        retrofit.create(PlantApiService::class.java)
    }
}