package com.example.blooddonar.acceptor

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import com.example.blooddonar.R
import com.example.blooddonar.base.BaseActivity
import com.example.blooddonar.donar.DonarProfileActivity
import com.example.blooddonar.hospital.DonorAdapter
import com.example.blooddonar.hospital.HospitalAvailabilityActivity
import com.example.blooddonar.hospital.HospitalsAvaibilityAdapter
import com.google.android.material.tabs.TabLayout
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.activity_acceptor_home.*
import kotlinx.android.synthetic.main.activity_acceptor_home.req
import kotlinx.android.synthetic.main.activity_hospital_home.*
import java.util.concurrent.TimeUnit
import android.content.Intent
import android.location.Address
import android.net.Uri
import android.util.Log
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.blooddonar.constants.hospitals
import com.example.blooddonar.constants.users
import com.example.blooddonar.models.HospitalDataModel
import com.example.blooddonar.models.UserModel
import com.example.blooddonar.sharedpref.MySharedPreference
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_acceptor_home.ic_profile
import kotlinx.android.synthetic.main.activity_donar_home.*
import kotlinx.android.synthetic.main.activity_signup2.*
import android.location.Geocoder
import com.example.blooddonar.models.AcceptorsModel
import com.example.blooddonar.models.UserTypeModel
import com.example.blooddonar.start.LoginActivity2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.GeoPoint
import java.io.IOException
import com.google.type.LatLng
import com.skydoves.powermenu.PowerMenuItem
import kotlinx.android.synthetic.main.activity_donar_profile.*
import java.util.HashMap


class AcceptorHomeActivity : BaseActivity(),
    HospitalsAvaibilityAdapter.CheckAvailability,
    DonorAdapter.OnDonarClick {

    private lateinit var database: DatabaseReference
    lateinit var userId: String
    var bloodGroup = ""
    var required = ""
    private var city = ""

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = getColor(R.color.transparent)
        window.navigationBarColor = getColor(R.color.black)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acceptor_home)



        pBar(1)
        getData {
            getDonors {
                pBar(0)
                initTabLayout()
                initListeners()
            }

        }
        userId = Firebase.auth.currentUser!!.uid

    }


    fun getDonors(completion: () -> Unit) {
        pBar(1)
        database = FirebaseDatabase.getInstance().reference
            .child("Donor")

        val userData = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    users.clear()

                    for (i in snapshot.children) {

                        if (i.child("uid").value != userId && i.child("online").value == "true" && i.child(
                                "city"
                            ).value == city
                        ) {

                            var user = i.getValue(UserModel::class.java)
                            Log.d("users", user.toString())
                            if (user != null) {
                                users.add(user)
                            }
                        }
                    }

                    donor.adapter =
                        DonorAdapter(
                            this@AcceptorHomeActivity,
                            userId,
                            this@AcceptorHomeActivity,
                        )
                    donor.adapter!!.notifyDataSetChanged()
                    completion()

                }
            }

            override fun onCancelled(error: DatabaseError) {

                pBar(0)
            }
        }
        database.addValueEventListener(userData)
    }

    fun getHospitals(completion: () -> Unit) {
        pBar(1)
        database = FirebaseDatabase.getInstance().reference
            .child("hospitals")

        val userData = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    hospitals.clear()

                    for (i in snapshot.children) {

                        if (i.child("uid").value != userId && i.child("city").value == city) {

                            var user = i.getValue(HospitalDataModel::class.java)
                            Log.d("users", user.toString())
                            if (user != null) {
                                hospitals.add(user)
                            }
                        }
                    }

                    hospital_rec.adapter = HospitalsAvaibilityAdapter(
                        this@AcceptorHomeActivity,
                        userId,
                        this@AcceptorHomeActivity
                    )
                    hospital_rec.adapter!!.notifyDataSetChanged()
                    completion()

                }
            }

            override fun onCancelled(error: DatabaseError) {

                pBar(0)
            }
        }
        database.addValueEventListener(userData)
    }

    private fun getData(completion: () -> Unit) {
        Firebase.auth.currentUser?.let {
            FirebaseDatabase
                .getInstance()
                .reference
                .child("Acceptor")
                .child(it.uid)
                .get()
                .addOnSuccessListener {

                    if (it.exists()) {
                        city = it.child("city").value.toString()

                        val circularProgressDrawable = CircularProgressDrawable(this)
                        circularProgressDrawable.centerRadius = 25f
                        circularProgressDrawable.start()

                        if (it.child("profileImage").value == null) {
                            ic_profile.setImageResource(R.drawable.ic_user)
                        } else {
                            Glide.with(this).load(it.child("profileImage").value)
                                .placeholder(circularProgressDrawable)
                                .into(ic_profile)
                        }

                        if (MySharedPreference(this).getAcceptorObject() == null) {
                            MySharedPreference(this).saveAcceptorObject(
                                AcceptorsModel(
                                    it.child("address").value.toString(),
                                    it.child("blood").value.toString(),
                                    it.child("city").value.toString(),
                                    it.child("dob").value.toString(),
                                    it.child("email").value.toString(),
                                    it.child("firstName").value.toString(),
                                    it.child("gender").value.toString(),
                                    it.child("lastName").value.toString(),
                                    it.child("phoneNumber").value.toString(),
                                    it.child("profileImage").value.toString(),
                                    it.child("type").value.toString(),
                                    it.child("uid").value.toString(),

                                    )
                            )

                            MySharedPreference(this).saveUserType(
                                UserTypeModel(
                                    it.child("type").value.toString()
                                )
                            )
                        }
                    } else {

                        apiToast("You are has been suspended by Admin") {
                            startActivity(this, LoginActivity2::class.java, true, -1)
                            FirebaseAuth.getInstance().signOut()
                        }
                    }

                }
        }

        completion()
    }

    @SuppressLint("CheckResult")
    private fun initListeners() {
        acc_tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {

                if (tab?.position ?: 0 == 0) {
                    getDonors {
                        pBar(0)
                        donor.visibility = View.VISIBLE
                        hospital_rec.visibility = View.GONE
                        req.visibility = View.GONE
                    }
                } else if (tab?.position ?: 0 == 1) {
                    getHospitals {
                        pBar(0)
                        hospital_rec.visibility = View.VISIBLE
                        donor.visibility = View.GONE
                        req.visibility = View.GONE
                    }
                } else {
                    req.visibility = View.VISIBLE
                    donor.visibility = View.GONE
                    hospital_rec.visibility = View.GONE
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        RxView.clicks(cont_blood_form).throttleFirst(2, TimeUnit.SECONDS).subscribe {
            popupDisplay(
                this,
                cont_blood_form,
                et_blood_form,
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

        RxView.clicks(ic_profile).throttleFirst(2, TimeUnit.SECONDS).subscribe {
            startActivity(this, DonarProfileActivity::class.java, false, 1)
        }

        RxView.clicks(cont_req).throttleFirst(2, TimeUnit.SECONDS).subscribe {
            popupDisplay(
                this,
                cont_req,
                et_req,
                false,
                arrayListOf(
                    PowerMenuItem("Required Urgent"),
                    PowerMenuItem("Required")
                ), POPUPDISPLAY_MATCHCONT,
                0
            ) { selectedText ->
                if (selectedText == "Required Urgent") {
                    required = "Required Urgent"
                } else if (selectedText == "Required") {
                    required = "Required"
                }
            }
        }

        RxView.clicks(login_submit).throttleFirst(2, TimeUnit.SECONDS).subscribe {
            if (validateForm()) {
                sendRequest()
            }
        }

    }

    private fun validateForm(): Boolean {

        var blood = et_blood_form.text.toString().trim()
        var req = et_req.text.toString().trim()
        var diagnosis = et_diagnosis.text.toString().trim()
        var hospital = et_hospital.text.toString().trim()
        var city = et_city_form.text.toString().trim()


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

        if (diagnosis.isEmpty() || diagnosis.isBlank()) {
            et_diagnosis.requestFocus()
            warningToast("Diagnosis should contain only characters")
            return false
        }

        if (hospital.isEmpty() || hospital.isBlank()) {
            et_hospital.requestFocus()
            warningToast("Hospital should contain only characters")
            return false
        }

        if (!nameRegex(city)) {
            et_city_form.requestFocus()
            warningToast("City should contain only characters")
            return false
        }

        return true
    }

    private fun sendRequest() {
        pBar(1)

        Log.d("blood", "blood: " + "abcd")
        var blood = et_blood_form.text.toString().trim()
        var req = et_req.text.toString().trim()
        var diagnosis = et_diagnosis.text.toString().trim()
        var hospital = et_hospital.text.toString().trim()
        var city = et_city_form.text.toString().trim()
        var profileImage = MySharedPreference(this).getAcceptorObject()?.profileImage
        var type = MySharedPreference(this).getAcceptorObject()?.type
        var number = MySharedPreference(this).getAcceptorObject()?.phoneNumber
        var name =
            MySharedPreference(this).getAcceptorObject()?.firstName?.capitalize() + MySharedPreference(
                this
            ).getAcceptorObject()?.lastName?.capitalize()


        var requiredMap = HashMap<String, String>()

        requiredMap["name"] = name
        requiredMap["phoneNumber"] = number.toString()
        requiredMap["city"] = city
        requiredMap["hospital"] = hospital
        requiredMap["diagnosis"] = diagnosis
        requiredMap["req"] = req
        requiredMap["blood"] = blood
        requiredMap["profileImage"] = profileImage.toString()
        requiredMap["type"] = type.toString()
        requiredMap["uid"] = FirebaseAuth.getInstance().currentUser?.uid.toString()

        FirebaseAuth.getInstance().currentUser?.uid?.let {
            FirebaseDatabase
                .getInstance()
                .reference
                .child("Requirements")
                .child("Acceptors")
                .child(it)
                .setValue(requiredMap)
                .addOnSuccessListener {
                    pBar(0)
                    apiToast("Successfully Sent") {
                        onDestroy()
                    }

                }
        }
    }

    private fun initTabLayout() {
        acc_tab_layout.addTab(acc_tab_layout.newTab().setText("Donor"))
        acc_tab_layout.addTab(acc_tab_layout.newTab().setText("Hospital"))
        acc_tab_layout.addTab(acc_tab_layout.newTab().setText("Required"))
    }

    override fun onCheckAvailability(position: Int) {

        startActivity(
            this,
            HospitalAvailabilityActivity::class.java,
            false,
            1,
            bundleOf(Pair("uid", hospitals[position].uid), Pair("name", hospitals[position].name))
        )

    }

    override fun onNavigation(position: Int) {

        val coder = Geocoder(this)
        try {
            val adresses =
                coder.getFromLocationName(
                    hospitals[position].address,
                    50
                ) as ArrayList<Address>
            for (add in adresses) {
                if (add.latitude != null && add.longitude != null) {
                    val gmmIntentUri =
                        Uri.parse("google.navigation:q=${add.latitude},${add.longitude}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    startActivity(mapIntent)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun onHospitalCallClick(position: Int) {
        val number = hospitals[position].phoneNumber

        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
        startActivity(intent)
    }

    override fun onMessageClick(position: Int) {

        val phoneNumber = users[position].phoneNumber

        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("smsto:" + Uri.encode(phoneNumber))
        startActivity(intent)
    }

    override fun onCallClick(position: Int) {
        val number = users[position].phoneNumber

        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        et_blood_form.text = null
        et_req.text = null
        et_diagnosis.text = null
        et_hospital.text = null
        et_city_form.text = null

    }

    override fun onStart() {
        super.onStart()
        getDonors {
            pBar(0)
            donor.visibility = View.VISIBLE
            hospital_rec.visibility = View.GONE
            req.visibility = View.GONE
        }
    }
}