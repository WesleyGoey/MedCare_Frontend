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

    // --- Form States ---
    private val _medicineName = MutableStateFlow("")
    val medicineName: StateFlow<String> = _medicineName.asStateFlow()

    private val _dosage = MutableStateFlow("")
    val dosage: StateFlow<String> = _dosage.asStateFlow()

    private val _stock = MutableStateFlow<Int?>(null)
    val stock: StateFlow<Int?> = _stock.asStateFlow()

    private val _minStock = MutableStateFlow<Int?>(null)
    val minStock: StateFlow<Int?> = _minStock.asStateFlow()

    private val _medicineType = MutableStateFlow("Tablet")
    val medicineType: StateFlow<String> = _medicineType.asStateFlow()

    private val _notes = MutableStateFlow<String?>(null)
    val notes: StateFlow<String?> = _notes.asStateFlow()

    // --- List & Selected Data ---
    private val _medicines = MutableStateFlow<List<MedicineDataWithSchedule>>(emptyList())
    val medicines: StateFlow<List<MedicineDataWithSchedule>> = _medicines.asStateFlow()

    private val _selectedMedicine = MutableStateFlow<MedicineDataWithSchedule?>(null)
    val selectedMedicine: StateFlow<MedicineDataWithSchedule?> = _selectedMedicine.asStateFlow()

    // --- Status States ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        getAllMedicines()
    }

    // --- Setters ---
    fun setMedicineName(value: String) { _medicineName.value = value }
    fun setDosage(value: String) { _dosage.value = value }
    fun setStock(value: Int?) { _stock.value = value }
    fun setMinStock(value: Int?) { _minStock.value = value }
    fun setMedicineType(value: String) { _medicineType.value = value }
    fun setNotes(value: String?) { _notes.value = value }

    fun resetSuccessMessage() { _successMessage.value = null }
    fun resetErrorMessage() { _errorMessage.value = null } // Tambahan untuk reset error di UI

    private fun validateForm(name: String, stock: Int?, minStock: Int?): String? {
        if (name.isBlank()) return "Medicine name is required"
        if (stock == null) return "Stock must be a valid number"
        if (minStock == null) return "Minimum stock must be a valid number"
        if (stock < 0) return "Stock cannot be negative"
        if (minStock < 0) return "Minimum stock cannot be negative"
        return null
    }

    // --- API Methods ---

    fun getAllMedicines() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val resp = repository.getAllMedicines()
                _medicines.value = resp?.data ?: emptyList()
            } catch (e: Exception) {
                Log.e("MedicineViewModel", "getAllMedicines error", e)
                _errorMessage.value = "Failed to load medicines. Please check your connection."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getMedicineById(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _selectedMedicine.value = null
            try {
                val resp = repository.getMedicineById(id)
                _selectedMedicine.value = resp?.data

                resp?.data?.let { m ->
                    _medicineName.value = m.name
                    _dosage.value = m.dosage
                    _stock.value = m.stock
                    _minStock.value = m.minStock
                    _medicineType.value = m.type
                    _notes.value = m.notes
                }
            } catch (e: Exception) {
                Log.e("MedicineViewModel", "getMedicineById error", e)
                _errorMessage.value = "Failed to retrieve medicine details"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addMedicine() {
        val validationError = validateForm(_medicineName.value, _stock.value, _minStock.value)
        if (validationError != null) {
            _errorMessage.value = validationError
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            try {
                val success = repository.addMedicine(
                    name = _medicineName.value,
                    type = _medicineType.value,
                    dosage = _dosage.value,
                    stock = _stock.value ?: 0,
                    minStock = _minStock.value ?: 0,
                    notes = _notes.value
                )
                if (success) {
                    clearForm()
                    _successMessage.value = "Medicine added successfully"
                    getAllMedicines()
                } else {
                    _errorMessage.value = "Failed to add medicine. Please try again."
                }
            } catch (e: Exception) {
                Log.e("MedicineViewModel", "addMedicine error", e)
                _errorMessage.value = e.message ?: "An unexpected error occurred while adding medicine"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateMedicine(id: Int) {
        val validationError = validateForm(_medicineName.value, _stock.value, _minStock.value)
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
                    name = _medicineName.value,
                    type = _medicineType.value,
                    dosage = _dosage.value,
                    stock = _stock.value!!,
                    minStock = _minStock.value!!,
                    notes = _notes.value
                )

                if (success) {
                    val updatedData = repository.getMedicineById(id)?.data
                    _selectedMedicine.value = updatedData
                    _successMessage.value = "Medicine updated successfully"
                    getAllMedicines()
                } else {
                    _errorMessage.value = "Failed to update medicine"
                }
            } catch (e: Exception) {
                Log.e("MedicineViewModel", "updateMedicine error", e)
                _errorMessage.value = e.message ?: "An error occurred while updating medicine"
            } finally {
                _isLoading.value = false
            }
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
                    _successMessage.value = "Medicine deleted successfully"
                    getAllMedicines()
                } else {
                    _errorMessage.value = "Failed to delete medicine"
                }
            } catch (e: Exception) {
                Log.e("MedicineViewModel", "deleteMedicine error", e)
                _errorMessage.value = "An error occurred while deleting medicine"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearForm() {
        _medicineName.value = ""
        _dosage.value = ""
        _stock.value = null
        _minStock.value = null
        _medicineType.value = "Tablet"
        _notes.value = null
    }
}