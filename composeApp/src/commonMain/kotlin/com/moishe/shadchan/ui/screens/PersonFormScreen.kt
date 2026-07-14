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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.moishe.shadchan.data.Gender
import com.moishe.shadchan.data.Person
import com.moishe.shadchan.data.PersonStatus
import com.moishe.shadchan.ui.ShadchanViewModel
import com.moishe.shadchan.ui.components.PhotoThumbnail
import com.moishe.shadchan.platform.rememberOpenFileLauncher
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonFormScreen(
    personId: Long?,
    initialGender: String,
    viewModel: ShadchanViewModel,
    onSaved: (Long) -> Unit,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val isEdit = personId != null && personId != 0L

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf(initialGender) }
    var age by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var yeshiva by remember { mutableStateOf("") }
    var community by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(PersonStatus.ACTIVE) }
    var photoPath by remember { mutableStateOf<String?>(null) }
    var resumePath by remember { mutableStateOf<String?>(null) }
    var existingId by remember { mutableStateOf(0L) }
    var existingDateCreated by remember { mutableStateOf(0L) }

    var duplicates by remember { mutableStateOf<List<Person>>(emptyList()) }
    var showDuplicateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(personId) {
        if (isEdit) {
            viewModel.getPerson(personId!!)?.let { p ->
                firstName = p.firstName; lastName = p.lastName; gender = p.gender
                age = p.age?.toString() ?: ""; city = p.city; yeshiva = p.yeshivaOrSeminary
                community = p.community; phone = p.phone; email = p.email
                notes = p.notes; status = p.status; photoPath = p.photoPath; resumePath = p.resumePath
                existingId = p.id; existingDateCreated = p.dateCreated
            }
        }
    }

    val photoLauncher = rememberOpenFileLauncher { source ->
        source?.let { photoPath = viewModel.copyPickedPhoto(it) }
    }
    val resumeLauncher = rememberOpenFileLauncher { source ->
        source?.let { resumePath = viewModel.copyPickedResume(it) }
    }

    fun buildPerson() = Person(
        id = existingId,
        firstName = firstName.trim(),
        lastName = lastName.trim(),
        gender = gender,
        age = age.toIntOrNull(),
        city = city.trim(),
        yeshivaOrSeminary = yeshiva.trim(),
        community = community.trim(),
        phone = phone.trim(),
        email = email.trim(),
        photoPath = photoPath,
        resumePath = resumePath,
        notes = notes.trim(),
        status = status,
        dateCreated = if (isEdit) existingDateCreated else System.currentTimeMillis()
    )

    fun doSave() {
        viewModel.savePerson(buildPerson()) { id -> onSaved(id) }
    }

    fun attemptSave() {
        scope.launch {
            val found = viewModel.checkDuplicates(firstName.trim(), lastName.trim(), age.toIntOrNull(), existingId)
            if (found.isNotEmpty()) {
                duplicates = found
                showDuplicateDialog = true
            } else {
                doSave()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEdit) "עריכת פרטים" else "הוספת אדם") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "חזרה") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                PhotoThumbnail(photoPath, size = 72.dp)
                Spacer(Modifier.width(12.dp))
                OutlinedButton(onClick = { photoLauncher.launch() }) {
                    Text("בחירת תמונה")
                }
            }
            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = gender == Gender.BOY, onClick = { gender = Gender.BOY }, label = { Text("בחור") })
                FilterChip(selected = gender == Gender.GIRL, onClick = { gender = Gender.GIRL }, label = { Text("בחורה") })
            }
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("שם פרטי") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("שם משפחה") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = age, onValueChange = { age = it.filter { c -> c.isDigit() } },
                label = { Text("גיל") }, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("עיר") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = yeshiva, onValueChange = { yeshiva = it },
                label = { Text(if (gender == Gender.BOY) "ישיבה" else "סמינר") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = community, onValueChange = { community = it }, label = { Text("קהילה (לא חובה)") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("טלפון") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("אימייל") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))

            Text("סטטוס", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PersonStatus.ALL.forEach { s ->
                    FilterChip(selected = status == s, onClick = { status = s }, label = { Text(PersonStatus.displayName(s)) })
                }
            }
            Spacer(Modifier.height(12.dp))

            OutlinedButton(onClick = { resumeLauncher.launch() }, modifier = Modifier.fillMaxWidth()) {
                Text(if (resumePath == null) "צירוף קורות חיים (PDF/DOC/DOCX)" else "קובץ קורות חיים מצורף ✓")
            }
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = notes, onValueChange = { notes = it },
                label = { Text("הערות כלליות") },
                modifier = Modifier.fillMaxWidth().height(120.dp)
            )
            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { attemptSave() },
                enabled = firstName.isNotBlank() && lastName.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEdit) "שמירת שינויים" else "הוספה")
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (showDuplicateDialog) {
        AlertDialog(
            onDismissRequest = { showDuplicateDialog = false },
            title = { Text("ייתכן שהאדם כבר קיים") },
            text = {
                Column {
                    Text("נמצאו אנשים עם שם וגיל דומים:")
                    duplicates.forEach { d ->
                        Text("• ${d.fullName}${d.age?.let { " (גיל $it)" } ?: ""}")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDuplicateDialog = false; doSave() }) { Text("המשך בכל זאת") }
            },
            dismissButton = {
                TextButton(onClick = { showDuplicateDialog = false }) { Text("ביטול") }
            }
        )
    }
}
