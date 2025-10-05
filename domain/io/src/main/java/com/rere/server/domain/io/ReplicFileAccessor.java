package com.rere.server.domain.io;

import com.rere.server.domain.model.exception.NotFoundException;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

/**
 * Allows for structured access to the files associated by the replics.
 */
public interface ReplicFileAccessor {

    /**
     * Creates a new file for a specified replic id.
     * @param replicId The id of the replic to create the file for.
     * @return The file created for the replic.
     */
    File createForReplic(UUID replicId);

    /**
     * Gets the size of the replic content-file in bytes.
     *
     * @param replicId The id of the replic.
     * @return The data size in bytes.
     * @throws NotFoundException If the file could not be found.
     */
    long getDataSize(UUID replicId) throws NotFoundException;

    /**
     * Gets the file stream for a specific replic id.
     *
     * @param replicId The id of the replic tog et the file stream.
     * @return The stream containing the file data.
     * @throws NotFoundException If the file could not be found.
     */
    InputStream getDataStream(UUID replicId) throws NotFoundException;

}
