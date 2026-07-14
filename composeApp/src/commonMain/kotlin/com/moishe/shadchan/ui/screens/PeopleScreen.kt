package com.moishe.shadchan.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.unit.dp
import com.moishe.shadchan.data.Gender
import com.moishe.shadchan.data.PersonStatus
import com.moishe.shadchan.ui.ShadchanViewModel
import com.moishe.shadchan.ui.components.PersonListRow
import com.moishe.shadchan.ui.components.SearchField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeopleScreen(
    viewModel: ShadchanViewModel,
    onOpenPerson: (Long) -> Unit,
    onAddPerson: (String) -> Unit
) {
    var tabIndex by remember { mutableStateOf(0) }
    val gender = if (tabIndex == 0) Gender.BOY else Gender.GIRL
    var query by remember { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf<String?>(null) }
    var showFilterMenu by remember { mutableStateOf(false) }

    val people by viewModel.peopleByGender(gender, query, statusFilter, null)
        .collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("אנשים") },
                actions = {
                    Box {
                        IconButton(onClick = { showFilterMenu = true }) {
                            Icon(Icons.Default.FilterList, contentDescription = "סינון")
                        }
                        DropdownMenu(expanded = showFilterMenu, onDismissRequest = { showFilterMenu = false }) {
                            DropdownMenuItem(text = { Text("הכל") }, onClick = { statusFilter = null; showFilterMenu = false })
                            PersonStatus.ALL.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text(PersonStatus.displayName(s)) },
                                    onClick = { statusFilter = s; showFilterMenu = false }
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAddPerson(gender) }) {
                Icon(Icons.Default.Add, contentDescription = "הוספת אדם")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = tabIndex) {
                Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }, text = { Text("בחורים") })
                Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }, text = { Text("בחורות") })
            }
            SearchField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.padding(12.dp),
                label = "חיפוש לפי שם, עיר, מוסד..."
            )
            if (people.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("לא נמצאו תוצאות", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(people, key = { it.id }) { person ->
                        PersonListRow(person = person, onClick = { onOpenPerson(person.id) })
                    }
                }
            }
        }
    }
}
