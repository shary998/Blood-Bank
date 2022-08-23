package com.example.blooddonar.admin


import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.example.blooddonar.R
import com.example.blooddonar.base.BaseActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.activity_update_hospital.*
import kotlinx.android.synthetic.main.activity_update_hospital.et_address
import kotlinx.android.synthetic.main.activity_update_hospital.et_city
import kotlinx.android.synthetic.main.activity_update_hospital.et_phone_number
import java.util.concurrent.TimeUnit

class UpdateHospitalActivity : BaseActivity() {

    private var uid = "0"
    private var mDatabase: DatabaseReference? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = getColor(R.color.transparent)
        window.navigationBarColor = getColor(R.color.black)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_hospital)


        uid = intent?.extras?.getString("uid") ?: "0"
        mDatabase = FirebaseDatabase.getInstance().reference.child("hospital")

        getData {
            pBar(0)
        }

        initListeners()
    }

    @SuppressLint("CheckResult")
    private fun initListeners() {
        RxView.clicks(update_btn).throttleFirst(2, TimeUnit.SECONDS).subscribe {
            if (validate()) {
                updateUser {
                    pBar(0)
                    apiToast("Updated Hospital Successfully") {
                        startActivity(this, AdminHomeActivity::class.java, true, -1)
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getData(completion: () -> Unit) {

        pBar(1)

        FirebaseDatabase
            .getInstance()
            .reference
            .child("hospital")
            .child(uid.trim())
            .get()
            .addOnSuccessListener {


                et_name.setText(it.child("name").value.toString().capitalize())
                et_phone_number.setText(it.child("phoneNumber").value.toString().capitalize())
                et_address.setText(it.child("address").value.toString().capitalize())
                et_city.setText(it.child("city").value.toString().capitalize())

                completion()
            }

    }

    private fun validate(): Boolean {

        var name = et_name.text.toString().trim()
        var phone = et_phone_number.text.toString().trim()
        var address = et_address.text.toString().trim()
        var city = et_city.text.toString().trim()

        if (city.isEmpty() || city.isBlank()) {
            et_address.requestFocus()
            warningToast("Invalid Address")
        }

        if (address.isEmpty() || address.isBlank()) {
            et_city.requestFocus()
            warningToast("Invalid City")
        }


        if (phone.isBlank() || phone.isEmpty()) {
            et_phone_number.requestFocus()
            warningToast("Invalid Phone Number")
            return false
        }

        if (phone.isBlank() || phone.isEmpty()) {
            et_phone_number.requestFocus()
            warningToast("Invalid Phone Number")
            return false
        }

        if (name.isEmpty() || name.isBlank()) {
            et_name.requestFocus()
            warningToast("Name Field Can Not Be Empty")
        }

        return true
    }

    private fun updateUser(completion: () -> Unit) {

        var name = et_name.text.toString().trim()
        var phone = et_phone_number.text.toString().trim()
        var address = et_address.text.toString().trim()
        var city = et_city.text.toString().trim()

        pBar(1)

        mDatabase
            ?.child(uid.trim())
            ?.child("name")
            ?.setValue(name)
            ?.addOnSuccessListener {
                mDatabase
                    ?.child(uid.trim())
                    ?.child("phoneNumber")
                    ?.setValue(phone)
                    ?.addOnSuccessListener {
                        mDatabase
                            ?.child(uid.trim())
                            ?.child("address")
                            ?.setValue(address)
                            ?.addOnSuccessListener {
                                mDatabase
                                    ?.child(uid.trim())
                                    ?.child("city")
                                    ?.setValue(city)
                                    ?.addOnSuccessListener {

                                        completion()
                                    }
                            }
                    }
            }
    }
}