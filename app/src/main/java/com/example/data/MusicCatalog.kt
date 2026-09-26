package com.example.data

import com.example.model.ConnectedDevice
import com.example.model.DeviceType
import com.example.model.LyricLine
import com.example.model.Playlist
import com.example.model.Track

object MusicCatalog {

    val sampleTracks = listOf(
        Track(
            id = "t1",
            title = "Midnight Neon Drive",
            artist = "Aura Synth",
            album = "Cyber Horizons",
            durationMs = 214000L,
            audioUrl = "https://commondatastorage.googleapis.com/codeskulptor-demos/DDR_assets/Kangaroo_MusiQue_-_The_Neverending_Story.mp3",
            coverUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=600&auto=format&fit=crop&q=80",
            genre = "Synthwave",
            isLiked = true,
            lyrics = listOf(
                LyricLine(0L, "Night city lights reflecting in the rain..."),
                LyricLine(12000L, "Cruising through the neon haze again"),
                LyricLine(24000L, "Distant echoes of an electronic beat"),
                LyricLine(36000L, "Empty shadows drifting down the street"),
                LyricLine(50000L, "We ride until the morning comes alive"),
                LyricLine(65000L, "Just you and me in midnight neon drive"),
                LyricLine(82000L, "Electric pulses racing through our veins"),
                LyricLine(98000L, "Leaving behind yesterday and all the pain"),
                LyricLine(120000L, "Feel the sound waves vibrating through the floor"),
                LyricLine(140000L, "Into the endless night forevermore")
            ),
            releaseYear = 2024
        ),
        Track(
            id = "t2",
            title = "Velvet Coffee Lofi",
            artist = "Moonlit Beats",
            album = "Café In The Rain",
            durationMs = 186000L,
            audioUrl = "https://commondatastorage.googleapis.com/codeskulptor-demos/riceracer_assets/music/race.ogg",
            coverUrl = "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?w=600&auto=format&fit=crop&q=80",
            genre = "Lo-Fi",
            isLiked = true,
            lyrics = listOf(
                LyricLine(0L, "Gentle warm drizzle tapping on the glass..."),
                LyricLine(15000L, "A cup of dark roast while the hours pass"),
                LyricLine(30000L, "Pages turning softly with vinyl crackle sound"),
                LyricLine(48000L, "Peace of mind is finally what I've found"),
                LyricLine(68000L, "Breathing slow as dusk turns into night"),
                LyricLine(90000L, "Bathed within the amber candle light"),
                LyricLine(120000L, "Let the rhythm soothe away your mind"),
                LyricLine(145000L, "All the noise and hurry left behind")
            ),
            releaseYear = 2024
        ),
        Track(
            id = "t3",
            title = "Solar Flare (Hyperdrive Mix)",
            artist = "Nova Kinetic",
            album = "Starlight Odyssey",
            durationMs = 238000L,
            audioUrl = "https://commondatastorage.googleapis.com/codeskulptor-assets/Epoq-Lepidoptera.ogg",
            coverUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80",
            genre = "Electronic",
            isLiked = false,
            lyrics = listOf(
                LyricLine(0L, "Ignition sequence starts at zero hour..."),
                LyricLine(16000L, "Warp speed surging with boundless power"),
                LyricLine(32000L, "Cosmic radiation dancing on the hull"),
                LyricLine(48000L, "Never let the supersonic cadence dull"),
                LyricLine(66000L, "Feel the gravity invert and spin"),
                LyricLine(88000L, "Where the interstellar realms begin"),
                LyricLine(115000L, "Solar flare burning across the sky"),
                LyricLine(135000L, "Watch the constellations drifting by")
            ),
            releaseYear = 2023
        ),
        Track(
            id = "t4",
            title = "Golden Hour Acoustic",
            artist = "Clara Vance",
            album = "Whispering Pines",
            durationMs = 202000L,
            audioUrl = "https://commondatastorage.googleapis.com/codeskulptor-demos/DDR_assets/Sevish_-__nbsp_.mp3",
            coverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
            genre = "Indie",
            isLiked = true,
            lyrics = listOf(
                LyricLine(0L, "Barefoot in the meadow when the sun goes down..."),
                LyricLine(14000L, "Miles away from the rushing crowd in town"),
                LyricLine(28000L, "Six steel strings singing out a song"),
                LyricLine(42000L, "Telling me this is where I belong"),
                LyricLine(60000L, "Warm summer breeze through the willow trees"),
                LyricLine(80000L, "Bringing my weary spirit to ease"),
                LyricLine(105000L, "Golden hour light painting every leaf"),
                LyricLine(130000L, "In simple moments we find relief")
            ),
            releaseYear = 2024
        ),
        Track(
            id = "t5",
            title = "Deep Ocean Meditation",
            artist = "Zenith Wave",
            album = "Submerged Calm",
            durationMs = 265000L,
            audioUrl = "https://commondatastorage.googleapis.com/codeskulptor-demos/riceracer_assets/music/menu.ogg",
            coverUrl = "https://images.unsplash.com/photo-1498038432885-c6f3f1b912ee?w=600&auto=format&fit=crop&q=80",
            genre = "Ambient",
            isLiked = false,
            lyrics = listOf(
                LyricLine(0L, "[Instrumental ambient soundscapes]"),
                LyricLine(30000L, "Diving deeper into the blue..."),
                LyricLine(60000L, "Weightless floating stillness"),
                LyricLine(90000L, "Inhale tranquility, exhale tension"),
                LyricLine(150000L, "Resonating in harmony with the ocean depth")
            ),
            releaseYear = 2023
        ),
        Track(
            id = "t6",
            title = "Retrograde Arcade",
            artist = "8-Bit Dreamer",
            album = "Pixelated Memories",
            durationMs = 175000L,
            audioUrl = "https://commondatastorage.googleapis.com/codeskulptor-demos/DDR_assets/Kangaroo_MusiQue_-_The_Neverending_Story.mp3",
            coverUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=600&auto=format&fit=crop&q=80",
            genre = "Chiptune",
            isLiked = false,
            lyrics = listOf(
                LyricLine(0L, "Coin inserted, player one ready..."),
                LyricLine(12000L, "Hold the joystick firm and steady"),
                LyricLine(26000L, "HighScore flashing on the glowing screen"),
                LyricLine(42000L, "Best adventure you have ever seen"),
                LyricLine(60000L, "Level up, jump the fire pit"),
                LyricLine(80000L, "Pixel power, this is definitely it!")
            ),
            releaseYear = 2024
        ),
        Track(
            id = "t7",
            title = "Tokyo Midnight Drift",
            artist = "Kenshi Mirage",
            album = "Shinjuku After Dark",
            durationMs = 228000L,
            audioUrl = "https://commondatastorage.googleapis.com/codeskulptor-demos/riceracer_assets/music/race.ogg",
            coverUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=600&auto=format&fit=crop&q=80",
            genre = "Electronic",
            isLiked = true,
            lyrics = listOf(
                LyricLine(0L, "Neon kanji signs glowing in the fog..."),
                LyricLine(18000L, "Tires grip the asphalt, engine hums"),
                LyricLine(35000L, "Bass drop echoes like ritual drums"),
                LyricLine(55000L, "Underground tunnels flashing white and red"),
                LyricLine(80000L, "Chasing thoughts that cannot be said"),
                LyricLine(110000L, "Speed through Shibuya as the rain falls fast"),
                LyricLine(140000L, "Make this electric moment last")
            ),
            releaseYear = 2024
        ),
        Track(
            id = "t8",
            title = "Eclipse of the Heart",
            artist = "The Lunar Sound",
            album = "Phases",
            durationMs = 242000L,
            audioUrl = "https://commondatastorage.googleapis.com/codeskulptor-assets/Epoq-Lepidoptera.ogg",
            coverUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600&auto=format&fit=crop&q=80",
            genre = "Indie",
            isLiked = false,
            lyrics = listOf(
                LyricLine(0L, "When the shadows cover the moon..."),
                LyricLine(16000L, "We hoped to find our harmony soon"),
                LyricLine(34000L, "Two orbits crossing in the velvet dark"),
                LyricLine(52000L, "Leaving behind a single glowing spark"),
                LyricLine(75000L, "Eclipse of hearts that wander free"),
                LyricLine(100000L, "Lost in this starry symphony")
            ),
            releaseYear = 2023
        )
    )

