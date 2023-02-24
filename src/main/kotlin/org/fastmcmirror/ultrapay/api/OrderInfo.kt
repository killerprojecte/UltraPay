package org.fastmcmirror.ultrapay.api

import org.bukkit.entity.Player

data class OrderInfo(
    val orderId: String,
    val paymentOrderId: String,
    val price: Double,
    val itemName: String,
    val player: Player
) {

}
