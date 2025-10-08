package com.rere.server.domain.model.replic;

import java.io.InputStream;

/**
 * Models data about the file of a replic.
 */
public interface ReplicFileData {

    /**
     * The size of the file of the replic.
     */
    long getSize();

    /**
     * The content stream of the replic.
     */
    InputStream getContentStream();

}
