package com.example.blooddonar.admin

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.blooddonar.R
import com.example.blooddonar.base.BaseActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jakewharton.rxbinding2.view.RxView
import com.skydoves.powermenu.PowerMenuItem
import kotlinx.android.synthetic.main.activity_admin_user_update.*
import kotlinx.android.synthetic.main.activity_admin_user_update.cont_blood
import kotlinx.android.synthetic.main.activity_admin_user_update.cont_gender
import kotlinx.android.synthetic.main.activity_admin_user_update.et_blood
import kotlinx.android.synthetic.main.activity_admin_user_update.et_gender
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AdminUserUpdateActivity : BaseActivity() {

    private var uid = "0"
    private var type = "0"
    private var gender = ""
    private var bloodGroup = ""
    private var userType = ""
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
        setContentView(R.layout.activity_admin_user_update)


        uid = intent?.extras?.getString("uid") ?: "0"
        type = intent?.extras?.getString("type") ?: "0"


        Log.d("type", "UserType: $type")

        mDatabase = FirebaseDatabase.getInstance().reference.child(type)

        getData {
            pBar(0)
            initListeners()
        }

    }

    @SuppressLint("CheckResult")
    private fun initListeners() {
        RxView.clicks(cont_gender).throttleFirst(2, TimeUnit.SECONDS).subscribe {
            popupDisplay(
                this,
                cont_gender,
                et_gender,
                false,
                arrayListOf(
                    PowerMenuItem("Male"),
                    PowerMenuItem("Female"),
                ),
                POPUPDISPLAY_MATCHCONT,
                0
            ) { selectedText ->
                if (selectedText == "Male") {
                    gender = "male"
                } else if (selectedText == "Female") {
                    gender = "female"
                }
            }
        }

        RxView.clicks(cont_blood).throttleFirst(2, TimeUnit.SECONDS).subscribe {
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

        RxView.clicks(update_btn).throttleFirst(2, TimeUnit.SECONDS).subscribe {
            if (validate()) {
                updateUser {
                    pBar(0)
                    apiToast("Updated User Successfully") {
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
            .child(type)
            .child(uid.trim())
            .get()
            .addOnSuccessListener {

                val circularProgressDrawable = CircularProgressDrawable(this)
                circularProgressDrawable.centerRadius = 25f
                circularProgressDrawable.start()

                if (it.child("profileImage").value == null) {
                    profile.setImageResource(R.drawable.ic_user)
                } else {
                    Glide.with(this).load(it.child("profileImage").value)
                        .placeholder(circularProgressDrawable)
                        .into(profile)
                }

                et_first_name.setText(it.child("firstName").value.toString().capitalize())
                et_last_name.setText(it.child("lastName").value.toString().capitalize())
                et_dd.setText(it.child("dob").value.toString().split("-")[0])
                et_mm.setText(it.child("dob").value.toString().split("-")[1])
                et_yyyy.setText(it.child("dob").value.toString().split("-")[2])
                et_gender.setText(it.child("gender").value.toString().capitalize())
                et_phone_number.setText(it.child("phoneNumber").value.toString().capitalize())
                et_blood.setText(it.child("blood").value.toString().capitalize())
                et_address.setText(it.child("address").value.toString().capitalize())
                et_city.setText(it.child("city").value.toString().capitalize())

                completion()
            }

    }


    private fun validate(): Boolean {

        val dd = et_dd.text.toString()
        val mm = et_mm.text.toString()
        val yyyy = et_yyyy.text.toString()
        val currentDate = "$dd-$mm-$yyyy"
        val finalDate = getCurrentDate("dd-MM-yyyy")
        val date1: Date
        val date2: Date
        val dates = SimpleDateFormat("dd-MM-yyyy")
        date1 = dates.parse(currentDate)
        date2 = dates.parse(finalDate)
        val difference: Long = Math.abs(date1.time - date2.time)
        val differenceDates = difference / (24 * 60 * 60 * 1000)
        val year = differenceDates / 365
        val YearDifference = year

        var firstName = et_first_name.text.toString().trim()
        var lastName = et_last_name.text.toString().trim()
        var gender = et_gender.text.toString().trim()
        var blood = et_blood.text.toString().trim()
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

        if (gender.isEmpty() || gender.isBlank()) {
            et_gender.requestFocus()
            warningToast("Invalid Gender")
            return false
        }


        if (blood.isEmpty() || blood.isBlank()) {
            et_blood.requestFocus()
            warningToast("Invalid Blood Group")
            return false
        }


        if (!nameRegex(firstName)) {
            et_first_name.requestFocus()
            warningToast("Name should contain only characters")
            return false
        }

        if (!nameRegex(lastName)) {
            et_last_name.requestFocus()
            warningToast("Name should contain only characters")
            return false
        }

        if (dd.isEmpty() || dd.isBlank()) {
            et_dd.requestFocus()
            warningToast("Invalid Birth Day")
            return false
        }

        if (mm.isEmpty() || mm.isBlank()) {
            et_mm.requestFocus()
            warningToast("Invalid Birth Month")
            return false
        }

        if (yyyy.isEmpty() || yyyy.isBlank()) {
            et_yyyy.requestFocus()
            warningToast("Invalid Birth Year")
            return false
        }

        if (dd.length < 2) {
            et_dd.requestFocus()
            warningToast("Invalid Birth Day")
            return false
        }

        if (mm.length < 2) {
            et_mm.requestFocus()
            warningToast("Invalid Birth Month")
            return false
        }

        if (yyyy.length < 4) {
            et_yyyy.requestFocus()
            warningToast("Invalid Birth Year")
            return false
        }

        if (!isValidDate("$dd-$mm-$yyyy")) {
            warningToast("Invalid Date Of Birth")
            return false
        }

        if (YearDifference < 18) {
            warningToast("You Must Be 18 Years")
            return false
        }

        return true
    }

    private fun updateUser(completion: () -> Unit) {


        val fName = et_first_name.text.toString()
        val lName = et_last_name.text.toString()
        val date = et_dd.text.toString()
        val month = et_mm.text.toString()
        val year = et_yyyy.text.toString()
        val gender = et_gender.text.toString()
        val number = et_phone_number.text.toString()
        val bloodType = et_blood.text.toString()
        val address = et_address.text.toString()
        val city = et_city.text.toString()
        val dob = "$date-$month-$year"


        pBar(1)
        mDatabase
            ?.child(uid.trim())
            ?.child("firstName")
            ?.setValue(fName)
            ?.addOnSuccessListener {
                mDatabase
                    ?.child(uid.trim())
                    ?.child("lastName")
                    ?.setValue(lName)
                    ?.addOnSuccessListener {
                        mDatabase
                            ?.child(uid.trim())
                            ?.child("dob")
                            ?.setValue(dob)
                            ?.addOnSuccessListener {
                                mDatabase
                                    ?.child(uid.trim())
                                    ?.child("gender")
                                    ?.setValue(gender)
                                    ?.addOnSuccessListener {
                                        mDatabase
                                            ?.child(uid.trim())
                                            ?.child("phoneNumber")
                                            ?.setValue(number)
                                            ?.addOnSuccessListener {
                                                mDatabase
                                                    ?.child(uid.trim())
                                                    ?.child("blood")
                                                    ?.setValue(bloodType)
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
                    }
            }

    }


}