package com.hsystudio.valtips.util

import java.io.File
import androidx.core.net.toUri

// Coil 이미지 로딩을 위한 경로 변환 유틸 함수
fun toCoilModel(path: String?): Any? {
    if (path.isNullOrBlank()) return null
    return when {
        path.startsWith("file://") -> path
        path.startsWith("content://") -> path.toUri()
        path.startsWith("http://") || path.startsWith("https://") -> path
        path.startsWith("/") -> File(path)
        else -> path
    }
}
