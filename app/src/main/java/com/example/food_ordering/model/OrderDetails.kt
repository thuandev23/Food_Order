package com.example.food_ordering.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.util.ArrayList

class OrderDetails(): Serializable {
    var userUId:String ?= null
    var userName:String ?= null
    var address:String ?= null
    var totalPrice:String ?= null
    var phoneNumber:String ?= null
    var orderAccepted:Boolean = false
    var paymentReceived:Boolean = false
    var itemPushKey:String ?= null
    var currentTime:Long = 0
    var foodNames:MutableList<String> ?= null
    var foodImages:MutableList<String> ?= null
    var foodPrices:MutableList<String> ?= null
    var foodQuantities:MutableList<Int> ?= null

    constructor(parcel: Parcel) : this() {
        userUId = parcel.readString()
        userName = parcel.readString()
        address = parcel.readString()
        totalPrice = parcel.readString()
        phoneNumber = parcel.readString()
        orderAccepted = parcel.readByte() != 0.toByte()
        paymentReceived = parcel.readByte() != 0.toByte()
        itemPushKey = parcel.readString()
        currentTime = parcel.readLong()
    }

    constructor (
        userId: String,
        name: String,
        foodItemsName: ArrayList<String>,
        foodItemPrices: ArrayList<String>,
        foodImage: ArrayList<String>,
        foodQuantity: ArrayList<Int>,
        address: String,
        phone: String,
        time: Long,
        itemPushKey: String?,
        b1: Boolean,
        b2: Boolean
    ) : this(){
        this.userUId = userId
        this.userName = name
        this.foodNames = foodItemsName
        this.foodPrices = foodItemPrices
        this.foodImages = foodImage
        this.foodQuantities = foodQuantity
        this.address = address
        this.phoneNumber = phone
        this.currentTime = time
        this.itemPushKey = itemPushKey
        this.orderAccepted = orderAccepted
        this.paymentReceived = paymentReceived

    }

    fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userUId)
        parcel.writeString(userName)
        parcel.writeString(address)
        parcel.writeString(totalPrice)
        parcel.writeString(phoneNumber)
        parcel.writeByte(if (orderAccepted) 1 else 0)
        parcel.writeByte(if (paymentReceived) 1 else 0)
        parcel.writeString(itemPushKey)
        parcel.writeLong(currentTime)
    }

    fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrderDetails> {
        override fun createFromParcel(parcel: Parcel): OrderDetails {
            return OrderDetails(parcel)
        }

        override fun newArray(size: Int): Array<OrderDetails?> {
            return arrayOfNulls(size)
        }
    }
}