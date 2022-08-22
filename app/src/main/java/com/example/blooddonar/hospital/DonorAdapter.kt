package com.example.blooddonar.hospital

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.blooddonar.R
import com.example.blooddonar.base.BaseActivity
import com.example.blooddonar.constants.users
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.donors_recycler.view.*
import java.util.concurrent.TimeUnit

class DonorAdapter(
    val context: Context,
    var userId: String,
//    private val items: ArrayList<GetFacilityDataPitchsize>,
    private val listener: OnDonarClick
) :
    RecyclerView.Adapter<DonorAdapter.DonorAdapter>() {
    class DonorAdapter(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DonorAdapter {
        return DonorAdapter(
            LayoutInflater.from(context).inflate(R.layout.donors_recycler, parent, false)
        )
    }

    override fun getItemCount(): Int = users.size

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: DonorAdapter, position: Int) {
        holder.itemView.apply {
            val circularProgressDrawable = CircularProgressDrawable(context)
            circularProgressDrawable.centerRadius = 25f
            circularProgressDrawable.start()

            userId = FirebaseAuth
                .getInstance()
                .currentUser
                ?.uid.toString()

            val item = users[position]

            if (userId != item.uid) {

                if (item.online == "true") {
                    if (!item.profileImage.isNullOrEmpty()) {
                        Glide.with(this).load(item.profileImage)
                            .placeholder(circularProgressDrawable)
                            .into(profile_image)
                    } else {
                        Glide.with(this).load(R.drawable.ic_user)
                            .placeholder(circularProgressDrawable)
                            .into(profile_image)
                    }

                    name.text = item.firstName.capitalize() + " " + item.lastName.capitalize()
                    city.text = item.city.capitalize()
                    tv_number.text = item.phoneNumber
                    tv_hos_name.text = item.address
                    tv_blood.text = item.blood.capitalize()
                }

            }

            RxView.clicks(cont_message).throttleFirst(2, TimeUnit.SECONDS).subscribe {
                listener.onMessageClick(position)
            }

            RxView.clicks(cont_call).throttleFirst(2, TimeUnit.SECONDS).subscribe {
                listener.onCallClick(position)
            }
        }
    }

    interface OnDonarClick {
        fun onMessageClick(position: Int)
        fun onCallClick(position: Int)
    }
}