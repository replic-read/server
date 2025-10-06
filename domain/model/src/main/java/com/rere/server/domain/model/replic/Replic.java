package com.rere.server.domain.model.replic;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Models a replic.
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@Setter
public class Replic extends BaseReplic {

    /**
     * The size of the content-file associated with this replic in bytes.
     */
    private final long size;

    /**
     * Creates a new replic with a given {@link BaseReplic}.
     * @param base The base replic.
     * @param size The size of the content-file in bytes.
     */
    public Replic(
            BaseReplic base,
            long size) {
        super(base.getId(), base.getCreationTimestamp(), base.getOriginalUrl(),
                base.getMediaMode(), base.getState(), base.getDescription(), base.getExpirationTimestamp(),
                base.getPasswordHash(), base.getOwner());
        this.size = size;
    }

}

