package webfx.platforms.core.services.bus.call;

import webfx.platforms.core.services.json.JsonObject;
import webfx.platforms.core.services.json.WritableJsonObject;
import webfx.platforms.core.services.json.codec.AbstractJsonCodec;
import webfx.platforms.core.services.json.codec.JsonCodecManager;


/*
 * @author Bruno Salmon
 */
class BusCallArgument {

    private static int SEQ = 0;

    private final String targetAddress;
    private final Object targetArgument;
    private final int callNumber;

    private Object jsonEncodedTargetArgument; // can be a JsonObject or simply a scalar

    public BusCallArgument(String targetAddress, Object targetArgument) {
        this(targetAddress, targetArgument, ++SEQ);
    }

    private BusCallArgument(String targetAddress, Object targetArgument, int callNumber) {
        this.targetAddress = targetAddress;
        this.targetArgument = targetArgument;
        this.callNumber = callNumber;
    }

    public String getTargetAddress() {
        return targetAddress;
    }

    public Object getTargetArgument() {
        return targetArgument;
    }

    public int getCallNumber() {
        return callNumber;
    }

    Object getJsonEncodedTargetArgument() {
        if (jsonEncodedTargetArgument == null && targetArgument != null)
            jsonEncodedTargetArgument = JsonCodecManager.encodeToJson(targetArgument);
        return jsonEncodedTargetArgument;
    }

    /****************************************************
     *                    Json Codec                    *
     * *************************************************/

    private static String CODEC_ID = "call";
    private static String CALL_NUMBER_KEY = "seq";
    private static String TARGET_ADDRESS_KEY = "addr";
    private static String TARGET_ARGUMENT_KEY = "arg";

    public static void registerJsonCodec() {
        new AbstractJsonCodec<BusCallArgument>(BusCallArgument.class, CODEC_ID) {

            @Override
            public void encodeToJson(BusCallArgument call, WritableJsonObject json) {
                json.set(CALL_NUMBER_KEY, call.callNumber)
                    .set(TARGET_ADDRESS_KEY, call.getTargetAddress())
                    .set(TARGET_ARGUMENT_KEY, call.getJsonEncodedTargetArgument());
            }

            @Override
            public BusCallArgument decodeFromJson(JsonObject json) {
                return new BusCallArgument(
                        json.getString(TARGET_ADDRESS_KEY),
                        JsonCodecManager.decodeFromJson(json.get(TARGET_ARGUMENT_KEY)),
                        json.getInteger(CALL_NUMBER_KEY)
                );
            }
        };
    }

}