package com.finanzasclaras.app.core.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

object IconUtils {
    fun getIcon(name: String): ImageVector {
        return when (name) {
            "category" -> Icons.Default.Category
            "shopping_cart" -> Icons.Default.ShoppingCart
            "restaurant" -> Icons.Default.Restaurant
            "directions_car" -> Icons.Default.DirectionsCar
            "local_hospital" -> Icons.Default.LocalHospital
            "school" -> Icons.Default.School
            "home" -> Icons.Default.Home
            "bolt" -> Icons.Default.Bolt
            "checkroom" -> Icons.Default.Checkroom
            "sports_esports" -> Icons.Default.SportsEsports
            "pets" -> Icons.Default.Pets
            "flight" -> Icons.Default.Flight
            "work" -> Icons.Default.Work
            "trending_up" -> Icons.Default.TrendingUp
            "attach_money" -> Icons.Default.AttachMoney
            "credit_card" -> Icons.Default.CreditCard
            "payments" -> Icons.Default.Payments
            "account_balance" -> Icons.Default.AccountBalance
            "agriculture" -> Icons.Default.Agriculture
            "card_giftcard" -> Icons.Default.CardGiftcard
            "computer" -> Icons.Default.Computer
            "more_horiz" -> Icons.Default.MoreHoriz
            else -> Icons.Default.Category
        }
    }
}
