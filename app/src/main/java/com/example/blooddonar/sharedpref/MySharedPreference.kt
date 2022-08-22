package com.example.blooddonar.sharedpref

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.blooddonar.models.*
import com.google.gson.Gson

class MySharedPreference(var context: Context) {

    private var sp: SharedPreferences? = null

    fun saveAvailableObject(user: AvailableModel?): Boolean {
        val gson = Gson()
        val stringUser = gson.toJson(user)
        sp = context.getSharedPreferences("SHARED_FILE", Context.MODE_PRIVATE)
        return sp?.edit()?.putString("availableObj", stringUser)?.commit()!!
    }

    fun getAvailableObject(): AvailableModel? {
        sp = context.getSharedPreferences("SHARED_FILE", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sp?.getString("availableObj", "")
        return gson.fromJson(json, AvailableModel::class.java)
    }

    fun saveUerObject(user: UserModel?): Boolean {
        val gson = Gson()
        val stringUser = gson.toJson(user)
        sp = context.getSharedPreferences("SHARED_FILE", Context.MODE_PRIVATE)
        return sp?.edit()?.putString("userObj", stringUser)?.commit()!!
    }

    fun getUserObject(): UserModel? {
        sp = context.getSharedPreferences("SHARED_FILE", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sp?.getString("userObj", "")
        return gson.fromJson(json, UserModel::class.java)
    }


    fun saveAcceptorObject(user: AcceptorsModel?): Boolean {
        val gson = Gson()
        val stringUser = gson.toJson(user)
        sp = context.getSharedPreferences("SHARED_FILE", Context.MODE_PRIVATE)
        return sp?.edit()?.putString("acceptorObj", stringUser)?.commit()!!
    }

    fun getAcceptorObject(): AcceptorsModel? {
        sp = context.getSharedPreferences("SHARED_FILE", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sp?.getString("acceptorObj", "")
        return gson.fromJson(json, AcceptorsModel::class.java)
    }

    fun saveHospitalObject(hospital: HospitalDataModel?): Boolean {
        val gson = Gson()
        val stringUser = gson.toJson(hospital)
        sp = context.getSharedPreferences("SHARED_FILE", Context.MODE_PRIVATE)
        return sp?.edit()?.putString("hospitalObj", stringUser)?.commit()!!
    }

    fun getHospitalObject(): HospitalDataModel? {
        sp = context.getSharedPreferences("SHARED_FILE", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sp?.getString("hospitalObj", "")
        return gson.fromJson(json, HospitalDataModel::class.java)
    }


    fun saveUserType(user: UserTypeModel?): Boolean {
        val gson = Gson()
        val stringUser = gson.toJson(user)
        sp = context.getSharedPreferences("SHARED_FILE", Context.MODE_PRIVATE)
        return sp?.edit()?.putString("typeObj", stringUser)?.commit()!!
    }

    fun getUserType(): UserTypeModel? {
        sp = context.getSharedPreferences("SHARED_FILE", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sp?.getString("typeObj", "")
        return gson.fromJson(json, UserTypeModel::class.java)
    }

    fun saveAdminObject(user: AdminModel?): Boolean {
        val gson = Gson()
        val stringUser = gson.toJson(user)
        sp = context.getSharedPreferences("SHARED_FILE", Context.MODE_PRIVATE)
        return sp?.edit()?.putString("adminObj", stringUser)?.commit()!!
    }

    fun getAdminObject(): AdminModel? {
        sp = context.getSharedPreferences("SHARED_FILE", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sp?.getString("adminObj", "")
        return gson.fromJson(json, AdminModel::class.java)
    }

    /*fun saveSearchHistoryArrayList(history: ArrayList<String>) {
        val gson = Gson()
        val jsonString = gson.toJson(history)
        sp = context.getSharedPreferences("SHARED_FILE", Context.MODE_PRIVATE)
        sp!!.edit().putString("search_arraylist", jsonString).apply()
    }

    fun getSearchHistoryArrayList(): ArrayList<String> {
        var history: ArrayList<String> = ArrayList()
        sp = context.getSharedPreferences("SHARED_FILE", Context.MODE_PRIVATE)
        val gson = Gson()
        val listOfObjects =
            object : TypeToken<List<String?>?>() {}.type
        val json = sp!!.getString("search_arraylist", "")
        if (json != "")
            history = gson.fromJson(json, listOfObjects)
        return history
    }*/

    fun saveTemporaryId(value: String) {
        sp = context.getSharedPreferences("SHARED_FILE", Context.MODE_PRIVATE)
        sp?.edit()?.putString("temp_id", value)?.apply()
    }

    fun getTemporaryId(): String {
        sp = context.getSharedPreferences("SHARED_FILE", Context.MODE_PRIVATE)
        return sp?.getString("temp_id", "")!!
    }

    fun saveUserEmail(email: String) {
        sp = context.getSharedPreferences("LOGIN_FILE", Context.MODE_PRIVATE)
        sp?.edit()?.putString("email", email)?.apply()
    }

    fun getUserEmail(): String {
        sp = context.getSharedPreferences("LOGIN_FILE", Context.MODE_PRIVATE)
        return sp?.getString("email", "")!!
    }

    fun saveUserPassword(password: String) {
        sp = context.getSharedPreferences("LOGIN_FILE", Context.MODE_PRIVATE)
        sp?.edit()?.putString("password", password)?.apply()
    }

    fun getUserPassword(): String {
        sp = context.getSharedPreferences("LOGIN_FILE", Context.MODE_PRIVATE)
        return sp?.getString("password", "")!!
    }

    fun clearAllPreferences() {
        clearPreferences("SHARED_FILE")
    }

    private fun clearPreferences(pref: String?) {
        try {
            val prefs = context.getSharedPreferences(
                pref,
                Context.MODE_PRIVATE
            )
            val prefEditor = prefs.edit()
            prefEditor.clear().apply()
        } catch (e: Exception) {
            Log.i("", "Exception : $e")
        }
    }
}