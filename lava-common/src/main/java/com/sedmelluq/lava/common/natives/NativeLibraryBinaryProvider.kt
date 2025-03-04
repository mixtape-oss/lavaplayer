package com.sedmelluq.lava.common.natives

import com.sedmelluq.lava.common.natives.architecture.SystemType
import java.io.InputStream

public interface NativeLibraryBinaryProvider {
    /**
     * @param systemType  Detected system type.
     * @param libraryName Name of the library to load.
     * @return Stream to the library binary. `null` causes failure.
     */
    public fun getLibraryStream(systemType: SystemType, libraryName: String): InputStream?
}
