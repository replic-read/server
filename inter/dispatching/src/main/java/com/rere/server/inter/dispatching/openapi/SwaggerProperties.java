package com.rere.server.inter.dispatching.openapi;

import com.rere.server.inter.dto.validation.FieldType;
import com.rere.server.inter.dto.validation.ValidationMetadata;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.oas.models.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.PropertyCustomizer;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Arrays;

/**
 * Component responsible for setting up the attributes for the swagger schemas.
 * <p>
 * An attribute in a schema in code is provided a {@link FieldType} via the {@link ValidationMetadata} annotation.
 * <pre>
 *     {@code
 * @ValidationMetadate(FieldType.USERNAME)
 * private String username;
 *     }
 * </pre>
 * <p>
 * Via this property, this class applies the metadata for the specific property to the attribute, to be visible in the swagger documentation.
 */
@Slf4j
@Component
public class SwaggerProperties implements PropertyCustomizer {

    private static final String PATTERN_FORMAT = "regex: '%s'";

    private static String getFormat(FieldType type) {
        if (type.getFormat() != null) {
            return type.getFormat();
        } else {
            return type.getPattern() != null ? PATTERN_FORMAT.formatted(type.getPattern())
                    : null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Schema<?> customize(Schema property, AnnotatedType type) {
        if (type.getCtxAnnotations() != null) {
            for (Annotation ctxAnnotation : type.getCtxAnnotations()) {
                if (ctxAnnotation instanceof ValidationMetadata schema) {
                    FieldType fieldType = schema.value();
                    if (fieldType == FieldType.NONE) {
                        continue;
                    }

                    property.setDescription(fieldType.getDescription());
                    property.setFormat(getFormat(fieldType));
                    property.setPattern(null);

                    if (fieldType.getValues() != null) {
                        property.setEnum(Arrays.asList(fieldType.getValues().get()));
                    }
                }
            }
        }

        return property;
    }
}
