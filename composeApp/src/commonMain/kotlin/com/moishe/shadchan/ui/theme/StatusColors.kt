package com.moishe.shadchan.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.moishe.shadchan.data.PersonStatus
import com.moishe.shadchan.data.SuggestionStatus

// Colors per the spec's "Status Colors" section.
object StatusColors {
    val PersonActive = Color(0xFF43A047)     // Green
    val PersonInactive = Color(0xFF9E9E9E)   // Gray
    val PersonEngaged = Color(0xFF1E88E5)    // Blue
    val PersonMarried = Color(0xFFD4AF37)    // Gold

    val SuggestionNew = Color(0xFF1E88E5)        // Blue
    val SuggestionDiscussion = Color(0xFFFB8C00) // Orange
    val SuggestionSuccess = Color(0xFF43A047)    // Green
    val SuggestionClosed = Color(0xFF9E9E9E)     // Gray
    val SuggestionCancelled = Color(0xFFE53935)  // Red

    fun forPersonStatus(status: String): Color = when (status) {
        PersonStatus.ACTIVE -> PersonActive
        PersonStatus.INACTIVE -> PersonInactive
        PersonStatus.ENGAGED -> PersonEngaged
        PersonStatus.MARRIED -> PersonMarried
        else -> PersonActive
    }

    fun forSuggestionStatus(status: String): Color = when (SuggestionStatus.colorCategory(status)) {
        "new" -> SuggestionNew
        "discussion" -> SuggestionDiscussion
        "success" -> SuggestionSuccess
        "closed" -> SuggestionClosed
        "cancelled" -> SuggestionCancelled
        else -> SuggestionNew
    }
}
