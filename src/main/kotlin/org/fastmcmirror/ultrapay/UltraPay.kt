package org.fastmcmirror.ultrapay

import com.alipay.api.AlipayClient
import com.alipay.api.AlipayConfig
import com.alipay.api.DefaultAlipayClient
import com.alipay.api.internal.util.AlipayLogger
import com.egzosn.pay.wx.api.WxPayConfigStorage
import com.egzosn.pay.wx.api.WxPayService
import org.bukkit.Bukkit
import org.fastmcmirror.ultrapay.gui.PayGUI
import taboolib.common.platform.Plugin
import taboolib.platform.util.bukkitPlugin


object UltraPay : Plugin() {

    override fun onEnable() {
        this.bukkitInstance = bukkitPlugin
        bukkitInstance.saveDefaultConfig()
        printLogo(
            "\n" +
                    "                              \n" +
                    "──────────────────────────────\n" +
                    "                              \n" +
                    "╦ ╦┬ ┌┬┐┬─┐┌─┐╔═╗┌─┐┬ ┬       \n" +
                    "║ ║│  │ ├┬┘├─┤╠═╝├─┤└┬┘       \n" +
                    "╚═╝┴─┘┴ ┴└─┴ ┴╩  ┴ ┴ ┴        \n" +
                    "                              \n" +
                    "──────────────────────────────\n" +
                    "Version: " + bukkitInstance.description.version + "\n" +
                    "Author: FlyProject\n" +
                    "(c) Copyright 2023 FlyProject\n" +
                    "──────────────────────────────\n" +
                    "Powered By TabooLib 6.0\n"
        )
        Bukkit.getPluginManager().registerEvents(PayGUI(), bukkitInstance)
        wxPayService = WxPayService(getWxConfig())
        aliPayService = getAliConfig()
    }

    @JvmStatic
    lateinit var wxPayService: WxPayService;

    @JvmStatic
    lateinit var aliPayService: AlipayClient;

    fun getWxConfig(): WxPayConfigStorage {
        val wxPayConfigStorage = WxPayConfigStorage()
        wxPayConfigStorage.setMchId(bukkitInstance.config.getString("wechat.mchId"))
        wxPayConfigStorage.setAppId(bukkitInstance.config.getString("wechat.appId"))
        wxPayConfigStorage.setSecretKey(bukkitInstance.config.getString("wechat.secretKey"))
        wxPayConfigStorage.setNotifyUrl(bukkitInstance.config.getString("wechat.notifyUrl"))
        wxPayConfigStorage.setSignType("MD5")
        wxPayConfigStorage.setInputCharset("utf-8")
        return wxPayConfigStorage
    }

    fun getAliConfig(): AlipayClient {
        val alipayConfig = AlipayConfig()
        alipayConfig.serverUrl = "https://openapi.alipay.com/gateway.do"
        alipayConfig.appId = bukkitInstance.config.getString("alipay.appId")
        alipayConfig.privateKey = bukkitInstance.config.getString("alipay.privateKey")
        alipayConfig.format = "json"
        alipayConfig.charset = "UTF8"
        alipayConfig.signType = "RSA2"
        alipayConfig.alipayPublicKey = bukkitInstance.config.getString("alipay.publicKey")
        val alipayClient: AlipayClient = DefaultAlipayClient(alipayConfig)
        AlipayLogger.setNeedEnableLogger(false)
        AlipayLogger.setJDKDebugEnabled(false)
        return alipayClient
    }

    private fun printLogo(text: String) {
        for (line in text.split("\n")) {
            bukkitInstance.logger.info(line)
        }
    }

    @JvmStatic
    lateinit var bukkitInstance: org.bukkit.plugin.Plugin
}