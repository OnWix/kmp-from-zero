package com.bvic.followmycrypto.model

data class Crypto(
    val symbol: String,
    val name: String,
    val price: Double,
    val changePercent: Double,
    val lowPrice: Double = 0.0,
    val highPrice: Double = 0.0,
    )
