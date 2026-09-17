package com.finanzasclaras.app.domain.model

data class Category(
    val id: String,
    val name: String,
    val nameEn: String,
    val icon: String,
    val type: TransactionType,
    val color: Int,
    val isDefault: Boolean
)
