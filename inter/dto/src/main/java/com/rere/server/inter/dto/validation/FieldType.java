package com.rere.server.inter.dto.validation;

import com.rere.server.domain.model.account.AccountState;
import com.rere.server.domain.model.config.AuthUserGroup;
import com.rere.server.domain.model.replic.MediaMode;
import com.rere.server.domain.model.replic.ReplicState;
import com.rere.server.domain.model.report.ReportState;
import com.rere.server.inter.dto.parameter.AccountSortParameter;
import com.rere.server.inter.dto.parameter.ReplicSortParameter;
import com.rere.server.inter.dto.parameter.ReportSortParameter;
import com.rere.server.inter.dto.parameter.SortDirectionParameter;
import lombok.Getter;

import java.util.function.Supplier;

import static com.rere.server.inter.dto.mapper.EnumMapper.getAll;

/**
 * Defines different semantic types a parameter (i.e. query parameter of path variable) or field (i.e. field of a request/response body) can have.
 * Used for documentation-generation as well as automatic validation.
 */
@Getter
public enum FieldType {

    /**
     * Placeholder for no field type being present.
     */
    NONE(),

    REPLIC_ID("uuid", ValidationPatterns.UUID, "The id of the replic."),
    REPORT_ID("uuid", ValidationPatterns.UUID, "The id of the report."),
    ACCOUNT_ID("uuid", ValidationPatterns.UUID, "The id of the account."),

    ACCOUNT_USERNAME("username", ValidationPatterns.USERNAME, "The username of the user."),
    ACCOUNT_EMAIL("email", ValidationPatterns.EMAIL, "The email-address of the user."),
    ACCOUNT_PROFILE_COLOR("rgb-int", "The color of the users profile.", SpecificFormat.POSITIVE_INTEGER),
    ACCOUNT_PASSWORD("password", ValidationPatterns.PASSWORD, "The password of the account."),
    ACCOUNT_ACCOUNT("The account of the user."),

    EMAIL_TOKEN("uuid", ValidationPatterns.UUID, "The email-verification token that was sent per e-mail."),
    REFRESH_TOKEN("uuid", ValidationPatterns.UUID, "The refresh token of the user."),
    ACCESS_TOKEN("jwt", "The access token of the user."),

    ACCOUNT_STATE("The current state of the account.", () -> getAll(AccountState.class)),

    REPLIC_DESCRIPTION("The description of the replic."),
    REPLIC_ORIGINAL_URL("url", "The URL of the site the replic is replicating.", SpecificFormat.URL),
    REPLIC_HOST_URL("url", "The URL of the replicated content.", SpecificFormat.URL),
    REPLIC_PASSWORD("password", ValidationPatterns.PASSWORD, "The password of the replic."),
    REPLIC_MEDIA_MODE("The media-mode of the replic.", () -> getAll(MediaMode.class)),
    REPLIC_EXPIRATION("iso-8601 timestamp", ValidationPatterns.INSTANT, "The expiration timestamp of the replic."),
    REPLIC_STATE("The state of the replic.", () -> getAll(ReplicState.class)),
    REPLIC_SIZE("The size in bytes of the replic."),
    REPLIC_AUTHOR_ID("uuid", ValidationPatterns.UUID, "The id of the author of the replic."),
    REPLIC_PASSWORD_PRESENT("Whether the replic is secured via a password."),

    CONFIG_CREATE_REPLIC_GROUP("What users can create replics.", () -> getAll(AuthUserGroup.class)),
    CONFIG_ACCESS_REPLIC_GROUP("What users can access replics.", () -> getAll(AuthUserGroup.class)),
    CONFIG_CREATE_REPORT_GROUP("What users can report replics.", () -> getAll(AuthUserGroup.class)),
    CONFIG_MAX_EXP("iso-8601 period", "The maximum duration that a replic can be available for.", SpecificFormat.JAVA_PERIOD),
    CONFIG_LIMIT_PERIOD("iso-8601 period", "The period for the replic-limit.", SpecificFormat.JAVA_PERIOD),
    CONFIG_LIMIT_COUNT("positive integer", "The amount of replics allowed to be created per user in the limit-period.", SpecificFormat.POSITIVE_INTEGER),
    CONFIG_LIMIT_START("iso-8601 instant", ValidationPatterns.INSTANT, "The timestamp at which the limit period has been started."),
    CONFIG_ALLOW_SIGNUP("Whether accounts can be created"),

