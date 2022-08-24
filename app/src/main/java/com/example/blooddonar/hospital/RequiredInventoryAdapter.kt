package com.example.blooddonar.hospital

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.example.blooddonar.R
import com.example.blooddonar.constants.hospitalRequiredStock
import com.example.blooddonar.constants.hospitals
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.required_inventory_hospital.view.*
import java.util.concurrent.TimeUnit

class RequiredInventoryAdapter(
    val context: Context,
    private val listener: CheckInventory
) :
    RecyclerView.Adapter<RequiredInventoryAdapter.RequiredInventoryAdapter>() {
    class RequiredInventoryAdapter(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RequiredInventoryAdapter {
        return RequiredInventoryAdapter(
            LayoutInflater.from(context)
                .inflate(R.layout.required_inventory_hospital, parent, false)
        )
    }

    override fun getItemCount(): Int = hospitalRequiredStock.size

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: RequiredInventoryAdapter, position: Int) {
        holder.itemView.apply {

            val item = hospitalRequiredStock[position]


            name.text = item.name.capitalize()
            city.text = item.city
            address.text = item.address.capitalize()
            tv_req.text = item.req.capitalize()
            req_blood.text = item.blood.capitalize()
            req_bags.text = item.bags + " " + "Bags"

            RxView.clicks(cont_delete).throttleFirst(2, TimeUnit.SECONDS).subscribe {
                listener.onInventoryDeleteClick(position)
            }
            RxView.clicks(cont_update).throttleFirst(2, TimeUnit.SECONDS).subscribe {
                listener.onInventoryUpdateClick(position)
            }
        }
    }

    interface CheckInventory {
        fun onInventoryDeleteClick(position: Int)
        fun onInventoryUpdateClick(position: Int)
    }
}