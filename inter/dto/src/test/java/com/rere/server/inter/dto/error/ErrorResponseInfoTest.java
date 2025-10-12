package com.rere.server.inter.dto.error;

import com.rere.server.domain.model.exception.InvalidExpirationException;
import com.rere.server.domain.model.exception.InvalidPasswordException;
import com.rere.server.domain.model.exception.InvalidTokenException;
import com.rere.server.domain.model.exception.NotFoundException;
import com.rere.server.domain.model.exception.NotUniqueException;
import com.rere.server.domain.model.exception.NotUniqueSubject;
import com.rere.server.domain.model.exception.OperationDisabledException;
import com.rere.server.domain.model.exception.OperationDisabledOperation;
import com.rere.server.domain.model.exception.ReplicQuotaMetException;
import com.rere.server.inter.dto.error.domain.MessageBasedInfo;
import com.rere.server.inter.dto.error.domain.NotUniqueInfo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

class ErrorResponseInfoTest {

    @Test
    void fromDomainExceptionWorksForNotUnique() {
        assertInstanceOf(NotUniqueInfo.class, ErrorResponseInfo.fromDomainException(new NotUniqueException(NotUniqueSubject.EMAIL)));
    }

    @Test
    void fromDomainExceptionWorksForInvalidExpiration() {
        assertInstanceOf(MessageBasedInfo.class, ErrorResponseInfo.fromDomainException(new InvalidExpirationException(false, Instant.now(), Instant.now())));
    }

    @Test
    void fromDomainExceptionWorksForInvalidToken() {
        assertInstanceOf(MessageBasedInfo.class, ErrorResponseInfo.fromDomainException(new InvalidTokenException()));
    }

    @Test
    void fromDomainExceptionWorksForReplicQuotaMet() {
        assertInstanceOf(MessageBasedInfo.class, ErrorResponseInfo.fromDomainException(new ReplicQuotaMetException(UUID.randomUUID())));
    }

    @Test
    void fromDomainExceptionWorksForOperationDisabled() {
        assertInstanceOf(MessageBasedInfo.class, ErrorResponseInfo.fromDomainException(new OperationDisabledException(OperationDisabledOperation.QUOTA_INFO)));
    }

    @Test
    void fromDomainExceptionWorksForOthers() {
        assertNull(ErrorResponseInfo.fromDomainException(NotFoundException.replic(UUID.randomUUID())));
        assertNull(ErrorResponseInfo.fromDomainException(new InvalidPasswordException()));
    }


}