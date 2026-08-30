package com.metro.store.data.model

data class DownloadInfo(
    val progress: Int = 0,
    val bytesCopied: Long = 0,
    val speed: Long = 0
)
