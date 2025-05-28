package com.example.lab1.ui.feature.orderhistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lab1.R

class OrderHistoryAdapter(
    private val orderHistoryList: List<OrderHistoryItem>
) : RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_order_history, parent, false)
        return OrderHistoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val currentItem = orderHistoryList[position]
        val context = holder.itemView.context

        holder.orderIdTextView.text = context.getString(R.string.order_history_item_id_label, currentItem.orderId)
        holder.orderDateTextView.text = context.getString(R.string.order_history_item_date_label, currentItem.date)
        holder.orderTotalTextView.text = context.getString(R.string.order_history_item_total_label, currentItem.totalAmount)
        holder.orderStatusTextView.text = context.getString(R.string.order_history_item_status_label, currentItem.status)
    }

    override fun getItemCount() = orderHistoryList.size

    class OrderHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderIdTextView: TextView = itemView.findViewById(R.id.textViewOrderId)
        val orderDateTextView: TextView = itemView.findViewById(R.id.textViewOrderDate)
        val orderTotalTextView: TextView = itemView.findViewById(R.id.textViewOrderTotal)
        val orderStatusTextView: TextView = itemView.findViewById(R.id.textViewOrderStatus)
    }
} 