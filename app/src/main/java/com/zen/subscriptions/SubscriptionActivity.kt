package com.zen.subscriptions

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.billingclient.api.*
import com.zen.subscriptions.databinding.ActivitySubscriptionBinding

class SubscriptionActivity : AppCompatActivity(), PurchasesUpdatedListener, ItemClickListener,
    ConsumeResponseListener, AcknowledgePurchaseResponseListener, PurchasesResponseListener {

    private lateinit var binding: ActivitySubscriptionBinding
    private lateinit var billingClient: BillingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubscriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setUpBillingClient()

        binding.get.setOnClickListener {
            if (billingClient.isReady) {
                billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS, this)
            } else {
                Toast.makeText(this, "Please wait..", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun getSubscriptions() {
        val skuList = ArrayList<String>()
        skuList.add("yearly_sub")
        skuList.add("monthly_sub")
        skuList.add("sub_in_app")
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(skuList)
            .setType(BillingClient.SkuType.SUBS)
            .build()
        billingClient.querySkuDetailsAsync(
            params
        ) { billingResult, list ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK)
                loadSubscriptions(list)
        }
    }

    private fun loadSubscriptions(skuDetailsList: List<SkuDetails>?) {
        runOnUiThread {
            val recyclerAdapter = skuDetailsList?.let { Adapter(it, this) }
            binding.recycler.apply {
                adapter = recyclerAdapter
                layoutManager = LinearLayoutManager(this@SubscriptionActivity)
            }
        }
        Log.e("Sku", skuDetailsList.toString())
    }

    private fun setUpBillingClient() {
        billingClient = BillingClient.newBuilder(this)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Toast.makeText(this@SubscriptionActivity, "Connected", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onBillingServiceDisconnected() {
                Toast.makeText(this@SubscriptionActivity, "Disconnected", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchaseList: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchaseList != null) {
            Toast.makeText(this, "Subscribed Successfully", Toast.LENGTH_SHORT).show()
            for (purchase in purchaseList) {
                handlePurchases(purchase)
            }
            nextActivity(purchaseList[0])
        }
    }

    private fun nextActivity(purchase: Purchase) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("productID", purchase.skus.toString())
        intent.putExtra("orderID", purchase.orderId)
        intent.putExtra("ack", purchase.isAcknowledged)
        intent.putExtra("payload", purchase.developerPayload)
        intent.putExtra("original", purchase.originalJson)
        intent.putExtra("token", purchase.purchaseToken)
        intent.putExtra("signature", purchase.signature)
        intent.putExtra("quantity", purchase.quantity)
        startActivity(intent)
        finish()
    }

    private fun handlePurchases(purchase: Purchase) {
        Log.e("Sku", purchase.toString())
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.consumeAsync(consumeParams, this)
        if (!purchase.isAcknowledged) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
            billingClient.acknowledgePurchase(acknowledgePurchaseParams.build(), this)
        } else {
            nextActivity(purchase)
        }
    }

    override fun onClick(skuDetails: SkuDetails) {
        val params = BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build()
        val responseCode = billingClient.launchBillingFlow(this, params)
        Log.e("Sku", responseCode.toString())
    }

    override fun onConsumeResponse(billingResult: BillingResult, string: String) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show()
    }

    override fun onAcknowledgePurchaseResponse(p0: BillingResult) {
        Toast.makeText(this, "Purchases Acknowledged", Toast.LENGTH_SHORT).show()
    }

    override fun onQueryPurchasesResponse(
        billingResult: BillingResult,
        purchaseList: MutableList<Purchase>
    ) {
        if (purchaseList.isEmpty())
            getSubscriptions()
        else
            for (purchase in purchaseList) {
                handlePurchases(purchase)
            }
    }
}