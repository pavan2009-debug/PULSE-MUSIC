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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PlaybackState
import com.example.model.Track
import com.example.ui.theme.SpotifyBlack
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

data class GenreTile(
    val name: String,
    val color1: Color,
    val color2: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    selectedGenre: String?,
    onGenreSelect: (String?) -> Unit,
    searchResults: List<Track>,
    playbackState: PlaybackState,
    onTrackSelect: (Track) -> Unit,
    onToggleLike: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    val genreTiles = listOf(
        GenreTile("Pop", Color(0xFFE91E63), Color(0xFF880E4F)),
        GenreTile("Synthwave", Color(0xFF9C27B0), Color(0xFF4A148C)),
        GenreTile("Lo-Fi", Color(0xFFFF9800), Color(0xFFE65100)),
        GenreTile("Electronic", Color(0xFF00BCD4), Color(0xFF006064)),
        GenreTile("Indie", Color(0xFF4CAF50), Color(0xFF1B5E20)),
        GenreTile("Ambient", Color(0xFF3F51B5), Color(0xFF1A237E)),
        GenreTile("Chiptune", Color(0xFFF44336), Color(0xFFB71C1C)),
        GenreTile("Rock", Color(0xFF795548), Color(0xFF3E2723))
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SpotifyBlack)
            .padding(horizontal = 16.dp)
            .testTag("search_screen")
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Search",
            color = TextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar with test tag
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            placeholder = { Text("What do you want to listen to?", color = TextTertiary, fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.Black
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = Color.Black
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = SpotifyGreen,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Genre filter pills
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                Surface(
                    onClick = { onGenreSelect(null) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (selectedGenre == null) SpotifyGreen else Color(0xFF282828)
                ) {
                    Text(
                        text = "All Genres",
                        color = if (selectedGenre == null) Color.Black else TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            items(genreTiles) { tile ->
                val isSelected = selectedGenre == tile.name
                Surface(
                    onClick = { onGenreSelect(tile.name) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) SpotifyGreen else Color(0xFF282828),
                    modifier = Modifier.testTag("genre_chip_${tile.name}")
                ) {
                    Text(
                        text = tile.name,
                        color = if (isSelected) Color.Black else TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (searchQuery.isNotBlank() || selectedGenre != null) {
            // Search Results List
            Text(
                text = "${searchResults.size} results found",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (searchResults.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tracks found matching \"$searchQuery\"",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    itemsIndexed(searchResults) { index, track ->
                        val isCurrent = playbackState.currentTrack?.id == track.id
                        TrackListRow(
                            track = track,
                            rank = index + 1,
                            isCurrent = isCurrent,
                            isPlaying = isCurrent && playbackState.isPlaying,
                            onSelect = { onTrackSelect(track) },
                            onToggleLike = { onToggleLike(track) }
                        )
                    }
                }
            }
        } else {
            // Browse All Genres Grid
            Text(
                text = "Browse all",
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                items(genreTiles) { tile ->
                    Box(
                        modifier = Modifier
                            .height(96.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(tile.color1, tile.color2)
                                )
                            )
                            .clickable { onGenreSelect(tile.name) }
                            .padding(12.dp)
                    ) {
                        Text(
                            text = tile.name,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
