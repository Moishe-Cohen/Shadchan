package com.moishe.shadchan.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import com.moishe.shadchan.ui.components.SearchField
import com.moishe.shadchan.ui.theme.StatusColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestionsScreen(
    viewModel: ShadchanViewModel,
    onOpenSuggestion: (Long) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf<String?>(null) }
    var showFilterMenu by remember { mutableStateOf(false) }

    val suggestions by viewModel.allSuggestions(query, statusFilter).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("הצעות שידוך") },
                actions = {
                    Box {
                        IconButton(onClick = { showFilterMenu = true }) {
                            Icon(Icons.Default.FilterList, contentDescription = "סינון")
                        }
                        DropdownMenu(expanded = showFilterMenu, onDismissRequest = { showFilterMenu = false }) {
                            DropdownMenuItem(text = { Text("הכל") }, onClick = { statusFilter = null; showFilterMenu = false })
                            SuggestionStatus.ALL.forEach { s ->
                                DropdownMenuItem(text = { Text(s) }, onClick = { statusFilter = s; showFilterMenu = false })
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            SearchField(value = query, onValueChange = { query = it }, modifier = Modifier.padding(12.dp), label = "חיפוש לפי שם או הערה")
            if (suggestions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("אין הצעות", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(suggestions, key = { it.id }) { s ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                            onClick = { onOpenSuggestion(s.id) }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("${s.boyName}  •  ${s.girlName}", fontWeight = FontWeight.Medium)
                                    Text(s.status, color = StatusColors.forSuggestionStatus(s.status))
                                }
                                Text(
                                    "עודכן: ${formatDayMonthYear(s.lastUpdated)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (s.notes.isNotBlank()) {
                                    Text(s.notes, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
