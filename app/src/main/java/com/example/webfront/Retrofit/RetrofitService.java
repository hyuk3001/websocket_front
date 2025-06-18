package com.example.webfront.Retrofit;


import com.example.webfront.dto.SmsSendRequest;
import com.example.webfront.dto.SmsVerificationRequest;
import com.example.webfront.dto.UserOperationResponse;
import com.example.webfront.dto.UserRegistrationRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitService {

    @POST("sms/send")
    Call<Void> sendSms(@Body SmsSendRequest smsSendRequest);

    @POST("sms/verify")
    Call<Void> verifySms(@Body SmsVerificationRequest smsVerificationRequest);

    @POST("auth/signup")
    Call<UserOperationResponse> signup(@Body UserRegistrationRequest userRequest);
}