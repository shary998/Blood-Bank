package com.example.blooddonar.hospital

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
import com.example.blooddonar.constants.hospitalRequiredStock
import com.example.blooddonar.models.RequiredStockModel
import com.example.blooddonar.sharedpref.MySharedPreference
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.jakewharton.rxbinding2.view.RxView
import com.skydoves.powermenu.PowerMenuItem
import kotlinx.android.synthetic.main.activity_required_blood.*
import kotlinx.android.synthetic.main.activity_required_blood.cont_req
import kotlinx.android.synthetic.main.activity_required_blood.et_hospital
import kotlinx.android.synthetic.main.activity_required_blood.et_req
import kotlinx.android.synthetic.main.activity_required_blood.login_submit
import kotlinx.android.synthetic.main.activity_required_blood.tab_layout
import java.util.HashMap
import java.util.concurrent.TimeUnit

class RequiredBloodActivity : BaseActivity(), RequiredInventoryAdapter.CheckInventory {

    private var bloodGroup = ""
    private var required = ""
    private lateinit var database: DatabaseReference

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = getColor(R.color.transparent)
        window.navigationBarColor = getColor(R.color.black)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_required_blood)


        et_hospital.setText(MySharedPreference(this).getHospitalObject()?.name.toString())
        hospital_name.text = MySharedPreference(this).getHospitalObject()?.name.toString()

        initListeners()
        initTabLayout()
    }

    @SuppressLint("CheckResult")
    private fun initListeners() {

        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position ?: 0 == 0) {
                    form.visibility = View.VISIBLE
                    data.visibility = View.GONE
                    noData.visibility = View.GONE

                } else {
                    getRequiredStock({
                        pBar(0)
                        data.visibility = View.VISIBLE
                        form.visibility = View.GONE
                        noData.visibility = View.GONE
                    }, {
                        pBar(0)
                        data.visibility = View.VISIBLE
                        noData.visibility = View.VISIBLE
                        required_inventory.visibility = View.GONE
                        form.visibility = View.GONE
                    })

                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        RxView.clicks(cont_blood).throttleFirst(1, TimeUnit.SECONDS).subscribe {
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

        RxView.clicks(cont_req).throttleFirst(1, TimeUnit.SECONDS).subscribe {
            popupDisplay(
                this,
                cont_req,
                et_req,
                false,
                arrayListOf(
                    PowerMenuItem("Required Urgent"),
                    PowerMenuItem("Required"),
                ),
                POPUPDISPLAY_MATCHCONT,
                0
            ) { selectedText ->
                if (selectedText == "Required Urgent") {
                    required = "Required Urgent"
                } else if (selectedText == "Required") {
                    required = "Required"
                }
            }
        }

        RxView.clicks(back).throttleFirst(2, TimeUnit.SECONDS).subscribe {

            startActivity(this, HospitalHomeActivity::class.java, true, -1)
        }

        RxView.clicks(login_submit).throttleFirst(2, TimeUnit.SECONDS).subscribe {
            if (validateForm()) {
                sendRequest()
            } else {
                warningToast("Something Went Wrong")
            }
        }
    }

    private fun validateForm(): Boolean {

        var blood = et_blood.text.toString().trim()
        var req = et_req.text.toString().trim()
        var hospital = et_hospital.text.toString().trim()
        var city = et_city.text.toString().trim()
        var bags = et_bags.text.toString().trim()


        if (blood.isEmpty() || blood.isBlank()) {
            et_blood.requestFocus()
            warningToast("Invalid Blood Group")
            return false
        }

        if (req.isEmpty() || req.isBlank()) {
            et_blood.requestFocus()
            warningToast("Invalid Blood Group")
            return false
        }


        if (hospital.isBlank() || hospital.isEmpty()) {
            et_hospital.requestFocus()
            warningToast("Hospital should contain only characters")
            return false
        }

        if (city.isBlank() || city.isEmpty()) {
            et_city.requestFocus()
            warningToast("City should contain only characters")
            return false
        }
        if (bags.isBlank() || bags.isEmpty()) {
            et_bags.requestFocus()
            warningToast("Bags field can not be empty")
            return false
        }

        return true
    }

    private fun sendRequest() {
        pBar(1)

        Log.d("blood", "blood: " + "abcd")
        var blood = et_blood.text.toString().trim()
        var req = et_req.text.toString().trim()
        var hospital = et_hospital.text.toString().trim()
        var city = et_city.text.toString().trim()
        var bags = et_bags.text.toString().trim()
        var type = MySharedPreference(this).getHospitalObject()?.type
        var number = MySharedPreference(this).getHospitalObject()?.phoneNumber
        var address =
            MySharedPreference(this).getHospitalObject()?.address?.capitalize()
        var name =
            MySharedPreference(this).getHospitalObject()?.name?.capitalize()


        val date = (System.currentTimeMillis() / 1000).toString()

        var requiredMap = HashMap<String, String>()

        requiredMap["address"] = address.toString()
        requiredMap["phoneNumber"] = number.toString()
        requiredMap["city"] = city
        requiredMap["hospital"] = hospital
        requiredMap["name"] = name.toString()
        requiredMap["req"] = req
        requiredMap["blood"] = blood
        requiredMap["bags"] = bags
        requiredMap["type"] = type.toString()
        requiredMap["uid"] = date



        FirebaseAuth.getInstance().currentUser?.uid?.let {
            FirebaseDatabase
                .getInstance()
                .reference
                .child("Requirements")
                .child("Hospitals")
                .child(it)
                .child(date)
                .setValue(requiredMap)
                .addOnSuccessListener {
                    pBar(0)
                    apiToast("Successfully Sent") {
                        startActivity(this, HospitalHomeActivity::class.java, true, -1)
                    }


                }
        }
    }

    private fun initTabLayout() {
        tab_layout.addTab(tab_layout.newTab().setText("Requirement"))
        tab_layout.addTab(tab_layout.newTab().setText("Required Stock"))
    }

    private fun getRequiredStock(completion: () -> Unit, Data: () -> Unit) {

        pBar(1)
        database = FirebaseDatabase.getInstance().reference
            .child("Requirements")
            .child("Hospitals")
            .child(Firebase.auth.currentUser?.uid.toString())

        val userData = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    hospitalRequiredStock.clear()

                    for (i in snapshot.children) {

                        var requirements = i.getValue(RequiredStockModel::class.java)
                        Log.d("requirements", requirements.toString())
                        if (requirements != null) {
                            hospitalRequiredStock.add(requirements)
                            hospitalRequiredStock.reverse()
                        }
                    }

                    required_inventory.adapter =
                        RequiredInventoryAdapter(
                            this@RequiredBloodActivity,
                            this@RequiredBloodActivity
                        )
                    required_inventory.adapter!!.notifyDataSetChanged()

                    completion()
                } else {
                    Data()
                }
            }

            override fun onCancelled(error: DatabaseError) {

                pBar(0)
            }
        }
        database.addValueEventListener(userData)

    }

    override fun onInventoryDeleteClick(position: Int) {
        pBar(1)
        FirebaseDatabase
            .getInstance()
            .reference
            .child("Requirements")
            .child("Hospitals")
            .child(Firebase.auth.currentUser?.uid.toString())
            .child(hospitalRequiredStock[position].uid)
            .removeValue()
            .addOnSuccessListener {
                if (hospitalRequiredStock.size == 0) {
                    startActivity(this, HospitalHomeActivity::class.java, true, -1)
                    pBar(0)
                } else {
                    pBar(0)
                }
            }
    }

    override fun onInventoryUpdateClick(position: Int) {

        startActivity(
            this,
            RequiredInventoryUpdateActivity::class.java,
            false,
            1,
            bundleOf(Pair("uid", hospitalRequiredStock[position].uid))
        )

    }

}