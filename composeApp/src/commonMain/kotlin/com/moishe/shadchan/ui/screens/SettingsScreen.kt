package com.moishe.shadchan.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moishe.shadchan.data.AppSettings
import com.moishe.shadchan.data.FontSize
import com.moishe.shadchan.data.ThemeMode
import com.moishe.shadchan.platform.currentTimeMillis
import com.moishe.shadchan.platform.rememberOpenFileLauncher
import com.moishe.shadchan.platform.rememberSaveFileLauncher
import com.moishe.shadchan.ui.ShadchanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: ShadchanViewModel) {
    val settings by viewModel.settings.collectAsState(initial = AppSettings())
    val backupMessage by viewModel.backupState.collectAsState()

    val backupFileName = "shadchan_backup_${currentTimeMillis()}.zip"
    val backupLauncher = rememberSaveFileLauncher(backupFileName) { dest ->
        dest?.let { viewModel.backupTo(it) }
    }
    var showRestoreConfirm by remember { mutableStateOf(false) }
    var pendingRestoreSource by remember { mutableStateOf<String?>(null) }
    val restoreLauncher = rememberOpenFileLauncher { source ->
        source?.let { pendingRestoreSource = it; showRestoreConfirm = true }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("הגדרות") }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("ערכת נושא", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
                FilterChip(selected = settings.themeMode == ThemeMode.SYSTEM, onClick = { viewModel.setThemeMode(ThemeMode.SYSTEM) }, label = { Text("מערכת") })
                FilterChip(selected = settings.themeMode == ThemeMode.LIGHT, onClick = { viewModel.setThemeMode(ThemeMode.LIGHT) }, label = { Text("בהיר") })
                FilterChip(selected = settings.themeMode == ThemeMode.DARK, onClick = { viewModel.setThemeMode(ThemeMode.DARK) }, label = { Text("כהה") })
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            Text("גודל גופן", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
                FilterChip(selected = settings.fontSize == FontSize.SMALL, onClick = { viewModel.setFontSize(FontSize.SMALL) }, label = { Text("קטן") })
                FilterChip(selected = settings.fontSize == FontSize.MEDIUM, onClick = { viewModel.setFontSize(FontSize.MEDIUM) }, label = { Text("בינוני") })
                FilterChip(selected = settings.fontSize == FontSize.LARGE, onClick = { viewModel.setFontSize(FontSize.LARGE) }, label = { Text("גדול") })
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            Text("גיבוי ושחזור", style = MaterialTheme.typography.titleMedium)
            Text(
                "גיבוי כולל את מסד הנתונים, התמונות וקבצי קורות החיים בקובץ ZIP אחד.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 6.dp)
            )
            Button(
                onClick = { backupLauncher.launch() },
                modifier = Modifier.fillMaxWidth()
            ) { Text("גיבוי מסד הנתונים") }

            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = { restoreLauncher.launch() },
                modifier = Modifier.fillMaxWidth()
            ) { Text("שחזור מגיבוי") }

            backupMessage?.let { msg ->
                Spacer(Modifier.height(8.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(msg, modifier = Modifier.padding(12.dp))
                }
            }
        }
    }

    if (showRestoreConfirm) {
        AlertDialog(
            onDismissRequest = { showRestoreConfirm = false },
            title = { Text("שחזור מגיבוי") },
            text = { Text("פעולה זו תחליף את כל הנתונים הקיימים באפליקציה בנתונים מהגיבוי. להמשיך?") },
            confirmButton = {
                TextButton(onClick = {
                    pendingRestoreSource?.let { viewModel.restoreFrom(it) }
                    showRestoreConfirm = false
                }) { Text("שחזור") }
            },
            dismissButton = { TextButton(onClick = { showRestoreConfirm = false }) { Text("ביטול") } }
        )
    }
}
