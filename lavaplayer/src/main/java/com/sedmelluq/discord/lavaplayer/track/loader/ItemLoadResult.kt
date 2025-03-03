package com.sedmelluq.discord.lavaplayer.track.loader

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.collection.AudioTrackCollection
import com.sedmelluq.lava.common.tools.exception.FriendlyException

interface ItemLoadResult {
    /**
     * Whether this item load result was a failure.
     */
    val isFailure: Boolean

    /**
     * There were no items found by the specified identifier.
     */
    object NoMatches : ItemLoadResult {
        override val isFailure: Boolean = true
    }

    /**
     * The requested item had failed to load.
     */
    data class LoadFailed(val exception: FriendlyException) : ItemLoadResult {
        override val isFailure: Boolean = true
    }

    /**
     * The requested item was a successfully loaded audio track.
     */
    data class TrackLoaded(val track: AudioTrack) : ItemLoadResult {
        override val isFailure: Boolean = false
    }

    /**
     * The requested item was a collection of tracks.
     */
    data class CollectionLoaded(val collection: AudioTrackCollection) : ItemLoadResult {
        override val isFailure: Boolean = false
    }
}
