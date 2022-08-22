package com.example.blooddonar.models

import com.google.gson.annotations.SerializedName

data class AdminModel(
    @SerializedName("email")
    var email: String = "",
    @SerializedName("firstName")
    var firstName: String = "",
    @SerializedName("lastName")
    var lastName: String = "",
    @SerializedName("phoneNumber")
    var phoneNumber: String = "",
    @SerializedName("type")
    var type: String = "",
    @SerializedName("uid")
    var uid: String = "",
)