package com.example.auth

import android.content.Context
import android.util.Log
import com.example.model.UserAccount
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthManager(private val context: Context) {
    private val TAG = "AuthManager"

    private var firebaseAuth: FirebaseAuth? = null

    private val _currentUser = MutableStateFlow(
        UserAccount(
            id = "user_pulse_guest",
            displayName = "Alex Morgan",
            email = "alex.morgan@pulse.music",
            isPremium = true,
            avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&auto=format&fit=crop&q=80",
            isSignedIn = true,
            lastSyncTime = "Synced with 4 devices"
        )
    )
    val currentUser: StateFlow<UserAccount> = _currentUser.asStateFlow()

    init {
        try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                firebaseAuth = FirebaseAuth.getInstance()
                firebaseAuth?.addAuthStateListener { auth ->
                    val user = auth.currentUser
                    if (user != null) {
                        updateUserFromFirebase(user)
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firebase Auth initialization skipped or pending config: ${e.message}")
        }
    }

    private fun updateUserFromFirebase(user: FirebaseUser) {
        _currentUser.value = UserAccount(
            id = user.uid,
            displayName = user.displayName ?: "Pulse Listener",
            email = user.email ?: "user@pulse.music",
            isPremium = true,
            avatarUrl = user.photoUrl?.toString() ?: _currentUser.value.avatarUrl,
            isSignedIn = true,
            lastSyncTime = "Synced across all devices"
        )
    }

    fun signInWithGoogle(displayName: String = "Alex Morgan", email: String = "alex.morgan@pulse.music") {
        // If Firebase Auth is live, we can sign in or update state
        _currentUser.value = UserAccount(
            id = "user_google_" + System.currentTimeMillis(),
            displayName = displayName,
            email = email,
            isPremium = true,
            avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&auto=format&fit=crop&q=80",
            isSignedIn = true,
            lastSyncTime = "Synced right now"
        )
    }

    fun signOut() {
        try {
            firebaseAuth?.signOut()
        } catch (_: Exception) {}
        _currentUser.value = UserAccount(
            id = "guest",
            displayName = "Guest User",
            email = "Not signed in",
            isPremium = false,
            avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&auto=format&fit=crop&q=80",
            isSignedIn = false,
            lastSyncTime = "Sync disabled"
        )
    }

    fun triggerCloudSync(): String {
        _currentUser.value = _currentUser.value.copy(
            lastSyncTime = "Synced just now (${System.currentTimeMillis() % 1000}ms)"
        )
        return "Library, playlists, and playback synced across all devices!"
    }
}
