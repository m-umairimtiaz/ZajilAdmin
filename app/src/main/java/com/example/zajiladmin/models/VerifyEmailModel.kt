package com.example.zajiladmin.models

import com.google.gson.annotations.SerializedName


data class VerifyEmailDataModel (

    @SerializedName("status"  ) var status  : Int?    = null,
    @SerializedName("message" ) var message : String? = null,
    @SerializedName("data"    ) var data    : VerifyEmailModel?   = VerifyEmailModel()

)

data class VerifyEmailModel (

    @SerializedName("otpCode" ) var otpCode : String? = null

)