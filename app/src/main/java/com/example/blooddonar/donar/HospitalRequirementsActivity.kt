package com.example.blooddonar.donar

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.example.blooddonar.R
import com.example.blooddonar.base.BaseActivity
import com.example.blooddonar.constants.hospitalsRequirements
import com.example.blooddonar.hospital.HospitalRequirementAdapterDonor
import com.example.blooddonar.models.HospitalReqModel
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_hopital_requirments.*

class HospitalRequirementsActivity : BaseActivity() {

    var uid = ""
    var name = ""
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
        setContentView(R.layout.activity_hopital_requirments)

        uid = intent?.extras?.getString("uid") ?: ""
        name = intent?.extras?.getString("name") ?: ""

        tv_name.text = name.capitalize()

        getRequirements({
            pBar(0)
            data.visibility = View.VISIBLE
            tv_data.visibility = View.GONE
        },{
            pBar(0)
            tv_data.visibility = View.VISIBLE
            data.visibility = View.GONE
        })

    }

    private fun getRequirements(completion : () -> Unit , noDta: () -> Unit) {
        pBar(1)
        database = FirebaseDatabase.getInstance().reference
            .child("Requirements")
            .child("Hospitals")
            .child(uid.trim())

        val userData = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    hospitalsRequirements.clear()

                    for (i in snapshot.children) {

                        var requirements = i.getValue(HospitalReqModel::class.java)
                        Log.d("requirements", requirements.toString())
                        if (requirements != null) {
                            hospitalsRequirements.add(requirements)

                        }
                    }

                    requirements.adapter =
                        HospitalRequirementAdapterDonor(
                            this@HospitalRequirementsActivity,
                        )
                    requirements.adapter!!.notifyDataSetChanged()
                    completion()

                }else{

                    noDta()
                }
            }

            override fun onCancelled(error: DatabaseError) {

                pBar(0)
            }
        }
        database.addValueEventListener(userData)
    }
}