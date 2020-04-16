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

import org.apache.http.HttpEntity;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.admin.cluster.node.tasks.list.ListTasksResponse;
import org.elasticsearch.action.admin.cluster.storedscripts.DeleteStoredScriptRequest;
import org.elasticsearch.action.admin.cluster.storedscripts.GetStoredScriptRequest;
import org.elasticsearch.action.admin.cluster.storedscripts.GetStoredScriptResponse;
import org.elasticsearch.action.admin.cluster.storedscripts.PutStoredScriptRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.explain.ExplainRequest;
import org.elasticsearch.action.explain.ExplainResponse;
import org.elasticsearch.action.fieldcaps.FieldCapabilitiesRequest;
import org.elasticsearch.action.fieldcaps.FieldCapabilitiesResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.core.MainRequest;
import org.elasticsearch.client.core.MainResponse;
import org.elasticsearch.client.core.MultiTermVectorsRequest;
import org.elasticsearch.client.core.MultiTermVectorsResponse;
import org.elasticsearch.client.core.TermVectorsRequest;
import org.elasticsearch.client.core.TermVectorsResponse;
import org.elasticsearch.client.tasks.TaskSubmissionResponse;
import org.elasticsearch.common.CheckedConsumer;
import org.elasticsearch.common.CheckedFunction;
import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.xcontent.ContextParser;
import org.elasticsearch.common.xcontent.DeprecationHandler;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.rankeval.RankEvalRequest;
import org.elasticsearch.index.rankeval.RankEvalResponse;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.ReindexRequest;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.plugins.spi.NamedXContentProvider;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.script.mustache.MultiSearchTemplateRequest;
import org.elasticsearch.script.mustache.MultiSearchTemplateResponse;
import org.elasticsearch.script.mustache.SearchTemplateRequest;
import org.elasticsearch.script.mustache.SearchTemplateResponse;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.adjacency.AdjacencyMatrixAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.adjacency.ParsedAdjacencyMatrix;
import org.elasticsearch.search.aggregations.bucket.composite.CompositeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.composite.ParsedComposite;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.FiltersAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.ParsedFilter;
import org.elasticsearch.search.aggregations.bucket.filter.ParsedFilters;
import org.elasticsearch.search.aggregations.bucket.geogrid.GeoHashGridAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.geogrid.GeoTileGridAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.geogrid.ParsedGeoHashGrid;
import org.elasticsearch.search.aggregations.bucket.geogrid.ParsedGeoTileGrid;
import org.elasticsearch.search.aggregations.bucket.global.GlobalAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.global.ParsedGlobal;
import org.elasticsearch.search.aggregations.bucket.histogram.AutoDateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.HistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedAutoDateHistogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedHistogram;
import org.elasticsearch.search.aggregations.bucket.missing.MissingAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.missing.ParsedMissing;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedReverseNested;
import org.elasticsearch.search.aggregations.bucket.nested.ReverseNestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.DateRangeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.GeoDistanceAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.IpRangeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.ParsedBinaryRange;
import org.elasticsearch.search.aggregations.bucket.range.ParsedDateRange;
import org.elasticsearch.search.aggregations.bucket.range.ParsedGeoDistance;
import org.elasticsearch.search.aggregations.bucket.range.ParsedRange;
import org.elasticsearch.search.aggregations.bucket.range.RangeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.sampler.InternalSampler;
import org.elasticsearch.search.aggregations.bucket.sampler.ParsedSampler;
import org.elasticsearch.search.aggregations.bucket.significant.ParsedSignificantLongTerms;
import org.elasticsearch.search.aggregations.bucket.significant.ParsedSignificantStringTerms;
import org.elasticsearch.search.aggregations.bucket.significant.SignificantLongTerms;
import org.elasticsearch.search.aggregations.bucket.significant.SignificantStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.DoubleTerms;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedDoubleTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ExtendedStatsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.GeoBoundsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.GeoCentroidAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.InternalHDRPercentileRanks;
import org.elasticsearch.search.aggregations.metrics.InternalHDRPercentiles;
import org.elasticsearch.search.aggregations.metrics.InternalTDigestPercentileRanks;
import org.elasticsearch.search.aggregations.metrics.InternalTDigestPercentiles;
import org.elasticsearch.search.aggregations.metrics.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.MedianAbsoluteDeviationAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.MinAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedAvg;
import org.elasticsearch.search.aggregations.metrics.ParsedCardinality;
import org.elasticsearch.search.aggregations.metrics.ParsedExtendedStats;
import org.elasticsearch.search.aggregations.metrics.ParsedGeoBounds;
import org.elasticsearch.search.aggregations.metrics.ParsedGeoCentroid;
import org.elasticsearch.search.aggregations.metrics.ParsedHDRPercentileRanks;
import org.elasticsearch.search.aggregations.metrics.ParsedHDRPercentiles;
import org.elasticsearch.search.aggregations.metrics.ParsedMax;
import org.elasticsearch.search.aggregations.metrics.ParsedMedianAbsoluteDeviation;
import org.elasticsearch.search.aggregations.metrics.ParsedMin;
import org.elasticsearch.search.aggregations.metrics.ParsedScriptedMetric;
import org.elasticsearch.search.aggregations.metrics.ParsedStats;
import org.elasticsearch.search.aggregations.metrics.ParsedSum;
import org.elasticsearch.search.aggregations.metrics.ParsedTDigestPercentileRanks;
import org.elasticsearch.search.aggregations.metrics.ParsedTDigestPercentiles;
import org.elasticsearch.search.aggregations.metrics.ParsedTopHits;
import org.elasticsearch.search.aggregations.metrics.ParsedValueCount;
import org.elasticsearch.search.aggregations.metrics.ParsedWeightedAvg;
import org.elasticsearch.search.aggregations.metrics.ScriptedMetricAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.StatsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.SumAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.TopHitsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ValueCountAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.WeightedAvgAggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.DerivativePipelineAggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.ExtendedStatsBucketPipelineAggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.InternalBucketMetricValue;
import org.elasticsearch.search.aggregations.pipeline.InternalSimpleValue;
import org.elasticsearch.search.aggregations.pipeline.ParsedBucketMetricValue;
import org.elasticsearch.search.aggregations.pipeline.ParsedDerivative;
import org.elasticsearch.search.aggregations.pipeline.ParsedExtendedStatsBucket;
import org.elasticsearch.search.aggregations.pipeline.ParsedPercentilesBucket;
import org.elasticsearch.search.aggregations.pipeline.ParsedSimpleValue;
import org.elasticsearch.search.aggregations.pipeline.ParsedStatsBucket;
import org.elasticsearch.search.aggregations.pipeline.PercentilesBucketPipelineAggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.StatsBucketPipelineAggregationBuilder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.elasticsearch.search.suggest.phrase.PhraseSuggestion;
import org.elasticsearch.search.suggest.phrase.PhraseSuggestionBuilder;
import org.elasticsearch.search.suggest.term.TermSuggestion;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;

