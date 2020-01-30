package com.ad4th.devote.network;

import android.content.Context;

import com.ad4th.devote.model.AvailVo;
import com.ad4th.devote.model.EventVo;
import com.ad4th.devote.model.IntroVo;
import com.ad4th.devote.model.QrCodeVo;
import com.ad4th.devote.model.UserResVo;
import com.ad4th.devote.model.UserVo;
import com.ad4th.devote.model.VotingResultVo;
import com.ad4th.devote.model.VotingVo;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroClient {

    private RetroApiService apiService;
    public static String baseUrl = RetroApiService.Companion.getBase_URL();
    private static Context mContext;
    private static Retrofit retrofit;

    private static class SingletonHolder {
        private static RetroClient INSTANCE = new RetroClient(mContext);
    }

    public static RetroClient getInstance(Context context) {
        if (context != null) {
            mContext = context;
        }
        return SingletonHolder.INSTANCE;
    }

    private RetroClient(Context context) {

//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(1, TimeUnit.MINUTES)
//                .readTimeout(5, TimeUnit.SECONDS)
//                .writeTimeout(5, TimeUnit.SECONDS)
//                .build();

        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory
                        .create(new GsonBuilder()
                                .serializeNulls()
                                .setDateFormat("yyyy-MM-dd'T'hh:mm:ss")
                                .create()))

                .baseUrl(baseUrl)

//                .client(okHttpClient)
                .build();

    }

    public RetroClient createBaseApi() {
        apiService = create(RetroApiService.class);
        return this;
    }

    /**
     * create you ApiService
     * Create an implementation of the API endpoints defined by the {@code service} interface.
     */
    public <T> T create(final Class<T> service) {
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }
        return retrofit.create(service);
    }

    /**
     * init 데이터
     */
    public void getSelectIntro(String version, final RetroCallback callback) {
        apiService.getSelectIntro(version).enqueue(new Callback<IntroVo>() {
            @Override
            public void onResponse(Call<IntroVo> call, Response<IntroVo> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.code(), response.body());
                } else {
                    callback.onFailure(response.code());
                }
            }

            @Override
            public void onFailure(Call<IntroVo> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    /**
     * 사용자 등록
     */
    public void postInsertUser(String url, UserVo userVo, final RetroCallback callback) {

        apiService.postInsertUser(url, userVo).enqueue(new Callback<UserResVo>() {
            @Override
            public void onResponse(Call<UserResVo> call, Response<UserResVo> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.code(), response.body());
                } else {
                    callback.onFailure(response.code());
                }
            }

            @Override
            public void onFailure(Call<UserResVo> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    /**
     * 투표
     */
    public void postInsertVotings(String url, VotingVo votingVo, final RetroCallback callback) {

        apiService.postInsertVotings(url, votingVo).enqueue(new Callback<VotingVo>() {
            @Override
            public void onResponse(Call<VotingVo> call, Response<VotingVo> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.code(), response.body());
                } else {
                    callback.onFailure(response.code());
                }
            }

            @Override
            public void onFailure(Call<VotingVo> call, Throwable t) {
                callback.onError(t);
            }
        });
    }


    /**
     * 투표 가능 상태
     */
    public void postInsertVotingsAvail(String url, AvailVo availVo, final RetroCallback callback) {

        apiService.postInsertVotingsAvail(url, availVo).enqueue(new Callback<AvailVo>() {
            @Override
            public void onResponse(Call<AvailVo> call, Response<AvailVo> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.code(), response.body());
                } else {
                    callback.onFailure(response.code());
                }
            }

            @Override
            public void onFailure(Call<AvailVo> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    /**
     * 이벤트 조회
     */
    public void getSelectEvent(String url, final RetroCallback callback) {

        apiService.getSelectEvent(url).enqueue(new Callback<EventVo>() {
            @Override
            public void onResponse(Call<EventVo> call, Response<EventVo> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.code(), response.body());
                } else {
                    callback.onFailure(response.code());
                }
            }

            @Override
            public void onFailure(Call<EventVo> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    /**
     * 이벤트 결과
     */
    public void getSelectListEventResult(String url, final RetroCallback callback) {

        apiService.getSelectListEventResult(url).enqueue(new Callback<List<VotingResultVo>>() {
            @Override
            public void onResponse(Call<List<VotingResultVo>> call, Response<List<VotingResultVo>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.code(), response.body());
                } else {
                    callback.onFailure(response.code());
                }
            }

            @Override
            public void onFailure(Call<List<VotingResultVo>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    /**
     * QR코드 Validation 체크
     */
    public void postValidationWithRegeditQrCode(QrCodeVo qrCodeVo, final RetroCallback callback) {

        apiService.postValidationWithRegeditQrCode(qrCodeVo).enqueue(new Callback<QrCodeVo>() {
            @Override
            public void onResponse(Call<QrCodeVo> call, Response<QrCodeVo> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.code(), response.body());
                } else {
                    callback.onFailure(response.code());
                }
            }

            @Override
            public void onFailure(Call<QrCodeVo> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

}