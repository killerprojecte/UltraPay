package org.fastmcmirror.ultrapay.commands

import com.egzosn.pay.wx.api.WxPayService
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.fastmcmirror.ultrapay.UltraPay
import org.fastmcmirror.ultrapay.api.PayAPI
import org.fastmcmirror.ultrapay.api.PaymentType
import org.fastmcmirror.ultrapay.gui.PayGUIHolder
import org.fastmcmirror.ultrapay.utils.Color.color
import org.fastmcmirror.ultrapay.utils.RewardParser
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.mainCommand

@CommandHeader(
    name = "ultrapay",
    aliases = arrayOf("upay", "cz"),
    permission = "ultrapay.use",
    permissionDefault = PermissionDefault.TRUE
)
object PayCommand {
    @CommandBody
    var main = mainCommand {
        literal("open") {
            dynamic {
                execute<Player> { sender, _, argument ->
                    run {
                        if (!argument.matches("-?\\d+(\\.\\d+)?".toRegex())) {
                            sender.sendMessage("&c&l参数无效: 请传入一个数字!".color())
                            return@run
                        }
                        if (argument.toDouble() < 0.01) {
                            sender.sendMessage("&c&l参数无效: 传入的数字过小!".color())
                            return@run
                        }
                        val holder = PayGUIHolder(argument.toDouble())
                        sender.openInventory(holder.inventory)
                    }
                }
            }
        }
        literal("pay") {
            literal("wx") {
                dynamic {
                    execute<Player> { sender, _, argument ->
                        run {
                            if (!argument.matches("-?\\d+(\\.\\d+)?".toRegex())) {
                                sender.sendMessage("&c&l参数无效: 请传入一个数字!".color())
                                return@run
                            }
                            if (argument.toDouble() < 0.01) {
                                sender.sendMessage("&c&l参数无效: 传入的数字过小!".color())
                                return@run
                            }
                            val price = argument.toDouble()
                            val future = PayAPI.createOrder(
                                PaymentType.WECHAT_PAY,
                                UltraPay.bukkitInstance.config.getString("itemName")!!
                                    .replace("%price%", price.toString()),
                                price,
                                sender
                            )
                            future.thenAcceptAsync { order ->
                                RewardParser.parseReward(UltraPay.bukkitInstance.config.getStringList("reward"), order)
                            }
                        }
                    }
                }
            }
            literal("zfb") {
                dynamic {
                    execute<Player> { sender, _, argument ->
                        run {
                            if (!argument.matches("-?\\d+(\\.\\d+)?".toRegex())) {
                                sender.sendMessage("&c&l参数无效: 请传入一个数字!".color())
                                return@run
                            }
                            if (argument.toDouble() < 0.01) {
                                sender.sendMessage("&c&l参数无效: 传入的数字过小!".color())
                                return@run
                            }
                            val price = argument.toDouble()
                            val future = PayAPI.createOrder(
                                PaymentType.ALIPAY,
                                UltraPay.bukkitInstance.config.getString("itemName")!!
                                    .replace("%price%", price.toString()),
                                price,
                                sender
                            )
                            future.thenAcceptAsync { order ->
                                RewardParser.parseReward(UltraPay.bukkitInstance.config.getStringList("reward"), order)
                            }
                        }
                    }
                }
            }
        }
        literal("reload", permission = "ultrapay.admin") {
            execute<CommandSender> { sender, _, _ ->
                run {
                    sender.sendMessage("&b&lUltraPay &7> &a&l插件配置已重载!".color())
                    UltraPay.bukkitInstance.reloadConfig()
                    UltraPay.wxPayService = WxPayService(UltraPay.getWxConfig())
                    UltraPay.aliPayService = UltraPay.getAliConfig()
                }
            }
        }
        execute<CommandSender> { sender, context, _ ->
            run {
                sender.sendMessage(("&b&lUltraPay &7- &ev" + UltraPay.bukkitInstance.description.version).color())
                sender.sendMessage("&c/ultrapay open <金额> &7———— &e打开支付方式选择界面".color())
                sender.sendMessage("&c/ultrapay pay <付款方式: wx/zfb> <金额> &7———— &e获取支付二维码".color())
                if (sender.hasPermission("ultrapay.admin")) {
                    sender.sendMessage("&c/ultrapay reload &7———— &e重载插件".color())
                }
            }
        }
    }
}