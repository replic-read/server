package com.rere.server.infrastructure.io;

import com.rere.server.domain.io.ReplicFileAccessor;
import com.rere.server.domain.model.exception.NotFoundException;
import com.rere.server.domain.model.exception.NotFoundSubject;
import com.rere.server.domain.model.impl.ReplicFileDataImpl;
import com.rere.server.domain.model.replic.ReplicFileData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Implementation of the file accessor.
 */
@Component
public class LocalReplicFileAccessor implements ReplicFileAccessor {

    private static final String FILE_NAME_FORMAT = "%s.html";

    /**
     * The path where the replic content files are stored.
     */
    private final Path rootDirectory;

    public LocalReplicFileAccessor(@Value("${rere.content.root}") String rootDirectory) {
        this.rootDirectory = Path.of(rootDirectory);
    }

    @Override
    public File createForReplic(UUID replicId) {
        String filename = FILE_NAME_FORMAT.formatted(replicId.toString());
        File file = new File(rootDirectory.toFile(), filename);
        try {
            boolean success = file.createNewFile();
            if (!success) {
                throw new IOException();
            }
        } catch (IOException e) {
            throw new IllegalStateException("File for replic could not be created.", e);
        }
        boolean success = file.setWritable(true);
        if (!success) {
            throw new IllegalStateException("File for replic could not be made writable.");
        }
        return file;
    }

    @Override
    public ReplicFileData getFileData(UUID replicId) throws NotFoundException {
        String filename = FILE_NAME_FORMAT.formatted(replicId.toString());
        File file = new File(rootDirectory.toFile(), filename);
        if (!file.exists()) {
            throw new NotFoundException(NotFoundSubject.REPLIC_FILE, replicId);
        } else {
            InputStream is;
            try {
                is = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new NotFoundException(NotFoundSubject.REPLIC_FILE, replicId);
            }
            return ReplicFileDataImpl.builder()
                    .size(file.length())
                    .contentStream(is)
                    .build();
        }
    }
}
