package com.wesley.medcare.ui.model

data class Medicine(
    val id: Int = 0,
    val userId: Int = 0,
    val name: String = "",
    val type: String = "",
    val dosage: String = "",
    val stock: Int = 0,
    val minStock: Int = 0,
    val notes: String? = null,
    val image: String? = null
)
