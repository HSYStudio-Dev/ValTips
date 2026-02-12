package com.hsystudio.valtips.data.auth

import android.net.Uri

object LoginDeepLinkParser {
    fun parseToken(uri: Uri?): String? {
        if (uri == null) return null
        if (uri.scheme != "valtips") return null
        if (uri.host != "login-success") return null

        return uri.getQueryParameter("token")
    }
}
