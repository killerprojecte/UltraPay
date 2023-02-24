package org.fastmcmirror.ultrapay.utils

import io.nayuki.qrcodegen.QrCode
import io.nayuki.qrcodegen.QrCode.Ecc
import java.awt.image.BufferedImage
import java.util.*


class QRUtil {
    companion object {
        @JvmStatic
        fun createQRCode(content: String): BufferedImage {
            val errCorLvl = Ecc.LOW
            val qr = QrCode.encodeText(content, errCorLvl)
            return toImage(qr, 10, 4)!!
        }

        private fun toImage(qr: QrCode, scale: Int, border: Int, lightColor: Int, darkColor: Int): BufferedImage? {
            Objects.requireNonNull(qr)
            require(!(scale <= 0 || border < 0)) { "Value out of range" }
            require(!(border > Int.MAX_VALUE / 2 || qr.size + border * 2L > Int.MAX_VALUE / scale)) { "Scale or border too large" }
            val result = BufferedImage(
                (qr.size + border * 2) * scale,
                (qr.size + border * 2) * scale,
                BufferedImage.TYPE_INT_RGB
            )
            for (y in 0 until result.height) {
                for (x in 0 until result.width) {
                    val color = qr.getModule(x / scale - border, y / scale - border)
                    result.setRGB(x, y, if (color) darkColor else lightColor)
                }
            }
            return result
        }

        private fun toImage(qr: QrCode, scale: Int, border: Int): BufferedImage? {
            return toImage(qr, scale, border, 0xFFFFFF, 0x000000)
        }
    }
}