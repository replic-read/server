package com.rere.server.inter.dto.mapper;

import com.rere.server.domain.model.account.AccountState;
import net.bytebuddy.description.modifier.Visibility;
import org.junit.jupiter.api.Test;

import static com.rere.server.inter.dto.mapper.EnumMapper.mapToEnum;
import static com.rere.server.inter.dto.mapper.EnumMapper.mapToString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Contains tests for the {@link EnumMapper} class.
 */
class EnumMapperTest {

    @Test
    void mapToStringWorks() {
        assertEquals("active", mapToString(AccountState.ACTIVE));
        assertEquals("inactive", mapToString(AccountState.INACTIVE));
        assertEquals("unverified", mapToString(AccountState.UNVERIFIED));
    }

    @Test
    void mapToEnumWorks() {
        assertEquals(AccountState.ACTIVE, mapToEnum("active", AccountState.class));
        assertEquals(AccountState.INACTIVE, mapToEnum("inactive", AccountState.class));
        assertEquals(AccountState.UNVERIFIED, mapToEnum("unverified", AccountState.class));
    }

    @Test
    void mapToEnumReturnsNulLForInvalidString() {
        assertNull(mapToEnum("blahh", AccountState.class));
    }

    @Test
    void mapToEnumThrowsForInvalidEnum() {
        assertThrows(IllegalArgumentException.class,
                () -> mapToEnum("private", Visibility.class));
    }

    @Test
    void mapToStringThrowsForInvalidEnum() {
        assertThrows(IllegalArgumentException.class,
                () -> mapToString(Visibility.PRIVATE));
    }

}
