package com.rere.server.domain.io;

import com.rere.server.domain.model.exception.NotFoundException;
import com.rere.server.domain.model.replic.ReplicFileData;

import java.io.File;
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
     * Gets the file data for the replic.
     *
     * @param replicId The id of the replic.
     * @return The file data.
     * @throws NotFoundException If the file could not be found.
     */
    ReplicFileData getFileData(UUID replicId) throws NotFoundException;

}
