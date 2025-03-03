package com.sedmelluq.discord.lavaplayer.track.encoder

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.lava.common.tools.io.MessageInput
import com.sedmelluq.lava.common.tools.io.MessageOutput
import java.io.IOException

/**
 * Handles encoding/decoding of audio tracks.
 */
interface TrackEncoder {
    companion object {
        const val TRACK_INFO_VERSION = 2
        const val TRACK_INFO_VERSIONED = 1
    }

    @Throws(IOException::class)
    fun encodeTrack(stream: MessageOutput, track: AudioTrack) =
        encodeTrack(stream, track, TRACK_INFO_VERSION)

    /**
     * Encode a track into an output stream. If the decoder is not supposed to know the number of tracks in advance, then
     * the encoder should call MessageOutput#finish() after all the tracks it wanted to write have been written. This will
     * make decodeTrack() return null at that position
     *
     * @param stream The message stream to write it to.
     * @param track  The track to encode.
     * @param version The version to encode.
     *
     * @throws IOException On IO error.
     */
    @Throws(IOException::class)
    fun encodeTrack(
        stream: MessageOutput,
        track: AudioTrack,
        version: Int
    )

    /**
     * Decode a track from an input stream. Null returns value indicates reaching the position where the decoder had
     * called MessageOutput#finish().
     *
     * @param stream The message stream to read it from.
     *
     * @return Holder containing the track if it was successfully decoded.
     * @throws IOException On IO error.
     */
    @Throws(IOException::class)
    fun decodeTrack(stream: MessageInput): DecodedTrackHolder?
}
