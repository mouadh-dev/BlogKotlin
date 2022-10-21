package com.example.Blog.Entity

import android.os.Parcel
import android.os.Parcelable

data class PostItem(
    var id:String? = "",
    var idUser: String? = "",
    var contentPost: String?= "",
    var datePost: String? = "",
    var hourPPost: String? = "",
    var picturePost:String? = "",
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(idUser)
        parcel.writeString(contentPost)
        parcel.writeString(datePost)
        parcel.writeString(hourPPost)
        parcel.writeString(picturePost)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PostItem> {
        override fun createFromParcel(parcel: Parcel): PostItem {
            return PostItem(parcel)
        }

        override fun newArray(size: Int): Array<PostItem?> {
            return arrayOfNulls(size)
        }
    }
}