/**
 * High level REST client that wraps an instance of the low level {@link RestClient} and allows to build requests and read responses. The
 * {@link RestClient} instance is internally built based on the provided {@link RestClientBuilder} and it gets closed automatically when
 * closing the {@link RestHighLevelClientCustomization} instance that wraps it.
 * <p>
 *
 * In case an already existing instance of a low-level REST client needs to be provided, this class can be subclassed and the
 * {link #RestHighLevelClient(RestClient, CheckedConsumer, List)} constructor can be used.
 * <p>
 *
 * This class can also be sub-classed to expose additional client methods that make use of endpoints added to Elasticsearch through plugins,
 * or to add support for custom response sections, again added to Elasticsearch through plugins.
 * <p>
 *
 *
 * The majority of the methods in this class come in two flavors, a blocking and an asynchronous version (e.g.
 * {link #search(SearchRequest, RequestOptions)} and {link #searchAsync(SearchRequest, RequestOptions, ActionListener)}, where the later
 * takes an implementation of an {@link ActionListener} as an argument that needs to implement methods that handle successful responses and
 * failure scenarios. Most of the blocking calls can throw an {@link IOException} or an unchecked {@link ElasticsearchException} in the
 * following cases:
 *
 * <ul>
 * <li>an {@link IOException} is usually thrown in case of failing to parse the REST response in the high-level REST client, the request
 * times out or similar cases where there is no response coming back from the Elasticsearch server</li>
 * <li>an {@link ElasticsearchException} is usually thrown in case where the server returns a 4xx or 5xx error code. The high-level client
 * then tries to parse the response body error details into a generic ElasticsearchException and suppresses the original
 * {@link ResponseExceptionCustomization}</li>
 * </ul>
 *
 */
public class RestHighLevelClientCustomization implements Closeable {

    private final RestClientCustomization client;
    private final NamedXContentRegistry registry;
    private final CheckedConsumer<RestClientCustomization, IOException> doClose;

