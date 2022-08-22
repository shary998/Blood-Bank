package com.example.blooddonar.donar

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.blooddonar.R
import com.example.blooddonar.acceptor.AcceptorHomeActivity
import com.example.blooddonar.base.BaseActivity
import com.example.blooddonar.hospital.HospitalHomeActivity
import com.example.blooddonar.models.AvailableModel
import com.example.blooddonar.sharedpref.MySharedPreference
import com.example.blooddonar.start.LoginActivity2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.activity_donar_home.*
import kotlinx.android.synthetic.main.activity_donar_profile.*
import java.util.concurrent.TimeUnit

class DonarProfileActivity : BaseActivity() {

    private var type = ""

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = getColor(R.color.transparent)
        window.navigationBarColor = getColor(R.color.black)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donar_profile)


        checkUser {
            getUserData {
                pBar(0)
            }
        }


        initListeners()
        checkStatus()

    }

    @SuppressLint("SetTextI18n")
    private fun getUserData(completion: () -> Unit) {

        pBar(1)

        FirebaseDatabase
            .getInstance()
            .reference
            .child(type.trim())
            .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .get()
            .addOnSuccessListener {

                val circularProgressDrawable = CircularProgressDrawable(this)
                circularProgressDrawable.centerRadius = 25f
                circularProgressDrawable.start()

                if (it.child("profileImage").value == null) {
                    ic_profile.setImageResource(R.drawable.ic_user)
                } else {
                    Glide.with(this).load(it.child("profileImage").value)
                        .placeholder(circularProgressDrawable)
                        .into(iv_profile)
                }

                if (type == "Donor") {
                    tv_name.text =
                        MySharedPreference(this).getUserObject()?.firstName?.capitalize() + " " + MySharedPreference(
                            this
                        ).getUserObject()?.lastName?.capitalize()

                    tv_blood.text =
                        "Blood Group" + " " + MySharedPreference(this).getUserObject()?.blood?.capitalize()

                    Log.d(
                        "blood",
                        "Type: " + MySharedPreference(this).getUserObject()?.blood?.capitalize()
                    )

                    tv_email.text = MySharedPreference(this).getUserObject()?.email
                    tv_age.text = MySharedPreference(this).getUserObject()?.dob
                    tv_gender.text = MySharedPreference(this).getUserObject()?.gender?.capitalize()
                    tv_num.text = MySharedPreference(this).getUserObject()?.phoneNumber
                    switch_status.visibility = View.VISIBLE

                    completion()
                } else if (type == "Acceptor") {
                    tv_name.text =
                        MySharedPreference(this).getAcceptorObject()?.firstName?.capitalize() + " " + MySharedPreference(
                            this
                        ).getAcceptorObject()?.lastName?.capitalize()

                    tv_blood.text =
                        "Blood Group" + " " + MySharedPreference(this).getAcceptorObject()?.blood?.capitalize()

                    tv_email.text = MySharedPreference(this).getAcceptorObject()?.email
                    tv_age.text = MySharedPreference(this).getAcceptorObject()?.dob
                    tv_gender.text =
                        MySharedPreference(this).getAcceptorObject()?.gender?.capitalize()
                    tv_num.text = MySharedPreference(this).getAcceptorObject()?.phoneNumber
                    switch_status.visibility = View.GONE
                    completion()
                }

            }

    }

    private fun checkUser(completion: () -> Unit) {

        type = when (MySharedPreference(this).getUserType()?.type) {
            "Donor" -> {
                "Donor"

            }
            "Acceptor" -> {
                "Acceptor"
            }
            else -> {
                "Hospital"
            }
        }

        completion()
    }

    private fun checkStatus() {
        if (MySharedPreference(this).getAvailableObject()?.online == "true") {

            switch_status.text = "Available"
            switch_status.isChecked = true
        } else {
            switch_status.isChecked = false
            switch_status.text = "Not Available"
        }
    }

    @SuppressLint("CheckResult")
    private fun initListeners() {
        switch_status.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                FirebaseDatabase
                    .getInstance()
                    .reference
                    .child("Donor")
                    .child(Firebase.auth.currentUser?.uid.toString())
                    .child("online")
                    .setValue("true")
                    .addOnSuccessListener {
                        MySharedPreference(this).saveAvailableObject(
                            AvailableModel(
                                "true"
                            )
                        )
                        switch_status.text = "Available"
                    }

            } else {

                FirebaseDatabase
                    .getInstance()
                    .reference
                    .child("Donor")
                    .child(Firebase.auth.currentUser?.uid.toString())
                    .child("online")
                    .setValue("false")
                    .addOnSuccessListener {
                        MySharedPreference(this).saveAvailableObject(
                            AvailableModel(
                                "false"
                            )
                        )
                        switch_status.text = "Not Available"
                    }
            }

        }

        RxView.clicks(back).throttleFirst(2, TimeUnit.SECONDS).subscribe {
            onBackPressed()
        }

        RxView.clicks(login_submit_button).throttleFirst(2, TimeUnit.SECONDS).subscribe {
            pBar(1)
            startActivity(this, LoginActivity2::class.java, true, -1)
            FirebaseAuth.getInstance().signOut()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        when (type) {
            "Donor" -> {
                startActivity(this, DonarHomeActivity::class.java, true, -1)
            }
            "Acceptor" -> {
                startActivity(this, AcceptorHomeActivity::class.java, true, -1)
            }
            " Hospital" -> {
                startActivity(this, HospitalHomeActivity::class.java, true, -1)
            }
        }
    }
}