package com.rere.server.inter.dto.mapper;

import com.rere.server.domain.model.account.AccountState;
import com.rere.server.domain.model.config.AuthUserGroup;
import com.rere.server.domain.model.replic.MediaMode;
import com.rere.server.domain.model.replic.ReplicState;
import com.rere.server.inter.dto.parameter.AccountSortParameter;
import com.rere.server.inter.dto.parameter.ReplicSortParameter;
import com.rere.server.inter.dto.parameter.ReportSortParameter;
import com.rere.server.inter.dto.parameter.SortDirectionParameter;

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
    private static final Map<AccountSortParameter, String> ACCOUNT_SORT_PARAMETER_MAP = Map.of(AccountSortParameter.STATUS, "status", AccountSortParameter.CREATION, "creation", AccountSortParameter.USERNAME, "username");
    private static final Map<ReplicSortParameter, String> REPLIC_SORT_PARAMETER_MAP = Map.of(ReplicSortParameter.DATE, "date", ReplicSortParameter.ORIGIN, "origin", ReplicSortParameter.SIZE, "size", ReplicSortParameter.EXPIRATION, "expiration");
    private static final Map<ReportSortParameter, String> REPORT_SORT_PARAMETER_MAP = Map.of(ReportSortParameter.DATE, "date", ReportSortParameter.USER, "user");
    private static final Map<SortDirectionParameter, String> SORT_DIRECTION_PARAMETER_MAP = Map.of(SortDirectionParameter.ASCENDING, "ascending", SortDirectionParameter.DESCENDING, "descending");

    private static final Map<Class<? extends Enum<?>>, Map<? extends Enum<?>, String>> MAPS
            = new HashMap<>();

    static {
        MAPS.put(AccountState.class, ACCOUNT_STATE_MAP);
        MAPS.put(ReplicState.class, REPLIC_STATE_MAP);
        MAPS.put(MediaMode.class, MEDIA_MODE_MAP);
        MAPS.put(AuthUserGroup.class, AUTH_USER_GROUP_MAP);
        MAPS.put(AccountSortParameter.class, ACCOUNT_SORT_PARAMETER_MAP);
        MAPS.put(ReplicSortParameter.class, REPLIC_SORT_PARAMETER_MAP);
        MAPS.put(ReportSortParameter.class, REPORT_SORT_PARAMETER_MAP);
        MAPS.put(SortDirectionParameter.class, SORT_DIRECTION_PARAMETER_MAP);
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

    /**
     * Maps a given string to an enum value.
     * @param value The string.
     * @param enumClass The enum class.
     * @return The enum value, or null.
     * @param <E> The enum type.
     */
    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> E mapToEnum(String value, Class<E> enumClass) {
        Map<E, String> map = (Map<E, String>) MAPS.get(enumClass);
        if (map == null) {
            throw new IllegalArgumentException("No stored enum-values found for class %s".formatted(enumClass));
        }

        return getKeyof(map, value);
    }

    /**
     * Gets all value for a given enum class.
     * @param enumClass The enum class.
     * @return All possible string value.
     */
    public static String[] getAll(Class<?> enumClass) {
        Map<?, String> map = MAPS.get(enumClass);
        return map.values().toArray(new String[0]);
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
