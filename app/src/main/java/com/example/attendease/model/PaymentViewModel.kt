package com.example.attendease.model

import android.app.Activity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import com.example.attendease.payment.PaymentState
import com.example.attendease.payment.RazorpayConfig
import com.razorpay.Checkout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject

class PaymentViewModel : ViewModel() {
    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
       val paymentState = _paymentState.asStateFlow()

    var userName: String = "User"

    fun startPayment(activity: Activity, amount:Int, description:String,color: Color){
        try{
            val options = JSONObject().apply {
                put("name", "ATTENDEASE")
                put("description", description)
                put("currency", "INR")
                put("amount", (amount * 100).toLong()) // Razorpay uses paise, not rupees
                put("theme.color", String.format("#%06X", 0xFFFFFF and color.toArgb())) // color as hex string

                put("method", JSONObject().apply {
                    put("upi", true)
                })

                put("upi", JSONObject().apply {
                    put("flow", "intent")
                })

                put("prefill", JSONObject().apply {
                    put("contact", userName)
                })

                put("readonly", JSONObject().apply {
                    put("contact", false)
                    put("email", false)
                })
            }

            val checkout = Checkout()
            checkout.setKeyID(RazorpayConfig.KEY_ID)
            checkout.open(activity,options)
        }catch (e:Exception){
            _paymentState.value=PaymentState.Error(e.message.toString())
        }
    }

    fun handlePaymentSuccess(paymentId:String){
        _paymentState.value=PaymentState.Success(paymentId)
    }

    fun handlePaymentError(){
        _paymentState.value=PaymentState.Error("Payment Failed due to Unknown Reasons so please TRY AGAIN")
    }
    fun resetPaymentState() {
        _paymentState.value = PaymentState.Idle
    }

}