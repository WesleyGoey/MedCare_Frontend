// app/src/main/java/com/wesley/medcare/ui/viewmodel/MedicineViewModel.kt
package com.wesley.medcare.ui.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wesley.medcare.data.container.AppContainer
import com.wesley.medcare.data.dto.Medicine.MedicineDataWithSchedule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MedicineViewModel(application: Application) : AndroidViewModel(application) {

    // initialize container with application context to avoid the repository init error
    private val repository = AppContainer(application).medicineRepository

    // form/state flows (private mutable + public read-only)
    private val _medicineName = MutableStateFlow("")
    val medicineName: StateFlow<String> = _medicineName

    private val _dosage = MutableStateFlow("")
    val dosage: StateFlow<String> = _dosage

    private val _stock = MutableStateFlow("")
    val stock: StateFlow<String> = _stock

    private val _minStock = MutableStateFlow("")
    val minStock: StateFlow<String> = _minStock

    private val _medicineType = MutableStateFlow("Tablet")
    val medicineType: StateFlow<String> = _medicineType

    private val _notes = MutableStateFlow<String?>(null)
    val notes: StateFlow<String?> = _notes

    private val _medicines = MutableStateFlow<List<MedicineDataWithSchedule>>(emptyList())
    val medicines: StateFlow<List<MedicineDataWithSchedule>> = _medicines

    private val _selectedMedicine = MutableStateFlow<MedicineDataWithSchedule?>(null)
    val selectedMedicine: StateFlow<MedicineDataWithSchedule?> = _selectedMedicine

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

//    private val _medicineSchedules = MutableStateFlow<List<Schedule>>(emptyList())
//    val medicineSchedules = _medicineSchedules.asStateFlow()

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
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
//    fun getSchedulesByMedicineId(medicineId: String): Flow<List<Schedule>> {
//        return scheduleDao.getSchedulesByMedicineId(medicineId)
//    }


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
        context: Context,
        name: String,
        type: String,
        dosage: String,
        stock: Int,
        minStock: Int,
        notes: String?
    ) {
        Log.d("MedicineViewModel", "addMedicine called: name=$name, type=$type, dosage=$dosage, stock=$stock, minStock=$minStock")

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            try {
                Log.d("MedicineViewModel", "Calling repository.addMedicine...")
                val success = repository.addMedicine(
                    name = name,
                    type = type,
                    dosage = dosage,
                    stock = stock,
                    minStock = minStock,
                    notes = notes
                )
                Log.d("MedicineViewModel", "Repository response: success=$success")

                if (success) {
                    clearForm()
                    _successMessage.value = "Medicine added"
                    getAllMedicines()
                } else {
                    _errorMessage.value = "Failed to add medicine"
                    Log.e("MedicineViewModel", "Repository returned false")
                }
            } catch (e: Exception) {
                Log.e("MedicineViewModel", "addMedicine error: ${e.message}", e)
                _errorMessage.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun updateMedicine(
        context: Context,
        id: Int,
        name: String?,
        type: String?,
        dosage: String?,
        stock: Int?,
        minStock: Int?,
        notes: String?
    ) {
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

    private fun clearForm() {
        _medicineName.value = ""
        _dosage.value = ""
        _stock.value = ""
        _minStock.value = ""
        _medicineType.value = "Tablet"
        _notes.value = null
    }
}