    private final IndicesClientCustomization indicesClient = new IndicesClientCustomization(this);

    public RestHighLevelClientCustomization(RestClientCustomization restClient) {
        this(restClient, RestClientCustomization::close, Collections.emptyList());
    }

    /**
     * Creates a {@link RestHighLevelClientCustomization} given the low level {@link RestClient} that it should use to perform requests and
     * a list of entries that allow to parse custom response sections added to Elasticsearch through plugins.
     * This constructor can be called by subclasses in case an externally created low-level REST client needs to be provided.
     * The consumer argument allows to control what needs to be done when the {@link #close()} method is called.
     * Also subclasses can provide parsers for custom response sections added to Elasticsearch through plugins.
     */
    protected RestHighLevelClientCustomization(RestClientCustomization restClient, CheckedConsumer<RestClientCustomization, IOException> doClose,
                                               List<NamedXContentRegistry.Entry> namedXContentEntries) {
        this.client = Objects.requireNonNull(restClient, "restClient must not be null");
        this.doClose = Objects.requireNonNull(doClose, "doClose consumer must not be null");
        this.registry = new NamedXContentRegistry(
            Stream.of(getDefaultNamedXContents().stream(), getProvidedNamedXContents().stream(), namedXContentEntries.stream())
                  .flatMap(Function.identity()).collect(toList()));
    }


    @Override
    public final void close() throws IOException {
        doClose.accept(client);
    }

    /**
     * Provides an {@link IndicesClient} which can be used to access the Indices API.
     *
     * See <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/indices.html">Indices API on elastic.co</a>
     */
    public final IndicesClientCustomization indices() {
        return indicesClient;
    }

    /**
     * Executes a bulk request using the Bulk API.
     * See <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-bulk.html">Bulk API on elastic.co</a>
     * @param bulkRequest the request
     * @param options the request options (e.g. headers), use {@link RequestOptions#DEFAULT} if nothing needs to be customized
     * @return the response
     */
    public final BulkResponse bulk(BulkRequest bulkRequest, RequestOptions options) throws IOException {
        return performRequestAndParseEntity(bulkRequest, RequestConverters::bulk, options, BulkResponse::fromXContent, emptySet());
    }

    /**
     * Executes a search request using the Search API.
     * See <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-search.html">Search API on elastic.co</a>
     * @param searchRequest the request
     * @param options the request options (e.g. headers), use {@link RequestOptions#DEFAULT} if nothing needs to be customized
     * @return the response
     */
    public final SearchResponse search(SearchRequest searchRequest, RequestOptions options) throws IOException {
        return performRequestAndParseEntity(
            searchRequest,
            r -> RequestConverters.search(r, "_search"),
            options,
            SearchResponse::fromXContent,
            emptySet());
    }


    /**
     * @deprecated If creating a new HLRC ReST API call, consider creating new actions instead of reusing server actions. The Validation
     * layer has been added to the ReST client, and requests should extend {@link Validatable} instead of {@link ActionRequest}.
     */
    @Deprecated
    protected final <Req extends ActionRequest, Resp> Resp performRequestAndParseEntity(Req request,
                                                                                        CheckedFunction<Req, Request, IOException> requestConverter,
                                                                                        RequestOptions options,
                                                                                        CheckedFunction<XContentParser, Resp, IOException> entityParser,
                                                                                        Set<Integer> ignores) throws IOException {
        return performRequest(request, requestConverter, options,
                              response -> parseEntity(response.getEntity(), entityParser), ignores);
    }

    /**
     * Defines a helper method for performing a request and then parsing the returned entity using the provided entityParser.
     */
    protected final <Req extends Validatable, Resp> Resp performRequestAndParseEntity(Req request,
                                                                                      CheckedFunction<Req, Request, IOException> requestConverter,
                                                                                      RequestOptions options,
                                                                                      CheckedFunction<XContentParser, Resp, IOException> entityParser,
                                                                                      Set<Integer> ignores) throws IOException {
        return performRequest(request, requestConverter, options,
                              response -> parseEntity(response.getEntity(), entityParser), ignores);
    }


