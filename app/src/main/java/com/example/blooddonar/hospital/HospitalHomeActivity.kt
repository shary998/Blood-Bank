package com.example.blooddonar.hospital

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.example.blooddonar.R
import com.example.blooddonar.base.BaseActivity
import com.example.blooddonar.constants.acceptorRequirements
import com.example.blooddonar.constants.users
import com.example.blooddonar.donar.AcceptorAdapter
import com.example.blooddonar.models.AcceptorRequirmentsModel
import com.example.blooddonar.models.HospitalDataModel
import com.example.blooddonar.models.UserModel
import com.example.blooddonar.models.UserTypeModel
import com.example.blooddonar.sharedpref.MySharedPreference
import com.example.blooddonar.start.LoginActivity2
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.jakewharton.rxbinding2.view.RxView
import com.skydoves.powermenu.PowerMenuItem
import kotlinx.android.synthetic.main.activity_acceptor_home.*
import kotlinx.android.synthetic.main.activity_donar_home.*
import kotlinx.android.synthetic.main.activity_hospital_home.*
import kotlinx.android.synthetic.main.activity_hospital_home.receiver
import kotlinx.android.synthetic.main.activity_hospital_home.req
import kotlinx.android.synthetic.main.activity_hospital_home.tab_layout
import kotlinx.android.synthetic.main.activity_signup2.*
import java.util.concurrent.TimeUnit


class HospitalHomeActivity : BaseActivity(), DonorAdapter.OnDonarClick,
    AcceptorAdapter.OnPositionClick {


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
        setContentView(R.layout.activity_hospital_home)


        userId = Firebase.auth.currentUser!!.uid

        initTabLayout()


        pBar(1)
        getData {
            pBar(0)
        }


        getAcceptors {
            pBar(0)
            initListeners()
        }

    }

    private fun getDonors(completion: () -> Unit) {
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

                    donors.adapter =
                        DonorAdapter(
                            this@HospitalHomeActivity,
                            userId,
                            this@HospitalHomeActivity,
                        )
                    donors.adapter!!.notifyDataSetChanged()
                    completion()

                }
            }

            override fun onCancelled(error: DatabaseError) {

                pBar(0)
            }
        }
        database.addValueEventListener(userData)
    }

    private fun getAcceptors(completion: () -> Unit) {
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
                            this@HospitalHomeActivity,
                            userId,
                            this@HospitalHomeActivity,
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

    private fun getData(completion: () -> Unit) {
        Firebase.auth.currentUser?.let {
            FirebaseDatabase
                .getInstance()
                .reference
                .child("hospitals")
                .child(it.uid)
                .get()
                .addOnSuccessListener {


                    if (it.exists()) {

                        if (MySharedPreference(this).getHospitalObject() == null) {
                            hospital_name.text = it.child("name").value.toString()
                            city = it.child("city").value.toString().capitalize()

                            MySharedPreference(this).saveHospitalObject(
                                HospitalDataModel(
                                    it.child("address").value.toString(),
                                    it.child("city").value.toString(),
                                    it.child("email").value.toString(),
                                    it.child("name").value.toString(),
                                    it.child("phoneNumber").value.toString(),
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
                            hospital_name.text = it.child("name").value.toString()
                            city = it.child("city").value.toString().capitalize()
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
        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position ?: 0 == 0) {
                    getAcceptors {
                        pBar(0)
                        receiver.visibility = View.VISIBLE
                        donors.visibility = View.GONE
                        req.visibility = View.GONE
                    }

                } else if (tab?.position ?: 0 == 1) {
                    getDonors {
                        pBar(0)
                        donors.visibility = View.VISIBLE
                        receiver.visibility = View.GONE
                        req.visibility = View.GONE
                    }

                } else {
                    req.visibility = View.VISIBLE
                    donors.visibility = View.GONE
                    receiver.visibility = View.GONE
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        RxView.clicks(stock).throttleFirst(2, TimeUnit.SECONDS).subscribe {
            startActivity(this, StockManagementActivity::class.java, false, 1)
        }

        RxView.clicks(required).throttleFirst(2, TimeUnit.SECONDS).subscribe {
            startActivity(this, RequiredBloodActivity::class.java, false, 1)
        }

        RxView.clicks(logout).throttleFirst(2, TimeUnit.SECONDS).subscribe {
            pBar(1)
            startActivity(this, LoginActivity2::class.java, true, -1)
            FirebaseAuth.getInstance().signOut()
        }


    }

    private fun initTabLayout() {
        tab_layout.addTab(tab_layout.newTab().setText("Acceptor"))
        tab_layout.addTab(tab_layout.newTab().setText("Donor"))
        tab_layout.addTab(tab_layout.newTab().setText("Required"))
    }


    override fun onMessageClick(position: Int) {
        val phoneNumber = "123456789"

        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("smsto:" + Uri.encode(phoneNumber))
        startActivity(intent)
    }

    override fun onCallClick(position: Int) {
        val number = "123456789"

        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
        startActivity(intent)
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

}