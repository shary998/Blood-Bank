package com.example.blooddonar.donar

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.blooddonar.R
import com.example.blooddonar.constants.hospitalsRequirements
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.hospital_recycler.view.*
import java.util.concurrent.TimeUnit

class HospitalsAdapter(
    val context: Context,
    var userId: String,
    private val listener: onHospitalClick
) :
    RecyclerView.Adapter<HospitalsAdapter.HospitalsAdapter>() {
    class HospitalsAdapter(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HospitalsAdapter {
        return HospitalsAdapter(
            LayoutInflater.from(context).inflate(R.layout.hospital_recycler, parent, false)
        )
    }

    override fun getItemCount(): Int = hospitalsRequirements.size

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: HospitalsAdapter, position: Int) {
        val item = hospitalsRequirements[position]

        holder.itemView.apply {

            tv_hospital_name.text = item.hospital.capitalize()
            tv_number.text = item.phoneNumber
            tv_hos_name.text = item.address.capitalize()
            tv_blood_name.text = item.blood.capitalize()
            tv_req.text = item.req

            RxView.clicks(cont_call).throttleFirst(2,TimeUnit.SECONDS).subscribe{
                listener.onHospitalCallClick(position)
            }
            RxView.clicks(cont_navigate).throttleFirst(2,TimeUnit.SECONDS).subscribe{
                listener.onHospitalNavigateClick(position)
            }

        }
    }

    interface onHospitalClick {
        fun onHospitalCallClick(position: Int)
        fun onHospitalNavigateClick(position: Int)
    }
}