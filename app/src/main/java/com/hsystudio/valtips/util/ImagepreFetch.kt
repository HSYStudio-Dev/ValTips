package com.hsystudio.valtips.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import coil3.ImageLoader
import coil3.request.ImageRequest

// 이미지 URL을 미리 로드하여 메모리/디스크 캐시에 저장
@Composable
fun PrefetchImages(
    imageLoader: ImageLoader,
    urls: List<String>,
    widthPx: Int,
    heightPx: Int
) {
    val context = LocalContext.current

    LaunchedEffect(urls, widthPx, heightPx) {
        urls.forEach { url ->
            val request = ImageRequest.Builder(context)
                .data(url)
                .size(widthPx, heightPx)
                .memoryCacheKey("$url-$widthPx-$heightPx")
                .diskCacheKey("$url-$widthPx-$heightPx")
                .build()
            imageLoader.enqueue(request)
        }
    }
}