    REPORT_REPLIC_ID("uuid", ValidationPatterns.UUID, "The id of the replic the report references."),
    REPORT_ACCOUNT_ID("uuid", ValidationPatterns.UUID, "The id of the account that created the report."),
    REPORT_DESCRIPTION("The description of the report."),
    REPORT_STATE("The description of the report.", () -> getAll(ReportState.class)),

    CREATED_TIMESTAMP("iso-8601 timestamp", ValidationPatterns.INSTANT, "The timestamp when the object was created."),

    SEND_VERIFICATION_EMAIL("Whether to send an email-verification link immediately."),
    IS_DIRECTLY_VERIFIED("Whether the freshly created account directly has its email verified."),
    HTML_EMAIL("Whether to send a nicely-formatted html email, or a plain text email with a token."),

    REPLIC_SORT("By what attribute the replics should be sorted.", () -> getAll(ReplicSortParameter.class)),
    ACCOUNT_SORT("By what attribute the accounts should be sorted.", () -> getAll(AccountSortParameter.class)),
    REPORT_SORT("By what attribute the reports should be sorted.", () -> getAll(ReportSortParameter.class)),
    SORT_DIRECTION("The direction of the sorting.", () -> getAll(SortDirectionParameter.class)),

    REPLIC_FILTER_USER("uuid", ValidationPatterns.UUID, "The id of the author that the replics will be filtered for."),
    REPLIC_FILTER_STATE("The replic states that will be filtered for.", () -> getAll(ReplicState.class)),
    ACCOUNT_FILTER_STATE("The account states that will be filtered for.", () -> getAll(AccountState.class)),
    FILTER_QUERY("The search query. Only objects containing the query will be returned."),

    QUOTA_PROGRESS("The number of replics created in the current period.");

    /**
     * Textual description of the expected format of the field.
     */
    private final String format;

    /**
     * Description of the parameter. Will be used for documentation.
     */
    private final String description;

    /**
     * Regex that describes allowed values. Will be used for validation.
     */
    private final String pattern;

    /**
     * The allowed values. Will be used for validation.
     * <br>
     * Supplier to allow non-constant values.
     */
    private final Supplier<String[]> values;

    /**
     * A specific format that can properly match more complex formats, i.e. URL#s.
     */
    private final SpecificFormat specificFormat;


    /**
     * Creates a new FieldType.
     */
    FieldType(String format, String description, String pattern, Supplier<String[]> values, SpecificFormat specificFormat) {
        this.format = format;
        this.description = description;
        this.pattern = pattern;
        this.values = values;
        this.specificFormat = specificFormat;
    }

    /**
     * Creates a new FieldType with a given format, pattern and description.
     */
    FieldType(String format, String pattern, String description) {
        this(format, description, pattern, null, null);
    }

    /**
     * Creates a FieldType from a format and description.
     * @param format The format.
     * @param description The description.
     */
    FieldType(String format, String description) {
        this(format, description, null, null, null);
    }

    /**
     * Creates a FieldType from a format, description and specific format.
     * @param format The format.
     * @param description The description.
     * @param specificFormat The speciifc format.
     */
    FieldType(String format, String description, SpecificFormat specificFormat) {
        this(format, description, null, null, specificFormat);
    }

    /**
     * Creates a FieldType from a description and values.
     * @param description The description.
     * @param values The allowed values.
     */
    FieldType(String description, Supplier<String[]> values) {
        this(null, description, null, values, null);
    }

    /**
     * Creates a FieldType from a description.
     * @param description The description.
     */
    FieldType(String description) {
        this(null, description, null, null, null);
    }

    /**
     * Creates a FieldType with no attributes set.
     */
    FieldType() {
        this(null, null, null, null, null);
    }
}
