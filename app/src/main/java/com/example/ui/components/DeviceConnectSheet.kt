package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.TabletMac
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ConnectedDevice
import com.example.model.DeviceType
import com.example.model.PlaybackState
import com.example.ui.theme.SpotifyBlack
import com.example.ui.theme.SpotifyCardGray
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.SpotifyMint
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceConnectSheet(
    devices: List<ConnectedDevice>,
    playbackState: PlaybackState,
    onSelectDevice: (ConnectedDevice) -> Unit,
    onVolumeChange: (ConnectedDevice, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showPairDialog by remember { mutableStateOf(false) }
    var losslessStreamingEnabled by remember { mutableStateOf(true) }

    val currentDevice = devices.firstOrNull { it.isCurrent } ?: devices.firstOrNull()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF1E1E1E),
        modifier = Modifier.testTag("device_connect_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Connect to a device",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Seamless cross-platform audio streaming",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(SpotifyGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(SpotifyGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SYNC READY",
                            color = SpotifyGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Currently Playing on Banner
            if (currentDevice != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    color = Color(0xFF282828)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(SpotifyGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getDeviceIcon(currentDevice.type),
                                    contentDescription = null,
                                    tint = SpotifyGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Currently listening on",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = currentDevice.name,
                                    color = SpotifyGreen,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = currentDevice.connectionProtocol,
                                    color = TextTertiary,
                                    fontSize = 10.sp
                                )
                            }

                            AudioEqualizerBars(
                                isPlaying = playbackState.isPlaying,
                                barCount = 4,
                                barWidth = 3.dp,
                                maxHeight = 16.dp,
                                barColor = SpotifyGreen
                            )
                        }

                        // Remote volume slider
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeDown,
                                contentDescription = "Volume down",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
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
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("device_volume_slider"),
                                colors = SliderDefaults.colors(
                                    thumbColor = SpotifyGreen,
                                    activeTrackColor = SpotifyGreen,
                                    inactiveTrackColor = Color(0xFF444444)
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Volume up",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Select another device to stream",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Devices list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(devices.filter { !it.isCurrent }) { device ->
                    DeviceRow(
                        device = device,
                        onClick = { onSelectDevice(device) }
                    )
                }
            }

            HorizontalDivider(color = Color(0xFF333333), modifier = Modifier.padding(vertical = 12.dp))

            // Cross-Platform Options
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = null,
                        tint = SpotifyMint,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Lossless Wi-Fi Handover",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "24-bit / 192kHz zero buffer transfer",
                            color = TextTertiary,
                            fontSize = 11.sp
                        )
                    }
                }

                Switch(
                    checked = losslessStreamingEnabled,
                    onCheckedChange = { losslessStreamingEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = SpotifyGreen,
                        checkedTrackColor = SpotifyGreen.copy(alpha = 0.4f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pair New Web / Desktop Device button
            Button(
                onClick = { showPairDialog = !showPairDialog },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pair_device_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E2E2E),
                    contentColor = TextPrimary
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode2,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (showPairDialog) "Hide Pairing Instructions" else "Pair Web, Mac, or Smart TV",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            AnimatedVisibility(visible = showPairDialog) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF262626)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Instant Cross-Platform Sync",
                            color = SpotifyGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "1. Open pulse.music in Chrome, Safari, or Pulse Desktop\n2. Log in with the same account or enter Sync PIN:\n",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF141414))
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "PULSE - 8 9 2 4",
                                color = SpotifyMint,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DeviceRow(
    device: ConnectedDevice,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .testTag("device_item_${device.id}"),
        color = Color(0xFF222222)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = getDeviceIcon(device.type),
                contentDescription = null,
                tint = if (device.isOnline) TextPrimary else TextTertiary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = device.name,
                    color = if (device.isOnline) TextPrimary else TextTertiary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (device.isOnline) device.connectionProtocol else "Offline",
                    color = if (device.isOnline) TextSecondary else TextTertiary,
                    fontSize = 11.sp
                )
            }

            if (device.isOnline) {
                Text(
                    text = "Tap to stream",
                    color = SpotifyGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
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
