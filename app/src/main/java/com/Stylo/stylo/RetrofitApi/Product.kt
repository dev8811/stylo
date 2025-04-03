package com.Stylo.stylo.RetrofitApi

import android.os.Parcel
import android.os.Parcelable

data class Product(
    val product_id: Int,
    val name: String,
    val description: String,
    val price: String,
    val stock_quantity: Int,
    val category_id: Int,
    val primary_image: String,
    val all_images: List<String>,
    val is_active: Boolean,
    val sizes: List<String> // ✅ Fixed: Now a List<String> instead of String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: listOf(),
        parcel.readByte() != 0.toByte(),
        parcel.createStringArrayList() ?: listOf() // ✅ Fixed: Properly reading List<String>
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(product_id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(price)
        parcel.writeInt(stock_quantity)
        parcel.writeInt(category_id)
        parcel.writeString(primary_image)
        parcel.writeStringList(all_images)
        parcel.writeByte(if (is_active) 1 else 0)
        parcel.writeStringList(sizes) // ✅ Fixed: Writing List<String> properly
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
