package com.mutualmobile.harvestKmp.android.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.mutualmobile.harvestKmp.MR
import java.math.BigDecimal

@Composable
fun getIconUrl(blokchainType: String): Painter {
    return when (blokchainType) {
        "BNB" -> painterResource(MR.images.bnb.drawableResId)
        "BTC" -> painterResource(MR.images.btc.drawableResId)
        "ETH" -> painterResource(MR.images.eth.drawableResId)
        "TRX" -> painterResource(MR.images.trx.drawableResId)
        "SOL" -> painterResource(MR.images.sol.drawableResId)
        "USDT" -> painterResource(MR.images.usdt.drawableResId)
        else -> {
            painterResource(MR.images.bnb.drawableResId)
        }
    }
}

object BlockchainUtils {

    fun fromWei(number: String?, unit: Unit): BigDecimal {
        return fromWei(BigDecimal(number), unit)
    }

    fun fromWei(number: BigDecimal, unit: Unit): BigDecimal {
        return number.divide(unit.weiFactor)
    }

    fun toWei(number: String?, unit: Unit): BigDecimal {
        return toWei(BigDecimal(number), unit)
    }

    fun toWei(number: BigDecimal, unit: Unit): BigDecimal {
        return number.multiply(unit.weiFactor)
    }

    enum class Unit(name: String, factor: Int) {
        WEI("wei", 0),
        KWEI("kwei", 3),
        MWEI("mwei", 6),
        GWEI("gwei", 9),
        SZABO("szabo", 12),
        FINNEY("finney", 15),
        ETHER("ether", 18),
        KETHER("kether", 21),
        METHER("mether", 24),
        GETHER("gether", 27);

        val weiFactor: BigDecimal

        init {
            weiFactor = BigDecimal.TEN.pow(factor)
        }

        override fun toString(): String {
            return name
        }

        companion object {
            fun fromString(name: String?): Unit {
                if (name != null) {
                    for (unit in Unit.values()) {
                        if (name.equals(unit.name, ignoreCase = true)) {
                            return unit
                        }
                    }
                }
                return Unit.valueOf(name!!)
            }
        }
    }


}