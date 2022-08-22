package com.example.blooddonar.hospital

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.example.blooddonar.R
import com.example.blooddonar.acceptor.AcceptorHomeActivity
import com.example.blooddonar.base.BaseActivity
import com.google.firebase.database.*
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.activity_hospital_availability.*
import java.util.concurrent.TimeUnit

class HospitalAvailabilityActivity : BaseActivity() {

    private var uid = ""
    private lateinit var database: DatabaseReference

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = getColor(R.color.transparent)
        window.navigationBarColor = getColor(R.color.black)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_availability)

        uid =  intent?.extras?.getString("uid") ?: ""


        getData(uid) {
            pBar(0)
            initListeners()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun getData(uid: String, completion: () -> Unit) {

        pBar(1)
        FirebaseDatabase
            .getInstance()
            .reference
            .child("hospitals")
            .child(uid.trim())
            .child("inventory")
            .get()
            .addOnSuccessListener {

                a_counter.text = it.child("aPos").value.toString() + " " + "Bags"
                a_neg_counter.text = it.child("aNeg").value.toString() + " " + "Bags"
                b_counter.text = it.child("bPos").value.toString() + " " + "Bags"
                b_neg_counter.text = it.child("bNeg").value.toString() + " " + "Bags"
                o_counter.text = it.child("oPos").value.toString() + " " + "Bags"
                o_neg_counter.text = it.child("oNeg").value.toString() + " " + "Bags"
                ab_counter.text = it.child("abPos").value.toString() + " " + "Bags"
                ab_neg_counter.text = it.child("abNeg").value.toString() + " " + "Bags"
                completion()
            }

    }

    @SuppressLint("CheckResult")
    private fun initListeners() {
        RxView.clicks(back).throttleFirst(2, TimeUnit.SECONDS).subscribe {

            startActivity(this, AcceptorHomeActivity::class.java, true, -1)
        }
    }
}