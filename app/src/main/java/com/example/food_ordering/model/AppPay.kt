package com.example.food_ordering.model

import android.app.Application
import com.paypal.checkout.PayPalCheckout
import com.paypal.checkout.config.CheckoutConfig
import com.paypal.checkout.config.Environment
import com.paypal.checkout.config.SettingsConfig
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.UserAction

class AppPay : Application() {
    override fun onCreate() {
        super.onCreate()
        val YOUR_CLIENT_ID = "AeTymQIeZjuVP4ZKxpuKHfLeSZfY3rhCt1K0CQ9fNZ5PBbyV9s2dylyBW_bs27b-msOlkbn-f7yd6_1y"
        val returnUrl = "nativexo://paypalpay"
        val config = CheckoutConfig(
            application = this,
            clientId = YOUR_CLIENT_ID,
            environment = Environment.SANDBOX,
            returnUrl = returnUrl,
            currencyCode = CurrencyCode.USD,
            userAction = UserAction.PAY_NOW,
            settingsConfig = SettingsConfig(
                loggingEnabled = true
            )
        )
        PayPalCheckout.setConfig(config)
    }
}