    /**
     * @deprecated If creating a new HLRC ReST API call, consider creating new actions instead of reusing server actions. The Validation
     * layer has been added to the ReST client, and requests should extend {@link Validatable} instead of {@link ActionRequest}.
     */
    @Deprecated
    protected final <Req extends ActionRequest, Resp> Resp performRequest(Req request,
                                                                          CheckedFunction<Req, Request, IOException> requestConverter,
                                                                          RequestOptions options,
                                                                          CheckedFunction<ResponseCustomization, Resp, IOException> responseConverter,
                                                                          Set<Integer> ignores) throws IOException {
        ActionRequestValidationException validationException = request.validate();
        if (validationException != null && validationException.validationErrors().isEmpty() == false) {
            throw validationException;
        }
        return internalPerformRequest(request, requestConverter, options, responseConverter, ignores);
    }

    /**
     * Defines a helper method for performing a request.
     */
    protected final <Req extends Validatable, Resp> Resp performRequest(Req request,
                                                                        CheckedFunction<Req, Request, IOException> requestConverter,
                                                                        RequestOptions options,
                                                                        CheckedFunction<ResponseCustomization, Resp, IOException> responseConverter,
                                                                        Set<Integer> ignores) throws IOException {
        Optional<ValidationException> validationException = request.validate();
        if (validationException != null && validationException.isPresent()) {
            throw validationException.get();
        }
        return internalPerformRequest(request, requestConverter, options, responseConverter, ignores);
    }


    protected final <Resp> Resp parseEntity(final HttpEntity entity,
                                            final CheckedFunction<XContentParser, Resp, IOException> entityParser) throws IOException {
        if (entity == null) {
            throw new IllegalStateException("Response body expected but not returned");
        }
        if (entity.getContentType() == null) {
            throw new IllegalStateException("Elasticsearch didn't return the [Content-Type] header, unable to parse response body");
        }
        XContentType xContentType = XContentType.fromMediaTypeOrFormat(entity.getContentType().getValue());
        if (xContentType == null) {
            throw new IllegalStateException("Unsupported Content-Type: " + entity.getContentType().getValue());
        }
        try (XContentParser parser = xContentType.xContent().createParser(registry, DEPRECATION_HANDLER, entity.getContent())) {
            return entityParser.apply(parser);
        }
    }

    /**
     * Provides common functionality for performing a request.
     */
    private <Req, Resp> Resp internalPerformRequest(Req request,
                                                    CheckedFunction<Req, Request, IOException> requestConverter,
                                                    RequestOptions options,
                                                    CheckedFunction<ResponseCustomization, Resp, IOException> responseConverter,
                                                    Set<Integer> ignores) throws IOException {
        Request req = requestConverter.apply(request);
        req.setOptions(options);
        ResponseCustomization response;
        try {
            response = client.performRequest(req.getMethod(), req.getEndpoint(), req.getParameters(), req.getEntity());
        } catch (ResponseExceptionCustomization e) {
            if (ignores.contains(e.getResponse().getStatusLine().getStatusCode())) {
                try {
                    return responseConverter.apply(e.getResponse());
                } catch (Exception innerException) {
                    // the exception is ignored as we now try to parse the response as an error.
                    // this covers cases like get where 404 can either be a valid document not found response,
                    // or an error for which parsing is completely different. We try to consider the 404 response as a valid one
                    // first. If parsing of the response breaks, we fall back to parsing it as an error.
                    throw parseResponseException(e);
                }
            }
            throw parseResponseException(e);
        }

        try {
            return responseConverter.apply(response);
        } catch(Exception e) {
            throw new IOException("Unable to parse response body for " + response, e);
        }
    }

    /**
     * Converts a {@link ResponseException} obtained from the low level REST client into an {@link ElasticsearchException}.
     * If a response body was returned, tries to parse it as an error returned from Elasticsearch.
     * If no response body was returned or anything goes wrong while parsing the error, returns a new {@link ElasticsearchStatusException}
     * that wraps the original {@link ResponseException}. The potential exception obtained while parsing is added to the returned
     * exception as a suppressed exception. This method is guaranteed to not throw any exception eventually thrown while parsing.
     */
    protected final ElasticsearchStatusException parseResponseException(ResponseExceptionCustomization responseException) {
        ResponseCustomization response = responseException.getResponse();
        HttpEntity entity = response.getEntity();
        ElasticsearchStatusException elasticsearchException;
        RestStatus restStatus = RestStatus.fromCode(response.getStatusLine().getStatusCode());

        if (entity == null) {
            elasticsearchException = new ElasticsearchStatusException(
                responseException.getMessage(), restStatus, responseException);
        } else {
            try {
                elasticsearchException = parseEntity(entity, BytesRestResponse::errorFromXContent);
                elasticsearchException.addSuppressed(responseException);
            } catch (Exception e) {
                elasticsearchException = new ElasticsearchStatusException("Unable to parse response body", restStatus, responseException);
                elasticsearchException.addSuppressed(e);
            }
        }
        return elasticsearchException;
    }

