package com.hsystudio.valtips.di

import android.content.Context
import coil3.ImageLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade

// 이미지 URL 네트워크 요청 및 메모리/디스크 캐시 저장 모듈
@Module
@InstallIn(SingletonComponent::class)
object ImageLoaderModule {
    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient
    ): ImageLoader = ImageLoader.Builder(context)
        .components {
            add(OkHttpNetworkFetcherFactory(okHttpClient))
        }
        .memoryCache {
            MemoryCache.Builder()
                .maxSizePercent(context, 0.25)  // 앱 메모리 25%
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve("image_cache"))
                .maxSizePercent(0.02)   // 앱 캐시 디렉토리 2%
                .build()
        }
        .crossfade(true)
        .build()
}
