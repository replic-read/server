package com.rere.server.inter.dto.request;

import com.rere.server.inter.dto.validation.FieldType;
import com.rere.server.inter.dto.validation.ValidationMetadata;

/**
 * Request body with information to reset the password of an account.
 */
public record ResetPasswordRequest(
        @ValidationMetadata(FieldType.ACCOUNT_PASSWORD) String password
) {

}
