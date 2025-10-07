package com.rere.server.domain.service;

import org.springframework.lang.NonNull;

import java.io.File;

/**
 * Callback to write in a file.
 */
public interface FileWriterCallback {

    /**
     * Writes into the specified file.
     * @param file The file to write to.
     * @return Whether writing was successful.
     */
    boolean write(@NonNull File file);

}
