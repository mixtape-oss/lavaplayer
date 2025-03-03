package com.sedmelluq.discord.lavaplayer.track.loader

import com.sedmelluq.discord.lavaplayer.source.ProbingItemSourceManager
import com.sedmelluq.discord.lavaplayer.tools.extensions.friendlyError
import com.sedmelluq.discord.lavaplayer.track.AudioItem
import com.sedmelluq.discord.lavaplayer.track.AudioReference
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.collection.AudioTrackCollection
import com.sedmelluq.discord.lavaplayer.track.loader.message.DefaultItemLoaderMessages
import com.sedmelluq.lava.common.tools.exception.FriendlyException
import com.sedmelluq.lava.common.tools.exception.wrapUnfriendlyException
import kotlinx.coroutines.async
import kotlinx.coroutines.invoke
import mu.KotlinLogging

open class DefaultItemLoader(
    reference: AudioReference,
    private val factory: DefaultItemLoaderFactory,
    override var resultHandler: ItemLoadResultHandler? = null,
) : ItemLoader {
    companion object {
        private const val MAXIMUM_LOAD_REDIRECTS = 5
        private val log = KotlinLogging.logger { }
    }

    override val state = LoaderState(reference, DefaultItemLoaderMessages())

    override suspend fun load(): ItemLoadResult = factory.dispatcher {
        val result = try {
            loadReference()
        } catch (t: Throwable) {
            val exception = t.wrapUnfriendlyException("Something went wrong when looking up the track", FriendlyException.Severity.SUSPICIOUS)
            log.friendlyError(exception) { "loading item '${state.reference.identifier}'" }

            ItemLoadResult.LoadFailed(exception)
        }

        state.messages.shutdown()
        if (result is ItemLoadResult.NoMatches) {
            log.debug { "No matches found for identifier '${state.reference.identifier}'" }
        }

        resultHandler?.handle(result)
        result
    }

    override fun loadAsync() = factory.async { load() }

    private suspend fun loadReference(): ItemLoadResult {
        var currentReference = state.reference
        for (redirect in 0 until MAXIMUM_LOAD_REDIRECTS) {
            if (currentReference.identifier == null) {
                break
            }

            val item = loadReference(currentReference)
                ?: return ItemLoadResult.NoMatches

            when (item) {
                is AudioTrack -> return ItemLoadResult.TrackLoaded(item)
                is AudioTrackCollection -> return ItemLoadResult.CollectionLoaded(item)
                is AudioReference -> currentReference = item
            }
        }

        return ItemLoadResult.NoMatches
    }

    private suspend fun loadReference(reference: AudioReference): AudioItem? {
        for (sourceManager in factory.sourceRegistry.sourceManagers) {
            if (reference.containerDescriptor != null && sourceManager !is ProbingItemSourceManager) {
                continue
            }

            val item = sourceManager.loadItem(state, reference)
                ?: continue

            if (item !is AudioReference) {
                val name = "audio track${" collection".takeIf { item is AudioTrackCollection } ?: ""}"
                log.debug { "Loaded an $name with identifier '${reference.uri}' using ${sourceManager::class.qualifiedName}." }
            }

            return item
        }

        return null
    }
}