    val samplePlaylists = listOf(
        Playlist(
            id = "p_top_hits",
            title = "Today's Top Hits",
            description = "The hottest music streaming worldwide right now.",
            coverUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600&auto=format&fit=crop&q=80",
            gradientStartHex = 0xFF1DB954,
            gradientEndHex = 0xFF121212
        ),
        Playlist(
            id = "p_synth",
            title = "Late Night Synthwave",
            description = "Neon aesthetics, analogue synthesizers, and retro futuristic vibes.",
            coverUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=600&auto=format&fit=crop&q=80",
            gradientStartHex = 0xFF9333EA,
            gradientEndHex = 0xFF0F172A
        ),
        Playlist(
            id = "p_lofi",
            title = "Deep Focus & Study",
            description = "Calm lo-fi beats, gentle crackle, and soothing background chords.",
            coverUrl = "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?w=600&auto=format&fit=crop&q=80",
            gradientStartHex = 0xFFD97706,
            gradientEndHex = 0xFF1E1B4B
        ),
        Playlist(
            id = "p_electronic",
            title = "Electronic Pulse 2024",
            description = "High energy drops, club bangers, and futuristic basslines.",
            coverUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80",
            gradientStartHex = 0xFF06B6D4,
            gradientEndHex = 0xFF022C22
        ),
        Playlist(
            id = "p_acoustic",
            title = "Acoustic Afternoon",
            description = "Unplugged intimate guitars, warm vocals, and indie warmth.",
            coverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
            gradientStartHex = 0xFFEA580C,
            gradientEndHex = 0xFF27272A
        )
    )

