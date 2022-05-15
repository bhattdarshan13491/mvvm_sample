package com.oncobuddy.app.views.adapters

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.oncobuddy.app.R
import com.oncobuddy.app.models.pojo.profile.WalletTransaction
import com.oncobuddy.app.utils.Constants
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class WalletTransactionsAdapter (private val transactions: ArrayList<WalletTransaction>):
    RecyclerView.Adapter<WalletTransactionsAdapter.WalletTransactionVH>() {

    @RequiresApi(Build.VERSION_CODES.O)
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletTransactionVH {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.wallet_transaction_listitem, parent, false)
        return WalletTransactionVH(view)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: WalletTransactionVH, position: Int) {
        holder.tvReference.text = transactions.get(position).reference
        var amtInr = transactions.get(position).amount /100
        val solution:Double = String.format("%.1f", amtInr).toDouble()
        holder.tvAmount.text = "\u20B9 "+ solution
        holder.tvDateTime.text = changeTimeFormat(transactions.get(position).createdOn.toString())
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    private fun changeTimeFormat(inputTime: String): String? {
        return try {
            val simpleTimeFormatInput = SimpleDateFormat(
                Constants.INPUT_DATE_FORMAT,
                Locale.getDefault()
            )
            val simpleTimeFormatOutput = SimpleDateFormat(
                "dd-MM-yyyy hh:mm aa",
                Locale.getDefault()
            )
            val mDateTime = simpleTimeFormatInput.parse(inputTime)
            simpleTimeFormatOutput.format(mDateTime)
        } catch (e: ParseException) {
            inputTime
        }

    }

    class WalletTransactionVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvReference: TextView = itemView.findViewById(R.id.tvId)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        /*val tvCredit: TextView = itemView.findViewById(R.id.tvCredit)*/
        val tvDateTime: TextView = itemView.findViewById(R.id.tvDate)

    }

}


