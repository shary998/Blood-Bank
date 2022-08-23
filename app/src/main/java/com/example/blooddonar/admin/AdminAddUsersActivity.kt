package com.example.blooddonar.admin

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.blooddonar.R
import com.example.blooddonar.base.BaseActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.jakewharton.rxbinding2.view.RxView
import com.skydoves.powermenu.PowerMenuItem
import kotlinx.android.synthetic.main.activity_admin_add_users.*
import kotlinx.android.synthetic.main.activity_admin_add_users.cont_blood
import kotlinx.android.synthetic.main.activity_admin_add_users.cont_gender
import kotlinx.android.synthetic.main.activity_admin_add_users.cont_hos_type
import kotlinx.android.synthetic.main.activity_admin_add_users.cont_type
import kotlinx.android.synthetic.main.activity_admin_add_users.et_address
import kotlinx.android.synthetic.main.activity_admin_add_users.et_blood
import kotlinx.android.synthetic.main.activity_admin_add_users.et_city
import kotlinx.android.synthetic.main.activity_admin_add_users.et_confirm_password
import kotlinx.android.synthetic.main.activity_admin_add_users.et_create_password
import kotlinx.android.synthetic.main.activity_admin_add_users.et_dd
import kotlinx.android.synthetic.main.activity_admin_add_users.et_email
import kotlinx.android.synthetic.main.activity_admin_add_users.et_first_name
import kotlinx.android.synthetic.main.activity_admin_add_users.et_gender
import kotlinx.android.synthetic.main.activity_admin_add_users.et_hos_type
import kotlinx.android.synthetic.main.activity_admin_add_users.et_last_name
import kotlinx.android.synthetic.main.activity_admin_add_users.et_mm
import kotlinx.android.synthetic.main.activity_admin_add_users.et_phone_number
import kotlinx.android.synthetic.main.activity_admin_add_users.et_type
import kotlinx.android.synthetic.main.activity_admin_add_users.et_yyyy
import kotlinx.android.synthetic.main.activity_admin_add_users.hospital
import kotlinx.android.synthetic.main.activity_admin_add_users.login_submit_button
import kotlinx.android.synthetic.main.activity_admin_add_users.user
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import com.example.blooddonar.hospital.HospitalHomeActivity
import kotlinx.android.synthetic.main.activity_admin_add_users.et_confirm_pass
import kotlinx.android.synthetic.main.activity_admin_add_users.et_hospital_address
import kotlinx.android.synthetic.main.activity_admin_add_users.et_hospital_city
import kotlinx.android.synthetic.main.activity_admin_add_users.et_hospital_email
import kotlinx.android.synthetic.main.activity_admin_add_users.et_hospital_password
import kotlinx.android.synthetic.main.activity_admin_add_users.et_name
import kotlinx.android.synthetic.main.activity_admin_add_users.et_number


class AdminAddUsersActivity : BaseActivity() {