    val sampleDevices = listOf(
        ConnectedDevice(
            id = "dev_phone",
            name = "Pixel 8 Pro (This Phone)",
            type = DeviceType.SMARTPHONE,
            isCurrent = true,
            isOnline = true,
            volumePercent = 80,
            connectionProtocol = "Local Device (Active Audio)",
            ipAddress = "192.168.1.102"
        ),
        ConnectedDevice(
            id = "dev_mac",
            name = "MacBook Pro 16\" (Desktop)",
            type = DeviceType.DESKTOP,
            isCurrent = false,
            isOnline = true,
            volumePercent = 65,
            connectionProtocol = "Pulse Connect Desktop App",
            ipAddress = "192.168.1.105"
        ),
        ConnectedDevice(
            id = "dev_tv",
            name = "LG OLED 65\" Living Room TV",
            type = DeviceType.SMART_TV,
            isCurrent = false,
            isOnline = true,
            volumePercent = 50,
            connectionProtocol = "Pulse Cast (WebOS)",
            ipAddress = "192.168.1.120"
        ),
        ConnectedDevice(
            id = "dev_speaker",
            name = "Sonos Era 300 (Studio)",
            type = DeviceType.SMART_SPEAKER,
            isCurrent = false,
            isOnline = true,
            volumePercent = 70,
            connectionProtocol = "AirPlay 2 / Spotify Connect",
            ipAddress = "192.168.1.135"
        ),
        ConnectedDevice(
            id = "dev_tablet",
            name = "iPad Pro 12.9\"",
            type = DeviceType.TABLET,
            isCurrent = false,
            isOnline = true,
            volumePercent = 85,
            connectionProtocol = "Pulse Connect iOS App",
            ipAddress = "192.168.1.111"
        ),
        ConnectedDevice(
            id = "dev_car",
            name = "Tesla Model 3 Dashboard",
            type = DeviceType.CAR,
            isCurrent = false,
            isOnline = false,
            volumePercent = 60,
            connectionProtocol = "In-Car LTE Streaming",
            ipAddress = "10.0.4.15"
        )
    )
}
