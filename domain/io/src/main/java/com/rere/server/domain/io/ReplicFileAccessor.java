package com.rere.server.domain.io;

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



}
