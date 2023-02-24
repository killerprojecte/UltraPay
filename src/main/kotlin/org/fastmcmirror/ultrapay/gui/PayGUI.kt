package org.fastmcmirror.ultrapay.gui

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.fastmcmirror.ultrapay.UltraPay
import org.fastmcmirror.ultrapay.api.OrderInfo
import org.fastmcmirror.ultrapay.api.PayAPI
import org.fastmcmirror.ultrapay.api.PaymentType
import org.fastmcmirror.ultrapay.utils.RewardParser
import java.util.concurrent.CompletableFuture

class PayGUI : Listener {
    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (event.isCancelled) return
        if (event.clickedInventory == null) return
        if (event.inventory.holder == null) return
        if (!(event.inventory.holder is PayGUIHolder)) return
        event.isCancelled = true
        val holder = event.inventory.holder as PayGUIHolder
        val future: CompletableFuture<OrderInfo>
        if (event.slot == 3) {
            future = PayAPI.createOrder(
                PaymentType.WECHAT_PAY,
                UltraPay.bukkitInstance.config.getString("itemName")!!.replace("%price%", holder.price.toString()),
                holder.price,
                event.whoClicked as Player
            )
            event.whoClicked.closeInventory()
        } else if (event.slot == 5) {
            future = PayAPI.createOrder(
                PaymentType.ALIPAY,
                UltraPay.bukkitInstance.config.getString("itemName")!!.replace("%price%", holder.price.toString()),
                holder.price,
                event.whoClicked as Player
            )
            event.whoClicked.closeInventory()
        } else {
            return
        }
        future.thenAcceptAsync { order ->
            RewardParser.parseReward(UltraPay.bukkitInstance.config.getStringList("reward"), order)
        }
    }
}