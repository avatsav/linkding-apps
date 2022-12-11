package dev.avatsav.linkding

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform