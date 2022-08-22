package com.example.blooddonar.models

import com.google.gson.annotations.SerializedName

data class InventoryModel(

    @SerializedName("aNeg")
    var aNeg: String = "",
    @SerializedName("aPos")
    var aPos: String = "",
    @SerializedName("abNeg")
    var abNeg: String = "",
    @SerializedName("abPos")
    var abPos: String = "",
    @SerializedName("bNeg")
    var bNeg: String = "",
    @SerializedName("bPos")
    var bPos: String = "",
    @SerializedName("oNeg")
    var oNeg: String = "",
    @SerializedName("oPos")
    var oPos: String = "",
)