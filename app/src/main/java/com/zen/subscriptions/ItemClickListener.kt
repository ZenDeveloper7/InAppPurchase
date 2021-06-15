package com.zen.subscriptions

import com.android.billingclient.api.SkuDetails

interface ItemClickListener {
    fun onClick(skuDetails: SkuDetails)
}