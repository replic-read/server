package com.rere.server.domain.model.replic;

/**
 * Different states a replic can be in.
 */
public enum ReplicState {

    /**
     * The replic is active and usable.
     */
    ACTIVE,

    /**
     * The replic was deactivated by the owner.
     */
    INACTIVE,

    /**
     * The replic was removed by an admin.
     */
    REMOVED

}
