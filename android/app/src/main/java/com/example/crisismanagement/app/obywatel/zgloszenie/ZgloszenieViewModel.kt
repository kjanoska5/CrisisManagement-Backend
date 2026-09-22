package com.example.crisismanagement.app.obywatel.zgloszenie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crisismanagement.app.common.network.NewReportRequest
import com.example.crisismanagement.app.common.network.RetrofitClient
import com.example.crisismanagement.app.common.network.ThreatType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ZgloszenieUiState(
    val listaKategorii : List<ThreatType> = emptyList(),
    val wybranaKategoria : ThreatType? = null,
    val komentarz : String = "",
    val zdjeciaUri : String? = null,
    val szerokoscGeo : Double? = null,
    val dlugoscGeo : Double? = null,
    val wysylanie : Boolean = false,
    val wyslano : Boolean = false,
    val komunikatBledu : String? = null
)
class ZgloszenieViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ZgloszenieUiState())
    val uiState: StateFlow<ZgloszenieUiState> = _uiState.asStateFlow()

    private fun pobierzTypyZagrozen(){
        viewModelScope.launch {
            try {
                val wynik = RetrofitClient.api.pobierzTypyZagrozen()
                _uiState.value = _uiState.value.copy(listaKategorii = wynik)
            } catch (e: Exception){
                _uiState.value = _uiState.value.copy(komunikatBledu = e.message)
            }
        }
    }

    init {
        pobierzTypyZagrozen()
    }

    fun wybierzTyp(typ: ThreatType){
        _uiState.value = _uiState.value.copy(wybranaKategoria = typ)
    }

    fun ustawKomentarz(tekst: String){
        _uiState.value = _uiState.value.copy(komentarz = tekst)
    }

    fun ustawZdjecie(uri: String){
        _uiState.value = _uiState.value.copy(zdjeciaUri = uri)
    }

    fun ustawLokalizacje(lat: Double, lng: Double){
        _uiState.value = _uiState.value.copy(szerokoscGeo = lat, dlugoscGeo = lng)
    }

    fun wyslijZgloszenie(uzytkownikId: String){
        val stan = _uiState.value
        val typ = stan.wybranaKategoria
        val lat = stan.szerokoscGeo
        val lng = stan.dlugoscGeo
        if (typ == null || lat== null || lng == null)
        {
            _uiState.value = stan.copy(komunikatBledu = "Wybierz kategorię i poczekaj na lokalizację")
            return
        }
        viewModelScope.launch {
            _uiState.value = stan.copy(wysylanie = true, komunikatBledu = null)
        try {
            RetrofitClient.api.wyslijZgloszenie(
                NewReportRequest(
                    userId = uzytkownikId,
                    threatTypeId = typ.id,
                    lat = lat,
                    lng = lng,
                    photoUrl = null,
                    comment = stan.komentarz.ifBlank { null }
                )
            )
            _uiState.value = _uiState.value.copy(wysylanie = false, wyslano = true)
        } catch (e: Exception){
                _uiState.value = _uiState.value.copy(wysylanie = false, komunikatBledu = "Błąd wysyłki : ${e.message}")
            }
        }
    }
}
