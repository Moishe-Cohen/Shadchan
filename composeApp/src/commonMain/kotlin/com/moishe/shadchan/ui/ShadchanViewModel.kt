package com.moishe.shadchan.ui

import com.moishe.shadchan.data.Contact
import com.moishe.shadchan.data.Person
import com.moishe.shadchan.data.SettingsRepository
import com.moishe.shadchan.data.ShadchanRepository
import com.moishe.shadchan.data.Suggestion
import com.moishe.shadchan.db.DatabaseHolder
import com.moishe.shadchan.platform.BackupManager
import com.moishe.shadchan.platform.FileStorage
import com.moishe.shadchan.platform.PlatformContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ShadchanViewModel(private val context: PlatformContext) {

    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val db = DatabaseHolder.get(context)
    val repository = ShadchanRepository(db)
    val settingsRepository = SettingsRepository(context)
    private val fileStorage = FileStorage(context)
    private val backupManager = BackupManager(context)
    private val platformActions = com.moishe.shadchan.platform.PlatformActions(context)

    fun openResume(path: String): Boolean = platformActions.openFile(path)

    val settings = settingsRepository.settings

    // ---------- Dashboard ----------
    fun boysCount() = repository.countByGender("BOY")
    fun girlsCount() = repository.countByGender("GIRL")
    fun activeSuggestionsCount() = repository.countActiveSuggestions()
    fun recentlyAddedPeople() = repository.recentlyAdded(5)
    fun recentActivity() = repository.recentActivity(5)

    // ---------- People ----------
    fun peopleByGender(gender: String, query: String, status: String?, city: String?) =
        repository.peopleByGender(gender, query, status, city)

    fun globalSearch(query: String) = repository.globalSearch(query)

    fun getPersonFlow(id: Long) = repository.getPersonFlow(id)

    suspend fun getPerson(id: Long) = repository.getPerson(id)

    fun allCities() = repository.allCities()

    val duplicateWarning: MutableStateFlow<List<Person>> = MutableStateFlow(emptyList())

    suspend fun checkDuplicates(firstName: String, lastName: String, age: Int?, excludeId: Long = 0): List<Person> =
        repository.findPossibleDuplicates(firstName, lastName, age?.toLong(), excludeId)

    fun savePerson(person: Person, onSaved: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = if (person.id == 0L) {
                repository.addPerson(person)
            } else {
                repository.updatePerson(person)
                person.id
            }
            onSaved(id)
        }
    }

    fun deletePerson(person: Person, onDeleted: () -> Unit = {}) {
        viewModelScope.launch {
            fileStorage.deleteIfExists(person.photoPath)
            fileStorage.deleteIfExists(person.resumePath)
            repository.deletePerson(person)
            onDeleted()
        }
    }

    /** [source] is a platform-native file reference: a content:// URI string on Android, an
     *  absolute path on desktop - see [com.moishe.shadchan.platform.rememberOpenFileLauncher]. */
    fun copyPickedPhoto(source: String): String? = fileStorage.copyPickedPhoto(source)

    fun copyPickedResume(source: String): String? = fileStorage.copyPickedResume(source)

    // ---------- Contacts ----------
    fun contactsForPerson(personId: Long) = repository.contactsForPerson(personId)

    fun saveContact(contact: Contact) {
        viewModelScope.launch {
            if (contact.id == 0L) repository.addContact(contact) else repository.updateContact(contact)
        }
    }

    fun deleteContact(contact: Contact) {
        viewModelScope.launch { repository.deleteContact(contact) }
    }

    // ---------- Suggestions ----------
    fun allSuggestions(query: String, status: String?) = repository.allSuggestions(query, status)

    fun suggestionsForPerson(personId: Long) = repository.suggestionsForPerson(personId)

    fun suggestionFlow(id: Long) = repository.suggestionFlow(id)

    suspend fun suggestionExists(boyId: Long, girlId: Long) = repository.suggestionExistsForPair(boyId, girlId)

    fun createSuggestions(boyId: Long, girlIds: List<Long>, createdBy: String = "") {
        viewModelScope.launch {
            girlIds.forEach { girlId ->
                if (!repository.suggestionExistsForPair(boyId, girlId)) {
                    repository.createSuggestion(boyId, girlId, createdBy)
                }
            }
        }
    }

    fun updateSuggestion(suggestion: Suggestion) {
        viewModelScope.launch { repository.updateSuggestion(suggestion) }
    }

    fun deleteSuggestion(suggestion: Suggestion) {
        viewModelScope.launch { repository.deleteSuggestion(suggestion) }
    }

    // ---------- Settings ----------
    fun setThemeMode(mode: String) {
        viewModelScope.launch { settingsRepository.setThemeMode(mode) }
    }

    fun setFontSize(size: String) {
        viewModelScope.launch { settingsRepository.setFontSize(size) }
    }

    // ---------- Backup / Restore ----------
    private val _backupState = MutableStateFlow<String?>(null)
    val backupState: StateFlow<String?> = _backupState

    fun backupTo(destination: String) {
        viewModelScope.launch {
            val result = backupManager.createBackup(destination)
            _backupState.value = if (result.isSuccess) "הגיבוי נשמר בהצלחה" else "שגיאה בגיבוי: ${result.exceptionOrNull()?.message}"
        }
    }

    fun restoreFrom(source: String) {
        viewModelScope.launch {
            val result = backupManager.restoreBackup(source)
            _backupState.value = if (result.isSuccess)
                "השחזור הושלם. יש להפעיל מחדש את האפליקציה."
            else "שגיאה בשחזור: ${result.exceptionOrNull()?.message}"
        }
    }

    fun clearBackupState() {
        _backupState.value = null
    }

    fun onCleared() {
        viewModelScope.launch { }
        (viewModelScope.coroutineContext[Job])?.cancel()
    }
}
