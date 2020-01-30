package loopchain.sdk.core;

import loopchain.sdk.core.request.RequestData;
import loopchain.sdk.core.response.LCResponse;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;


public class RESTClient {

    private RESTApiService loopchainService;

    public RESTClient(String host) throws Exception {
        OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {

                okhttp3.Request original = chain.request();

                okhttp3.Request request = original.newBuilder()
                        .addHeader("Content-Type", "application/json; charset=utf-8")
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            }
        }).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(host)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        loopchainService = retrofit.create(RESTApiService.class);
    }

    public Call<LCResponse> sendRequest(RequestData reqData) throws IOException {
        Call<LCResponse> response = loopchainService.sendRequest(reqData);
        return response;
    }
}
