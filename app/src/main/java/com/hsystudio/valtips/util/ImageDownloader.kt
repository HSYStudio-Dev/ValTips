package com.hsystudio.valtips.util

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

// Image Url을 받아 앱 내부 캐시 폴더에 저장하는 유틸
@Singleton
class ImageDownloader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val client: OkHttpClient
) {
    private val tag = "ImageDownloader"

    private val baseDir by lazy { File(context.filesDir, "valtips/img").apply { mkdirs() } }

    private fun fileFor(filename: String): File = File(baseDir, filename)

    // HEAD 로 content-length 구하기 (실패 시 -1)
    private fun headContentLength(url: String): Long {
        return try {
            val req = Request.Builder().url(url).head().build()
            client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) return -1L
                resp.header("Content-Length")?.toLongOrNull() ?: -1L
            }
        } catch (_: Throwable) {
            -1L
        }
    }

    /**
     * 단일 파일 다운로드
     * @param onBytesRead 현재 파일에서 읽힌 바이트 증가량 전달
     */
    private suspend fun downloadToWithProgress(
        url: String,
        filename: String,
        forceRefresh: Boolean,
        onBytesRead: ((delta: Long) -> Unit)? = null
    ): String = withContext(Dispatchers.IO) {
        val dest = fileFor(filename)

        if (!forceRefresh && dest.exists() && dest.length() > 0L) {
            return@withContext dest.absolutePath
        }

        val tmp = File(dest.parentFile, "${dest.name}.part")
        try {
            val req = Request.Builder().url(url).build()
            client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) error("HTTP ${resp.code}")
                val body = resp.body ?: error("Empty body")

                body.byteStream().use { input ->
                    tmp.outputStream().use { output ->
                        val buf = ByteArray(DEFAULT_BUFFER_SIZE)
                        while (true) {
                            val read = input.read(buf)
                            if (read <= 0) break
                            output.write(buf, 0, read)
                            onBytesRead?.invoke(read.toLong())
                        }
                    }
                }
            }
            if (dest.exists()) dest.delete()
            tmp.renameTo(dest)
            dest.absolutePath
        } catch (e: Exception) {
            Log.e(tag, "downloadToWithProgress() 실패 : $url", e)
            tmp.delete()
            dest.delete()
            throw e
        }
    }

    /**
     * 여러 이미지 병렬 다운로드
     * - 전체 바이트 합과 지금까지 받은 바이트를 기반으로 진행률 계산
     * - 평균 전송 속도(bytes/sec) 계산
     *
     * @param onProgress (doneItems, totalItems, bytesRead, totalBytes, bytesPerSec)
     * @return Map<URL, LocalPath> (성공한 항목만)
     */
    suspend fun downloadAll(
        tasks: List<Pair<String, String>>,
        concurrency: Int = 6,
        forceRefresh: Boolean = false,
        onProgress: ((done: Int, total: Int, bytesRead: Long, totalBytes: Long, bytesPerSec: Long) -> Unit)? = null
    ): Map<String, String> = coroutineScope {
        if (tasks.isEmpty()) return@coroutineScope emptyMap()

        // 1) 총 바이트 미리 추정 (알 수 없으면 -1 유지)
        val sizes = tasks.associate { it.first to headContentLength(it.first) }
        val totalBytesKnown = sizes.values.filter { it > 0 }.sum().takeIf { it > 0 } ?: -1L

        val totalItems = tasks.size
        val completedItems = AtomicInteger(0)
        val bytesReadAll = AtomicLong(0L)
        val startTimeNs = System.nanoTime()

        // 2) 세마포어로 동시성 제한
        val semaphore = Semaphore(concurrency)
        val results = mutableMapOf<String, String>()

        val jobs = tasks.map { (url, filename) ->
            async(Dispatchers.IO) {
                semaphore.withPermit {
                    // 파일 다운로드 중 바이트 단위 콜백 → 전체 누적 갱신 + 속도 계산
                    val localOnBytes: (Long) -> Unit = { delta ->
                        val all = bytesReadAll.addAndGet(delta)
                        if (onProgress != null) {
                            val elapsedSec = max(1L, (System.nanoTime() - startTimeNs) / 1_000_000_000L)
                            val speed = all / elapsedSec // bytes/sec 평균
                            val done = completedItems.get()
                            onProgress.invoke(done, totalItems, all, totalBytesKnown, speed)
                        }
                    }

                    try {
                        val path = downloadToWithProgress(url, filename, forceRefresh, localOnBytes)
                        synchronized(results) { results[url] = path }
                    } catch (_: Throwable) {
                    } finally {
                        val done = completedItems.incrementAndGet()
                        if (onProgress != null) {
                            val all = bytesReadAll.get()
                            val elapsedSec = max(1L, (System.nanoTime() - startTimeNs) / 1_000_000_000L)
                            val speed = all / elapsedSec
                            onProgress.invoke(done, totalItems, all, totalBytesKnown, speed)
                        }
                    }
                }
            }
        }
        jobs.forEach { it.await() }
        results
    }
}
