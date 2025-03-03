package com.sedmelluq.discord.lavaplayer.tools.io

import java.io.IOException
import java.nio.ByteBuffer

/**
 * Helper for reading a specific number of bits at a time from a byte buffer.
 * @param buffer Byte buffer to read bytes from
 */
class BitBufferReader(private val buffer: ByteBuffer) : BitStreamReader(null) {
    override fun asLong(bitsNeeded: Int): Long {
        try {
            return super.asLong(bitsNeeded)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    override fun asInteger(bitsNeeded: Int): Int {
        try {
            return super.asInteger(bitsNeeded)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    override fun readByte(): Int = buffer.get().toInt() and 0xFF
}
