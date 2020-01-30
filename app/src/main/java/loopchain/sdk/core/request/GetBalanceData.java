package loopchain.sdk.core.request;

import com.google.gson.JsonObject;
import loopchain.sdk.core.Constants;

public class GetBalanceData extends RequestData{

    public GetBalanceData(String id, String address) {
        this.method = Constants.METHOD_GETBALANCE;
        this.id = id;
        
        JsonObject params = new JsonObject();
		params.addProperty("address", address);

        this.params = params;
    }
}
