package com.bignerdranch.android.shared

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform