package org.fastmcmirror.ultrapay.gui

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.fastmcmirror.ultrapay.utils.Color.color
import java.util.*


class PayGUIHolder(val price: Double) : InventoryHolder {
    private val inv: Inventory;

    init {
        inv = Bukkit.createInventory(this, 9, "&7[&bUltraPay 支付方式&7]".color())
        val wx: ItemStack =
            simpleSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWY5YTQwMWMzNzRhZDcwODNmMjVhZGNkZDUyYjQ2YTc0NzQyYzQ4YmEyZTM5ZGQ2YmMzZTAwMTAzZjJmOThkOSJ9fX0=")
        val wxmeta: ItemMeta = wx.getItemMeta()!!
        wxmeta.setDisplayName("&a&l微信".color())
        val wxlore: MutableList<String> = ArrayList()
        wxlore.add("&7&l点击使用微信付款".color())
        wxmeta.lore = wxlore
        wx.setItemMeta(wxmeta)
        val alipay: ItemStack =
            simpleSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA5YWRkNDZlMWMxZjNkMzBkOThkYjQ1NjNkNTMyNjE3MGUyZjk0ZjRlYTY5YWY2ZDJmNzc1NDk5ZTM3MGVmNCJ9fX0=")
        val alipaymeta: ItemMeta = alipay.getItemMeta()!!
        alipaymeta.setDisplayName("&b&l支付宝".color())
        val alipaylore: MutableList<String> = ArrayList()
        alipaylore.add("&7&l点击使用支付宝付款".color())
        alipaymeta.lore = alipaylore
        alipay.setItemMeta(alipaymeta)
        inv.setItem(3, wx)
        inv.setItem(5, alipay)
    }

    override fun getInventory(): Inventory {
        return inv
    }

    private fun simpleSkull(value: String): ItemStack {
        val uuid: UUID = UUID.nameUUIDFromBytes(value.toByteArray())
        return Bukkit.getUnsafe().modifyItemStack(
            ItemStack(Material.PLAYER_HEAD),
            "{SkullOwner:{Id:\"$uuid\",Properties:{textures:[{Value:\"$value\"}]}}}"
        )
    }
}