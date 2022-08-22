package com.example.blooddonar.hospital

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.example.blooddonar.R
import com.example.blooddonar.base.BaseActivity
import com.example.blooddonar.sharedpref.MySharedPreference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.jakewharton.rxbinding2.view.RxView
import com.skydoves.powermenu.PowerMenuItem
import kotlinx.android.synthetic.main.activity_acceptor_home.*
import kotlinx.android.synthetic.main.activity_required_blood.*
import kotlinx.android.synthetic.main.activity_required_blood.cont_req
import kotlinx.android.synthetic.main.activity_required_blood.et_hospital
import kotlinx.android.synthetic.main.activity_required_blood.et_req
import kotlinx.android.synthetic.main.activity_required_blood.login_submit
import java.util.HashMap
import java.util.concurrent.TimeUnit

class RequiredBloodActivity : BaseActivity() {

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
        setContentView(R.layout.activity_required_blood)


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

            startActivity(this, HospitalHomeActivity::class.java, true, -1)
        }

        RxView.clicks(login_submit).throttleFirst(2, TimeUnit.SECONDS).subscribe {
            if (validateForm()) {
                sendRequest()
            }
        }
    }

    private fun validateForm(): Boolean {

        var blood = et_blood.text.toString().trim()
        var req = et_req.text.toString().trim()
        var hospital = et_hospital.text.toString().trim()
        var city = et_city.text.toString().trim()


        if (blood.isEmpty() || blood.isBlank()) {
            et_blood_form.requestFocus()
            warningToast("Invalid Blood Group")
            return false
        }

        if (req.isEmpty() || req.isBlank()) {
            et_blood_form.requestFocus()
            warningToast("Invalid Blood Group")
            return false
        }


        if (hospital.isBlank() || hospital.isEmpty()) {
            et_hospital.requestFocus()
            warningToast("Hospital should contain only characters")
            return false
        }

        if (city.isBlank() || city.isEmpty()) {
            et_city_form.requestFocus()
            warningToast("City should contain only characters")
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
        var type = MySharedPreference(this).getHospitalObject()?.type
        var number = MySharedPreference(this).getHospitalObject()?.phoneNumber
        var address =
            MySharedPreference(this).getHospitalObject()?.address?.capitalize()


        var requiredMap = HashMap<String, String>()

        requiredMap["address"] = address.toString()
        requiredMap["phoneNumber"] = number.toString()
        requiredMap["city"] = city
        requiredMap["hospital"] = hospital
        requiredMap["req"] = req
        requiredMap["blood"] = blood
        requiredMap["type"] = type.toString()
        requiredMap["uid"] = FirebaseAuth.getInstance().currentUser?.uid.toString()

        FirebaseAuth.getInstance().currentUser?.uid?.let {
            FirebaseDatabase
                .getInstance()
                .reference
                .child("Requirements")
                .child("Hospitals")
                .child(it)
                .setValue(requiredMap)
                .addOnSuccessListener {
                    pBar(0)
                    apiToast("Successfully Sent") {
                        startActivity(this, HospitalHomeActivity::class.java, true, -1)
                    }


                }
        }
    }

}