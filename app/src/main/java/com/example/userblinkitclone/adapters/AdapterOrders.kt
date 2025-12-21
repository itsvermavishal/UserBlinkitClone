package com.example.userblinkitclone.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.userblinkitclone.R
import com.example.userblinkitclone.databinding.ItemViewOrdersBinding
import com.example.userblinkitclone.models.OrderedItem

class AdapterOrders(val context: Context, val onOrderItemViewClicked: (OrderedItem) -> Unit) : RecyclerView.Adapter<AdapterOrders.OrdersViewHolder>() {

    class OrdersViewHolder (val binding: ItemViewOrdersBinding): ViewHolder(binding.root)

    val diffUtil = object : DiffUtil.ItemCallback<OrderedItem>(){
        override fun areItemsTheSame(
            oldItem: OrderedItem,
            newItem: OrderedItem
        ): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(
            oldItem: OrderedItem,
            newItem: OrderedItem
        ): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffUtil)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewtype: Int
    ): OrdersViewHolder {
        return OrdersViewHolder(ItemViewOrdersBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(
        holder: OrdersViewHolder,
        position: Int
    ) {
        val order = differ.currentList[position]
        holder.binding.apply {
            tvOrderTitles.text = order.itemTitle
            tvOrderDate.text = order.itemDate
            tvOrderAmount.text = "₹${order.itemPrice.toString()}"

            when(order.itemStatus){
                0 -> {
                    tvOrderStatus.text = "Order Placed"
                    tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.yellow)
                }
                1 -> {
                    tvOrderStatus.text = "Received"
                    tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.blue)
                }
                2 -> {
                    tvOrderStatus.text = "Dispatched"
                    tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.green)
                }
                3 -> {
                    tvOrderStatus.text = "Delivered"
                    tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.orange)
                }
            }
        }
        holder.itemView.setOnClickListener {
            onOrderItemViewClicked(order)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}