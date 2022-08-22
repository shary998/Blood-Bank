package com.example.blooddonar.donar


import android.annotation.SuppressLint
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
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
import com.example.blooddonar.constants.acceptorRequirements
import com.example.blooddonar.constants.hospitals
import com.example.blooddonar.constants.hospitalsRequirements
import com.example.blooddonar.constants.users
import com.example.blooddonar.hospital.DonorAdapter
import com.example.blooddonar.models.*
import com.example.blooddonar.sharedpref.MySharedPreference
import com.example.blooddonar.start.LoginActivity2
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.activity_acceptor_home.*
import kotlinx.android.synthetic.main.activity_donar_home.*
import kotlinx.android.synthetic.main.activity_donar_home.active
import kotlinx.android.synthetic.main.activity_donar_home.disable
import kotlinx.android.synthetic.main.activity_donar_home.ic_profile
import kotlinx.android.synthetic.main.activity_signup2.*
import kotlinx.android.synthetic.main.activity_signup2.hospital
import kotlinx.android.synthetic.main.activity_signup2.tab_layout
import kotlinx.android.synthetic.main.activity_signup2.user
import java.io.IOException
import java.util.concurrent.TimeUnit

class DonarHomeActivity : BaseActivity(), AcceptorAdapter.OnPositionClick,
    HospitalsAdapter.onHospitalClick {

    private lateinit var database: DatabaseReference
    lateinit var userId: String
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
        setContentView(R.layout.activity_donar_home)


        userId = Firebase.auth.currentUser!!.uid


        pBar(1)

        getData {
            pBar(0)
            checkStatus()
        }

        initTabLayout()
        getAcceptorRequirements {
            pBar(0)
            initListeners()
            checkStatus()
        }
    }

    private fun getData(completion: () -> Unit) {

        Firebase.auth.currentUser?.let {
            FirebaseDatabase
                .getInstance()
                .reference
                .child("Donor")
                .child(it.uid)
                .get()
                .addOnSuccessListener {

                    if (it.exists()) {
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

                        city = it.child("city").value.toString()

                        if (MySharedPreference(this).getUserObject() == null) {
                            MySharedPreference(this).saveUerObject(
                                UserModel(
                                    it.child("address").value.toString(),
                                    it.child("blood").value.toString(),
                                    it.child("city").value.toString(),
                                    it.child("dob").value.toString(),
                                    it.child("email").value.toString(),
                                    it.child("firstName").value.toString(),
                                    it.child("gender").value.toString(),
                                    it.child("online").value.toString(),
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
                        } else {

                            if (it.child("profileImage").value == null) {
                                ic_profile.setImageResource(R.drawable.ic_user)
                            } else {
                                Glide.with(this).load(it.child("profileImage").value)
                                    .placeholder(circularProgressDrawable)
                                    .into(ic_profile)
                            }

                            city = it.child("city").value.toString()
                        }


                    } else {
                        apiToast("You are has been suspended by Admin") {
                            startActivity(this, LoginActivity2::class.java, true, -1)
                            FirebaseAuth.getInstance().signOut()
                        }
                    }
                    completion()
                }
        }


    }

    private fun getAcceptorRequirements(completion: () -> Unit) {
        pBar(1)
        database = FirebaseDatabase.getInstance().reference
            .child("Requirements")
            .child("Acceptors")

        val userData = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    acceptorRequirements.clear()

                    for (i in snapshot.children) {

                        if (i.child("uid").value != userId) {

                            var requirements = i.getValue(AcceptorRequirmentsModel::class.java)
                            Log.d("requirements", requirements.toString())
                            if (requirements != null) {
                                acceptorRequirements.add(requirements)
                            }
                        }
                    }

                    receiver.adapter =
                        AcceptorAdapter(
                            this@DonarHomeActivity,
                            userId,
                            this@DonarHomeActivity,
                        )
                    receiver.adapter!!.notifyDataSetChanged()
                    completion()

                }
            }

            override fun onCancelled(error: DatabaseError) {

                pBar(0)
            }
        }
        database.addValueEventListener(userData)
    }

    private fun getHospitalRequirements(completion: () -> Unit) {
        pBar(1)
        database = FirebaseDatabase.getInstance().reference
            .child("Requirements")
            .child("Hospitals")

        val userData = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    hospitalsRequirements.clear()

                    for (i in snapshot.children) {

                        if (i.child("uid").value != userId && i.child("city").value == city) {

                            var hosrequirements = i.getValue(HospitalReqModel::class.java)
                            Log.d("hosrequirements", hosrequirements.toString())
                            if (hosrequirements != null) {
                                hospitalsRequirements.add(hosrequirements)
                            }
                        }
                    }

                    hospital2.adapter =
                        HospitalsAdapter(
                            this@DonarHomeActivity,
                            userId,
                            this@DonarHomeActivity,
                        )
                    hospital2.adapter!!.notifyDataSetChanged()
                    completion()

                }
            }

            override fun onCancelled(error: DatabaseError) {

                pBar(0)
            }
        }
        database.addValueEventListener(userData)

    }

    private fun checkStatus() {

        when (MySharedPreference(this).getAvailableObject()?.online) {
            null -> {

                FirebaseDatabase
                    .getInstance()
                    .reference
                    .child("Donor")
                    .child(Firebase.auth.currentUser?.uid.toString())
                    .child("online")
                    .get()
                    .addOnSuccessListener {

                        if (it.value.toString() == "true") {
                            MySharedPreference(this).saveAvailableObject(
                                AvailableModel(
                                    it.value.toString()
                                )
                            )
                            active.visibility = View.VISIBLE
                            disable.visibility = View.GONE
                        } else {
                            MySharedPreference(this).saveAvailableObject(
                                AvailableModel(
                                    it.value.toString()
                                )
                            )
                            active.visibility = View.GONE
                            disable.visibility = View.VISIBLE
                        }
                    }
            }
            "true" -> {
                active.visibility = View.VISIBLE
                disable.visibility = View.GONE
            }
            else -> {
                active.visibility = View.GONE
                disable.visibility = View.VISIBLE
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun initListeners() {
        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position ?: 0 == 0) {
                    getAcceptorRequirements {
                        pBar(0)
                        receiver.visibility = View.VISIBLE
                        hospital2.visibility = View.GONE
                    }

                } else {
                    getHospitalRequirements {
                        pBar(0)
                        hospital2.visibility = View.VISIBLE
                        receiver.visibility = View.GONE
                    }

                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        RxView.clicks(ic_profile).throttleFirst(2, TimeUnit.SECONDS).subscribe {
            startActivity(this, DonarProfileActivity::class.java, false, 1)
        }
    }

    private fun initTabLayout() {
        tab_layout.addTab(tab_layout.newTab().setText("Acceptor"))
        tab_layout.addTab(tab_layout.newTab().setText("Hospital"))
    }

    override fun onAcceptorMessageClick(position: Int) {
        val phoneNumber = acceptorRequirements[position].phoneNumber

        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("smsto:" + Uri.encode(phoneNumber))
        startActivity(intent)
    }

    override fun onAcceptorCallClick(position: Int) {
        val number = acceptorRequirements[position].phoneNumber

        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
        startActivity(intent)
    }

    override fun onHospitalCallClick(position: Int) {
        val number = hospitalsRequirements[position].phoneNumber

        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
        startActivity(intent)
    }

    override fun onHospitalNavigateClick(position: Int) {

        val coder = Geocoder(this)
        try {
            val adresses =
                coder.getFromLocationName(
                    hospitalsRequirements[position].address,
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


}