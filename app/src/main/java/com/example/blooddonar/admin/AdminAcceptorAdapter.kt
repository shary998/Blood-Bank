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
import com.example.blooddonar.constants.acceptorRequirements
import com.example.blooddonar.constants.acceptors
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.add_acceptor.view.*
import java.util.concurrent.TimeUnit

class AdminAcceptorAdapter(
    val context: Context,
    private val listener: OnPositionClick
) :
    RecyclerView.Adapter<AdminAcceptorAdapter.AdminAcceptorAdapter>() {
    class AdminAcceptorAdapter(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdminAcceptorAdapter {
        return AdminAcceptorAdapter(
            LayoutInflater.from(context).inflate(R.layout.add_acceptor, parent, false)
        )
    }

    override fun getItemCount(): Int = acceptors.size

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: AdminAcceptorAdapter, position: Int) {
        val item = acceptors[position]

        holder.itemView.apply {
            val circularProgressDrawable = CircularProgressDrawable(context)
            circularProgressDrawable.centerRadius = 25f
            circularProgressDrawable.start()


            if (!item.profileImage.isNullOrEmpty()) {
                Glide.with(this).load(item.profileImage)
                    .placeholder(circularProgressDrawable)
                    .into(iv_profile)
            } else {
                Glide.with(this).load(R.drawable.ic_user)
                    .placeholder(circularProgressDrawable)
                    .into(iv_profile)
            }

            name.text = item.firstName.capitalize() + " " + item.lastName.capitalize()
            city.text = item.city.capitalize()
            address.text = item.address.capitalize()
            req_blood.text = item.blood.capitalize()

            RxView.clicks(cont_delete).throttleFirst(2, TimeUnit.SECONDS).subscribe {
                listener.onAcceptorDeleteClick(position)
            }

            RxView.clicks(cont_update).throttleFirst(2, TimeUnit.SECONDS).subscribe {
                listener.onAcceptorUpdateClick(position)
            }


        }
    }

    interface OnPositionClick {
        fun onAcceptorUpdateClick(position: Int)
        fun onAcceptorDeleteClick(position: Int)
    }
}