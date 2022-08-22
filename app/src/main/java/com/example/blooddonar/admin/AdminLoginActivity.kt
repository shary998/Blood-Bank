package com.example.blooddonar.admin

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.blooddonar.R
import com.example.blooddonar.acceptor.AcceptorHomeActivity
import com.example.blooddonar.base.BaseActivity
import com.example.blooddonar.donar.DonarHomeActivity
import com.example.blooddonar.hospital.HospitalHomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.activity_admin_login.*
import kotlinx.android.synthetic.main.activity_admin_login.et_email
import kotlinx.android.synthetic.main.activity_admin_login.et_password
import kotlinx.android.synthetic.main.activity_admin_login.login_submit_button
import kotlinx.android.synthetic.main.activity_login2.*
import java.util.concurrent.TimeUnit

class AdminLoginActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = getColor(R.color.transparent)
        window.navigationBarColor = getColor(R.color.black)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)


        auth = FirebaseAuth.getInstance()
        initListeners()
    }

    @SuppressLint("CheckResult")
    private fun initListeners() {
        RxView.clicks(login_submit_button).throttleFirst(2, TimeUnit.SECONDS).subscribe {
            if (validate()) {
                getUserData()
            }
        }
    }

    private fun validate(): Boolean {

        var email = et_email.text.toString().trim()
        var pass = et_password.text.toString().trim()

        if (email.isEmpty() || email.isBlank()) {
            Toast.makeText(this, "Email can not be empty", Toast.LENGTH_SHORT).show()
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email format doesn't match! ", Toast.LENGTH_SHORT).show()
            return false
        }
        if (pass.isEmpty() || pass.isBlank()) {
            et_password.requestFocus()
            Toast.makeText(this, "Invalid Password", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun getUserData() {
        var vEmail = et_email.text.toString().trim()
        var vPass = et_password.text.toString().trim()
        pBar(1)
        auth.signInWithEmailAndPassword(vEmail, vPass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Firebase.auth.uid?.let {
                        FirebaseDatabase.getInstance()
                            .reference
                            .child("Admin")
                            .child(it)
                            .get()
                            .addOnSuccessListener {
                                pBar(0)
                                startActivity(this, AdminHomeActivity::class.java, true, 1)
                            }
                    }

                } else {
                    pBar(0)
                    Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show()
                }
            }
    }
}