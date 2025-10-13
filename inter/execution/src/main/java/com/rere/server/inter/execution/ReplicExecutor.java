package com.rere.server.inter.execution;

import com.rere.server.inter.dto.request.CreateReplicRequest;
import com.rere.server.inter.dto.response.ReplicResponse;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

/**
 * Executor for the replic class.
 */
public interface ReplicExecutor<RS, RSP, D, I, C> {

    /**
     * Executor for GET /replics/.
     * @param sort The 'sort' query parameter.
     * @param direction The 'direction' query parameter.
     * @param replicId The 'replic_id' query parameter.
     * @param accountId The 'account_in' query parameter.
     * @param stateFilter The 'filter' query parameter.
     * @param query The 'query' query parameter.
     * @return The response body.
     */
    List<ReplicResponse> getReplics(RSP sort, D direction,
                                    I replicId, I accountId, Set<RS> stateFilter, String query);

    /**
     * Executor for POST /replics/.
     * @param request The request body.
     * @param contentStream The multipart file content.
     * @return The response body.
     */
    ReplicResponse createReplic(CreateReplicRequest request, C contentStream);

    /**
     * Executor for GET /replics/{id}/content/
     * @param id The 'id' path variable.
     * @param password The 'password' query parameter.
     * @return The stream that contains the replic content.
     */
    InputStream getReplicContent(I id, String password);

    /**
     * Executor for PUT /replics/{id}/.
     * @param id The 'id' path variable.
     * @param state The 'state' query parameter.
     */
    void updateReplicState(I id, RS state);

}
