package com.example.blooddonar.admin

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.blooddonar.R
import com.example.blooddonar.constants.acceptors
import com.example.blooddonar.constants.hospitals
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.add_acceptor.view.*
import java.util.concurrent.TimeUnit

class AdminHospitalModel(
    val context: Context,
    private val listener: OnHospitalClick
) :
    RecyclerView.Adapter<AdminHospitalModel.AdminHospitalModel>() {
    class AdminHospitalModel(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdminHospitalModel {
        return AdminHospitalModel(
            LayoutInflater.from(context).inflate(R.layout.admin_hospital, parent, false)
        )
    }

    override fun getItemCount(): Int = hospitals.size

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: AdminHospitalModel, position: Int) {
        val item = hospitals[position]

        holder.itemView.apply {

            name.text = item.name.capitalize()
            city.text = item.city.capitalize()
            address.text = item.address.capitalize()

            RxView.clicks(cont_delete).throttleFirst(2, TimeUnit.SECONDS).subscribe {
                listener.onHospitalDeleteClick(position)
            }

            RxView.clicks(cont_update).throttleFirst(2, TimeUnit.SECONDS).subscribe {
                listener.onHospitalUpdateClick(position)
            }


        }
    }

    interface OnHospitalClick {
        fun onHospitalUpdateClick(position: Int)
        fun onHospitalDeleteClick(position: Int)
    }
}