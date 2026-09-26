package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.ConnectedDevice
import com.example.model.DeviceType

@Entity(tableName = "devices")
data class ConnectedDeviceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val typeName: String,
    val isCurrent: Boolean,
    val isOnline: Boolean,
    val volumePercent: Int,
    val connectionProtocol: String,
    val ipAddress: String
) {
    fun toDevice(): ConnectedDevice {
        val type = try {
            DeviceType.valueOf(typeName)
        } catch (_: Exception) {
            DeviceType.SMARTPHONE
        }
        return ConnectedDevice(
            id = id,
            name = name,
            type = type,
            isCurrent = isCurrent,
            isOnline = isOnline,
            volumePercent = volumePercent,
            connectionProtocol = connectionProtocol,
            ipAddress = ipAddress
        )
    }

    companion object {
        fun fromDevice(device: ConnectedDevice): ConnectedDeviceEntity {
            return ConnectedDeviceEntity(
                id = device.id,
                name = device.name,
                typeName = device.type.name,
                isCurrent = device.isCurrent,
                isOnline = device.isOnline,
                volumePercent = device.volumePercent,
                connectionProtocol = device.connectionProtocol,
                ipAddress = device.ipAddress
            )
        }
    }
}
