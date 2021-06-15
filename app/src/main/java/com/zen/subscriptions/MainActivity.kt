package com.zen.subscriptions

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zen.subscriptions.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.confirmation.text = getConfirmation()
    }

    private fun getConfirmation(): String {
        val productId = intent.extras?.getString("productID", "")
        val orderID = intent.extras?.getString("orderID", "")
        val ack = intent.extras?.getString("ack", " ")
        val payload = intent.extras?.getString("payload", "")
        val original = intent.extras?.getString("original", "")
        val token = intent.extras?.getString("token", "")
        val signature = intent.extras?.getString("signature", "")
        val quantity = intent.extras?.getString("quantity", "")
        return "Successfully subscribed to $productId. \n" +
                "OrderID - $orderID \n " +
                "Acknowledged - $ack \n " +
                "Payload - $payload \n " +
                "Token - $token \n " +
                "Signature - $signature \n " +
                "Quantity - $quantity \n \n \t Original - $original"
    }


}