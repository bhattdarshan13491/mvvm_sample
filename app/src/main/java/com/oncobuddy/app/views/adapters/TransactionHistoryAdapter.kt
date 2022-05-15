package com.oncobuddy.app.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.RawTransactionBinding
import com.oncobuddy.app.models.pojo.patient_transactions.Transaction
import com.oncobuddy.app.utils.CommonMethods


class TransactionHistoryAdapter(private var youtubeVideosList: ArrayList<Transaction>,
                                private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var youtubeFilterList = ArrayList<Transaction>()

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Transaction>() {

        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.transactionId == newItem.transactionId
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.equals(newItem)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val mBinder: RawTransactionBinding
        mBinder = DataBindingUtil.inflate(
            LayoutInflater
                .from(parent.context), R.layout.raw_transaction, parent, false
        )

        return ShoppingItemVH(
            mBinder,
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ShoppingItemVH -> {
                holder.bind(differ.currentList.get(position),position)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Transaction>) {
        differ.submitList(list)
    }



    class ShoppingItemVH constructor(
        binding: RawTransactionBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RawTransactionBinding = binding

        fun bind(item: Transaction, pos : Int) {
            mBinder.tvId.setText("Paid for Appointment ID: "+item.transactionId)
            //mBinder.tvDate.setText(""+item.paymentDateTime)
            mBinder.tvAmount.setText(item.currency+" "+item.amount)

            if(item.paymentDateTime.isNullOrEmpty()){
                mBinder.tvDate.setText("")
            }else{
                mBinder.tvDate.setText(CommonMethods.changeCOmmentDateTimeFormat(item.paymentDateTime))
            }
        }
    }

    interface Interaction {
        fun onSpecialistSelected(position: Int, item: Transaction, view : View)
    }



}
