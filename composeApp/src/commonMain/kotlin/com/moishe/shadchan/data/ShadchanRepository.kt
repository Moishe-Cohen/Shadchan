package com.moishe.shadchan.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.moishe.shadchan.db.ShadchanDatabase
import com.moishe.shadchan.platform.currentTimeMillis
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ShadchanRepository(
    private val db: ShadchanDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val q get() = db.shadchanQueries

    // ---------- People ----------

    fun peopleByGender(gender: String, query: String = "", status: String? = null, city: String? = null): Flow<List<Person>> =
        q.getByGender(gender, query, status, city, ::mapPerson).asFlow().mapToList(ioDispatcher)

    fun globalSearch(query: String): Flow<List<Person>> =
        q.globalSearch(query, ::mapPerson).asFlow().mapToList(ioDispatcher)

    suspend fun getPerson(id: Long): Person? = withContext(ioDispatcher) {
        q.getById(id, ::mapPerson).executeAsOneOrNull()
    }

    fun getPersonFlow(id: Long): Flow<Person?> =
        q.getById(id, ::mapPerson).asFlow().mapToOneOrNull(ioDispatcher)

    fun countByGender(gender: String): Flow<Int> =
        q.countByGender(gender).asFlow().mapToOne(ioDispatcher).map { it.toInt() }

    fun recentlyAdded(limit: Long = 5): Flow<List<Person>> =
        q.recentlyAdded(limit, ::mapPerson).asFlow().mapToList(ioDispatcher)

    fun allCities(): Flow<List<String>> =
        q.allCities().asFlow().mapToList(ioDispatcher)

    suspend fun findPossibleDuplicates(firstName: String, lastName: String, age: Long?, excludeId: Long = 0): List<Person> =
        withContext(ioDispatcher) { q.findPossibleDuplicates(firstName, lastName, excludeId, age, ::mapPerson).executeAsList() }

    suspend fun addPerson(person: Person): Long = withContext(ioDispatcher) {
        val now = nowMillis()
        q.insertPerson(
            firstName = person.firstName, lastName = person.lastName, gender = person.gender,
            age = person.age?.toLong(), birthDate = person.birthDate, city = person.city,
            yeshivaOrSeminary = person.yeshivaOrSeminary, community = person.community,
            phone = person.phone, email = person.email, photoPath = person.photoPath,
            resumePath = person.resumePath, notes = person.notes, status = person.status,
            dateCreated = now, dateUpdated = now
        )
        q.lastInsertRowId().executeAsOne()
    }

    suspend fun updatePerson(person: Person) = withContext(ioDispatcher) {
        q.updatePerson(
            firstName = person.firstName, lastName = person.lastName, gender = person.gender,
            age = person.age?.toLong(), birthDate = person.birthDate, city = person.city,
            yeshivaOrSeminary = person.yeshivaOrSeminary, community = person.community,
            phone = person.phone, email = person.email, photoPath = person.photoPath,
            resumePath = person.resumePath, notes = person.notes, status = person.status,
            dateUpdated = nowMillis(), id = person.id
        )
    }

    suspend fun deletePerson(person: Person) = withContext(ioDispatcher) { q.deletePerson(person.id) }

    suspend fun getAllPeopleOnce(): List<Person> = withContext(ioDispatcher) {
        q.getAllPeopleOnce(::mapPerson).executeAsList()
    }

    // ---------- Contacts ----------

    fun contactsForPerson(personId: Long): Flow<List<Contact>> =
        q.getContactsForPerson(personId, ::mapContact).asFlow().mapToList(ioDispatcher)

    suspend fun addContact(contact: Contact): Long = withContext(ioDispatcher) {
        q.insertContact(contact.personId, contact.name, contact.relationship, contact.phone, contact.email, contact.notes)
        q.lastInsertRowId().executeAsOne()
    }

    suspend fun updateContact(contact: Contact) = withContext(ioDispatcher) {
        q.updateContact(contact.name, contact.relationship, contact.phone, contact.email, contact.notes, contact.id)
    }

    suspend fun deleteContact(contact: Contact) = withContext(ioDispatcher) { q.deleteContact(contact.id) }

    // ---------- Suggestions ----------

    fun allSuggestions(query: String = "", status: String? = null): Flow<List<SuggestionWithNames>> =
        q.getAllSuggestions(query, status) { id, boyId, girlId, boyName, girlName, dateCreated, createdBy, status2, lastUpdated, notes ->
            SuggestionWithNames(id, boyId, girlId, boyName, girlName, dateCreated, createdBy, status2, lastUpdated, notes)
        }.asFlow().mapToList(ioDispatcher)

    fun suggestionsForPerson(personId: Long): Flow<List<SuggestionWithNames>> =
        q.getSuggestionsForPerson(personId) { id, boyId, girlId, boyName, girlName, dateCreated, createdBy, status, lastUpdated, notes ->
            SuggestionWithNames(id, boyId, girlId, boyName, girlName, dateCreated, createdBy, status, lastUpdated, notes)
        }.asFlow().mapToList(ioDispatcher)

    fun suggestionFlow(id: Long): Flow<SuggestionWithNames?> =
        q.getSuggestionById(id) { qid, boyId, girlId, boyName, girlName, dateCreated, createdBy, status, lastUpdated, notes ->
            SuggestionWithNames(qid, boyId, girlId, boyName, girlName, dateCreated, createdBy, status, lastUpdated, notes)
        }.asFlow().mapToOneOrNull(ioDispatcher)

    suspend fun getRawSuggestion(id: Long): Suggestion? = withContext(ioDispatcher) {
        q.getRawSuggestionById(id, ::mapSuggestion).executeAsOneOrNull()
    }

    fun countActiveSuggestions(): Flow<Int> =
        q.countActiveSuggestions().asFlow().mapToOne(ioDispatcher).map { it.toInt() }

    fun recentActivity(limit: Long = 5): Flow<List<SuggestionWithNames>> =
        q.recentActivity(limit) { id, boyId, girlId, boyName, girlName, dateCreated, createdBy, status, lastUpdated, notes ->
            SuggestionWithNames(id, boyId, girlId, boyName, girlName, dateCreated, createdBy, status, lastUpdated, notes)
        }.asFlow().mapToList(ioDispatcher)

    suspend fun suggestionExistsForPair(boyId: Long, girlId: Long): Boolean = withContext(ioDispatcher) {
        q.suggestionExistsForPair(boyId, girlId).executeAsOne() > 0
    }

    suspend fun createSuggestion(boyId: Long, girlId: Long, createdBy: String = "", notes: String = ""): Long = withContext(ioDispatcher) {
        val now = nowMillis()
        q.insertSuggestion(boyId, girlId, now, createdBy, SuggestionStatus.NEW, now, notes)
        q.lastInsertRowId().executeAsOne()
    }

    suspend fun updateSuggestion(suggestion: Suggestion) = withContext(ioDispatcher) {
        q.updateSuggestion(suggestion.status, nowMillis(), suggestion.notes, suggestion.id)
    }

    suspend fun deleteSuggestion(suggestion: Suggestion) = withContext(ioDispatcher) { q.deleteSuggestion(suggestion.id) }

    // ---------- mapping ----------

    private fun mapPerson(
        id: Long, firstName: String, lastName: String, gender: String, age: Long?, birthDate: Long?,
        city: String, yeshivaOrSeminary: String, community: String, phone: String, email: String,
        photoPath: String?, resumePath: String?, notes: String, status: String, dateCreated: Long, dateUpdated: Long
    ) = Person(id, firstName, lastName, gender, age?.toInt(), birthDate, city, yeshivaOrSeminary, community, phone, email, photoPath, resumePath, notes, status, dateCreated, dateUpdated)

    private fun mapContact(id: Long, personId: Long, name: String, relationship: String, phone: String, email: String, notes: String) =
        Contact(id, personId, name, relationship, phone, email, notes)

    private fun mapSuggestion(id: Long, boyId: Long, girlId: Long, dateCreated: Long, createdBy: String, status: String, lastUpdated: Long, notes: String) =
        Suggestion(id, boyId, girlId, dateCreated, createdBy, status, lastUpdated, notes)

    private fun nowMillis(): Long = currentTimeMillis()
}
