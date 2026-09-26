package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.SpeakerGroup
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TabletMac
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ConnectedDevice
import com.example.model.DeviceType
import com.example.model.PlaybackState
import com.example.ui.components.AudioEqualizerBars
import com.example.ui.theme.SpotifyBlack
import com.example.ui.theme.SpotifyCardGray
import com.example.ui.theme.SpotifyDarkCharcoal
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.SpotifyMint
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun DevicesScreen(
    devices: List<ConnectedDevice>,
    playbackState: PlaybackState,
    onSelectDevice: (ConnectedDevice) -> Unit,
    onVolumeChange: (ConnectedDevice, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var multiRoomSync by remember { mutableStateOf(false) }
    var autoTransferOnWifi by remember { mutableStateOf(true) }

    val currentDevice = devices.firstOrNull { it.isCurrent } ?: devices.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SpotifyBlack)
            .padding(horizontal = 16.dp)
            .testTag("connect_hub_screen"),
        contentPadding = PaddingValues(top = 20.dp, bottom = 120.dp)
    ) {
        item {
            Text(
                text = "Pulse Connect",
                color = TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Stream seamlessly across mobile, desktop, TV, and speakers",
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Active Device Hero Card
        if (currentDevice != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .testTag("active_device_card"),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF222824))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(SpotifyGreen.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = getDeviceIcon(currentDevice.type),
                                        contentDescription = null,
                                        tint = SpotifyGreen,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "STREAMING ACTIVE",
                                        color = SpotifyGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = currentDevice.name,
                                        color = TextPrimary,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            AudioEqualizerBars(
                                isPlaying = playbackState.isPlaying,
                                barCount = 4,
                                barWidth = 3.dp,
                                maxHeight = 18.dp,
                                barColor = SpotifyGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Audio specs pill
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF161A17))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Format: 24-bit / 96kHz Lossless",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "Latency: < 5ms",
                                color = SpotifyMint,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Volume Control
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                            var sliderVal by remember(currentDevice.volumePercent) {
                                mutableFloatStateOf(currentDevice.volumePercent.toFloat())
                            }
                            Slider(
                                value = sliderVal,
                                onValueChange = { newVal ->
                                    sliderVal = newVal
                                    onVolumeChange(currentDevice, newVal.toInt())
                                },
                                valueRange = 0f..100f,
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(
                                    thumbColor = SpotifyGreen,
                                    activeTrackColor = SpotifyGreen,
                                    inactiveTrackColor = Color(0xFF444444)
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${currentDevice.volumePercent}%",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Available Devices Section
        item {
            Text(
                text = "Available On Your Network",
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(devices.filter { !it.isCurrent }) { device ->
            Surface(
                onClick = { onSelectDevice(device) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp)),
                color = Color(0xFF1E1E1E)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2C2C2C)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getDeviceIcon(device.type),
                            contentDescription = null,
                            tint = if (device.isOnline) TextPrimary else TextTertiary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = device.name,
                            color = if (device.isOnline) TextPrimary else TextTertiary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (device.isOnline) "${device.connectionProtocol} • ${device.ipAddress}" else "Offline",
                            color = TextTertiary,
                            fontSize = 11.sp
                        )
                    }

                    if (device.isOnline) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SpotifyGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Transfer Audio",
                                color = SpotifyGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Advanced Cross-Platform Toggles
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Sync & Handover Settings",
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1E1E1E),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Auto Handover on Wi-Fi",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Transfer playback to home speakers when entering Wi-Fi",
                                color = TextTertiary,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = autoTransferOnWifi,
                            onCheckedChange = { autoTransferOnWifi = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SpotifyGreen,
                                checkedTrackColor = SpotifyGreen.copy(alpha = 0.4f)
                            )
                        )
                    }

                    HorizontalDivider(color = Color(0xFF2C2C2C), modifier = Modifier.padding(vertical = 10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Multi-Room Speaker Broadcast",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Sync audio simultaneously across all room speakers",
                                color = TextTertiary,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = multiRoomSync,
                            onCheckedChange = { multiRoomSync = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SpotifyGreen,
                                checkedTrackColor = SpotifyGreen.copy(alpha = 0.4f)
                            )
                        )
                    }
                }
            }
        }

        // Web Client Pairing Instructions
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161E1A)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = null,
                        tint = SpotifyMint,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Stream on Web & Desktop",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Visit pulse.music/play on any laptop or browser to stream seamlessly with this device.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

private fun getDeviceIcon(type: DeviceType): ImageVector {
    return when (type) {
        DeviceType.SMARTPHONE -> Icons.Default.PhoneAndroid
        DeviceType.TABLET -> Icons.Default.TabletMac
        DeviceType.DESKTOP -> Icons.Default.Computer
        DeviceType.SMART_TV -> Icons.Default.Tv
        DeviceType.SMART_SPEAKER -> Icons.Default.Speaker
        DeviceType.CAR -> Icons.Default.DirectionsCar
    }
}
