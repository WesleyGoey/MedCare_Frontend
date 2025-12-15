package com.wesley.medcare.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wesley.medcare.data.dto.Medicine.AddMedicineRequest
import com.wesley.medcare.data.repository.MedicineRepository
import com.wesley.medcare.ui.model.Medicine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MedicineViewModel(private val repository: MedicineRepository) : ViewModel() {
    private val _medicines = MutableStateFlow<List<Medicine>>(emptyList())
    val medicines: StateFlow<List<Medicine>> = _medicines.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _addSuccess = MutableStateFlow(false)
    val addSuccess: StateFlow<Boolean> = _addSuccess.asStateFlow()

    init {
        loadMedicines()
    }

    fun loadMedicines() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getAllMedicines()
                _medicines.value = response.data.map { medicineData ->
                    Medicine(
                        id = medicineData.id,
                        userId = medicineData.userId,
                        name = medicineData.name,
                        type = medicineData.type,
                        dosage = medicineData.dosage,
                        stock = medicineData.stock,
                        minStock = medicineData.minStock,
                        notes = medicineData.notes
                    )
                }
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Unknown error"
            }
            _isLoading.value = false
        }
    }

    fun saveMedicineForm(
        name: String,
        dosage: String,
        stockStr: String,
        minStockStr: String,
        type: String,
        notes: String?
    ) {
        val stock = stockStr.toIntOrNull() ?: 0
        val minStock = minStockStr.toIntOrNull() ?: 0

        val request = AddMedicineRequest(
            name = name.trim(),
            dosage = dosage.trim(),
            stock = stock,
            minStock = minStock,
            type = type,
            notes = notes?.trim()
        )

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.addMedicine(request)
                val medicineData = response.data
                val newMedicine = Medicine(
                    id = medicineData.id,
                    userId = medicineData.userId,
                    name = medicineData.name,
                    type = medicineData.type,
                    dosage = medicineData.dosage,
                    stock = medicineData.stock,
                    minStock = medicineData.minStock,
                    notes = medicineData.notes
                )
                _medicines.value = _medicines.value + newMedicine
                _addSuccess.value = true
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to add medicine"
                _addSuccess.value = false
            }
            _isLoading.value = false
        }
    }

    fun resetAddSuccess() {
        _addSuccess.value = false
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
