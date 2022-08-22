package com.example.blooddonar.donar

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
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.acceptor_recycler.view.*
import java.util.concurrent.TimeUnit


class AcceptorAdapter(
    val context: Context,
    var userId: String,
    private val listener: OnPositionClick
) :
    RecyclerView.Adapter<AcceptorAdapter.AcceptorAdapter>() {
    class AcceptorAdapter(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AcceptorAdapter {
        return AcceptorAdapter(
            LayoutInflater.from(context).inflate(R.layout.acceptor_recycler, parent, false)
        )
    }

    override fun getItemCount(): Int = acceptorRequirements.size

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: AcceptorAdapter, position: Int) {
        val item = acceptorRequirements[position]

        holder.itemView.apply {
            val circularProgressDrawable = CircularProgressDrawable(context)
            circularProgressDrawable.centerRadius = 25f
            circularProgressDrawable.start()

            userId = FirebaseAuth
                .getInstance()
                .currentUser
                ?.uid.toString()

            if (userId != item.uid) {

                if (!item.profileImage.isNullOrEmpty()) {
                    Glide.with(this).load(item.profileImage)
                        .placeholder(circularProgressDrawable)
                        .into(acceptor_image)
                } else {
                    Glide.with(this).load(R.drawable.ic_user)
                        .placeholder(circularProgressDrawable)
                        .into(acceptor_image)
                }

                name.text = item.name.capitalize()
                city.text = item.city.capitalize()
                tv_number.text = item.phoneNumber
                tv_hos_name.text = item.hospital.capitalize()
                tv_diagnosis_name.text = item.diagnosis.capitalize()
                req_blood.text = item.blood.capitalize()
                tv_req.text = item.req.capitalize()

                RxView.clicks(cont_call).throttleFirst(2, TimeUnit.SECONDS).subscribe {
                    listener.onAcceptorCallClick(position)
                }

                RxView.clicks(cont_message).throttleFirst(2, TimeUnit.SECONDS).subscribe {
                    listener.onAcceptorMessageClick(position)
                }

            }
        }
    }

    interface OnPositionClick {
        fun onAcceptorMessageClick(position: Int)
        fun onAcceptorCallClick(position: Int)
    }
}