package com.github.omarmiatello.kotlinscripttoolbox.twitter

import com.github.omarmiatello.kotlinscripttoolbox.core.MessageBreakFallback
import com.github.omarmiatello.kotlinscripttoolbox.core.MessageL1Scope
import com.github.omarmiatello.kotlinscripttoolbox.core.ThrowError

fun MessageL1Scope.twitterUrl(
    url: String,
    fallback: MessageBreakFallback = ThrowError,
) = url(
    url = url,
    fallback = fallback,
    messageSize = TwitterScope.URL_MAX_SIZE,
)