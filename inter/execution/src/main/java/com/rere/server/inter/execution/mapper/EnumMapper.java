package com.rere.server.inter.execution.mapper;

import com.rere.server.domain.model.account.AccountState;
import com.rere.server.domain.model.config.AuthUserGroup;
import com.rere.server.domain.model.replic.MediaMode;
import com.rere.server.domain.model.replic.ReplicState;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains methods to map enums to strings for the REST-API.
 */
public final class EnumMapper {

    private static final Map<AccountState, String> ACCOUNT_STATE_MAP = Map.of(AccountState.ACTIVE, "active", AccountState.INACTIVE, "inactive", AccountState.UNVERIFIED, "unverified");
    private static final Map<ReplicState, String> REPLIC_STATE_MAP = Map.of(ReplicState.ACTIVE, "active", ReplicState.INACTIVE, "inactive", ReplicState.REMOVED, "removed");
    private static final Map<MediaMode, String> MEDIA_MODE_MAP = Map.of(MediaMode.ALL, "all", MediaMode.NONE, "none", MediaMode.IMAGES, "images");
    private static final Map<AuthUserGroup, String> AUTH_USER_GROUP_MAP = Map.of(AuthUserGroup.ALL, "all", AuthUserGroup.ACCOUNT, "account", AuthUserGroup.VERIFIED, "verified");

    private static final Map<Class<? extends Enum<?>>, Map<? extends Enum<?>, String>> MAPS
            = new HashMap<>();

    static {
        MAPS.put(AccountState.class, ACCOUNT_STATE_MAP);
        MAPS.put(ReplicState.class, REPLIC_STATE_MAP);
        MAPS.put(MediaMode.class, MEDIA_MODE_MAP);
        MAPS.put(AuthUserGroup.class, AUTH_USER_GROUP_MAP);
    }

    private EnumMapper() {
    }

    /**
     * Maps a given value of an enum to a string.
     * @param value The value to map to a string.
     * @return The string value.
     * @param <E> The enum type.
     */
    public static <E extends Enum<E>> String mapToString(E value) {
        Map<? extends Enum<?>, String> map = MAPS.get(value.getClass());
        if (map == null) {
            throw new IllegalArgumentException("No stored string-values found for class %s".formatted(value.getClass()));
        }

        return map.get(value);
    }

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> E mapToEnum(String value, Class<E> enumClass) {
        Map<E, String> map = (Map<E, String>) MAPS.get(enumClass);
        if (map == null) {
            throw new IllegalArgumentException("No stored enum-values found for class %s".formatted(enumClass));
        }

        return getKeyof(map, value);
    }

    private static <K, V> K getKeyof(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }

        return null;
    }

}
