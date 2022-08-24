package com.example.blooddonar.hospital

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.example.blooddonar.R
import com.example.blooddonar.base.BaseActivity
import com.example.blooddonar.sharedpref.MySharedPreference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.jakewharton.rxbinding2.view.RxView
import com.skydoves.powermenu.PowerMenuItem
import kotlinx.android.synthetic.main.activity_required_inventory_update.*
import java.util.HashMap
import java.util.concurrent.TimeUnit

class RequiredInventoryUpdateActivity : BaseActivity() {

    private var uid = ""
    private var bloodGroup = ""
    private var required = ""

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = getColor(R.color.transparent)
        window.navigationBarColor = getColor(R.color.black)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_required_inventory_update)

        uid = intent?.extras?.getString("uid") ?: ""

        hospital_name.text = MySharedPreference(this).getHospitalObject()?.name.toString()

        getData()
        initListeners()
    }

    @SuppressLint("CheckResult")
    private fun initListeners() {
        RxView.clicks(cont_blood).throttleFirst(1, TimeUnit.SECONDS).subscribe {
            popupDisplay(
                this,
                cont_blood,
                et_blood,
                false,
                arrayListOf(
                    PowerMenuItem("A+"),
                    PowerMenuItem("A-"),
                    PowerMenuItem("B+"),
                    PowerMenuItem("B-"),
                    PowerMenuItem("O+"),
                    PowerMenuItem("O-"),
                    PowerMenuItem("AB+"),
                    PowerMenuItem("AB-"),

                    ),
                POPUPDISPLAY_MATCHCONT,
                0
            ) { selectedText ->
                if (selectedText == "A+") {
                    bloodGroup = "A+"
                } else if (selectedText == "A-") {
                    bloodGroup = "A-"
                } else if (selectedText == "B+") {
                    bloodGroup = "B+"
                } else if (selectedText == "B-") {
                    bloodGroup = "B-"
                } else if (selectedText == "O+") {
                    bloodGroup = "O+"
                } else if (selectedText == "O-") {
                    bloodGroup = "O-"
                } else if (selectedText == "AB+") {
                    bloodGroup = "AB+"
                } else if (selectedText == "AB-") {
                    bloodGroup = "AB-"
                }
            }
        }

        RxView.clicks(cont_req).throttleFirst(1, TimeUnit.SECONDS).subscribe {
            popupDisplay(
                this,
                cont_req,
                et_req,
                false,
                arrayListOf(
                    PowerMenuItem("Required Urgent"),
                    PowerMenuItem("Required"),
                ),
                POPUPDISPLAY_MATCHCONT,
                0
            ) { selectedText ->
                if (selectedText == "Required Urgent") {
                    required = "Required Urgent"
                } else if (selectedText == "Required") {
                    required = "Required"
                }
            }
        }

        RxView.clicks(back).throttleFirst(2, TimeUnit.SECONDS).subscribe {
            onBackPressed()
        }

        RxView.clicks(update).throttleFirst(2,TimeUnit.SECONDS).subscribe {
            if (validateForm()){
                sendRequest()
            }else{
                warningToast("Something Went Wrong")
            }
        }
    }

    private fun getData() {

        pBar(1)

        FirebaseDatabase
            .getInstance()
            .reference
            .child("Requirements")
            .child("Hospitals")
            .child(Firebase.auth.currentUser?.uid.toString())
            .child(uid.trim())
            .get()
            .addOnSuccessListener {

                et_blood.setText(it.child("blood").value.toString().capitalize())
                et_req.setText(it.child("req").value.toString().capitalize())
                et_hospital.setText(it.child("name").value.toString().capitalize())
                et_city.setText(it.child("city").value.toString().capitalize())
                et_bags.setText(it.child("bags").value.toString().capitalize())


                pBar(0)
            }

    }

    private fun validateForm(): Boolean {

        var blood = et_blood.text.toString().trim()
        var req = et_req.text.toString().trim()
        var hospital = et_hospital.text.toString().trim()
        var city = et_city.text.toString().trim()
        var bags = et_bags.text.toString().trim()


        if (blood.isEmpty() || blood.isBlank()) {
            et_blood.requestFocus()
            warningToast("Invalid Blood Group")
            return false
        }

        if (req.isEmpty() || req.isBlank()) {
            et_blood.requestFocus()
            warningToast("Invalid Blood Group")
            return false
        }


        if (hospital.isBlank() || hospital.isEmpty()) {
            et_hospital.requestFocus()
            warningToast("Hospital should contain only characters")
            return false
        }

        if (city.isBlank() || city.isEmpty()) {
            et_city.requestFocus()
            warningToast("City should contain only characters")
            return false
        }
        if (bags.isBlank() || bags.isEmpty()) {
            et_bags.requestFocus()
            warningToast("Bags field can not be empty")
            return false
        }

        return true
    }

    private fun sendRequest() {
        pBar(1)

        Log.d("blood", "blood: " + "abcd")
        var blood = et_blood.text.toString().trim()
        var req = et_req.text.toString().trim()
        var hospital = et_hospital.text.toString().trim()
        var city = et_city.text.toString().trim()
        var bags = et_bags.text.toString().trim()
        var type = MySharedPreference(this).getHospitalObject()?.type
        var number = MySharedPreference(this).getHospitalObject()?.phoneNumber
        var address =
            MySharedPreference(this).getHospitalObject()?.address?.capitalize()
        var name =
            MySharedPreference(this).getHospitalObject()?.name?.capitalize()


        var requiredMap = HashMap<String, String>()

        requiredMap["address"] = address.toString()
        requiredMap["phoneNumber"] = number.toString()
        requiredMap["city"] = city
        requiredMap["hospital"] = hospital
        requiredMap["name"] = name.toString()
        requiredMap["req"] = req
        requiredMap["blood"] = blood
        requiredMap["bags"] = bags
        requiredMap["type"] = type.toString()
        requiredMap["uid"] = uid



        FirebaseAuth.getInstance().currentUser?.uid?.let {
            FirebaseDatabase
                .getInstance()
                .reference
                .child("Requirements")
                .child("Hospitals")
                .child(it)
                .child(uid.trim())
                .setValue(requiredMap)
                .addOnSuccessListener {
                    pBar(0)
                    apiToast("Successfully Updated") {
                        startActivity(this, HospitalHomeActivity::class.java, true, -1)
                    }


                }
        }
    }

}