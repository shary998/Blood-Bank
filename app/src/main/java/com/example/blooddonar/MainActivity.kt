package com.example.blooddonar


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.example.blooddonar.acceptor.AcceptorHomeActivity
import com.example.blooddonar.admin.AdminHomeActivity
import com.example.blooddonar.base.BaseActivity
import com.example.blooddonar.donar.DonarHomeActivity
import com.example.blooddonar.hospital.HospitalHomeActivity
import com.example.blooddonar.sharedpref.MySharedPreference
import com.example.blooddonar.start.LoginActivity2
import com.google.firebase.auth.FirebaseAuth


class MainActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        Handler().postDelayed({
            openNextActivity()
        }, 3000)
    }

    private fun openNextActivity() {

        if (auth.currentUser == null) {
            val intent = Intent(this, LoginActivity2::class.java)
            startActivity(intent)
            finish()
        } else {
            checkUser({
                startActivity(this, DonarHomeActivity::class.java, true, 1)
            }, {
                startActivity(this, AcceptorHomeActivity::class.java, true, 1)
            }, {
                startActivity(this, HospitalHomeActivity::class.java, true, 1)
            }, {
                startActivity(this, AdminHomeActivity::class.java, true, 1)
            })
        }
    }

    private fun checkUser(
        donor: () -> Unit,
        acceptor: () -> Unit,
        hospital: () -> Unit,
        admin: () -> Unit
    ) {

        when (MySharedPreference(this).getUserType()?.type) {
            "Donor" -> {
                donor()
            }
            "Acceptor" -> {
                acceptor()
            }
            "Hospital" -> {
                hospital()
            }
            "Admin" -> {
                admin()
            }
            else -> {
                warningToast("Something Went Wrong!")
            }
        }

    }

}
