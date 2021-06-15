package com.zen.subscriptions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.Purchase

class PurchasedAdapter(
    private val purchases: List<Purchase>,
    private val listener: ItemClickListener
) :
    RecyclerView.Adapter<PurchasedAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.item_name)
        val price: TextView = view.findViewById(R.id.item_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.sub_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = purchases[position].orderId
        holder.price.text = purchases[position].isAcknowledged.toString()
    }

    override fun getItemCount(): Int = purchases.size
}