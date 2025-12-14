package com.wesley.medcare.data.dto.Medicine

data class GetAllMedicinesResponse(
    val `data`: List<MedicineDataWithSchedule>
)