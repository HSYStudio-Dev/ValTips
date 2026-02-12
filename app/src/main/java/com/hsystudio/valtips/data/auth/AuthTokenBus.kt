package com.hsystudio.valtips.data.auth

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthTokenBus @Inject constructor() {
    private val _tokens = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val tokens: SharedFlow<String> = _tokens.asSharedFlow()

    fun emit(token: String) {
        _tokens.tryEmit(token)
    }
}
