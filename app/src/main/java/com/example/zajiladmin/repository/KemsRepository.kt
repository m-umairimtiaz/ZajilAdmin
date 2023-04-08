package com.example.zajiladmin.repository

import android.util.ArrayMap
import android.util.Log
import com.example.zajiladmin.api.RetrofitInstance
import com.example.zajiladmin.models.VerifyEmailDataModel
import com.example.zajiladmin.models.VerifySendModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response

class KemsRepository {

    suspend fun getVerifyEmail(email: String): Response<VerifyEmailDataModel?> {
       var body = VerifySendModel(email)

      /*  val jsonParams: MutableMap<String?, Any?> = ArrayMap()
        jsonParams["email"] = email

        val body = JSONObject(jsonParams).toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
*/
      return  RetrofitInstance.api.getVerifyEmail(body)

    }

}