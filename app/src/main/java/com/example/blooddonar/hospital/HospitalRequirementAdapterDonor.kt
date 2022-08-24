package com.example.blooddonar.hospital

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.blooddonar.R
import com.example.blooddonar.constants.hospitalsRequirements
import kotlinx.android.synthetic.main.hospital_rec.view.*


class HospitalRequirementAdapterDonor(
    val context: Context,
) :
    RecyclerView.Adapter<HospitalRequirementAdapterDonor.HospitalRequirementAdapterDonor>() {
    class HospitalRequirementAdapterDonor(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HospitalRequirementAdapterDonor {
        return HospitalRequirementAdapterDonor(
            LayoutInflater.from(context).inflate(R.layout.hospital_rec, parent, false)
        )
    }

    override fun getItemCount(): Int = hospitalsRequirements.size

    @SuppressLint("CheckResult", "SetTextI18n")
    override fun onBindViewHolder(holder: HospitalRequirementAdapterDonor, position: Int) {
        val item = hospitalsRequirements[position]

        holder.itemView.apply {

            tv_hospital_name.text = item.hospital.capitalize()
            tv_number.text = item.bags + " " + "Bags"
            tv_hos_name.text = item.address.capitalize()
            req_blood.text = item.blood.capitalize()
            tv_req.text = item.req.capitalize()


        }
    }
}