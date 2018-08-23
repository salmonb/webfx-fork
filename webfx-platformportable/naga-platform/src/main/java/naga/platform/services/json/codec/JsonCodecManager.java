package naga.platform.services.json.codec;

import naga.platform.bus.call.BusCallService;
import naga.platform.services.json.Json;
import naga.platform.services.json.JsonArray;
import naga.platform.services.json.JsonObject;
import naga.platform.services.json.WritableJsonArray;
import naga.platform.services.json.WritableJsonObject;
import naga.platform.services.query.QueryArgument;
import naga.platform.services.query.QueryResult;
import naga.platform.services.query.push.QueryPushArgument;
import naga.platform.services.query.push.QueryPushResult;
import naga.platform.services.query.push.diff.impl.QueryResultTranslation;
import naga.platform.services.update.GeneratedKeyBatchIndex;
import naga.platform.services.update.UpdateArgument;
import naga.platform.services.update.UpdateResult;
import naga.util.Dates;
import naga.util.Numbers;
import naga.util.async.Batch;

import java.lang.reflect.Array;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

/*
 * @author Bruno Salmon
 */

public class JsonCodecManager {

    private static final String CODEC_ID_KEY = "$codec";

    private static final Map<Class, JsonCodec> encoders = new HashMap<>();
    private static final Map<String, JsonCodec> decoders = new HashMap<>();

    public static <J> void registerJsonCodec(Class<? extends J> javaClass, JsonCodec<J> codec) {
        encoders.put(javaClass, codec);
        decoders.put(codec.getCodecId(), codec);
    }

    /* Not supported in J2ME CLDC
    public static void useSameJsonCodecAs(Class javaClass1, Class javaClass2) {
        registerJsonCodec(javaClass1, getJsonEncoder(javaClass2));
    } */

    public static <J> JsonCodec<J> getJsonEncoder(Class<J> javaClass) {
        return encoders.get(javaClass);
        /* getSuperclass() is not supported in J2ME CLDC
        for (Class c = javaClass; c != null; c = c.getSuperclass()) {
            JsonCodec<J> codec = encoders.get(c);
            if (codec != null)
                return codec;
        }
        return null;
        */
    }

    public static JsonCodec getJsonDecoder(String codecId) {
        return decoders.get(codecId);
    }

    public static Object encodeToJson(Object object) {
        // Keeping null and primitives as is
        if (object == null || object instanceof String || Numbers.isNumber(object) || object instanceof Boolean)
            return object;
        // Managing date objects (Instant, LocalDate and LocalDateTime)
        Instant instant = Dates.asInstant(object);
        if (instant == null) {
            LocalDateTime localDateTime = Dates.asLocalDateTime(object);
            if (localDateTime == null)
                localDateTime = Dates.toLocalDateTime(Dates.asLocalDate(object));
            if (localDateTime != null)
                instant = localDateTime.toInstant(ZoneOffset.UTC);
        }
        if (instant != null)
            return Dates.formatIso(instant);
        // Other java objects are serialized into json
        return encodeToJsonObject(object);
    }

    public static JsonObject encodeToJsonObject(Object object) {
        if (object == null)
            return null;
        if (object instanceof JsonObject)
            return (JsonObject) object;
        return encodeJavaObjectToJsonObject(object, Json.createObject());
    }

    private static WritableJsonObject encodeJavaObjectToJsonObject(Object javaObject, WritableJsonObject json) {
        JsonCodec encoder = getJsonEncoder(javaObject.getClass());
        if (encoder == null)
            throw new IllegalArgumentException("No JsonCodec for type: " + javaObject.getClass());
        json.set(CODEC_ID_KEY, encoder.getCodecId());
        encoder.encodeToJson(javaObject, json);
        return json;
    }

    public static <J> J decodeFromJsonObject(JsonObject json) {
        if (json == null)
            return null;
        String codecId = json.getString(CODEC_ID_KEY);
        JsonCodec<J> decoder = getJsonDecoder(codecId);
        if (decoder == null)
            throw new IllegalArgumentException("No JsonCodec found for id: '" + codecId + "' when trying to decode " + json.toJsonString());
        return decoder.decodeFromJson(json);
    }

    public static <J> J decodeFromJson(Object object) {
        if (object instanceof JsonObject)
            return decodeFromJsonObject((JsonObject) object);
        object = Dates.fastToInstantIfIsoString(object);
        return (J) object;
    }

    public static JsonArray encodePrimitiveArrayToJsonArray(Object[] primArray) {
        if (primArray == null)
            return null;
        WritableJsonArray ca = Json.createArray();
        for (Object value : primArray)
            ca.push(encodeToJson(value));
        return ca;
    }

    public static Object[] decodePrimitiveArrayFromJsonArray(JsonArray ca) {
        if (ca == null)
            return null;
        int n = ca.size();
        Object[] array = new Object[n];
        for (int i = 0; i < n; i++)
            array[i] = decodeFromJson(ca.getElement(i));
        return array;
    }

    public static JsonArray encodeToJsonArray(Object[] array) {
        if (array == null)
            return null;
        WritableJsonArray ca = Json.createArray();
        for (Object object : array)
            ca.push(encodeToJsonObject(object));
        return ca;
    }

    public static <A> A[] decodeFromJsonArray(JsonArray ca, Class<A> expectedClass) {
        if (ca == null)
            return null;
        int n = ca.size();
        A[] array = (A[]) Array.newInstance(expectedClass, n);
        for (int i = 0; i < n; i++)
            array[i] = decodeFromJson(ca.getObject(i));
        return array;
    }

    /****************************************************
     *          JsonCodec registration             *
     * *************************************************/

    // Batch json Serialization support

    private static String BATCH_CODEC_ID = "Batch";
    private static String BATCH_ARRAY_KEY = "array";

    static void registerBatchJsonCodec() {
        new AbstractJsonCodec<Batch>(Batch.class, BATCH_CODEC_ID) {

            @Override
            public void encodeToJson(Batch batch, WritableJsonObject json) {
                json.set(BATCH_ARRAY_KEY, encodeToJsonArray(batch.getArray()));
            }

            @Override
            public Batch decodeFromJson(JsonObject json) {
                JsonArray array = json.getArray(BATCH_ARRAY_KEY);
                Class contentClass = Object.class;
                if (array.size() > 0) {
                    switch (array.getObject(0).getString(JsonCodecManager.CODEC_ID_KEY)) {
                        case QueryResult.CODEC_ID:
                            contentClass = QueryResult.class;
                            break;
                        case QueryArgument.CODEC_ID:
                            contentClass = QueryArgument.class;
                            break;
                        case UpdateArgument.CODEC_ID:
                            contentClass = UpdateArgument.class;
                            break;
                        case UpdateResult.CODEC_ID:
                            contentClass = UpdateResult.class;
                            break;
                    }
                }
                return new Batch<>(decodeFromJsonArray(array, contentClass));
            }
        };
    }

    static {
        // Registering all required json codecs (especially for network bus calls)
        BusCallService.registerJsonCodecs();
        QueryArgument.registerJsonCodec();
        QueryResult.registerJsonCodec();
        QueryPushArgument.registerJsonCodec();
        QueryPushResult.registerJsonCodec();
        QueryResultTranslation.registerJsonCodec();
        UpdateArgument.registerJsonCodec();
        GeneratedKeyBatchIndex.registerJsonCodec();
        UpdateResult.registerJsonCodec();
        registerBatchJsonCodec();
    }
}