package com.personal.financeapp.presentation.screens.receipt

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personal.financeapp.domain.model.Transaction
import com.personal.financeapp.domain.usecase.AddTransactionUseCase
import com.personal.financeapp.domain.usecase.ProcessReceiptUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class ReceiptViewModel @Inject constructor(
    private val processReceiptUseCase: ProcessReceiptUseCase,
    private val addTransactionUseCase: AddTransactionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReceiptUiState>(ReceiptUiState.Idle)
    val uiState: StateFlow<ReceiptUiState> = _uiState.asStateFlow()

    fun onImageSelected(bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.value = ReceiptUiState.Loading
            
            val base64Image = bitmapToBase64(bitmap)
            val result = processReceiptUseCase(base64Image)
            
            result.onSuccess { transaction ->
                _uiState.value = ReceiptUiState.Success(transaction)
            }.onFailure { error ->
                _uiState.value = ReceiptUiState.Error(error.message ?: "Erro desconhecido")
            }
        }
    }

    fun confirmTransaction(transaction: Transaction) {
        viewModelScope.launch {
            addTransactionUseCase(transaction)
            _uiState.value = ReceiptUiState.Saved
        }
    }

    fun reset() {
        _uiState.value = ReceiptUiState.Idle
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}

sealed class ReceiptUiState {
    object Idle : ReceiptUiState()
    object Loading : ReceiptUiState()
    data class Success(val transaction: Transaction) : ReceiptUiState()
    data class Error(val message: String) : ReceiptUiState()
    object Saved : ReceiptUiState()
}
