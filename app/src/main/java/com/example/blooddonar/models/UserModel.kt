package com.example.blooddonar.models

import com.google.gson.annotations.SerializedName

data class UserModel(
    @SerializedName("address")
    var address: String = "",
    @SerializedName("blood")
    var blood: String = "",
    @SerializedName("city")
    var city: String = "",
    @SerializedName("dob")
    var dob: String = "",
    @SerializedName("email")
    var email: String = "",
    @SerializedName("firstName")
    var firstName: String = "",
    @SerializedName("gender")
    var gender: String = "",
    @SerializedName("online")
    var online: String = "",
    @SerializedName("lastName")
    var lastName: String = "",
    @SerializedName("phoneNumber")
    var phoneNumber: String = "",
    @SerializedName("profileImage")
    var profileImage: String = "",
    @SerializedName("type")
    var type: String = "",
    @SerializedName("uid")
    var uid: String = "",

)
