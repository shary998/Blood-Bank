package com.example.blooddonar.hospital

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.example.blooddonar.R
import com.example.blooddonar.base.BaseActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.activity_stock_management.*
import java.util.HashMap
import java.util.concurrent.TimeUnit

class StockManagementActivity : BaseActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = getColor(R.color.transparent)
        window.navigationBarColor = getColor(R.color.black)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_management)


        getData {
            pBar(0)
            changeInventory()
            initListeners()
        }
    }

    @SuppressLint("CheckResult")
    private fun initListeners() {

        RxView.clicks(back).throttleFirst(2, TimeUnit.SECONDS).subscribe {
            startActivity(this, HospitalHomeActivity::class.java, true, -1)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getData(completion: () -> Unit) {

        pBar(1)

        FirebaseDatabase
            .getInstance()
            .reference
            .child("hospitals")
            .child(Firebase.auth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener {
                hospital_name.text = it.child("name").value.toString().capitalize()

                FirebaseDatabase
                    .getInstance()
                    .reference
                    .child("hospitals")
                    .child(Firebase.auth.currentUser?.uid.toString())
                    .child("inventory")
                    .get()
                    .addOnSuccessListener {


                        if (it.exists()) {
                            a_counter.setText(it.child("aPos").value.toString() + " " + "Bags")
                            a_neg_counter.setText(it.child("aNeg").value.toString() + " " + "Bags")
                            b_counter.setText(it.child("bPos").value.toString() + " " + "Bags")
                            b_neg_counter.setText(it.child("bNeg").value.toString() + " " + "Bags")
                            o_counter.setText(it.child("oPos").value.toString() + " " + "Bags")
                            o_neg_counter.setText(it.child("oNeg").value.toString() + " " + "Bags")
                            ab_counter.setText(it.child("abPos").value.toString() + " " + "Bags")
                            ab_neg_counter.setText(it.child("abNeg").value.toString() + " " + "Bags")

                        } else {
                            a_counter.setText("0" + " " + "Bags")
                            a_neg_counter.setText("0" + " " + "Bags")
                            b_counter.setText("0" + " " + "Bags")
                            b_neg_counter.setText("0" + " " + "Bags")
                            o_counter.setText("0" + " " + "Bags")
                            o_neg_counter.setText("0" + " " + "Bags")
                            ab_counter.setText("0" + " " + "Bags")
                            ab_neg_counter.setText("0" + " " + "Bags")
                        }


                        completion()
                    }
            }





    }

    @SuppressLint("CheckResult")
    private fun changeInventory() {
        RxView.clicks(submit_inventory).subscribe {


            pBar(1)
            var inventoryMap = HashMap<String, String>()

            inventoryMap["aPos"] = a_counter.text.toString()
            inventoryMap["aNeg"] = a_neg_counter.text.toString()
            inventoryMap["bPos"] = b_counter.text.toString()
            inventoryMap["bNeg"] = b_neg_counter.text.toString()
            inventoryMap["oPos"] = o_counter.text.toString()
            inventoryMap["oNeg"] = o_neg_counter.text.toString()
            inventoryMap["abPos"] = ab_counter.text.toString()
            inventoryMap["abNeg"] = ab_neg_counter.text.toString()


            if (validate()) {
                FirebaseDatabase
                    .getInstance()
                    .reference
                    .child("hospitals")
                    .child(Firebase.auth.currentUser?.uid.toString())
                    .child("inventory")
                    .setValue(inventoryMap)
                    .addOnSuccessListener {

                        apiToast("Saved SuccessFully") {
                            pBar(0)
                            startActivity(this, HospitalHomeActivity::class.java, true, -1)
                        }
                    }
            }

        }
    }

    private fun validate(): Boolean {

        val aPos = a_counter.text.toString()
        val aNeg = a_neg_counter.text.toString()
        val bPos = b_counter.text.toString()
        val bNeg = b_neg_counter.text.toString()
        val oPos = o_counter.text.toString()
        val oNeg = o_neg_counter.text.toString()
        val abPos = ab_counter.text.toString()
        val abNeg = ab_neg_counter.text.toString()


        if (aPos.isEmpty() || aPos.isBlank()) {
            a_counter.requestFocus()
            return false
        }
        if (aNeg.isEmpty() || aNeg.isBlank()) {
            a_neg_counter.requestFocus()
            return false
        }

        if (bPos.isEmpty() || bPos.isBlank()) {
            b_counter.requestFocus()
            return false
        }
        if (bNeg.isEmpty() || bNeg.isBlank()) {
            b_neg_counter.requestFocus()
            return false
        }

        if (oPos.isEmpty() || oPos.isBlank()) {
            o_counter.requestFocus()
            return false
        }
        if (oNeg.isEmpty() || oNeg.isBlank()) {
            o_neg_counter.requestFocus()
            return false
        }
        if (abPos.isEmpty() || abPos.isBlank()) {
            ab_counter.requestFocus()
            return false
        }
        if (abNeg.isEmpty() || abNeg.isBlank()) {
            ab_neg_counter.requestFocus()
            return false
        }

        return true
    }
}