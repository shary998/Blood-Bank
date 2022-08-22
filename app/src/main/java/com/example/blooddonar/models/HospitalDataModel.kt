package com.example.blooddonar.models

import com.google.gson.annotations.SerializedName

data class HospitalDataModel(

    @SerializedName("address")
    var address: String = "",
    @SerializedName("city")
    var city: String = "",
    @SerializedName("email")
    var email: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("phoneNumber")
    var phoneNumber: String = "",
    @SerializedName("type")
    var type: String = "",
    @SerializedName("uid")
    var uid: String = "",
)