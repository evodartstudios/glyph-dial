package com.evodart.glyphdial

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Glyph Dial Application class
 * 
 * Entry point for the app with Hilt dependency injection
 */
@HiltAndroidApp
class GlyphDialApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize any app-wide components here
        // e.g., crash reporting, analytics, etc.
    }
}
