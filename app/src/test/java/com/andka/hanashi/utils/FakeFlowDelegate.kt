package com.andka.hanashi.utils

import kotlinx.coroutines.flow.MutableSharedFlow

class FakeFlowDelegate<T> {
    val flow: MutableSharedFlow<T> = MutableSharedFlow()
    suspend fun emit(v: T) = flow.emit(v)
}