package org.fastmcmirror.ultrapay.api

import com.alipay.api.domain.AlipayTradePrecreateModel
import com.alipay.api.domain.AlipayTradeQueryModel
import com.alipay.api.request.AlipayTradePrecreateRequest
import com.alipay.api.request.AlipayTradeQueryRequest
import com.alipay.api.response.AlipayTradePrecreateResponse
import com.alipay.api.response.AlipayTradeQueryResponse
import com.egzosn.pay.common.bean.PayOrder
import com.egzosn.pay.wx.bean.WxTransactionType
import com.egzosn.pay.wx.v3.bean.response.order.TradeState
import org.bukkit.entity.Player
import org.fastmcmirror.ultrapay.UltraPay
import org.fastmcmirror.ultrapay.utils.Color.color
import org.fastmcmirror.ultrapay.utils.QRUtil
import taboolib.common.platform.function.submit
import taboolib.module.nms.NMSMap
import taboolib.module.nms.sendMap
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.CompletableFuture

class PayAPI {
    companion object {
        @JvmStatic
        fun createOrder(
            paymentType: PaymentType,
            itemName: String,
            price: Double,
            player: Player
        ): CompletableFuture<OrderInfo> {
            val completableFuture = CompletableFuture<OrderInfo>()
            if (paymentType == PaymentType.WECHAT_PAY) {
                val payOrder = PayOrder(itemName, "", BigDecimal.valueOf(price), createOutTradeNo())
                val qrPay = UltraPay.wxPayService.getQrPay(payOrder)
                payOrder.transactionType = WxTransactionType.NATIVE
                val task = submit(async = true, period = 10) {
                    val infoMap = queryOrderWechat(payOrder)
                    if (infoMap.containsKey("trade_state")) {
                        val tradeState = TradeState.valueOf(infoMap.get("trade_state").toString())
                        if (tradeState == TradeState.SUCCESS) {
                            payOrder.tradeNo = infoMap.get("transaction_id").toString()
                            val orderInfo = OrderInfo(
                                payOrder.outTradeNo,
                                payOrder.tradeNo,
                                payOrder.price.toDouble(),
                                payOrder.subject,
                                player
                            )
                            completableFuture.complete(orderInfo)
                            cancel()
                        }
                    }
                }
                submit(async = true, delay = (20L * 60L * 5L)) {
                    task.cancel()
                }
                player.sendMap(QRUtil.createQRCode(qrPay), NMSMap.Hand.MAIN)
                player.sendTitle(
                    UltraPay.bukkitInstance.config.getString("title.main")!!.replace("%price%", price.toString())
                        .color(),
                    UltraPay.bukkitInstance.config.getString("title.sub")!!.replace("%price%", price.toString())
                        .color(),
                    UltraPay.bukkitInstance.config.getInt("title.fadeIn"),
                    UltraPay.bukkitInstance.config.getInt("title.stay"),
                    UltraPay.bukkitInstance.config.getInt("title.fadeOut")
                )
                completableFuture.thenAcceptAsync {
                    player.updateInventory()
                }
            } else {
                val tradeRequest = AlipayTradePrecreateRequest()
                val tradeRequestModel = AlipayTradePrecreateModel()
                tradeRequestModel.outTradeNo = createOutTradeNo()
                tradeRequestModel.subject = itemName
                tradeRequestModel.totalAmount = price.toString()
                tradeRequest.bizModel = tradeRequestModel
                val tradeResponse = UltraPay.aliPayService.execute(tradeRequest)
                val task = submit(async = true, period = 10) {
                    val queryResponse = queryOrderAlipay(tradeResponse)
                    if (queryResponse.isSuccess) {
                        val tradeState = queryResponse.tradeStatus
                        if (tradeState.equals("TRADE_SUCCESS", ignoreCase = true)) {
                            val orderInfo = OrderInfo(
                                queryResponse.outTradeNo,
                                queryResponse.tradeNo,
                                queryResponse.totalAmount.toDouble(),
                                itemName,
                                player
                            )
                            completableFuture.complete(orderInfo)
                            cancel()
                        }
                    } else if (!queryResponse.code.equals("40004")) {
                        UltraPay.bukkitInstance.logger.severe("支付宝请求订单错误 状态码: " + queryResponse.code + " 提示: " + queryResponse.subCode + " 商户订单号: " + queryResponse.outTradeNo)
                    }
                }
                submit(async = true, delay = (20L * 60L * 5L)) {
                    task.cancel()
                }
                player.sendMap(QRUtil.createQRCode(tradeResponse.qrCode), NMSMap.Hand.MAIN)
                player.sendTitle(
                    UltraPay.bukkitInstance.config.getString("title.main")!!.replace("%price%", price.toString())
                        .color(),
                    UltraPay.bukkitInstance.config.getString("title.sub")!!.replace("%price%", price.toString())
                        .color(),
                    UltraPay.bukkitInstance.config.getInt("title.fadeIn"),
                    UltraPay.bukkitInstance.config.getInt("title.stay"),
                    UltraPay.bukkitInstance.config.getInt("title.fadeOut")
                )
                completableFuture.thenAcceptAsync {
                    player.updateInventory()
                }
            }
            return completableFuture
        }

        private fun createOutTradeNo(): String {
            val calendar = Calendar.getInstance()
            return "" + calendar[1] + (calendar[2] + 1) + calendar[5] + (System.currentTimeMillis() + (Math.random() * 10000000.0).toLong())
        }

        private fun queryOrderWechat(order: PayOrder): Map<String, Any> {
            return UltraPay.wxPayService.query(order)
        }

        private fun queryOrderAlipay(tradePrecreateResponse: AlipayTradePrecreateResponse): AlipayTradeQueryResponse {
            val alipayTradeQueryRequest = AlipayTradeQueryRequest()
            val alipayTradeQueryModel = AlipayTradeQueryModel()
            alipayTradeQueryModel.outTradeNo = tradePrecreateResponse.outTradeNo
            alipayTradeQueryModel.tradeNo = null
            alipayTradeQueryRequest.bizModel = alipayTradeQueryModel
            val alipayTradeQueryResponse = UltraPay.aliPayService.execute(alipayTradeQueryRequest)
            return alipayTradeQueryResponse
        }
    }
}