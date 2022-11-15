package k.studio.tiktokrec.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import k.studio.tiktokrec.data.vo.db.OrderHeart
import k.studio.tiktokrec.databinding.ItemOrderHeartBinding

class OrderHeartPagingDataAdapter :
    PagingDataAdapter<OrderHeart, OrderHeartViewHolder>(OrderHeartComparator) {

    override fun onBindViewHolder(holder: OrderHeartViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHeartViewHolder {
        return OrderHeartViewHolder.from(parent)
    }

    object OrderHeartComparator : DiffUtil.ItemCallback<OrderHeart>() {
        override fun areItemsTheSame(oldItem: OrderHeart, newItem: OrderHeart): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrderHeart, newItem: OrderHeart): Boolean {
            return oldItem == newItem
        }
    }
}

class OrderHeartViewHolder(val binding: ItemOrderHeartBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): OrderHeartViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemOrderHeartBinding.inflate(layoutInflater, parent, false)
            return OrderHeartViewHolder(binding)
        }
    }

    fun bind(item: OrderHeart?) {
        item?.let { orderHeart ->
            binding.heartsNumber.text = orderHeart.heartsNumber.toString()
            binding.videoLink.text = orderHeart.videoLink
        }
    }
}