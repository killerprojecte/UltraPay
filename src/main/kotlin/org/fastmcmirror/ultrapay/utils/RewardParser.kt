package org.fastmcmirror.ultrapay.utils

import org.bukkit.Bukkit
import org.fastmcmirror.ultrapay.UltraPay
import org.fastmcmirror.ultrapay.api.OrderInfo
import org.fastmcmirror.ultrapay.utils.Color.color
import taboolib.common.util.sync

class RewardParser {
    companion object {
        fun parseReward(list: List<String>, orderInfo: OrderInfo) {
            for (line in list) {
                sync {
                    if (line.startsWith("[command] ")) {
                        Bukkit.dispatchCommand(
                            Bukkit.getConsoleSender(), line
                                .substring(10)
                                .replace("%price%", orderInfo.price.toString())
                                .replace(
                                    "%point%", (orderInfo.price * UltraPay.bukkitInstance.config.getInt("point"))
                                        .toInt().toString()
                                )
                                .replace("%player%", orderInfo.player.name).color()
                        )
                    } else if (line.startsWith("[message] ")) {
                        orderInfo.player.sendMessage(
                            line
                                .substring(10)
                                .replace("%price%", orderInfo.price.toString())
                                .replace(
                                    "%point%", (orderInfo.price * UltraPay.bukkitInstance.config.getInt("point"))
                                        .toInt().toString()
                                )
                                .replace("%player%", orderInfo.player.name).color()
                        )
                    } else if (line.startsWith("[broadcast] ")) {
                        for (online in Bukkit.getOnlinePlayers()) {
                            online.sendMessage(
                                line
                                    .substring(12)
                                    .replace("%price%", orderInfo.price.toString())
                                    .replace(
                                        "%point%", (orderInfo.price * UltraPay.bukkitInstance.config.getInt("point"))
                                            .toInt().toString()
                                    )
                                    .replace("%player%", orderInfo.player.name).color()
                            )
                        }
                    } else if (line.startsWith("[title] ")) {
                        val args = line.substring(8).split("||");
                        orderInfo.player.sendTitle(
                            args[0].replace("%price%", orderInfo.price.toString())
                                .replace(
                                    "%point%", (orderInfo.price * UltraPay.bukkitInstance.config.getInt("point"))
                                        .toInt().toString()
                                )
                                .replace("%player%", orderInfo.player.name).color(),
                            args[1].replace("%price%", orderInfo.price.toString())
                                .replace(
                                    "%point%", (orderInfo.price * UltraPay.bukkitInstance.config.getInt("point"))
                                        .toInt().toString()
                                )
                                .replace("%player%", orderInfo.player.name).color(),
                            args[2].toInt(),
                            args[3].toInt(),
                            args[4].toInt()
                        )
                    } else {

                    }
                }
            }
        }
    }
}