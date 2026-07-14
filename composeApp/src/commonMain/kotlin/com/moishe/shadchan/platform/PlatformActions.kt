package com.moishe.shadchan.platform

expect class PlatformActions(context: PlatformContext) {
    /** Opens the given absolute file path with whatever the OS offers for its type. */
    fun openFile(path: String): Boolean
}
