package com.moishe.shadchan.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import com.moishe.shadchan.data.Contact
import com.moishe.shadchan.data.Person
import com.moishe.shadchan.data.PersonStatus
import com.moishe.shadchan.data.RelationshipType
import com.moishe.shadchan.ui.ShadchanViewModel
import com.moishe.shadchan.ui.components.PhotoThumbnail
import com.moishe.shadchan.ui.theme.StatusColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonProfileScreen(
    personId: Long,
    viewModel: ShadchanViewModel,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onDeleted: () -> Unit,
    onAddSuggestion: (Long) -> Unit,
    onOpenSuggestion: (Long) -> Unit
) {
    val person by viewModel.getPersonFlow(personId).collectAsState(initial = null)
    val contacts by viewModel.contactsForPerson(personId).collectAsState(initial = emptyList())
    val suggestions by viewModel.suggestionsForPerson(personId).collectAsState(initial = emptyList())

    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showAddContact by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(person?.fullName ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "חזרה") }
                },
                actions = {
                    IconButton(onClick = { onEdit(personId) }) { Icon(Icons.Default.Edit, contentDescription = "עריכה") }
                    IconButton(onClick = { showDeleteConfirm = true }) { Icon(Icons.Default.Delete, contentDescription = "מחיקה") }
                }
            )
        }
    ) { padding ->
        val p = person
        if (p == null) {
            Column(modifier = Modifier.fillMaxSize().padding(padding), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(40.dp))
                Text("טוען...")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                PhotoThumbnail(p.photoPath, size = 96.dp)
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(p.fullName, style = MaterialTheme.typography.headlineSmall)
                    Text(
                        PersonStatus.displayName(p.status),
                        color = StatusColors.forPersonStatus(p.status),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            InfoRow("גיל", p.age?.toString() ?: "—")
            InfoRow("עיר", p.city.ifBlank { "—" })
            InfoRow(if (p.gender == "BOY") "ישיבה" else "סמינר", p.yeshivaOrSeminary.ifBlank { "—" })
            InfoRow("קהילה", p.community.ifBlank { "—" })
            InfoRow("טלפון", p.phone.ifBlank { "—" })
            InfoRow("אימייל", p.email.ifBlank { "—" })

            Spacer(Modifier.height(8.dp))
            if (p.resumePath != null) {
                OutlinedButton(onClick = { viewModel.openResume(p.resumePath) }, modifier = Modifier.fillMaxWidth()) {
                    Text("פתיחת קורות חיים")
                }
                Spacer(Modifier.height(8.dp))
            }

            Text("אנשי קשר", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 12.dp))
            contacts.forEach { c ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("${c.name} (${c.relationship})", style = MaterialTheme.typography.bodyLarge)
                        if (c.phone.isNotBlank()) Text(c.phone, style = MaterialTheme.typography.bodySmall)
                        if (c.notes.isNotBlank()) Text(c.notes, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            TextButton(onClick = { showAddContact = true }) { Text("+ הוספת איש קשר") }

            Spacer(Modifier.height(8.dp))
            Text("הערות", style = MaterialTheme.typography.titleMedium)
            Text(p.notes.ifBlank { "אין הערות" }, style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(16.dp))
            Text("הצעות שידוך", style = MaterialTheme.typography.titleMedium)
            suggestions.forEach { s ->
                val otherName = if (s.boyId == personId) s.girlName else s.boyName
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    onClick = { onOpenSuggestion(s.id) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(otherName)
                        Text(s.status, color = StatusColors.forSuggestionStatus(s.status))
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = { onAddSuggestion(personId) }, modifier = Modifier.fillMaxWidth()) {
                Text("הצעת שידוך חדשה")
            }
            Spacer(Modifier.height(32.dp))
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("מחיקת רשומה") },
            text = { Text("האם למחוק את ${person?.fullName}? פעולה זו אינה הפיכה.") },
            confirmButton = {
                TextButton(onClick = {
                    person?.let { viewModel.deletePerson(it) { onDeleted() } }
                    showDeleteConfirm = false
                }) { Text("מחיקה") }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirm = false }) { Text("ביטול") } }
        )
    }

    if (showAddContact) {
        AddContactDialog(
            onDismiss = { showAddContact = false },
            onConfirm = { name, rel, phone, notes ->
                viewModel.saveContact(Contact(personId = personId, name = name, relationship = rel, phone = phone, notes = notes))
                showAddContact = false
            }
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value)
    }
}

@Composable
private fun AddContactDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, relationship: String, phone: String, notes: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf(RelationshipType.SUGGESTIONS.first()) }
    var phone by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("הוספת איש קשר") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("שם") })
                Spacer(Modifier.height(8.dp))
                Text("קרבה", style = MaterialTheme.typography.labelMedium)
                Row {
                    RelationshipType.SUGGESTIONS.forEach { r ->
                        TextButton(onClick = { relationship = r }) {
                            Text(r, color = if (relationship == r) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("טלפון") })
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("הערות") })
            }
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onConfirm(name, relationship, phone, notes) }) { Text("הוספה") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("ביטול") } }
    )
}
