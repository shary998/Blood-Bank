package com.example.blooddonar.start

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.blooddonar.R
import com.example.blooddonar.acceptor.AcceptorHomeActivity
import com.example.blooddonar.base.BaseActivity
import com.example.blooddonar.donar.DonarHomeActivity
import com.example.blooddonar.hospital.HospitalHomeActivity
import com.example.blooddonar.sharedpref.MySharedPreference
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.ibotta.android.support.pickerdialogs.SupportedDatePickerDialog
import com.ibotta.android.support.pickerdialogs.SupportedTimePickerDialog
import com.jakewharton.rxbinding2.view.RxView
import com.skydoves.powermenu.PowerMenuItem
import kotlinx.android.synthetic.main.activity_signup2.*
import kotlinx.android.synthetic.main.activity_signup2.cont_blood
import kotlinx.android.synthetic.main.activity_signup2.cont_gender
import kotlinx.android.synthetic.main.activity_signup2.et_blood
import kotlinx.android.synthetic.main.activity_signup2.et_gender
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class Signup2Activity : BaseActivity() {


    private var gender = ""
    private var bloodGroup = ""
    private var userType = ""
    private var hospitalType = ""
    private var isPictureUploaded = false
    private var shouldLoadDataFromApi = true
    private lateinit var auth: FirebaseAuth
    private var isImageUploaded = true
    var profImageLink = ""
    private lateinit var storageRef: StorageReference
    lateinit var database: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference
    var userUid = Firebase.auth.currentUser?.uid
    private var profileImage: InputStream? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = getColor(R.color.transparent)
        window.navigationBarColor = getColor(R.color.black)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_signup2)


        auth = Firebase.auth
        database = FirebaseDatabase.getInstance()
        databaseReference = database.reference
        storageRef = Firebase.storage.reference

        initTabLayout()
        initListeners()


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

        RxView.clicks(cont_type).throttleFirst(2, TimeUnit.SECONDS).subscribe {
            popupDisplay(
                this,
                cont_type,
                et_type,
                false,
                arrayListOf(
                    PowerMenuItem("Acceptor"),
                    PowerMenuItem("Donor"),
                ),
                POPUPDISPLAY_MATCHCONT,
                0
            ) { selectedText ->
                if (selectedText == "Acceptor") {
                    userType = "Acceptor"
                } else if (selectedText == "Donor") {
                    userType = "Donor"
                }
            }
        }

        RxView.clicks(cont_hos_type).throttleFirst(2, TimeUnit.SECONDS).subscribe {
            popupDisplay(
                this,
                cont_hos_type,
                et_hos_type,
                false,
                arrayListOf(
                    PowerMenuItem("Hospital"),
                ),
                POPUPDISPLAY_MATCHCONT,
                0
            ) { selectedText ->
                if (selectedText == "Hospital") {
                    hospitalType = "Hospital"
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

        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position ?: 0 == 0) {
                    user.visibility = View.VISIBLE
                    hospital.visibility = View.GONE
                } else {
                    hospital.visibility = View.VISIBLE
                    user.visibility = View.GONE
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        RxView.clicks(profile).throttleFirst(2, TimeUnit.SECONDS).subscribe {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(
                    1080,
                    1080
                )
                .start()
        }

        RxView.clicks(login_submit_button).throttleFirst(2, TimeUnit.SECONDS).subscribe {

            if (et_hos_type.text.toString() == "Hospital") {

                if (validateHospital()) {
                    createHospitalData()
                } else {
                    Toast.makeText(this, "Some thing went wrong", Toast.LENGTH_SHORT).show()
                }

            } else {
                if (validate()) {
                    if (et_type.text.toString() == "Acceptor") {
                        createUser("Acceptor")
                    } else {
                        createUser("Donor")
                    }

                } else {
                    Toast.makeText(this, "Some thing went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        }

        RxView.clicks(cont_dd).throttleFirst(2, TimeUnit.SECONDS).subscribe {

        }
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
                    if (isImageUploaded) {
                        uploadUserData(true, type)

                    } else {
                        uploadUserData(false, type)
                    }


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
                    startActivity(this, HospitalHomeActivity::class.java, true, 1)
                    Toast.makeText(this, "Thank You To Join Our Community", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            Activity.RESULT_OK -> {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data
                val bitmap = MediaStore.Images.Media.getBitmap(
                    contentResolver,
                    fileUri
                )
                imageViewAnimatedChange(
                    profile,
                    bitmap
                )
                isPictureUploaded = true

                profileImage = data?.data?.let { contentResolver.openInputStream(it) }
            }
            ImagePicker.RESULT_ERROR -> {
                errorToast(ImagePicker.getError(data))
            }
            else -> {
                errorToast("Task Cancelled")
            }
        }
    }

    private fun initTabLayout() {
        tab_layout.addTab(tab_layout.newTab().setText("User"))
        tab_layout.addTab(tab_layout.newTab().setText("Hospital"))
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

    private fun uploadUserData(isPictureUploaded: Boolean?, type: String) {


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

                if (isPictureUploaded == true) {
                    uploadImage(profileImage, userUid.toString(), "profileImages", type)
                } else {
                    pBar(0)
                    if (vType == "Donor") {
                        startActivity(this, DonarHomeActivity::class.java, true, 1)
                    } else {
                        startActivity(this, AcceptorHomeActivity::class.java, true, 1)
                    }

                }


            }.addOnFailureListener {
                pBar(0)
            }

            Toast.makeText(baseContext, "Authenticated", Toast.LENGTH_SHORT).show()
        }

    }

    private fun uploadImage(
        file: InputStream?,
        fileName: String,
        folderName: String,
        type: String
    ) {

        pBar(1)
        Toast.makeText(this, "Please wait while creating your profile...", Toast.LENGTH_LONG).show()
        var usersStorageRef =
            storageRef.child("users").child(folderName).child(userUid.toString())
        pBar(1)

        var uploadTask = usersStorageRef.putStream(file!!)
        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            usersStorageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                profImageLink = downloadUri.toString()
                Firebase.auth.currentUser?.let { it2 ->
                    //     databaseReference
                    FirebaseDatabase.getInstance().reference.child(type).child(it2.uid)
                        .child("profileImage")
                        .setValue(downloadUri.toString()).addOnSuccessListener {
                            isImageUploaded = true
                            pBar(0)
                            if (type == "Donor") {
                                startActivity(this, DonarHomeActivity::class.java, true, 1)
                            } else {
                                startActivity(this, AcceptorHomeActivity::class.java, true, 1)
                            }

                        }

                        .addOnFailureListener {
                            Log.d("failure", it.localizedMessage)
                            pBar(0)

                        }
                }
            } else {
                pBar(0)
            }
        }

    }


}
