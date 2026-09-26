package com.example.model

enum class ThemeMode(val title: String) {
    DARK("Dark"),
    LIGHT("Light"),
    SYSTEM("System Default")
}

enum class GlassEffect(val title: String, val description: String) {
    STANDARD("Standard Solid", "Classic clean opaque background surfaces"),
    SOFT_GLASS("Soft Glass", "Gentle translucent acrylic with subtle glowing border"),
    FROSTED_GLASS("Frosted Glass", "High-refraction frosted glass with translucent depth")
}

enum class AudioQuality(val title: String, val bitrate: String) {
    NORMAL("Normal", "96 kbps"),
    HIGH("High", "160 kbps"),
    VERY_HIGH("Very High", "320 kbps"),
    LOSSLESS("Lossless FLAC", "1411 kbps")
}

data class UserAccount(
    val id: String = "user_default",
    val displayName: String = "Guest Listener",
    val email: String = "listener@pulse.music",
    val isPremium: Boolean = true,
    val avatarUrl: String = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&auto=format&fit=crop&q=80",
    val isSignedIn: Boolean = false,
    val lastSyncTime: String = "Just now"
)

data class StorageStats(
    val downloadedBytes: Long = 1_450_000_000L, // 1.45 GB
    val otherAppsBytes: Long = 34_500_000_000L,  // 34.5 GB
    val freeSpaceBytes: Long = 92_000_000_000L   // 92 GB
)
