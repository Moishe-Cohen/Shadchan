package com.moishe.shadchan.platform

/**
 * Opaque per-platform context. On Android this wraps the app Context (needed for the
 * SQLDelight driver, DataStore, file storage, and share/open intents). On desktop it
 * carries nothing - everything there is resolved via java.io/java.awt.Desktop directly.
 */
expect class PlatformContext
