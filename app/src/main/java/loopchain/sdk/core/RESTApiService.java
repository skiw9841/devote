package loopchain.sdk.core;

import loopchain.sdk.core.request.RequestData;
import loopchain.sdk.core.response.LCResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by js on 2018. 2. 15..
 */

public interface RESTApiService {

    @POST(Constants.LC_API_HEADER + Constants.LC_API_VERSION)
    Call<LCResponse> sendRequest(@Body RequestData requestData);
}
