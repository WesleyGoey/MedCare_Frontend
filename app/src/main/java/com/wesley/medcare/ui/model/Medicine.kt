package com.wesley.medcare.ui.model

data class Medicine(
    val id: Int,
    val userId: Int,
    val name: String,
    val type: String,
    val dosage: String,
    val stock: Int,
    val minStock: Int,
    val notes: String? = null,
    val image: String? = null
)
