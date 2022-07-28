package com.github.omarmiatello.kotlinscripttoolbox.core

sealed interface MessageAppendStrategy
object AppendIfNotDivide : MessageAppendStrategy
object AppendIfRoom : MessageAppendStrategy
object AppendNever : MessageAppendStrategy
