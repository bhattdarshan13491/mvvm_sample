package com.oncobuddy.app.models.pojo.records_list

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "product")
@Parcelize()
data class Product(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "product_id")
    val productId: Int?,

    @ColumnInfo(name = "product_name")
    var productName: String,

    @ColumnInfo(name = "product_price")
    var productPrice: Double,

    @ColumnInfo(name = "product_img_url")
    var productImgUrl: String,

    @ColumnInfo(name = "product_is_sold")
    var product_is_sold: Boolean
) : Parcelable