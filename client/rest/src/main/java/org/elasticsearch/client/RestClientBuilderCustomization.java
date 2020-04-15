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

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.Objects;

/**
 * Helps creating a new {@link RestClient}. Allows to set the most common http client configuration options when internally
 * creating the underlying {@link org.apache.http.nio.client.HttpAsyncClient}. Also allows to provide an externally created
 * {@link org.apache.http.nio.client.HttpAsyncClient} in case additional customization is needed.
 */
public final class RestClientBuilderCustomization {
    public static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 1000;
    public static final int DEFAULT_SOCKET_TIMEOUT_MILLIS = 10000;
    public static final int DEFAULT_MAX_RETRY_TIMEOUT_MILLIS = DEFAULT_SOCKET_TIMEOUT_MILLIS;
    public static final int DEFAULT_CONNECTION_REQUEST_TIMEOUT_MILLIS = 500;

    private static final Header[] EMPTY_HEADERS = new Header[0];

    private final HttpHost[] hosts;
    private int maxRetryTimeout = DEFAULT_MAX_RETRY_TIMEOUT_MILLIS;
    private Header[] defaultHeaders = EMPTY_HEADERS;
    private RestClientCustomization.FailureListener failureListener;
    private CloseableHttpClient client;
    private RestClientCustomization.RequestConfigCallback requestConfigCallback;

    /**
     * Creates a new builder instance and sets the hosts that the client will send requests to.
     */
    private RestClientBuilderCustomization(HttpHost... hosts) {
        if (hosts == null || hosts.length == 0) {
            throw new IllegalArgumentException("no hosts provided");
        }
        this.hosts = hosts;
    }

    /**
     * Sets the maximum timeout (in milliseconds) to honour in case of multiple retries of the same request.
     * {@link #DEFAULT_MAX_RETRY_TIMEOUT_MILLIS} if not specified.
     *
     * @throws IllegalArgumentException if maxRetryTimeoutMillis is not greater than 0
     */
    public RestClientBuilderCustomization setMaxRetryTimeoutMillis(int maxRetryTimeoutMillis) {
        if (maxRetryTimeoutMillis <= 0) {
            throw new IllegalArgumentException("maxRetryTimeoutMillis must be greater than 0");
        }
        this.maxRetryTimeout = maxRetryTimeoutMillis;
        return this;
    }

    /**
     * Sets the default request headers, to be used sent with every request unless overridden on a per request basis
     */
    public RestClientBuilderCustomization setDefaultHeaders(Header[] defaultHeaders) {
        Objects.requireNonNull(defaultHeaders, "defaultHeaders must not be null");
        for (Header defaultHeader : defaultHeaders) {
            Objects.requireNonNull(defaultHeader, "default header must not be null");
        }
        this.defaultHeaders = defaultHeaders;
        return this;
    }

    RestClientBuilderCustomization setFailureListener(RestClientCustomization.FailureListener failureListener) {
        Objects.requireNonNull(failureListener, "failureListener must not be null");
        this.failureListener = failureListener;
        return this;
    }

    RestClientBuilderCustomization setClient(CloseableHttpClient client) {
        Objects.requireNonNull(client, "client must not be null");
        this.client = client;
        return this;
    }

    RestClientBuilderCustomization setRequestConfigCallback(RestClientCustomization.RequestConfigCallback requestConfigCallback) {
        Objects.requireNonNull(requestConfigCallback, "requestConfigCallback must not be null");
        this.requestConfigCallback = requestConfigCallback;
        return this;
    }

    /**
     * Creates a new {@link RestClient} based on the provided configuration.
     */
    public RestClientCustomization build() {
        if (failureListener == null) {
            failureListener = new RestClientCustomization.FailureListener();
        }
        return new RestClientCustomization(client, maxRetryTimeout, defaultHeaders, hosts, failureListener);
    }
}
