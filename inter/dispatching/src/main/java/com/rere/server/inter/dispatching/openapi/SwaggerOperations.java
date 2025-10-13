package com.rere.server.inter.dispatching.openapi;

import com.rere.server.inter.dispatching.documentation.endpoint.ApiResponseType;
import com.rere.server.inter.dispatching.documentation.endpoint.AuthorizationType;
import com.rere.server.inter.dispatching.documentation.endpoint.EndpointMetadata;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Type;

/**
 * Component responsible for setting up the attributes for the swagger operations.
 * <p>
 * An endpoint handler method in a spring-mvc controller is applied the {@link ApiResponseType} and {@link AuthorizationType} via the {@link EndpointMetadata} annotation:
 * <pre>
 *     {@code
 * @EndpointMetadate(
 *      responseTypes = {ApiResponseType.SUCCESS, ApiResponseType.UNAUTHORIZED},
 *      authorizationType = AuthorizationType.ADMIN
 * )
 * public void signup(@RequestBody SignupRequest body) {
 *     //…
 * }
 *     }
 * </pre>
 * <p>
 * Via this property, this class applies the metadata for the specific property to the operation, to be visible in the swagger documentation.
 */
@Slf4j
@Component
public class SwaggerOperations implements OperationCustomizer {

    private static final String APP_JSON_MEDIATYPE = "application/json";
    private static final String FORMAT_DESCRIPTION = "%s<br><br><b>Authorization:</b> %s";

    private static void setupContent(Type contentClass, ApiResponse response) {
        Schema<?> targetSchema = ModelConverters.getInstance()
                .readAllAsResolvedSchema(contentClass)
                .schema;

        MediaType mediaType = new MediaType().schema(targetSchema);
        Content content = new Content().addMediaType(APP_JSON_MEDIATYPE, mediaType);
        response.setContent(content);
    }

    private static void setupDescription(Operation operation, EndpointMetadata metadata) {
        String description = FORMAT_DESCRIPTION.formatted(operation.getDescription(), metadata.authorizationType().getDescription());
        operation.setDescription(description);
    }

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        EndpointMetadata metadata = handlerMethod.getMethodAnnotation(EndpointMetadata.class);
        ApiResponseType[] responseTypes = {};
        if (metadata != null) {
            responseTypes = metadata.responseTypes();
        }

        if (metadata != null) {
            setupDescription(operation, metadata);
        }

        Content declaredResponseType = operation.getResponses().get("200").getContent();
        operation.getResponses().clear();

        for (ApiResponseType responseType : responseTypes) {
            String statusCode = String.valueOf(responseType.getResponseCode().value());

            ApiResponse apiResponse = new ApiResponse().description(responseType.getDescription());
            if (responseType.equals(ApiResponseType.SUCCESS)) {
                apiResponse.setContent(declaredResponseType);
            } else if (responseType.getContentClass() != null) {
                setupContent(responseType.getContentClass(), apiResponse);
            }

            operation.getResponses().addApiResponse(statusCode, apiResponse);
        }

        return operation;
    }

}
