package com.example.blooddonar.models

import com.google.gson.annotations.SerializedName

data class HospitalReqModel(
    @SerializedName("address")
    var address: String = "",
    @SerializedName("blood")
    var blood: String = "",
    @SerializedName("city")
    var city: String = "",
    @SerializedName("hospital")
    var hospital: String = "",
    @SerializedName("phoneNumber")
    var phoneNumber: String = "",
    @SerializedName("req")
    var req: String = "",
    @SerializedName("type")
    var type: String = "",
    @SerializedName("uid")
    var uid: String = "",
)
