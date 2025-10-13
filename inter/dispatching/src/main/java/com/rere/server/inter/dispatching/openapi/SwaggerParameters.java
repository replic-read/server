package com.rere.server.inter.dispatching.openapi;

import com.rere.server.inter.dto.validation.FieldType;
import com.rere.server.inter.dto.validation.ValidationMetadata;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.ParameterCustomizer;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;

/**
 * Component responsible for setting up the parameters for the swagger schemas.
 * <p>
 * A parameter in request method is provided a {@link FieldType} via the {@link ValidationMetadata} annotation.
 * <pre>
 *     {@code
 * public ResponseEntity<String> query(@ValidationMetadata(FieldType.SEND_VERIFICATION_EMAIL) @RequestParameter boolean sendVerification) {
 *      // …
 * }
 *     }
 * </pre>
 * <p>
 * Via this property, this class applies the metadata for the specific property to the attribute, to be visible in the swagger documentation.
 */
@Slf4j
@Component
public class SwaggerParameters implements ParameterCustomizer {
    @Override
    public Parameter customize(Parameter parameterModel, MethodParameter methodParameter) {
        for (Annotation annotation : methodParameter.getParameterAnnotations()) {
            if (annotation instanceof ValidationMetadata schema) {
                FieldType fieldType = schema.value();
                if (fieldType == FieldType.NONE) {
                    continue;
                }

                parameterModel.setDescription(fieldType.getDescription());

                if (fieldType.getValues() != null) {
                    if (!Collection.class.isAssignableFrom(methodParameter.getParameterType())) {
                        Schema<String> parameterSchema = new StringSchema();
                        parameterSchema.setEnum(Arrays.asList(fieldType.getValues().get()));
                        parameterModel.setSchema(parameterSchema);
                    } else {
                        Schema<Object> parameterSchema = new ArraySchema();
                        Schema<String> itemSchema = new StringSchema();
                        itemSchema.setType("string");
                        itemSchema.setEnum(Arrays.asList(fieldType.getValues().get()));
                        parameterSchema.setItems(itemSchema);
                        parameterModel.setSchema(parameterSchema);
                    }
                }
            }
        }

        return parameterModel;
    }
}
