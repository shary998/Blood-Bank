package com.example.blooddonar.hospital

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.example.blooddonar.R
import com.example.blooddonar.constants.hospitals
import com.example.blooddonar.constants.users
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.hospital_availability.view.*
import java.util.concurrent.TimeUnit

class HospitalsAvaibilityAdapter(
    val context: Context,
    var userId: String,
//    private val items: ArrayList<GetFacilityDataPitchsize>,
    private val listener: CheckAvailability
) :
    RecyclerView.Adapter<HospitalsAvaibilityAdapter.HospitalsAvaibilityAdapter>() {
    class HospitalsAvaibilityAdapter(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HospitalsAvaibilityAdapter {
        return HospitalsAvaibilityAdapter(
            LayoutInflater.from(context).inflate(R.layout.hospital_availability, parent, false)
        )
    }

    override fun getItemCount(): Int = hospitals.size

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: HospitalsAvaibilityAdapter, position: Int) {
//        val pitch = items[position]

        holder.itemView.apply {

            val circularProgressDrawable = CircularProgressDrawable(context)
            circularProgressDrawable.centerRadius = 25f
            circularProgressDrawable.start()

            userId = FirebaseAuth
                .getInstance()
                .currentUser
                ?.uid.toString()

            val item = hospitals[position]
            if (userId != item.uid) {

                tv_hospital_name.text = item.name.capitalize()
                tv_number.text = item.phoneNumber
                tv_hos_name.text = item.address.capitalize()
            }
            RxView.clicks(cont_available).throttleFirst(2, TimeUnit.SECONDS).subscribe {
                listener.onCheckAvailability(position)
            }
            RxView.clicks(cont_navigate).throttleFirst(2, TimeUnit.SECONDS).subscribe {
                listener.onNavigation(position)
            }
            RxView.clicks(cont_call).throttleFirst(2, TimeUnit.SECONDS).subscribe {
                listener.onHospitalCallClick(position)
            }
        }
    }

    interface CheckAvailability {
        fun onCheckAvailability(position: Int)
        fun onNavigation(position: Int)
        fun onHospitalCallClick(position: Int)
    }
}