    /**
     * Ignores deprecation warnings. This is appropriate because it is only
     * used to parse responses from Elasticsearch. Any deprecation warnings
     * emitted there just mean that you are talking to an old version of
     * Elasticsearch. There isn't anything you can do about the deprecation.
     */
    private static final DeprecationHandler DEPRECATION_HANDLER = new DeprecationHandler() {
        @Override
        public void usedDeprecatedName(String usedName, String modernName) {}
        @Override
        public void usedDeprecatedField(String usedName, String replacedWith) {}
    };

    static List<NamedXContentRegistry.Entry> getDefaultNamedXContents() {
        Map<String, ContextParser<Object, ? extends Aggregation>> map = new HashMap<>();
        map.put(CardinalityAggregationBuilder.NAME, (p, c) -> ParsedCardinality.fromXContent(p, (String) c));
        map.put(InternalHDRPercentiles.NAME, (p, c) -> ParsedHDRPercentiles.fromXContent(p, (String) c));
        map.put(InternalHDRPercentileRanks.NAME, (p, c) -> ParsedHDRPercentileRanks.fromXContent(p, (String) c));
        map.put(InternalTDigestPercentiles.NAME, (p, c) -> ParsedTDigestPercentiles.fromXContent(p, (String) c));
        map.put(InternalTDigestPercentileRanks.NAME, (p, c) -> ParsedTDigestPercentileRanks.fromXContent(p, (String) c));
        map.put(PercentilesBucketPipelineAggregationBuilder.NAME, (p, c) -> ParsedPercentilesBucket.fromXContent(p, (String) c));
        map.put(MedianAbsoluteDeviationAggregationBuilder.NAME, (p, c) -> ParsedMedianAbsoluteDeviation.fromXContent(p, (String) c));
        map.put(MinAggregationBuilder.NAME, (p, c) -> ParsedMin.fromXContent(p, (String) c));
        map.put(MaxAggregationBuilder.NAME, (p, c) -> ParsedMax.fromXContent(p, (String) c));
        map.put(SumAggregationBuilder.NAME, (p, c) -> ParsedSum.fromXContent(p, (String) c));
        map.put(AvgAggregationBuilder.NAME, (p, c) -> ParsedAvg.fromXContent(p, (String) c));
        map.put(WeightedAvgAggregationBuilder.NAME, (p, c) -> ParsedWeightedAvg.fromXContent(p, (String) c));
        map.put(ValueCountAggregationBuilder.NAME, (p, c) -> ParsedValueCount.fromXContent(p, (String) c));
        map.put(InternalSimpleValue.NAME, (p, c) -> ParsedSimpleValue.fromXContent(p, (String) c));
        map.put(DerivativePipelineAggregationBuilder.NAME, (p, c) -> ParsedDerivative.fromXContent(p, (String) c));
        map.put(InternalBucketMetricValue.NAME, (p, c) -> ParsedBucketMetricValue.fromXContent(p, (String) c));
        map.put(StatsAggregationBuilder.NAME, (p, c) -> ParsedStats.fromXContent(p, (String) c));
        map.put(StatsBucketPipelineAggregationBuilder.NAME, (p, c) -> ParsedStatsBucket.fromXContent(p, (String) c));
        map.put(ExtendedStatsAggregationBuilder.NAME, (p, c) -> ParsedExtendedStats.fromXContent(p, (String) c));
        map.put(ExtendedStatsBucketPipelineAggregationBuilder.NAME,
                (p, c) -> ParsedExtendedStatsBucket.fromXContent(p, (String) c));
        map.put(GeoBoundsAggregationBuilder.NAME, (p, c) -> ParsedGeoBounds.fromXContent(p, (String) c));
        map.put(GeoCentroidAggregationBuilder.NAME, (p, c) -> ParsedGeoCentroid.fromXContent(p, (String) c));
        map.put(HistogramAggregationBuilder.NAME, (p, c) -> ParsedHistogram.fromXContent(p, (String) c));
        map.put(DateHistogramAggregationBuilder.NAME, (p, c) -> ParsedDateHistogram.fromXContent(p, (String) c));
        map.put(AutoDateHistogramAggregationBuilder.NAME, (p, c) -> ParsedAutoDateHistogram.fromXContent(p, (String) c));
        map.put(StringTerms.NAME, (p, c) -> ParsedStringTerms.fromXContent(p, (String) c));
        map.put(LongTerms.NAME, (p, c) -> ParsedLongTerms.fromXContent(p, (String) c));
        map.put(DoubleTerms.NAME, (p, c) -> ParsedDoubleTerms.fromXContent(p, (String) c));
        map.put(MissingAggregationBuilder.NAME, (p, c) -> ParsedMissing.fromXContent(p, (String) c));
        map.put(NestedAggregationBuilder.NAME, (p, c) -> ParsedNested.fromXContent(p, (String) c));
        map.put(ReverseNestedAggregationBuilder.NAME, (p, c) -> ParsedReverseNested.fromXContent(p, (String) c));
        map.put(GlobalAggregationBuilder.NAME, (p, c) -> ParsedGlobal.fromXContent(p, (String) c));
        map.put(FilterAggregationBuilder.NAME, (p, c) -> ParsedFilter.fromXContent(p, (String) c));
        map.put(InternalSampler.PARSER_NAME, (p, c) -> ParsedSampler.fromXContent(p, (String) c));
        map.put(GeoHashGridAggregationBuilder.NAME, (p, c) -> ParsedGeoHashGrid.fromXContent(p, (String) c));
        map.put(GeoTileGridAggregationBuilder.NAME, (p, c) -> ParsedGeoTileGrid.fromXContent(p, (String) c));
        map.put(RangeAggregationBuilder.NAME, (p, c) -> ParsedRange.fromXContent(p, (String) c));
        map.put(DateRangeAggregationBuilder.NAME, (p, c) -> ParsedDateRange.fromXContent(p, (String) c));
        map.put(GeoDistanceAggregationBuilder.NAME, (p, c) -> ParsedGeoDistance.fromXContent(p, (String) c));
        map.put(FiltersAggregationBuilder.NAME, (p, c) -> ParsedFilters.fromXContent(p, (String) c));
        map.put(AdjacencyMatrixAggregationBuilder.NAME, (p, c) -> ParsedAdjacencyMatrix.fromXContent(p, (String) c));
        map.put(SignificantLongTerms.NAME, (p, c) -> ParsedSignificantLongTerms.fromXContent(p, (String) c));
        map.put(SignificantStringTerms.NAME, (p, c) -> ParsedSignificantStringTerms.fromXContent(p, (String) c));
        map.put(ScriptedMetricAggregationBuilder.NAME, (p, c) -> ParsedScriptedMetric.fromXContent(p, (String) c));
        map.put(IpRangeAggregationBuilder.NAME, (p, c) -> ParsedBinaryRange.fromXContent(p, (String) c));
        map.put(TopHitsAggregationBuilder.NAME, (p, c) -> ParsedTopHits.fromXContent(p, (String) c));
        map.put(CompositeAggregationBuilder.NAME, (p, c) -> ParsedComposite.fromXContent(p, (String) c));
        List<NamedXContentRegistry.Entry> entries = map.entrySet().stream()
                                                       .map(entry -> new NamedXContentRegistry.Entry(Aggregation.class, new ParseField(entry.getKey()), entry.getValue()))
                                                       .collect(Collectors.toList());
        entries.add(new NamedXContentRegistry.Entry(Suggest.Suggestion.class, new ParseField(TermSuggestionBuilder.SUGGESTION_NAME),
                                                    (parser, context) -> TermSuggestion.fromXContent(parser, (String)context)));
        entries.add(new NamedXContentRegistry.Entry(Suggest.Suggestion.class, new ParseField(PhraseSuggestionBuilder.SUGGESTION_NAME),
                                                    (parser, context) -> PhraseSuggestion.fromXContent(parser, (String)context)));
        entries.add(new NamedXContentRegistry.Entry(Suggest.Suggestion.class, new ParseField(CompletionSuggestionBuilder.SUGGESTION_NAME),
                                                    (parser, context) -> CompletionSuggestion.fromXContent(parser, (String)context)));
        return entries;
    }

    /**
     * Loads and returns the {@link NamedXContentRegistry.Entry} parsers provided by plugins.
     */
    static List<NamedXContentRegistry.Entry> getProvidedNamedXContents() {
        List<NamedXContentRegistry.Entry> entries = new ArrayList<>();
        for (NamedXContentProvider service : ServiceLoader.load(NamedXContentProvider.class)) {
            entries.addAll(service.getNamedXContentParsers());
        }
        return entries;
    }
}