    private var gender = ""
    private var bloodGroup = ""
    private var userType = ""
    private var hospitalType = ""
    private lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference
    var type = "0"

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = getColor(R.color.transparent)
        window.navigationBarColor = getColor(R.color.black)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_users)

        type = intent?.extras?.getString("type") ?: "0"

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance()
        databaseReference = database.reference

        getType {
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

        RxView.clicks(login_submit_button).throttleFirst(2, TimeUnit.SECONDS).subscribe {
            if (et_hos_type.text.toString() == "Hospital") {
                if (validateHospital()) {
                    createHospitalData()
                } else {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                }

            } else {
                if (validate()) {
                    if (et_type.text.toString() == "Acceptor") {
                        createUser("Acceptor")
                    } else {
                        createUser("Donor")
                    }

                } else {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        }

        RxView.clicks(ic_back).throttleFirst(2, TimeUnit.SECONDS).subscribe {
            startActivity(this, AdminHomeActivity::class.java, true, -1)
        }

    }

    private fun getType(completion: () -> Unit) {

        pBar(1)
        if (type == "Hospital") {
            et_hos_type.setText(type)
            hospital.visibility = View.VISIBLE
            user.visibility = View.GONE
        } else {
            et_type.setText(type)
            user.visibility = View.VISIBLE
            hospital.visibility = View.GONE
        }
        completion()
    }

    private fun validateHospital(): Boolean {
        var name = et_name.text.toString().trim()
        var email = et_hospital_email.text.toString().trim()
        var phone = et_number.text.toString().trim()
        var address = et_hospital_address.text.toString().trim()
        var city = et_hospital_city.text.toString().trim()
        var type = et_hos_type.text.toString().trim()
        var password = et_hospital_password.text.toString().trim()
        var confirmPassword = et_confirm_pass.text.toString().trim()


        if (city.isEmpty() || city.isBlank()) {
            et_hospital_city.requestFocus()
            warningToast("Invalid Address")
        }

        if (address.isEmpty() || address.isBlank()) {
            et_hospital_address.requestFocus()
            warningToast("Invalid City")
        }

        if (password.isEmpty() || password.isBlank()) {
            et_hospital_password.requestFocus()
            warningToast("Invalid Password")
            return false
        }

        if (confirmPassword.isEmpty() || confirmPassword.isBlank()) {
            et_confirm_pass.requestFocus()
            warningToast("Invalid Confirm Password")
            return false
        }

        if (confirmPassword != password) {
            et_confirm_pass.requestFocus()
            warningToast("Passwords Did Not Match")
            return false
        }

        if (password.length < 6) {
            et_hospital_password.requestFocus()
            warningToast("Password should not be less than 6 digits")
            return false
        }

        if (phone.isBlank() || phone.isEmpty()) {
            et_number.requestFocus()
            warningToast("Invalid Phone Number")
            return false
        }

        if (phone.isBlank() || phone.isEmpty()) {
            et_number.requestFocus()
            warningToast("Invalid Phone Number")
            return false
        }

        if (name.isEmpty() || name.isBlank()) {
            et_name.requestFocus()
            warningToast("Name should contain only characters")
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_hospital_email.requestFocus()
            warningToast("Invalid Email Address")
            return false
        }


        if (type.isEmpty() || type.isBlank()) {
            et_hos_type.requestFocus()
            warningToast("Invalid User Type")
            return false
        }

        return true

    }

    private fun createHospitalData() {
        var Vemail = et_hospital_email.text.toString().trim()
        var Vpass = et_hospital_password.text.toString().trim()
        pBar(1)

        Log.d("name", " email: $Vemail pass: $Vpass")
        auth.createUserWithEmailAndPassword(Vemail, Vpass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")
                    Toast.makeText(
                        baseContext, "Authentication Pass",
                        Toast.LENGTH_SHORT
                    ).show()
                    uploadHospitalData()
                } else {
                    pBar(0)
                    Log.w("task", "createUserWithEmail:failure", task.exception?.cause)
                    warningToast("Something Went Wrong")

                }
            }
            .addOnFailureListener {
                pBar(0)
            }
    }

    private fun uploadHospitalData() {

        var name = et_name.text.toString().trim()
        var email = et_hospital_email.text.toString().trim()
        var phone = et_number.text.toString().trim()
        var address = et_hospital_address.text.toString().trim()
        var city = et_hospital_city.text.toString().trim()
        var type = et_hos_type.text.toString().trim()

        var hospitalMap = HashMap<String, String>()

        hospitalMap["name"] = name
        hospitalMap["email"] = email
        hospitalMap["phoneNumber"] = phone
        hospitalMap["city"] = city
        hospitalMap["type"] = type
        hospitalMap["address"] = address
        hospitalMap["uid"] = FirebaseAuth.getInstance().currentUser?.uid.toString()

        FirebaseAuth.getInstance().currentUser?.uid?.let {
            FirebaseDatabase
                .getInstance()
                .reference
                .child("hospitals")
                .child(it)
                .setValue(hospitalMap)
                .addOnSuccessListener {

                    pBar(0)
                    apiToast("Account Created Successfully") {
                        startActivity(this, AdminHomeActivity::class.java, true, -1)
                    }
                }
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
        var email = et_email.text.toString().trim()
        var gender = et_gender.text.toString().trim()
        var blood = et_blood.text.toString().trim()
        var type = et_type.text.toString().trim()
        var phone = et_phone_number.text.toString().trim()
        var address = et_address.text.toString().trim()
        var city = et_city.text.toString().trim()
        var password = et_create_password.text.toString().trim()
        var confirmPassword = et_confirm_password.text.toString().trim()



        if (city.isEmpty() || city.isBlank()) {
            et_address.requestFocus()
            warningToast("Invalid Address")
        }

        if (address.isEmpty() || address.isBlank()) {
            et_city.requestFocus()
            warningToast("Invalid City")
        }

        if (password.isEmpty() || password.isBlank()) {
            et_create_password.requestFocus()
            warningToast("Invalid Password")
            return false
        }

        if (confirmPassword.isEmpty() || confirmPassword.isBlank()) {
            et_confirm_password.requestFocus()
            warningToast("Invalid Confirm Password")
            return false
        }

        if (confirmPassword != password) {
            et_confirm_password.requestFocus()
            warningToast("Passwords Did Not Match")
            return false
        }

        if (password.length < 6) {
            et_create_password.requestFocus()
            warningToast("Password should not be less than 6 digits")
            return false
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



        if (type.isEmpty() || type.isBlank()) {
            et_type.requestFocus()
            warningToast("Invalid User Type")
            return false
        }


        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_email.requestFocus()
            warningToast("Invalid Email Address")
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

    private fun createUser(type: String) {
        var Vemail = et_email.text.toString().trim()
        var Vpass = et_create_password.text.toString().trim()
        pBar(1)

        Log.d("name", " email: $Vemail pass: $Vpass")
        auth.createUserWithEmailAndPassword(Vemail, Vpass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")
                    Toast.makeText(
                        baseContext, "Authentication Pass",
                        Toast.LENGTH_SHORT
                    ).show()
                    uploadUserData(type)
                } else {
                    pBar(0)
                    Log.w("task", "createUserWithEmail:failure", task.exception?.cause)
                    warningToast("Something Went Wrong")

                }
            }
            .addOnFailureListener {
                pBar(0)
            }
    }

    private fun uploadUserData(type: String) {


        Log.d("function", "Func:1")

        var vEmail = et_email.text.toString().trim()
        var vFirstName = et_first_name.text.toString().trim()
        var vLastName = et_last_name.text.toString().trim()
        var vNum = et_phone_number.text.toString().trim()
        var vCity = et_city.text.toString().trim()
        var vGender = et_gender.text.toString().trim()
        var vBlood = et_blood.text.toString().trim()
        var vType = et_type.text.toString().trim()
        var vAddress = et_address.text.toString().trim()
        var vDob =
            et_dd.text.toString() + "-" + et_mm.text.toString() + "-" + et_yyyy.text.toString()


        var userMap = HashMap<String, String>()

        if (type == "Acceptor") {
            userMap["firstName"] = vFirstName
            userMap["lastName"] = vLastName
            userMap["email"] = vEmail
            userMap["phoneNumber"] = vNum
            userMap["city"] = vCity
            userMap["gender"] = vGender
            userMap["blood"] = vBlood
            userMap["type"] = vType
            userMap["address"] = vAddress
            userMap["dob"] = vDob
            userMap["uid"] = FirebaseAuth.getInstance().currentUser?.uid.toString()
        } else {
            userMap["firstName"] = vFirstName
            userMap["lastName"] = vLastName
            userMap["email"] = vEmail
            userMap["phoneNumber"] = vNum
            userMap["city"] = vCity
            userMap["gender"] = vGender
            userMap["blood"] = vBlood
            userMap["type"] = vType
            userMap["address"] = vAddress
            userMap["dob"] = vDob
            userMap["online"] = "true"
            userMap["uid"] = FirebaseAuth.getInstance().currentUser?.uid.toString()
        }
        Firebase.auth.currentUser?.let { it2 ->

            var userRef = FirebaseDatabase.getInstance().reference.child(type).child(it2.uid)

            userRef.setValue(userMap).addOnSuccessListener {

                pBar(0)
                if (vType == "Donor") {
                    apiToast("Account Created Successfully") {
                        startActivity(this, AdminHomeActivity::class.java, true, -1)
                    }
                } else {
                    apiToast("Account Created Successfully") {
                        startActivity(this, AdminHomeActivity::class.java, true, -1)
                    }
                }


            }.addOnFailureListener {
                pBar(0)
            }

            Toast.makeText(baseContext, "Authenticated", Toast.LENGTH_SHORT).show()
        }

    }

}