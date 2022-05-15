package com.oncobuddy.app.views.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.RawPdfImageBinding
import com.oncobuddy.app.models.pojo.PdfImage
import com.oncobuddy.app.utils.Constants
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class PdfPagesAdapter(private val context: Context, private val interaction: Interaction? = null, private val selectedItem: Int = 0) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PdfImage>() {

        override fun areItemsTheSame(oldItem: PdfImage, newItem: PdfImage): Boolean {
            return oldItem.index == newItem.index
        }

        override fun areContentsTheSame(oldItem: PdfImage, newItem: PdfImage): Boolean {
            return oldItem.equals(newItem)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val mBinder: RawPdfImageBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.raw_pdf_image, parent, false)

        return ShoppingItemVH(
            mBinder,
            context,
            interaction,
            selectedItem
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
        Log.d("pdf_adap_log", "adapter 0.1 size " + differ.currentList.size)
        return differ.currentList.size
    }

    fun submitList(list: List<PdfImage>) {
        differ.submitList(list)
    }



    class ShoppingItemVH constructor(
        binding: RawPdfImageBinding,
        context: Context,
        private val interaction: Interaction?,
        private val selectedItem: Int,
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RawPdfImageBinding = binding

        fun bind(item: PdfImage, pos : Int) {
            Log.d("pdf_adap_log", "Showing page "+item.index)
            Log.d("pdf_adap_log","Bitmap not null")
                val argUri = Uri.parse(item.path)
                Log.d("pdf_adap_log", "uri done")
                val bitmap = MediaStore.Images.Media.getBitmap(
                    FourBaseCareApp.activityFromApp.contentResolver,
                    argUri
                )
                Log.d("pdf_adap_log", "adap bitmp done done")
                mBinder.ivView.setImageBitmap(bitmap)

                if(selectedItem == pos){
                    mBinder.relContainer.background = ContextCompat.getDrawable(FourBaseCareApp.activityFromApp,R.drawable.red_border_gray_bg)
                }else{
                    mBinder.relContainer.background = ContextCompat.getDrawable(FourBaseCareApp.activityFromApp,R.drawable.blue_border_white_bg)
                }

            mBinder.relContainer.setOnClickListener(View.OnClickListener {
                interaction?.onPageSelected(pos, item, mBinder.root)
            })

                //Glide.with(FourBaseCareApp.activityFromApp).load(bitmap).into(mBinder.ivView)


        }

    }

    interface Interaction {
        fun onPageSelected(position: Int, item: PdfImage, view : View)
    }






}
