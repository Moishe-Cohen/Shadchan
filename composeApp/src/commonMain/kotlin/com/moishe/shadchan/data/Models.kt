package com.moishe.shadchan.data

object Gender {
    const val BOY = "BOY"
    const val GIRL = "GIRL"
}

object PersonStatus {
    const val ACTIVE = "ACTIVE"
    const val ENGAGED = "ENGAGED"
    const val MARRIED = "MARRIED"
    const val INACTIVE = "INACTIVE"

    val ALL = listOf(ACTIVE, ENGAGED, MARRIED, INACTIVE)

    fun displayName(status: String): String = when (status) {
        ACTIVE -> "פעיל"
        ENGAGED -> "מאורס"
        MARRIED -> "נשוי"
        INACTIVE -> "לא פעיל"
        else -> status
    }
}

data class Person(
    val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val gender: String,
    val age: Int? = null,
    val birthDate: Long? = null,
    val city: String = "",
    val yeshivaOrSeminary: String = "",
    val community: String = "",
    val phone: String = "",
    val email: String = "",
    val photoPath: String? = null,
    val resumePath: String? = null,
    val notes: String = "",
    val status: String = PersonStatus.ACTIVE,
    val dateCreated: Long = 0,
    val dateUpdated: Long = 0
) {
    val fullName: String get() = "$firstName $lastName"
}

object RelationshipType {
    val SUGGESTIONS = listOf("הורה", "רב", "חבר", "שכן", "שדכן", "קרוב משפחה", "אחר")
}

data class Contact(
    val id: Long = 0,
    val personId: Long,
    val name: String,
    val relationship: String,
    val phone: String = "",
    val email: String = "",
    val notes: String = ""
)

object SuggestionStatus {
    const val NEW = "חדש"
    const val PROPOSED = "הוצע"
    const val UNDER_DISCUSSION = "בבירור"
    const val MET = "נפגשו"
    const val ADVANCING = "מתקדם"
    const val CLOSED = "נסגר"
    const val MARRIED = "התחתנו"
    const val CANCELLED = "בוטל"

    val ALL = listOf(NEW, PROPOSED, UNDER_DISCUSSION, MET, ADVANCING, CLOSED, MARRIED, CANCELLED)

    fun colorCategory(status: String): String = when (status) {
        NEW, PROPOSED -> "new"
        UNDER_DISCUSSION, MET -> "discussion"
        ADVANCING, MARRIED -> "success"
        CLOSED -> "closed"
        CANCELLED -> "cancelled"
        else -> "new"
    }
}

data class Suggestion(
    val id: Long = 0,
    val boyId: Long,
    val girlId: Long,
    val dateCreated: Long = 0,
    val createdBy: String = "",
    val status: String = SuggestionStatus.NEW,
    val lastUpdated: Long = 0,
    val notes: String = ""
)

data class SuggestionWithNames(
    val id: Long,
    val boyId: Long,
    val girlId: Long,
    val boyName: String,
    val girlName: String,
    val dateCreated: Long,
    val createdBy: String,
    val status: String,
    val lastUpdated: Long,
    val notes: String
)

data class AppSettings(
    val themeMode: String = ThemeMode.SYSTEM,
    val fontSize: String = FontSize.MEDIUM
)

object FontSize {
    const val SMALL = "SMALL"
    const val MEDIUM = "MEDIUM"
    const val LARGE = "LARGE"
}

object ThemeMode {
    const val SYSTEM = "SYSTEM"
    const val LIGHT = "LIGHT"
    const val DARK = "DARK"
}
