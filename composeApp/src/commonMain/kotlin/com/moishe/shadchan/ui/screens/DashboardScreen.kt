package com.moishe.shadchan.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moishe.shadchan.ui.ShadchanViewModel
import com.moishe.shadchan.ui.theme.StatusColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: ShadchanViewModel,
    onOpenPerson: (Long) -> Unit,
    onOpenSuggestion: (Long) -> Unit
) {
    val boys by viewModel.boysCount().collectAsState(initial = 0)
    val girls by viewModel.girlsCount().collectAsState(initial = 0)
    val activeSuggestions by viewModel.activeSuggestionsCount().collectAsState(initial = 0)
    val recentPeople by viewModel.recentlyAddedPeople().collectAsState(initial = emptyList())
    val recentActivity by viewModel.recentActivity().collectAsState(initial = emptyList())

    Scaffold(topBar = { TopAppBar(title = { Text("לוח בקרה") }) }) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard("בחורים", boys.toString(), Modifier.weight(1f))
                    StatCard("בחורות", girls.toString(), Modifier.weight(1f))
                    StatCard("הצעות פעילות", activeSuggestions.toString(), Modifier.weight(1f))
                }
            }
            item {
                Text(
                    "נוספו לאחרונה",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            items(recentPeople, key = { "p${it.id}" }) { person ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                    onClick = { onOpenPerson(person.id) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(person.fullName)
                        Text(
                            if (person.gender == "BOY") "בחור" else "בחורה",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            item {
                Text(
                    "פעילות אחרונה",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            items(recentActivity, key = { "s${it.id}" }) { s ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                    onClick = { onOpenSuggestion(s.id) }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("${s.boyName} ו${s.girlName}", fontWeight = FontWeight.Medium)
                        Text(s.status, color = StatusColors.forSuggestionStatus(s.status))
                    }
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
        ) {
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.bodySmall)
        }
    }
}
