package com.github.omarmiatello.kotlinscripttoolbox.core

class Cache {
    private val map = mutableMapOf<CacheKey<*>, Any>()

    fun <T> getOrNull(key: CacheKey<T>): T? =
        map[key] as T?

    fun <T : Any> get(key: CacheKey<T>): T =
        getOrNull(key) ?: error("Key not found: $key")

    fun <T : Any> put(key: CacheKey<T>, value: T) {
        map[key] = value
    }
}

class CacheKey<T>
