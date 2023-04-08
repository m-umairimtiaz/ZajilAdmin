package com.example.zajiladmin.api

import com.example.zajiladmin.models.VerifyEmailDataModel
import com.example.zajiladmin.models.VerifySendModel
import com.example.zajiladmin.utils.Constants
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface KemsApi {

    @POST(Constants.BASE_URL + "users/verifyemail")
    suspend fun getVerifyEmail(
        @Body
        verifySendModel: VerifySendModel
    ): Response<VerifyEmailDataModel?>


}