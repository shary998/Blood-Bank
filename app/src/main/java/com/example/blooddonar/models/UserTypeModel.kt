package com.example.blooddonar.models

import com.google.gson.annotations.SerializedName

data class UserTypeModel(
    @SerializedName("type")
    var type: String = "",
)
