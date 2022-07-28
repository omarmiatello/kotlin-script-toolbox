package com.github.omarmiatello.kotlinscripttoolbox.core

sealed interface MessageBreakFallback
object Skip : MessageBreakFallback
object ThrowError : MessageBreakFallback
object Crop : MessageBreakFallback
object Split : MessageBreakFallback