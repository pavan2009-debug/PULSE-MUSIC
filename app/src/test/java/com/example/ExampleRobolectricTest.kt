package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.MusicCatalog
import com.example.viewmodel.AudioPlayerViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Pulse", appName)
  }

  @Test
  fun `catalog has tracks and playlists`() {
    assertTrue(MusicCatalog.sampleTracks.isNotEmpty())
    assertTrue(MusicCatalog.samplePlaylists.isNotEmpty())
    assertTrue(MusicCatalog.sampleDevices.isNotEmpty())
  }

  @Test
  fun `audio player viewmodel supports play pause skip and progress tracking`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = AudioPlayerViewModel(app)

    assertNotNull(viewModel.currentTrack.value)
    assertEquals(MusicCatalog.sampleTracks.first().id, viewModel.currentTrack.value?.id)

    // Toggle play
    viewModel.togglePlayPause()
    assertTrue(viewModel.isPlaying.value)

    // Skip next
    val firstTrackId = viewModel.currentTrack.value?.id
    viewModel.skipNext()
    val nextTrackId = viewModel.currentTrack.value?.id
    assertTrue(firstTrackId != nextTrackId)

    // Progress seeking
    viewModel.seekTo(15000L)
    assertEquals(15000L, viewModel.currentPositionMs.value)

    // Toggle pause
    viewModel.pause()
    assertEquals(false, viewModel.isPlaying.value)
  }
}
