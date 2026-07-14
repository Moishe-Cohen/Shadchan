package com.moishe.shadchan.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import com.moishe.shadchan.data.Gender
import com.moishe.shadchan.ui.ShadchanViewModel
import com.moishe.shadchan.ui.components.PersonListRow
import com.moishe.shadchan.ui.components.SearchField
import com.moishe.shadchan.ui.theme.StatusColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceScreen(
    initialBoyId: Long?,
    initialGirlId: Long?,
    viewModel: ShadchanViewModel,
    onBack: () -> Unit,
    onDone: () -> Unit
) {
    var selectedBoyId by remember { mutableStateOf(initialBoyId?.takeIf { it > 0 }) }
    var query by remember { mutableStateOf("") }
    val selectedGirlIds = remember {
        mutableStateOf(setOfNotNull(initialGirlId?.takeIf { it > 0 }))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("סדנת שידוכים") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "חזרה") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            val boyId = selectedBoyId
            if (boyId == null) {
                // Step 1: choose a boy
                Text(
                    "שלב 1: בחירת בחור",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                SearchField(value = query, onValueChange = { query = it }, modifier = Modifier.padding(horizontal = 12.dp))
                val boys by viewModel.peopleByGender(Gender.BOY, query, null, null).collectAsState(initial = emptyList())
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(boys, key = { it.id }) { person ->
                        PersonListRow(person = person, onClick = { selectedBoyId = person.id; query = "" })
                    }
                }
            } else {
                // Step 2: selected boy header + existing suggestions + girls multi-select
                val boyFlow by viewModel.getPersonFlow(boyId).collectAsState(initial = null)
                val boyName = boyFlow?.fullName ?: ""

                Card(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("בחור נבחר", style = MaterialTheme.typography.labelMedium)
                            Text(boyName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        Button(onClick = { selectedBoyId = null; selectedGirlIds.value = emptySet() }) {
                            Text("החלפה")
                        }
                    }
                }

                val existingSuggestions by viewModel.suggestionsForPerson(boyId).collectAsState(initial = emptyList())
                if (existingSuggestions.isNotEmpty()) {
                    Text(
                        "הצעות קיימות לבחור זה",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
                        Column {
                            existingSuggestions.take(3).forEach { s ->
                                Text(
                                    "• ${s.girlName} — ${s.status}",
                                    color = StatusColors.forSuggestionStatus(s.status),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }

                Text(
                    "שלב 2: בחירת בחורה/ות",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                SearchField(value = query, onValueChange = { query = it }, modifier = Modifier.padding(horizontal = 12.dp))
                val girls by viewModel.peopleByGender(Gender.GIRL, query, null, null).collectAsState(initial = emptyList())
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(girls, key = { it.id }) { person ->
                        val isSelected = selectedGirlIds.value.contains(person.id)
                        PersonListRow(
                            person = person,
                            onClick = {
                                selectedGirlIds.value = if (isSelected) selectedGirlIds.value - person.id else selectedGirlIds.value + person.id
                            },
                            selected = isSelected,
                            selectable = true,
                            onSelectToggle = {
                                selectedGirlIds.value = if (isSelected) selectedGirlIds.value - person.id else selectedGirlIds.value + person.id
                            }
                        )
                    }
                }

                Button(
                    onClick = {
                        viewModel.createSuggestions(boyId, selectedGirlIds.value.toList())
                        onDone()
                    },
                    enabled = selectedGirlIds.value.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Text("יצירת הצעה (${selectedGirlIds.value.size})")
                }
            }
        }
    }
}
