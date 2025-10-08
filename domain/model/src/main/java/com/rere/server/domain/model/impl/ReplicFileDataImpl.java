package com.rere.server.domain.model.impl;

import com.rere.server.domain.model.replic.ReplicFileData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Data
@AllArgsConstructor
@Builder
public class ReplicFileDataImpl implements ReplicFileData {

    @Builder.Default
    private long size = 0;

    @Builder.Default
    private InputStream contentStream = new ByteArrayInputStream(new byte[0]);

}
