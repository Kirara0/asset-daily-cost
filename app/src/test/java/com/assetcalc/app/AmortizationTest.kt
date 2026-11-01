package com.assetcalc.app

import com.assetcalc.app.data.AssetItem
import com.assetcalc.app.domain.Amortization
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class AmortizationTest {

    private val today: LocalDate = LocalDate.of(2026, 11, 1)

    private fun item(name: String, purchaseDate: String, amount: Double): AssetItem =
        AssetItem(
            name = name,
            purchaseEpochDay = LocalDate.parse(purchaseDate).toEpochDay(),
            amount = amount
        )

    @Test
    fun usedDays_countsBothPurchaseDayAndToday() {
        val laptop = item("笔记本电脑", "2024-06-15", 8999.0)
        assertEquals(870, Amortization.usedDays(laptop.purchaseDate, today))
    }

    @Test
    fun usedDays_isOneWhenBoughtToday() {
        assertEquals(1, Amortization.usedDays(today, today))
    }

    @Test
    fun usedDays_isOneForFuturePurchaseDate() {
        assertEquals(1, Amortization.usedDays(today.plusDays(10), today))
    }

    @Test
    fun dailyCost_matchesExpectedValues() {
        val laptop = Amortization.costOf(item("笔记本电脑", "2024-06-15", 8999.0), today)
        val phone = Amortization.costOf(item("手机", "2025-01-20", 4999.0), today)

        assertEquals(870, laptop.usedDays)
        assertEquals(10.34368, laptop.dailyCost, 0.0001)

        assertEquals(651, phone.usedDays)
        assertEquals(7.67896, phone.dailyCost, 0.0001)
    }

    @Test
    fun totals_areSummedOverAllItems() {
        val costs = Amortization.costsOf(
            listOf(
                item("笔记本电脑", "2024-06-15", 8999.0),
                item("手机", "2025-01-20", 4999.0)
            ),
            today
        )

        assertEquals(13998.0, Amortization.totalAmount(costs), 0.0001)
        assertEquals(18.0226, Amortization.totalDailyCost(costs), 0.0005)
        assertEquals(761, Amortization.averageUsedDays(costs))
    }

    @Test
    fun money_isFormattedWithTwoDecimals() {
        assertEquals("¥8,999.00", Amortization.money(8999.0))
        assertEquals("¥10.34", Amortization.money(10.3437))
        assertEquals("¥0.00", Amortization.money(0.0))
    }
}
