package com.moishe.shadchan.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moishe.shadchan.data.SuggestionStatus
import com.moishe.shadchan.ui.ShadchanViewModel
import com.moishe.shadchan.platform.formatDayMonthYear
import com.moishe.shadchan.ui.theme.StatusColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestionDetailScreen(
    suggestionId: Long,
    viewModel: ShadchanViewModel,
    onBack: () -> Unit,
    onDeleted: () -> Unit,
    onOpenPerson: (Long) -> Unit
) {
    val suggestion by viewModel.suggestionFlow(suggestionId).collectAsState(initial = null)
    var notes by remember { mutableStateOf("") }
    var notesInitialized by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(suggestion?.id) {
        if (!notesInitialized && suggestion != null) {
            notes = suggestion!!.notes
            notesInitialized = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("פרטי הצעה") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "חזרה") }
                }
            )
        }
    ) { padding ->
        val s = suggestion
        if (s == null) {
            Column(modifier = Modifier.fillMaxSize().padding(padding)) { Text("טוען...", modifier = Modifier.padding(16.dp)) }
            return@Scaffold
        }
        

        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = { onOpenPerson(s.boyId) }) { Text(s.boyName, fontWeight = FontWeight.Bold) }
                Text("♥", color = MaterialTheme.colorScheme.primary)
                TextButton(onClick = { onOpenPerson(s.girlId) }) { Text(s.girlName, fontWeight = FontWeight.Bold) }
            }
            Spacer(Modifier.height(8.dp))
            Text("נוצר: ${formatDayMonthYear(s.dateCreated)}", style = MaterialTheme.typography.bodySmall)
            Text("עודכן: ${formatDayMonthYear(s.lastUpdated)}", style = MaterialTheme.typography.bodySmall)
            if (s.createdBy.isNotBlank()) Text("נוצר על ידי: ${s.createdBy}", style = MaterialTheme.typography.bodySmall)

            Spacer(Modifier.height(16.dp))
            Text("סטטוס", style = MaterialTheme.typography.titleMedium)
            StatusChipsGrid(current = s.status) { newStatus ->
                viewModel.updateSuggestion(
                    com.moishe.shadchan.data.Suggestion(
                        id = s.id, boyId = s.boyId, girlId = s.girlId,
                        dateCreated = s.dateCreated, createdBy = s.createdBy,
                        status = newStatus, notes = notes
                    )
                )
            }

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("הערות") },
                modifier = Modifier.fillMaxWidth().height(120.dp)
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    viewModel.updateSuggestion(
                        com.moishe.shadchan.data.Suggestion(
                            id = s.id, boyId = s.boyId, girlId = s.girlId,
                            dateCreated = s.dateCreated, createdBy = s.createdBy,
                            status = s.status, notes = notes
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("שמירת הערות") }

            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {
                    viewModel.updateSuggestion(
                        com.moishe.shadchan.data.Suggestion(
                            id = s.id, boyId = s.boyId, girlId = s.girlId,
                            dateCreated = s.dateCreated, createdBy = s.createdBy,
                            status = SuggestionStatus.MARRIED, notes = notes
                        )
                    )
                }) { Text("סימון כהתחתנו") }
                OutlinedButton(onClick = {
                    viewModel.updateSuggestion(
                        com.moishe.shadchan.data.Suggestion(
                            id = s.id, boyId = s.boyId, girlId = s.girlId,
                            dateCreated = s.dateCreated, createdBy = s.createdBy,
                            status = SuggestionStatus.CLOSED, notes = notes
                        )
                    )
                }) { Text("סגירת הצעה") }
            }
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = { showDeleteConfirm = true }) {
                Text("מחיקת הצעה", color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (showDeleteConfirm) {
        val s = suggestion
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("מחיקת הצעה") },
            text = { Text("האם למחוק את ההצעה בין ${s?.boyName} ל${s?.girlName}?") },
            confirmButton = {
                TextButton(onClick = {
                    s?.let {
                        viewModel.deleteSuggestion(
                            com.moishe.shadchan.data.Suggestion(
                                id = it.id, boyId = it.boyId, girlId = it.girlId,
                                dateCreated = it.dateCreated, createdBy = it.createdBy,
                                status = it.status, notes = it.notes
                            )
                        )
                    }
                    showDeleteConfirm = false
                    onDeleted()
                }) { Text("מחיקה") }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirm = false }) { Text("ביטול") } }
        )
    }
}

@Composable
private fun StatusChipsGrid(current: String, onSelect: (String) -> Unit) {
    val rows = SuggestionStatus.ALL.chunked(3)
    Column {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 3.dp)) {
                row.forEach { status ->
                    FilterChip(
                        selected = current == status,
                        onClick = { onSelect(status) },
                        label = { Text(status) }
                    )
                }
            }
        }
    }
}
