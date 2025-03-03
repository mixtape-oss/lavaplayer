package com.sedmelluq.discord.lavaplayer.source.soundcloud

import com.sedmelluq.discord.lavaplayer.tools.DataFormatTools
import com.sedmelluq.discord.lavaplayer.tools.ExceptionTools
import com.sedmelluq.discord.lavaplayer.tools.TextRange
import com.sedmelluq.discord.lavaplayer.tools.io.HttpClientTools
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface
import com.sedmelluq.discord.lavaplayer.tools.json.JsonTools
import com.sedmelluq.lava.common.tools.exception.FriendlyException.Severity.SUSPICIOUS
import com.sedmelluq.lava.common.tools.exception.friendlyError
import mu.KotlinLogging
import org.apache.http.HttpStatus
import org.apache.http.client.methods.HttpGet
import org.apache.http.util.EntityUtils
import java.io.IOException

class DefaultSoundCloudHtmlDataLoader : SoundCloudHtmlDataLoader {
    companion object {
        private val log = KotlinLogging.logger { }
        private val JSON_RANGES: List<TextRange> = listOf(
            "window.__sc_hydration =" to ";</script>",
            "catch(e){}})}," to ");</script>",
            "){}})}," to ");</script>",
        )
    }


    @Throws(IOException::class)
    override fun load(httpInterface: HttpInterface, url: String): SoundCloudRootDataModel? {
        httpInterface.execute(HttpGet(url)).use { response ->
            if (response.statusLine.statusCode == HttpStatus.SC_NOT_FOUND) {
                return null
            }

            HttpClientTools.assertSuccessWithContent(response, "video page response")

            val html = EntityUtils.toString(response.entity, Charsets.UTF_8)
            val rootData = DataFormatTools.extractBetween(html, JSON_RANGES)
                ?: friendlyError(
                    "This url does not appear to be a playable track.",
                    SUSPICIOUS,
                    ExceptionTools.throwWithDebugInfo(log, "No track JSON found", "html", html, null)
                )

            return JsonTools.decode(rootData)
        }
    }
}
