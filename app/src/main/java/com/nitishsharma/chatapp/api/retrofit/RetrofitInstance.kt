package com.nitishsharma.chatapp.api.retrofit

import com.nitishsharma.chatapp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = BuildConfig.BASE_URL

object RetrofitInstance {
    //interceptors
    var loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    var clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder().addInterceptor(
        loggingInterceptor
    )

    //private instance
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(clientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //public instance
    val api: ChatRoomAPI by lazy {
        retrofit.create(ChatRoomAPI::class.java)
    }
}