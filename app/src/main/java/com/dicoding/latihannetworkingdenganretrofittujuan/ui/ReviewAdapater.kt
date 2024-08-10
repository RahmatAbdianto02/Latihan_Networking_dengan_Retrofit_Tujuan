package com.dicoding.latihannetworkingdenganretrofittujuan.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.latihannetworkingdenganretrofittujuan.data.response.CustomerReviewsItem
import com.dicoding.latihannetworkingdenganretrofittujuan.databinding.ItemReviewBinding

class ReviewAdapater : ListAdapter<CustomerReviewsItem,ReviewAdapater.MyViewHolder>(DIFF_CALLBACK) {
    class MyViewHolder (val binding: ItemReviewBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(review: CustomerReviewsItem){
            binding.tvItem.text ="${review.review}\n- ${review.name}"
        }



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =   ItemReviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val review = getItem(position)
        holder.bind(review)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CustomerReviewsItem>(){
            override fun areItemsTheSame(
                oldItem: CustomerReviewsItem,
                newItem: CustomerReviewsItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: CustomerReviewsItem,
                newItem: CustomerReviewsItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}