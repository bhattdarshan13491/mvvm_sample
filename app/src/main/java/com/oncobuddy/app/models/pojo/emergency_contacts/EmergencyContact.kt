package com.oncobuddy.app.models.pojo.emergency_contacts

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "emergency_contact")
class EmergencyContact {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "emergencyContactId")
    var emergencyContactId = 0

    @kotlin.jvm.JvmField
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: String? = null

    @kotlin.jvm.JvmField
    @SerializedName("fullName")
    @Expose
    @ColumnInfo(name = "fullName")
    var fullName: String? = null

    @kotlin.jvm.JvmField
    @SerializedName("mobileCode")
    @Expose
    @ColumnInfo(name = "mobileCode")
    var mobileCode: String? = null

    @kotlin.jvm.JvmField
    @SerializedName("phoneNumber")
    @Expose
    @ColumnInfo(name = "phoneNumber")
    var phoneNumber: String? = null

    @kotlin.jvm.JvmField
    @SerializedName("emailAddress")
    @Expose
    @ColumnInfo(name = "emailAddress")
    var emailAddress: String? = null

}