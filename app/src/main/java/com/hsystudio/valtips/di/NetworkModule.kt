package com.hsystudio.valtips.di

import com.hsystudio.valtips.BuildConfig
import com.hsystudio.valtips.data.remote.api.LineupApi
import com.hsystudio.valtips.data.remote.api.ResourceApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.BASIC
            }
        }

    @Provides
    @Singleton
    fun provideCommonHeaderInterceptor(): Interceptor = Interceptor { chain ->
        val ua = "ValTips/${BuildConfig.VERSION_NAME} (Android)"
        val req = chain.request().newBuilder()
            .header("User-Agent", ua)
            .build()
        chain.proceed(req)
    }

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        logging: HttpLoggingInterceptor,
        commonHeader: Interceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(commonHeader)
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .callTimeout(40, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient,
        json: Json
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun provideResourceApi(retrofit: Retrofit): ResourceApi =
        retrofit.create(ResourceApi::class.java)

    @Provides
    @Singleton
    fun provideLineupApi(retrofit: Retrofit): LineupApi =
        retrofit.create(LineupApi::class.java)
}
