package com.example.blooddonar.admin

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import com.example.blooddonar.R
import com.example.blooddonar.base.BaseActivity
import com.example.blooddonar.constants.acceptors
import com.example.blooddonar.constants.users
import com.example.blooddonar.models.*
import com.example.blooddonar.sharedpref.MySharedPreference
import com.example.blooddonar.start.LoginActivity2
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.activity_admin.*
import kotlinx.android.synthetic.main.activity_admin.tab_layout
import kotlinx.android.synthetic.main.activity_donar_home.*
import kotlinx.android.synthetic.main.activity_signup2.*
import java.util.concurrent.TimeUnit

class AdminHomeActivity : BaseActivity(), AdminAcceptorAdapter.OnPositionClick,
    AdminHospitalAdapter.OnHospitalClick, AdminDonorAdapter.OnDonarClick {

    private lateinit var database: DatabaseReference
    var position = ""

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = getColor(R.color.transparent)
        window.navigationBarColor = getColor(R.color.black)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        getData {
            pBar(0)
            initTabLayout()
            initListeners()
        }

        getAcceptors {
            pBar(0)
            add_user_btn.text = "Add Acceptor"
        }

    }


    private fun getAcceptors(completion: () -> Unit) {
        pBar(1)
        database = FirebaseDatabase.getInstance().reference
            .child("Acceptor")

        val userData = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    acceptors.clear()

                    for (i in snapshot.children) {

                        var requirements = i.getValue(AcceptorsModel::class.java)
                        Log.d("requirements", requirements.toString())
                        if (requirements != null) {
                            acceptors.add(requirements)

                        }
                    }

                    acceptor.adapter =
                        AdminAcceptorAdapter(
                            this@AdminHomeActivity,
                            this@AdminHomeActivity,
                        )
                    acceptor.adapter!!.notifyDataSetChanged()
                    completion()

                } else {
                    pBar(0)
                }
            }

            override fun onCancelled(error: DatabaseError) {

                pBar(0)
            }
        }
        database.addValueEventListener(userData)
    }

    private fun getHospitals(completion: () -> Unit) {
        pBar(1)
        database = FirebaseDatabase.getInstance().reference
            .child("hospitals")

        val userData = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    com.example.blooddonar.constants.hospitals.clear()

                    for (i in snapshot.children) {

                        var requirements = i.getValue(HospitalDataModel::class.java)
                        Log.d("requirements", requirements.toString())
                        if (requirements != null) {
                            com.example.blooddonar.constants.hospitals.add(requirements)

                        }
                    }

                    hospitals.adapter =
                        AdminHospitalAdapter(
                            this@AdminHomeActivity,
                            this@AdminHomeActivity,
                        )
                    hospitals.adapter!!.notifyDataSetChanged()
                    completion()

                }
            }

            override fun onCancelled(error: DatabaseError) {

                pBar(0)
            }
        }
        database.addValueEventListener(userData)
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

                        var donor = i.getValue(UserModel::class.java)
                        Log.d("donor", donor.toString())
                        if (donor != null) {
                            users.add(donor)

                        }
                    }

                    donor.adapter =
                        AdminDonorAdapter(
                            this@AdminHomeActivity,
                            this@AdminHomeActivity,
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


    @SuppressLint("CheckResult")
    private fun initListeners() {
        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {

                position = tab?.position?.toString().toString()

                when {
                    tab?.position ?: 0 == 0 -> {
                        getAcceptors {
                            add_user_btn.text = "Add Acceptor"
                            acceptor.visibility = View.VISIBLE
                            hospitals.visibility = View.GONE
                            donor.visibility = View.GONE
                            pBar(0)
                        }

                    }
                    tab?.position ?: 0 == 1 -> {
                        getHospitals {
                            add_user_btn.text = "Add Hospital"
                            hospitals.visibility = View.VISIBLE
                            acceptor.visibility = View.GONE
                            donor.visibility = View.GONE
                            pBar(0)
                        }
                    }
                    else -> {
                        getDonors {
                            add_user_btn.text = "Add Donor"
                            donor.visibility = View.VISIBLE
                            hospitals.visibility = View.GONE
                            acceptor.visibility = View.GONE
                            pBar(0)
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        RxView.clicks(logout).throttleFirst(2, TimeUnit.SECONDS).subscribe {
            startActivity(this, LoginActivity2::class.java, true, -1)
            FirebaseAuth.getInstance().signOut()
        }

        RxView.clicks(add_user_btn).throttleFirst(2, TimeUnit.SECONDS).subscribe {

            when (add_user_btn.text) {
                "Add Acceptor" -> {
                    startActivity(this,
                    AdminAddUsersActivity::class.java,
                    true,
                    1,
                    bundleOf(Pair("type","Acceptor")))
                }
                "Add Hospital" -> {
                    startActivity(this,
                        AdminAddUsersActivity::class.java,
                        true,
                        1,
                        bundleOf(Pair("type","Hospital")))
                }
                "Add Donor" -> {
                    startActivity(this,
                        AdminAddUsersActivity::class.java,
                        true,
                        1,
                        bundleOf(Pair("type","Donor")))
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
            .child("Admin")
            .child(Firebase.auth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener {

                MySharedPreference(this).saveAdminObject(
                    AdminModel(
                        it.child("email").value.toString(),
                        it.child("firstName").value.toString(),
                        it.child("lastName").value.toString(),
                        it.child("phoneNUmber").value.toString(),
                        it.child("type").value.toString(),
                        it.child("uid").value.toString(),
                    )
                )

                MySharedPreference(this).saveUserType(
                    UserTypeModel(
                        it.child("type").value.toString()
                    )
                )

                completion()

            }
    }

    private fun initTabLayout() {
        tab_layout.addTab(tab_layout.newTab().setText("Acceptor"))
        tab_layout.addTab(tab_layout.newTab().setText("Hospital"))
        tab_layout.addTab(tab_layout.newTab().setText("Donor"))
    }

    override fun onAcceptorUpdateClick(position: Int) {

        startActivity(
            this,
            AdminUserUpdateActivity::class.java,
            true,
            1,
            bundleOf(Pair("uid", acceptors[position].uid), Pair("type", acceptors[position].type))
        )
    }

    override fun onAcceptorDeleteClick(position: Int) {

        disclaimer("Are you Sure You want to delete?") {

            FirebaseDatabase
                .getInstance()
                .reference
                .child("Acceptor")
                .child(acceptors[position].uid)
                .removeValue()
                .addOnSuccessListener {
                    warningToast("Acceptor Removed Successfully!")
                }
        }

    }

    override fun onHospitalUpdateClick(position: Int) {
        startActivity(
            this,
            UpdateHospitalActivity::class.java,
            true,
            1,
            bundleOf(Pair("uid", com.example.blooddonar.constants.hospitals[position].uid))
        )
    }

    override fun onHospitalDeleteClick(position: Int) {
        disclaimer("Are you Sure You want to delete?") {

            FirebaseDatabase
                .getInstance()
                .reference
                .child("hospitals")
                .child(com.example.blooddonar.constants.hospitals[position].uid)
                .removeValue()
                .addOnSuccessListener {
                    warningToast("Hospital Removed Successfully!")
                }
        }
    }

    override fun onDonorUpdateClick(position: Int) {
        startActivity(
            this,
            AdminUserUpdateActivity::class.java,
            true,
            1,
            bundleOf(Pair("uid", users[position].uid), Pair("type", users[position].type))
        )
    }

    override fun onDonorDeleteClick(position: Int) {
        disclaimer("Are you Sure You want to delete?") {

            FirebaseDatabase
                .getInstance()
                .reference
                .child("Donor")
                .child(users[position].uid)
                .removeValue()
                .addOnSuccessListener {
                    warningToast("Donor Removed Successfully!")
                }
        }
    }

}