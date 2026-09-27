package com.example.crisismanagement.app.obywatel.medicalcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crisismanagement.app.common.network.MedicalCardRequest
import com.example.crisismanagement.app.common.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MedicalCardUiState(
    val bloodType: String = "",
    val allergies: String = "",
    val chronicDiseases: String = "",
    val emergencyContact: String = "",
    val medicines: String = "",
    val cardExists: Boolean = false,
    val loading: Boolean = true,
    val saving: Boolean = false,
    val saved: Boolean = false,
    val deleting: Boolean = false,
    val deleted: Boolean = false,
    val error: String? = null,
)

class MedicalCardViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MedicalCardUiState())
    val uiState: StateFlow<MedicalCardUiState> = _uiState.asStateFlow()

    fun downloadCard(userId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.getMedicalCard(userId)
                if (response.isSuccessful) {
                    val card = response.body()
                    if (card != null) {
                        _uiState.value = _uiState.value.copy(
                            bloodType = card.bloodType.orEmpty(),
                            allergies = card.allergies.orEmpty(),
                            chronicDiseases = card.chronicDiseases.orEmpty(),
                            emergencyContact = card.emergencyContact.orEmpty(),
                            medicines = card.medications.orEmpty(),
                            cardExists = true,
                            loading = false,
                            error = null
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(loading = false, error = null)
                    }
                } else if (response.code() == 404) {
                    _uiState.value = _uiState.value.copy(loading = false)
                } else {
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        error = "Nieoczekiwany błąd odpowiedzi ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = "Błąd Połączenia: ${e.message ?: "Nieznany błąd"}"
                )
            }
        }
    }

    fun setBloodType(type: String) {
        _uiState.value = _uiState.value.copy(bloodType = type)
    }
    fun setAllergies(allergies: String) {
        _uiState.value = _uiState.value.copy(allergies = allergies)
    }
    fun setChronicDiseases(chronicDiseases: String) {
        _uiState.value = _uiState.value.copy(chronicDiseases = chronicDiseases)
    }
    fun setEmergencyContact(emergencyContact: String) {
        _uiState.value = _uiState.value.copy(emergencyContact = emergencyContact)
    }
    fun setMedicines(medicines: String) {
        _uiState.value = _uiState.value.copy(medicines = medicines)
    }

    fun saveCard(userId: String) {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(saving = true, error = null)
            try {
                val response = RetrofitClient.api.updateMedicalCard(
                    userId,
                    MedicalCardRequest(
                        bloodType = state.bloodType.ifBlank { null },
                        allergies = state.allergies.ifBlank { null },
                        chronicDiseases = state.chronicDiseases.ifBlank { null },
                        emergencyContact = state.emergencyContact.ifBlank { null },
                        medications = state.medicines.ifBlank { null }
                    )
                )
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(saving = false, saved = true, cardExists = true)
                } else {
                    _uiState.value = _uiState.value.copy(saving = false, error = "Kod Błędu: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    saving = false,
                    error = "Błąd Połączenia: ${e.message ?: "Nieznany błąd"}"
                )
            }
        }
    }

    fun deleteCard(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(deleting = true, error = null)
            try {
                val response = RetrofitClient.api.deleteMedicalCard(userId)
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(deleting = false, deleted = true, cardExists = false)
                } else {
                    _uiState.value = _uiState.value.copy(deleting = false, error = "Kod Błędu: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    deleting = false,
                    error = "Błąd Połączenia: ${e.message ?: "Nieznany błąd"}"
                )
            }
        }
    }
}