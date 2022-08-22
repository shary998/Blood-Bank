package com.example.blooddonar.start

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
import com.example.blooddonar.base.BaseActivity
import com.jakewharton.rxbinding2.view.RxView
import com.skydoves.powermenu.PowerMenuItem
import kotlinx.android.synthetic.main.activity_forgot_password.*
import java.util.concurrent.TimeUnit
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.android.synthetic.main.activity_forgot_password.cont_type
import kotlinx.android.synthetic.main.activity_forgot_password.et_email
import kotlinx.android.synthetic.main.activity_forgot_password.et_type
import kotlinx.android.synthetic.main.activity_login2.*


class ForgotPasswordActivity : BaseActivity() {

    private var userType = ""
    var auth = FirebaseAuth.getInstance()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = getColor(R.color.transparent)
        window.navigationBarColor = getColor(R.color.black)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)


        initListeners()

    }

    @SuppressLint("CheckResult")
    private fun initListeners() {
        RxView.clicks(cont_type).throttleFirst(2, TimeUnit.SECONDS).subscribe {
            popupDisplay(
                this,
                cont_type,
                et_type,
                false,
                arrayListOf(
                    PowerMenuItem("Acceptor"),
                    PowerMenuItem("Donor"),
                    PowerMenuItem("Hospital"),
                ),
                POPUPDISPLAY_MATCHCONT,
                0
            ) { selectedText ->
                when (selectedText) {
                    "Acceptor" -> {
                        userType = "Acceptor"
                    }
                    "Donor" -> {
                        userType = "Donor"
                    }
                    "Hospital" -> {
                        userType = "Hospital"
                    }
                }
            }
        }

        RxView.clicks(login_continue).throttleFirst(2, TimeUnit.SECONDS).subscribe {
            if (validate()) {
                sendCodeToEmail()
            }

        }
    }

    private fun validate(): Boolean {
        var email = et_email.text.toString().trim()
        var type = et_type.text.toString().trim()

        if (email.isEmpty() || email.isBlank()) {
            Toast.makeText(this, "Email can not be empty", Toast.LENGTH_SHORT).show()
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email format doesn't match! ", Toast.LENGTH_SHORT).show()
            return false
        }
        if (type.isEmpty() || type.isBlank()) {
            et_type.requestFocus()
            warningToast("Invalid User Type")
            return false
        }
        return true
    }


    private fun sendCodeToEmail() {
        var email = et_email.text.toString().trim()

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    apiToast("Email Sent Successfully!") {
                        startActivity(this, LoginActivity2::class.java, true, -1)
                    }
                } else {
                    apiToast("Some Thing Went Wrong") {
                        startActivity(this, LoginActivity2::class.java, true, -1)
                    }
                }
            }
    }
}