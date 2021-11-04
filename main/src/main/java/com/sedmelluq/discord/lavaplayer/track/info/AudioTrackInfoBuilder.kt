package com.sedmelluq.discord.lavaplayer.track.info

import com.sedmelluq.discord.lavaplayer.tools.Units.DURATION_MS_UNKNOWN
import com.sedmelluq.discord.lavaplayer.tools.io.SeekableInputStream
import com.sedmelluq.discord.lavaplayer.track.AudioReference
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo

/**
 * Builder for [AudioTrackInfo].
 */
class AudioTrackInfoBuilder internal constructor() : AudioTrackInfoProvider {
    companion object {
        private const val UNKNOWN_TITLE = "Unknown title"
        private const val UNKNOWN_ARTIST = "Unknown artist"

        fun apply(build: AudioTrackInfoBuilder.() -> Unit): AudioTrackInfoBuilder {
            return AudioTrackInfoBuilder()
                .apply(build)
        }

        /**
         * Creates an instance of an audio track builder based on an audio reference and a stream.
         *
         * @param reference Audio reference to use as the starting point for the builder.
         * @param stream    Stream to get additional data from.
         * @return An instance of the builder with the reference and track info providers from the stream pre-applied.
         */
        @JvmStatic
        @JvmOverloads
        fun create(
            reference: AudioReference?,
            stream: SeekableInputStream?,
            build: AudioTrackInfoBuilder.() -> Unit = {}
        ) = apply {
            author = UNKNOWN_ARTIST
            title = UNKNOWN_TITLE
            length = DURATION_MS_UNKNOWN

            apply(reference)
            if (stream != null) {
                for (provider in stream.trackInfoProviders) {
                    apply(provider)
                }
            }

            apply(build)
        }

        /**
         * @return Empty instance of audio track builder.
         */
        @JvmStatic
        fun empty(): AudioTrackInfoBuilder {
            return AudioTrackInfoBuilder()
        }
    }

    override var title: String? = null
        set(value) { field = value ?: field }

    override var author: String? = null
        set(value) { field = value ?: field }

    override var length: Long? = null
        set(value) { field = value ?: field }

    override var identifier: String? = null
        set(value) { field = value ?: field }

    override var uri: String? = null
        set(value) { field = value ?: field }

    override var artworkUrl: String? = null
        set(value) { field = value ?: field }

    var isStream: Boolean? = null
        set(value) { field = value ?: field }

    fun setIsStream(stream: Boolean?): AudioTrackInfoBuilder {
        isStream = stream
        return this
    }

    /**
     * @param provider The track info provider to apply to the builder.
     * @return this
     */
    fun apply(provider: AudioTrackInfoProvider?): AudioTrackInfoBuilder {
        return if (provider == null) this else apply {
            title = provider.title
            author = provider.author
            length = provider.length
            identifier = provider.identifier
            uri = provider.uri
            artworkUrl = provider.artworkUrl
        }
    }


    /**
     * @return Audio track info instance.
     */
    fun build(): AudioTrackInfo {
        val length = length ?: DURATION_MS_UNKNOWN
        return AudioTrackInfo(
            title!!,
            author!!,
            length,
            identifier!!,
            uri,
            artworkUrl,
            isStream ?: (length == DURATION_MS_UNKNOWN)
        )
    }
}
