package com.example.model

enum class DeviceType {
    SMARTPHONE,
    TABLET,
    DESKTOP,
    SMART_TV,
    SMART_SPEAKER,
    CAR
}

data class ConnectedDevice(
    val id: String,
    val name: String,
    val type: DeviceType,
    val isCurrent: Boolean,
    val isOnline: Boolean = true,
    val volumePercent: Int = 75,
    val connectionProtocol: String = "Pulse Connect (Zero-Latency)",
    val ipAddress: String = "192.168.1.100"
)
