// kotlin
package com.wesley.medcare.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wesley.medcare.data.container.AppContainer
import com.wesley.medcare.data.dto.Medicine.MedicineDataWithSchedule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MedicineViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppContainer(application).medicineRepository

    private val _medicineName = MutableStateFlow("")
    val medicineName: StateFlow<String> = _medicineName.asStateFlow()

    private val _dosage = MutableStateFlow("")
    val dosage: StateFlow<String> = _dosage.asStateFlow()

    private val _stock = MutableStateFlow<Int>(0)
    val stock: StateFlow<Int> = _stock.asStateFlow()

    private val _minStock = MutableStateFlow<Int>(0)
    val minStock: StateFlow<Int> = _minStock.asStateFlow()

    private val _medicineType = MutableStateFlow("Tablet")
    val medicineType: StateFlow<String> = _medicineType.asStateFlow()

    private val _notes = MutableStateFlow<String?>(null)
    val notes: StateFlow<String?> = _notes.asStateFlow()

    private val _medicines = MutableStateFlow<List<MedicineDataWithSchedule>>(emptyList())
    val medicines: StateFlow<List<MedicineDataWithSchedule>> = _medicines.asStateFlow()

    private val _selectedMedicine = MutableStateFlow<MedicineDataWithSchedule?>(null)
    val selectedMedicine: StateFlow<MedicineDataWithSchedule?> = _selectedMedicine.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init { getAllMedicines() }

    fun setMedicineName(value: String) { _medicineName.value = value }
    fun setDosage(value: String) { _dosage.value = value }
    fun setStock(value: Int) { _stock.value = value }
    fun setMinStock(value: Int) { _minStock.value = value }
    fun setMedicineType(value: String) { _medicineType.value = value }
    fun setNotes(value: String?) { _notes.value = value }

    fun resetSuccessMessage() { _successMessage.value = null }

    private fun validateForm(name: String, stock: Int?, minStock: Int?): String? {
        if (name.isBlank()) return "Medication name is required"
        if (stock == null) return "Stock must be a number"
        if (minStock == null) return "Minimum stock must be a number"
        if (stock < 0) return "Stock cannot be negative"
        if (minStock < 0) return "Minimum stock cannot be negative"
        return null
    }

    fun getAllMedicines() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val resp = repository.getAllMedicines()
                _medicines.value = resp?.`data` ?: emptyList()
            } catch (e: Exception) {
                Log.e("MedicineViewModel", "getAllMedicines error", e)
                _errorMessage.value = e.message ?: "Failed to load medicines"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getMedicineById(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val resp = repository.getMedicineById(id)
                _selectedMedicine.value = resp?.`data`
            } catch (e: Exception) {
                Log.e("MedicineViewModel", "getMedicineById error", e)
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getLowStock() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val resp = repository.getLowStock()
                Log.d("MedicineViewModel", "low stock count = ${resp?.`data`?.size ?: 0}")
            } catch (e: Exception) {
                Log.e("MedicineViewModel", "getLowStock error", e)
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addMedicine(
        name: String = _medicineName.value,
        type: String = _medicineType.value,
        dosage: String = _dosage.value,
        stock: Int = _stock.value,
        minStock: Int = _minStock.value,
        notes: String? = _notes.value
    ) {
        val validationError = validateForm(name, stock, minStock)
        if (validationError != null) {
            _errorMessage.value = validationError
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            try {
                repository.addMedicine(
                    name = name,
                    type = type,
                    dosage = dosage,
                    stock = stock,
                    minStock = minStock,
                    notes = notes
                ).also { success ->
                    if (success) {
                        clearForm()
                        _successMessage.value = "Medicine added"
                        getAllMedicines()
                    } else {
                        _errorMessage.value = "Failed to add medicine"
                    }
                }
            } catch (e: Exception) {
                Log.e("MedicineViewModel", "addMedicine error", e)
                _errorMessage.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateMedicine(
        id: Int,
        name: String? = _medicineName.value,
        type: String? = _medicineType.value,
        dosage: String? = _dosage.value,
        stock: Int? = _stock.value,
        minStock: Int? = _minStock.value,
        notes: String? = _notes.value
    ) {
        val validationError = validateForm(name ?: "", stock, minStock)
        if (validationError != null) {
            _errorMessage.value = validationError
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            try {
                val success = repository.updateMedicine(
                    id = id,
                    name = name,
                    type = type,
                    dosage = dosage,
                    stock = stock,
                    minStock = minStock,
                    notes = notes
                )
                if (success) {
                    _successMessage.value = "Medicine updated"
                    getAllMedicines()
                } else {
                    _errorMessage.value = "Failed to update medicine"
                }
            } catch (e: Exception) {
                Log.e("MedicineViewModel", "updateMedicine error", e)
                _errorMessage.value = e.message
            } finally { _isLoading.value = false }
        }
    }

    fun deleteMedicine(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            try {
                val success = repository.deleteMedicine(id)
                if (success) {
                    _successMessage.value = "Medicine deleted"
                    getAllMedicines()
                } else {
                    _errorMessage.value = "Failed to delete medicine"
                }
            } catch (e: Exception) {
                Log.e("MedicineViewModel", "deleteMedicine error", e)
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearForm() {
        _medicineName.value = ""
        _dosage.value = ""
        _stock.value = 0
        _minStock.value = 0
        _medicineType.value = "Tablet"
        _notes.value = null
    }
    fun resetState() {
        _successMessage.value = null
        _errorMessage.value = null
        _isLoading.value = false
    }
}
