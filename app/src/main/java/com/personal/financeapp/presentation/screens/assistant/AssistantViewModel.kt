package com.personal.financeapp.presentation.screens.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.personal.financeapp.domain.model.VehicleAlert
import com.personal.financeapp.domain.repository.AiRepository
import com.personal.financeapp.domain.repository.TransactionRepository
import com.personal.financeapp.domain.repository.VehicleAlertRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssistantViewModel @Inject constructor(
    private val aiRepository: AiRepository,
    private val transactionRepository: TransactionRepository,
    private val alertRepository: VehicleAlertRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssistantUiState())
    val uiState: StateFlow<AssistantUiState> = _uiState.asStateFlow()

    private val gson = Gson()

    fun sendMessage(message: String) {
        if (message.isBlank()) return

        val currentMessages = _uiState.value.messages.toMutableList()
        currentMessages.add(ChatMessage(message, isUser = true))
        
        _uiState.value = _uiState.value.copy(
            messages = currentMessages,
            isLoading = true
        )

        viewModelScope.launch {
            // Pegamos as transações recentes para dar contexto à IA
            val transactions = transactionRepository.getAllTransactions().first()
            val context = transactions.take(20).joinToString("\n") { 
                "${it.date}: ${it.description} - R$ ${it.amount} (${it.category.name})"
            }

            val result = aiRepository.askAssistant(message, context)
            
            result.onSuccess { response ->
                var finalResponse = response
                
                // Extrair possível JSON de alerta
                val regex = Regex("```json\\s*(.*?)\\s*```", RegexOption.DOT_MATCHES_ALL)
                val match = regex.find(response)
                
                if (match != null) {
                    try {
                        val jsonStr = match.groupValues[1]
                        val alertData = gson.fromJson(jsonStr, AiAlertDto::class.java)
                        if (alertData.action == "create_alert") {
                            alertRepository.insertAlert(
                                VehicleAlert(
                                    alertType = alertData.alertType,
                                    nextKm = alertData.nextKm,
                                    nextDate = alertData.nextDate
                                )
                            )
                            // Remove o bloco JSON da resposta
                            finalResponse = response.replace(match.value, "").trim()
                            if (finalResponse.isBlank()) {
                                finalResponse = "Alerta criado com sucesso!"
                            } else {
                                finalResponse += "\n\n(Alerta criado e salvo no seu Dashboard!)"
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                val updatedMessages = _uiState.value.messages.toMutableList()
                updatedMessages.add(ChatMessage(finalResponse, isUser = false))
                _uiState.value = _uiState.value.copy(
                    messages = updatedMessages,
                    isLoading = false
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message
                )
            }
        }
    }
}

data class AiAlertDto(
    val action: String,
    val alertType: String,
    val nextKm: Int? = null,
    val nextDate: String? = null
)

data class AssistantUiState(
    val messages: List<ChatMessage> = listOf(
        ChatMessage("Olá! Eu sou seu assistente financeiro. Como posso ajudar você hoje?", isUser = false)
    ),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)
