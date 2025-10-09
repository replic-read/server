package com.rere.server.infrastructure.io;

import com.rere.server.domain.io.ReplicFileAccessor;
import com.rere.server.domain.model.exception.NotFoundException;
import com.rere.server.domain.model.exception.NotFoundSubject;
import com.rere.server.domain.model.replic.ReplicFileData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Contains tests for the {@link LocalReplicFileAccessor} class.
 */
class LocalReplicFileAccessorTest {

    private static final String TEMP_PREFIX = "com.rere.server.test";

    private ReplicFileAccessor subject;

    private String tempDirectory;

    @BeforeEach
    void init() throws IOException {
        tempDirectory = Files.createTempDirectory(TEMP_PREFIX).toFile().getAbsolutePath();

        subject = new LocalReplicFileAccessor(tempDirectory);
    }

    @Test
    void createForReplicCreatesFile() {
        UUID replicId = UUID.randomUUID();

        subject.createForReplic(replicId);

        File actual = new File(Path.of(tempDirectory).toFile(), replicId + ".html");

        assertTrue(actual.exists());
        assertTrue(actual.isFile());
        assertTrue(Files.isWritable(actual.toPath()));
    }

    @Test
    void createForReplicThrowsWhenNotCreated() {
        UUID replicId = UUID.randomUUID();

        Path.of(tempDirectory).toFile().setWritable(false);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> subject.createForReplic(replicId));

        assertEquals("File for replic could not be created.", ex.getMessage());
    }

    @Test
    void getFileDataReturnsSizeAndContent() throws IOException, NotFoundException {
        byte[] content = new byte[1024];
        new Random().nextBytes(content);

        UUID id = UUID.randomUUID();

        File targetFile = new File(Path.of(tempDirectory).toFile(), id + ".html");
        targetFile.createNewFile();
        try (OutputStream os = new FileOutputStream(targetFile)) {
            os.write(content);
        }

        ReplicFileData fileData = subject.getFileData(id);

        assertEquals(1024, fileData.getSize());
        assertArrayEquals(content, fileData.getContentStream().readAllBytes());
    }

    @Test
    void getFileDataThrowsForNotFoundFile() {
        UUID id = UUID.randomUUID();

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> subject.getFileData(id));

        assertEquals(NotFoundSubject.REPLIC_FILE, ex.getSubject());
        assertEquals(id, ex.getIdentifier());
    }

}
