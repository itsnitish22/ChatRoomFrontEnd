package com.nitishsharma.chatapp.di

import com.nitishsharma.chatapp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

val networkModule = module {
    fun provideLoggingInterceptor() =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    single { provideLoggingInterceptor() }

    fun provideClientBuilder(httpLoggingInterceptor: HttpLoggingInterceptor) =
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS) // Adjust the timeout as needed
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
    single { provideClientBuilder(get()) }

    fun provideRetrofit(httpClientBuilder: OkHttpClient.Builder): Retrofit {
        return try {
            Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(httpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        } catch (e: java.net.SocketTimeoutException) {
            Timber.tag("Socket Connection Exception").e(e.message.toString())
            throw e
        } catch (e: Exception) {
            Timber.tag("Network Exception").e(e.message.toString())
            throw e
        } ?: throw IllegalStateException("All tries failed to create Retrofit instance")
    }

    single { provideRetrofit(get()) }
}