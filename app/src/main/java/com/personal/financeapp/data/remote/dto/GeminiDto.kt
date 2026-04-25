package com.personal.financeapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GeminiRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null
) {
    data class Content(
        val parts: List<Part>
    )

    data class Part(
        val text: String? = null,
        @SerializedName("inline_data")
        val inlineData: InlineData? = null
    )

    data class InlineData(
        @SerializedName("mime_type")
        val mimeType: String,
        val data: String // Base64 encoded image
    )

    data class GenerationConfig(
        @SerializedName("response_mime_type")
        val responseMimeType: String? = null
    )
}

data class GeminiResponse(
    val candidates: List<Candidate>
) {
    data class Candidate(
        val content: Content
    )

    data class Content(
        val parts: List<Part>
    )

    data class Part(
        val text: String
    )
}
