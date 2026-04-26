package com.personal.financeapp.data.repository

import com.google.gson.Gson
import com.personal.financeapp.BuildConfig
import com.personal.financeapp.data.remote.api.GeminiApi
import com.personal.financeapp.data.remote.dto.GeminiRequest
import com.personal.financeapp.domain.repository.AiRepository
import com.personal.financeapp.domain.repository.TransactionData
import javax.inject.Inject

class AiRepositoryImpl @Inject constructor(
    private val geminiApi: GeminiApi
) : AiRepository {

    override suspend fun analyzeReceiptImage(base64Image: String): Result<TransactionData> {
        return try {
            val prompt = """
                Analise esta imagem de um comprovante fiscal ou recibo. 
                Extraia as seguintes informações em formato JSON puro:
                {
                    "amount": 0.0,
                    "description": "nome do estabelecimento",
                    "date": "YYYY-MM-DD",
                    "categoryName": "categoria sugerida"
                }
                Retorne APENAS o JSON, sem markdown ou explicações.
            """.trimIndent()

            val request = GeminiRequest(
                contents = listOf(
                    GeminiRequest.Content(
                        parts = listOf(
                            GeminiRequest.Part(text = prompt),
                            GeminiRequest.Part(
                                inlineData = GeminiRequest.InlineData(
                                    mimeType = "image/jpeg",
                                    data = base64Image
                                )
                            )
                        )
                    )
                ),
                generationConfig = GeminiRequest.GenerationConfig(
                    responseMimeType = "application/json"
                )
            )

            val response = geminiApi.generateContent(
                apiKey = BuildConfig.GEMINI_API_KEY,
                request = request
            )

            val jsonText = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?.replace("```json", "")
                ?.replace("```", "")
                ?.trim()

            if (jsonText != null) {
                val data = Gson().fromJson(jsonText, TransactionData::class.java)
                Result.success(data)
            } else {
                Result.failure(Exception("Não foi possível processar a resposta da IA"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun askAssistant(prompt: String, context: String): Result<String> {
        return try {
            val fullPrompt = """
                Você é um assistente financeiro pessoal inteligente e amigável.
                Contexto das transações atuais do usuário:
                $context
                
                Se o usuário pedir para ser lembrado de algo relacionado a veículo/manutenção/alertas, 
                você deve incluir na sua resposta um bloco JSON com a formatação exata abaixo, ALÉM do texto normal da sua resposta:
                ```json
                {
                    "action": "create_alert",
                    "alertType": "Nome da Manutenção",
                    "nextKm": 10000, 
                    "nextDate": "15 dias"
                }
                ```
                (Os campos nextKm e nextDate são opcionais, preencha de acordo com o pedido).
                
                Pergunta do usuário: $prompt
                
                Responda de forma concisa, direta e útil em português (Brasil).
            """.trimIndent()

            val request = GeminiRequest(
                contents = listOf(
                    GeminiRequest.Content(
                        parts = listOf(
                            GeminiRequest.Part(text = fullPrompt)
                        )
                    )
                )
            )

            val response = geminiApi.generateContent(
                apiKey = BuildConfig.GEMINI_API_KEY,
                request = request
            )

            val responseText = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text

            if (responseText != null) {
                Result.success(responseText)
            } else {
                Result.failure(Exception("Não foi possível obter resposta do assistente"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
