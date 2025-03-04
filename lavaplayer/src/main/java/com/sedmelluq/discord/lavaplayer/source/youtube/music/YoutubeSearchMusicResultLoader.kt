package com.sedmelluq.discord.lavaplayer.source.youtube.music

import com.sedmelluq.discord.lavaplayer.tools.http.ExtendedHttpConfigurable
import com.sedmelluq.discord.lavaplayer.track.AudioItem
import com.sedmelluq.discord.lavaplayer.track.AudioTrackFactory

interface YoutubeSearchMusicResultLoader {
    val httpConfiguration: ExtendedHttpConfigurable

    fun loadSearchMusicResult(query: String, trackFactory: AudioTrackFactory): AudioItem
}
