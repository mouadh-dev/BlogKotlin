package com.example.Blog.Entity

import android.os.Parcel
import android.os.Parcelable

data class UserItem(
    var fullname: String? = "",
    var mail: String = "",
    var password: String = "",
    var confirmpassword: String? = "",
    var id: String? = "",
    var profilePhoto: String? = null,
): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fullname)
        parcel.writeString(mail)
        parcel.writeString(password)
        parcel.writeString(confirmpassword)
        parcel.writeString(id)
        parcel.writeString(profilePhoto)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserItem> {
        override fun createFromParcel(parcel: Parcel): UserItem {
            return UserItem(parcel)
        }

        override fun newArray(size: Int): Array<UserItem?> {
            return arrayOfNulls(size)
        }
    }
}