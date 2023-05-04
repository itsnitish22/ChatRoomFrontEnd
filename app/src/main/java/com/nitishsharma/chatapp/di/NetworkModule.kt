package com.nitishsharma.chatapp.di

import com.nitishsharma.chatapp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    fun provideLoggingInterceptor() =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    single { provideLoggingInterceptor() }

    fun provideClientBuilder(httpLoggingInterceptor: HttpLoggingInterceptor) =
        OkHttpClient.Builder().addInterceptor(
            httpLoggingInterceptor
        )
    single { provideClientBuilder(get()) }

    fun provideRetrofit(httpClientBuilder: OkHttpClient.Builder) = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(httpClientBuilder.build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    single { provideRetrofit(get()) }
}