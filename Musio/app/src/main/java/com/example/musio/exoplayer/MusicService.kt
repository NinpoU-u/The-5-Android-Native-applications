package com.example.musio.exoplayer

import android.app.PendingIntent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.example.musio.di.DependencyModule
import com.example.musio.exoplayer.callbacks.MusicPlayerNotificationListener
import com.example.musio.other.Constants.SERVICE_TAG
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

class MusicService : MediaBrowserServiceCompat() {

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var musicNotificationManager: MusicNotificationManager
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector

    var isForegroundService = false

    override fun onCreate() {
        super.onCreate()
        //intent to our activity
        val activityIntent = packageManager?.getLaunchIntentForPackage(packageName).let {
            PendingIntent.getActivity(this, 0, it, 0)
        }

        // token to get information about media session
        mediaSession = MediaSessionCompat(this, SERVICE_TAG).apply {
            setSessionActivity(activityIntent)
            isActive = true
        }

        sessionToken = mediaSession.sessionToken

        musicNotificationManager = MusicNotificationManager(
                this,
                mediaSession.sessionToken,
                MusicPlayerNotificationListener(this)
        ) {
            // lyambda
        }

        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlayer(DependencyModule.provideExoPlayer(this, DependencyModule.provideAudioAttributes()))
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        TODO("Not yet implemented")
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        TODO("Not yet implemented")
    }


    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}