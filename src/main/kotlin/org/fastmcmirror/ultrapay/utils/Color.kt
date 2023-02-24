package org.fastmcmirror.ultrapay.utils

import org.bukkit.ChatColor

object Color {
    fun String.color(): String {
        return ChatColor.translateAlternateColorCodes('&', this)
    }
}