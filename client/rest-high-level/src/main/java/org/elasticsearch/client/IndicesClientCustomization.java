/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.client;

import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.rest.RestStatus;

import java.io.IOException;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

/**
 * A wrapper for the {@link RestHighLevelClient} that provides methods for accessing the Indices API.
 * <p>
 * See <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/indices.html">Indices API on elastic.co</a>
 */
public final class IndicesClientCustomization {
    private final RestHighLevelClientCustomization restHighLevelClient;

    IndicesClientCustomization(RestHighLevelClientCustomization restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    /**
     * Deletes an index using the Delete Index API.
     * See <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-delete-index.html">
     * Delete Index API on elastic.co</a>
     *
     * @param deleteIndexRequest the request
     * @param options the request options (e.g. headers), use {@link RequestOptions#DEFAULT} if nothing needs to be customized
     * @return the response
     * @throws IOException in case there is a problem sending the request or parsing back the response
     */
    public AcknowledgedResponse delete(DeleteIndexRequest deleteIndexRequest, RequestOptions options) throws IOException {
        return restHighLevelClient.performRequestAndParseEntity(deleteIndexRequest, IndicesRequestConverters::deleteIndex, options,
                                                                AcknowledgedResponse::fromXContent, emptySet());
    }


    /**
     * Creates an index using the Create Index API.
     * See <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-create-index.html">
     * Create Index API on elastic.co</a>
     *
     * @param createIndexRequest the request
     * @param options the request options (e.g. headers), use {@link RequestOptions#DEFAULT} if nothing needs to be customized
     * @return the response
     * @throws IOException in case there is a problem sending the request or parsing back the response
     */
    public CreateIndexResponse create(CreateIndexRequest createIndexRequest,
                                      RequestOptions options) throws IOException {
        return restHighLevelClient.performRequestAndParseEntity(createIndexRequest, IndicesRequestConverters::createIndex, options,
                                                                CreateIndexResponse::fromXContent, emptySet());
    }

    /**
     * Creates an index using the Create Index API.
     * See <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-create-index.html">
     * Create Index API on elastic.co</a>
     *
     * @param createIndexRequest the request
     * @param options the request options (e.g. headers), use {@link RequestOptions#DEFAULT} if nothing needs to be customized
     * @return the response
     * @throws IOException in case there is a problem sending the request or parsing back the response
     * @deprecated This method uses an old request object which still refers to types, a deprecated feature. The
     * method {@link #create(CreateIndexRequest, RequestOptions)} should be used instead, which accepts a new
     * request object.
     */
    @Deprecated
    public org.elasticsearch.action.admin.indices.create.CreateIndexResponse create(
        org.elasticsearch.action.admin.indices.create.CreateIndexRequest createIndexRequest,
        RequestOptions options) throws IOException {
        return restHighLevelClient.performRequestAndParseEntity(createIndexRequest,
                                                                IndicesRequestConverters::createIndex, options,
                                                                org.elasticsearch.action.admin.indices.create.CreateIndexResponse::fromXContent, emptySet());
    }

    /**
     * Updates the mappings on an index using the Put Mapping API.
     * See <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-put-mapping.html">
     * Put Mapping API on elastic.co</a>
     *
     * @param putMappingRequest the request
     * @param options the request options (e.g. headers), use {@link RequestOptions#DEFAULT} if nothing needs to be customized
     * @return the response
     * @throws IOException in case there is a problem sending the request or parsing back the response
     */
    public AcknowledgedResponse putMapping(PutMappingRequest putMappingRequest, RequestOptions options) throws IOException {
        return restHighLevelClient.performRequestAndParseEntity(putMappingRequest, IndicesRequestConverters::putMapping, options,
                                                                AcknowledgedResponse::fromXContent, emptySet());
    }


    /**
     * Updates the mappings on an index using the Put Mapping API.
     * See <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-put-mapping.html">
     * Put Mapping API on elastic.co</a>
     *
     * @param putMappingRequest the request
     * @param options the request options (e.g. headers), use {@link RequestOptions#DEFAULT} if nothing needs to be customized
     * @return the response
     * @throws IOException in case there is a problem sending the request or parsing back the response
     * @deprecated This method uses an old request object which still refers to types, a deprecated feature. The method
     * {@link #putMapping(PutMappingRequest, RequestOptions)} should be used instead, which accepts a new request object.
     */
    @Deprecated
    public AcknowledgedResponse putMapping(org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest putMappingRequest,
                                           RequestOptions options) throws IOException {
        return restHighLevelClient.performRequestAndParseEntity(putMappingRequest, IndicesRequestConverters::putMapping, options,
                                                                AcknowledgedResponse::fromXContent, emptySet());
    }

    /**
     * Retrieves the mappings on an index or indices using the Get Mapping API.
     * See <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-get-mapping.html">
     * Get Mapping API on elastic.co</a>
     *
     * @param getMappingsRequest the request
     * @param options the request options (e.g. headers), use {@link RequestOptions#DEFAULT} if nothing needs to be customized
     * @return the response
     * @throws IOException in case there is a problem sending the request or parsing back the response
     */
    public GetMappingsResponse getMapping(GetMappingsRequest getMappingsRequest, RequestOptions options) throws IOException {
        return restHighLevelClient.performRequestAndParseEntity(getMappingsRequest,
                                                                IndicesRequestConverters::getMappings,
                                                                options,
                                                                GetMappingsResponse::fromXContent,
                                                                emptySet());
    }


    /**
     * Retrieves the mappings on an index or indices using the Get Mapping API.
     * See <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-get-mapping.html">
     * Get Mapping API on elastic.co</a>
     *
     * @param getMappingsRequest the request
     * @param options the request options (e.g. headers), use {@link RequestOptions#DEFAULT} if nothing needs to be customized
     * @return the response
     * @throws IOException in case there is a problem sending the request or parsing back the response
     * @deprecated This method uses old request and response objects which still refer to types, a deprecated
     * feature. The method {@link #getMapping(GetMappingsRequest, RequestOptions)} should be used instead, which
     * accepts a new request object.
     */
    @Deprecated
    public org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse getMapping(
        org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest getMappingsRequest,
        RequestOptions options) throws IOException {
        return restHighLevelClient.performRequestAndParseEntity(getMappingsRequest,
                                                                IndicesRequestConverters::getMappings,
                                                                options,
                                                                org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse::fromXContent,
                                                                emptySet());
    }

    /**
     * Updates aliases using the Index Aliases API.
     * See <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-aliases.html">
     * Index Aliases API on elastic.co</a>
     *
     * @param indicesAliasesRequest the request
     * @param options the request options (e.g. headers), use {@link RequestOptions#DEFAULT} if nothing needs to be customized
     * @return the response
     * @throws IOException in case there is a problem sending the request or parsing back the response
     */
    public AcknowledgedResponse updateAliases(IndicesAliasesRequest indicesAliasesRequest, RequestOptions options) throws IOException {
        return restHighLevelClient.performRequestAndParseEntity(indicesAliasesRequest, IndicesRequestConverters::updateAliases, options,
                                                                AcknowledgedResponse::fromXContent, emptySet());
    }

    /**
     * Refresh one or more indices using the Refresh API.
     * See <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-refresh.html"> Refresh API on elastic.co</a>
     *
     * @param refreshRequest the request
     * @param options the request options (e.g. headers), use {@link RequestOptions#DEFAULT} if nothing needs to be customized
     * @return the response
     * @throws IOException in case there is a problem sending the request or parsing back the response
     */
    public RefreshResponse refresh(RefreshRequest refreshRequest, RequestOptions options) throws IOException {
        return restHighLevelClient.performRequestAndParseEntity(refreshRequest, IndicesRequestConverters::refresh, options,
                                                                RefreshResponse::fromXContent, emptySet());
    }


    /**
     * Gets one or more aliases using the Get Index Aliases API.
     * See <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-aliases.html"> Indices Aliases API on
     * elastic.co</a>
     *
     * @param getAliasesRequest the request
     * @param options the request options (e.g. headers), use {@link RequestOptions#DEFAULT} if nothing needs to be customized
     * @return the response
     * @throws IOException in case there is a problem sending the request or parsing back the response
     */
    public GetAliasesResponse getAlias(GetAliasesRequest getAliasesRequest, RequestOptions options) throws IOException {
        return restHighLevelClient.performRequestAndParseEntity(getAliasesRequest, IndicesRequestConverters::getAlias, options,
                                                                GetAliasesResponse::fromXContent, singleton(RestStatus.NOT_FOUND.getStatus()));
    }


}
