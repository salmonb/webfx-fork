package naga.platform.services.update;

import naga.util.Arrays;
import naga.platform.services.json.codec.AbstractJsonCodec;
import naga.platform.services.json.codec.JsonCodecManager;
import naga.platform.services.json.JsonObject;
import naga.platform.services.json.WritableJsonObject;

/**
 * @author Bruno Salmon
 */
public class UpdateResult {

    private final int rowCount;
    private final Object[] generatedKeys;

    public UpdateResult(int rowCount, Object[] generatedKeys) {
        this.rowCount = rowCount;
        this.generatedKeys = generatedKeys;
    }

    public int getRowCount() {
        return rowCount;
    }

    public Object[] getGeneratedKeys() {
        return generatedKeys;
    }

    /****************************************************
     *                    Json Codec                    *
     * *************************************************/

    public final static String CODEC_ID = "UpdateRes";
    private final static String ROW_COUNT_KEY = "update";
    private final static String GENERATED_KEYS_KEY = "genKeys";

    public static void registerJsonCodec() {
        new AbstractJsonCodec<UpdateResult>(UpdateResult.class, CODEC_ID) {

            @Override
            public void encodeToJson(UpdateResult arg, WritableJsonObject json) {
                json.set(ROW_COUNT_KEY, arg.getRowCount());
                if (!Arrays.isEmpty(arg.getGeneratedKeys()))
                    json.set(GENERATED_KEYS_KEY, JsonCodecManager.encodePrimitiveArrayToJsonArray(arg.getGeneratedKeys()));
            }

            @Override
            public UpdateResult decodeFromJson(JsonObject json) {
                return new UpdateResult(
                        json.getInteger(ROW_COUNT_KEY),
                        JsonCodecManager.decodePrimitiveArrayFromJsonArray(json.getArray(GENERATED_KEYS_KEY))
                );
            }
        };
    }

}