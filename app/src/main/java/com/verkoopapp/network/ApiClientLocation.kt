package com.verkoopapp.network

import com.google.gson.GsonBuilder

import java.util.concurrent.TimeUnit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ApiClientLocation {
    private const val BASE_URL = "https://maps.googleapis.com/maps/api/place/"
 //   https://maps.googleapis.com/maps/api/place/textsearch/json?query=bareilly&key=AIzaSyC6ezTSfIS-pq-VX9zyq2qATzKWXZaPJqc
    private var retrofit: Retrofit? = null
   
    val client: MyService
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(GsonBuilder()
                                .setLenient()
                                .create()))
                        .client(buildClient())
                        .build()
            }
            return retrofit!!.create(MyService::class.java)
        }

    private fun buildClient(): OkHttpClient {
       
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .addInterceptor(interceptor)
                .build()
    }
}
