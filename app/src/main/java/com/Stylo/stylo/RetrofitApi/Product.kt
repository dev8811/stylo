package com.Stylo.stylo.RetrofitApi

import android.os.Parcel
import android.os.Parcelable

data class Product(
    val categoryid: String,
    val description: String,
    val discount: String,
    val originalprice: String,
    val productid: String,
    val productimage: String,
    val productname: String,
    val rating: String,
    val review: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(categoryid)
        parcel.writeString(description)
        parcel.writeString(discount)
        parcel.writeString(originalprice)
        parcel.writeString(productid)
        parcel.writeString(productimage)
        parcel.writeString(productname)
        parcel.writeString(rating)
        parcel.writeString(review)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}