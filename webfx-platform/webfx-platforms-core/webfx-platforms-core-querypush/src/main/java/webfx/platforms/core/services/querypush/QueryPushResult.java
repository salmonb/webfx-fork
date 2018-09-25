package webfx.platforms.core.services.querypush;

import webfx.platforms.core.services.json.codec.AbstractJsonCodec;
import webfx.platforms.core.services.json.codec.JsonCodecManager;
import webfx.platforms.core.services.json.JsonObject;
import webfx.platforms.core.services.json.WritableJsonObject;
import webfx.platforms.core.services.query.QueryResult;
import webfx.platforms.core.services.querypush.diff.QueryResultDiff;

/**
 * @author Bruno Salmon
 */
public final class QueryPushResult {

    private final Object queryStreamId;
    private final QueryResult queryResult;
    private final QueryResultDiff queryResultDiff;

    public QueryPushResult(Object queryStreamId, QueryResult queryResult) {
        this(queryStreamId, queryResult, null);
    }

    public QueryPushResult(Object queryStreamId, QueryResultDiff queryResultDiff) {
        this(queryStreamId, null, queryResultDiff);
    }

    public QueryPushResult(Object queryStreamId, QueryResult queryResult, QueryResultDiff queryResultDiff) {
        this.queryStreamId = queryStreamId;
        this.queryResult = queryResultDiff != null ? null : queryResult;
        this.queryResultDiff = queryResultDiff;
    }

    public Object getQueryStreamId() {
        return queryStreamId;
    }

    public QueryResult getQueryResult() {
        return queryResult;
    }

    public QueryResultDiff getQueryResultDiff() {
        return queryResultDiff;
    }

    /****************************************************
     *                    Json Codec                    *
     * *************************************************/

    public static final class Codec extends AbstractJsonCodec<QueryPushResult> {

        private static final String CODEC_ID = "QueryPushResult";
        private static final String QUERY_STREAM_ID_KEY = "queryStreamId";
        private static final String QUERY_RESULT_KEY = "queryResult";
        private static final String QUERY_RESULT_DIFF_KEY = "queryResultDiff";

        public Codec() {
            super(QueryPushResult.class, CODEC_ID);
        }

        @Override
        public void encodeToJson(QueryPushResult arg, WritableJsonObject json) {
            AbstractJsonCodec.encodeKeyIfNotNull(QUERY_STREAM_ID_KEY, arg.getQueryStreamId(), json);
            AbstractJsonCodec.encodeKeyIfNotNull(QUERY_RESULT_KEY, arg.getQueryResult(), json);
            AbstractJsonCodec.encodeKeyIfNotNull(QUERY_RESULT_DIFF_KEY, arg.getQueryResultDiff(), json);
        }

        @Override
        public QueryPushResult decodeFromJson(JsonObject json) {
            return new QueryPushResult(
                    json.get(QUERY_STREAM_ID_KEY),
                    JsonCodecManager.decodeFromJson(json.get(QUERY_RESULT_KEY)),
                    JsonCodecManager.decodeFromJson(json.get(QUERY_RESULT_DIFF_KEY))
            );
        }
    }
}
