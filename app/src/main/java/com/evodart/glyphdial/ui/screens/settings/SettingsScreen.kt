package com.evodart.glyphdial.ui.screens.settings
import com.evodart.glyphdial.ui.components.animation.nothingClickable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import com.evodart.glyphdial.ui.components.animation.nothingClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evodart.glyphdial.data.settings.AccentColor
import com.evodart.glyphdial.data.settings.ScrollbarPosition
import com.evodart.glyphdial.ui.theme.LocalAccentColor
import com.evodart.glyphdial.ui.theme.NothingColors

/**
 * Multi-page Settings screen with sub-screens
 */
@Composable
fun SettingsScreen(
    defaultStartPage: String,
    scrollbarPosition: ScrollbarPosition,
    showRecommendations: Boolean,
    accentColor: AccentColor,
    defaultSimSlot: Int = -1,
    dualSimEnabled: Boolean = false,
    sim1Name: String = "SIM 1",
    sim2Name: String = "SIM 2",
    onDefaultStartPageChange: (String) -> Unit,
    onScrollbarPositionChange: (ScrollbarPosition) -> Unit,
    onShowRecommendationsChange: (Boolean) -> Unit,
    onAccentColorChange: (AccentColor) -> Unit,
    onSetDefaultDialerClick: () -> Unit,
    onDefaultSimSlotChange: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf<SettingsSubScreen?>(null) }
    val accent = LocalAccentColor.current
    
    Box(modifier = modifier.fillMaxSize()) {
        // Main settings list
        SettingsMainScreen(
            accentColor = accent,
            onNavigate = { currentScreen = it },
            onSetDefaultDialerClick = onSetDefaultDialerClick
        )
        
        // Sub-screens
        AnimatedVisibility(
            visible = currentScreen == SettingsSubScreen.APPEARANCE,
            enter = slideInHorizontally { it } + fadeIn(),
            exit = slideOutHorizontally { it } + fadeOut()
        ) {
            AppearanceSettingsScreen(
                accentColor = accentColor,
                onAccentColorChange = onAccentColorChange,
                onBack = { currentScreen = null }
            )
        }
        
        AnimatedVisibility(
            visible = currentScreen == SettingsSubScreen.STARTUP,
            enter = slideInHorizontally { it } + fadeIn(),
            exit = slideOutHorizontally { it } + fadeOut()
        ) {
            StartupSettingsScreen(
                defaultStartPage = defaultStartPage,
                onDefaultStartPageChange = onDefaultStartPageChange,
                onBack = { currentScreen = null }
            )
        }
        
        AnimatedVisibility(
            visible = currentScreen == SettingsSubScreen.CONTACTS,
            enter = slideInHorizontally { it } + fadeIn(),
            exit = slideOutHorizontally { it } + fadeOut()
        ) {
            ContactsSettingsScreen(
                scrollbarPosition = scrollbarPosition,
                onScrollbarPositionChange = onScrollbarPositionChange,
                onBack = { currentScreen = null }
            )
        }
        
        AnimatedVisibility(
            visible = currentScreen == SettingsSubScreen.SEARCH,
            enter = slideInHorizontally { it } + fadeIn(),
            exit = slideOutHorizontally { it } + fadeOut()
        ) {
            SearchSettingsScreen(
                showRecommendations = showRecommendations,
                onShowRecommendationsChange = onShowRecommendationsChange,
                onBack = { currentScreen = null }
            )
        }
        
        AnimatedVisibility(
            visible = currentScreen == SettingsSubScreen.CALLS,
            enter = slideInHorizontally { it } + fadeIn(),
            exit = slideOutHorizontally { it } + fadeOut()
        ) {
            CallsSettingsScreen(
                defaultSimSlot = defaultSimSlot,
                dualSimEnabled = dualSimEnabled,
                sim1Name = sim1Name,
                sim2Name = sim2Name,
                onDefaultSimSlotChange = onDefaultSimSlotChange,
                onBack = { currentScreen = null }
            )
        }
        
        AnimatedVisibility(
            visible = currentScreen == SettingsSubScreen.ABOUT,
            enter = slideInHorizontally { it } + fadeIn(),
            exit = slideOutHorizontally { it } + fadeOut()
        ) {
            AboutScreen(
                onBack = { currentScreen = null }
            )
        }
    }
}

enum class SettingsSubScreen {
    APPEARANCE, STARTUP, CONTACTS, SEARCH, CALLS, ABOUT
}

// ============ MAIN SETTINGS SCREEN ============

@Composable
private fun SettingsMainScreen(
    accentColor: Color,
    onNavigate: (SettingsSubScreen) -> Unit,
    onSetDefaultDialerClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            color = NothingColors.PureWhite,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Set as Default Dialer
        SettingsSection(title = "General", accentColor = accentColor) {
            SettingsNavItem(
                icon = Icons.Filled.PhoneAndroid,
                title = "Set as Default Dialer",
                subtitle = "Handle all phone calls with GlyphDial",
                onClick = onSetDefaultDialerClick
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Appearance
        SettingsSection(title = "Display", accentColor = accentColor) {
            SettingsNavItem(
                icon = Icons.Filled.Palette,
                title = "Appearance",
                subtitle = "Theme, colors, and display options",
                onClick = { onNavigate(SettingsSubScreen.APPEARANCE) }
            )
            SettingsDivider()
            SettingsNavItem(
                icon = Icons.Filled.Home,
                title = "Startup",
                subtitle = "Default page and launch options",
                onClick = { onNavigate(SettingsSubScreen.STARTUP) }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Features
        SettingsSection(title = "Features", accentColor = accentColor) {
            SettingsNavItem(
                icon = Icons.Filled.Contacts,
                title = "Contacts",
                subtitle = "Contact list display options",
                onClick = { onNavigate(SettingsSubScreen.CONTACTS) }
            )
            SettingsDivider()
            SettingsNavItem(
                icon = Icons.Filled.Search,
                title = "Search",
                subtitle = "T9 and contact search options",
                onClick = { onNavigate(SettingsSubScreen.SEARCH) }
            )
            SettingsDivider()
            SettingsNavItem(
                icon = Icons.Filled.Call,
                title = "Calls",
                subtitle = "Speed dial, blocking, and more",
                onClick = { onNavigate(SettingsSubScreen.CALLS) }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // About
        SettingsSection(title = "Other", accentColor = accentColor) {
            SettingsNavItem(
                icon = Icons.Filled.Info,
                title = "About",
                subtitle = "Version and app information",
                onClick = { onNavigate(SettingsSubScreen.ABOUT) }
            )
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

// ============ SUB-SCREENS ============

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppearanceSettingsScreen(
    accentColor: AccentColor,
    onAccentColorChange: (AccentColor) -> Unit,
    onBack: () -> Unit
) {
    val accent = LocalAccentColor.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NothingColors.PureBlack)
    ) {
        TopAppBar(
            title = { Text("Appearance", color = NothingColors.PureWhite) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = NothingColors.PureWhite)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = NothingColors.PureBlack)
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Accent Color
            SettingsSection(title = "Theme", accentColor = accent) {
                ColorPickerFull(
                    selectedColor = accentColor,
                    onColorSelected = onAccentColorChange
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Theme Mode (placeholder)
            SettingsSection(title = "Mode", accentColor = accent) {
                SettingsInfoItem(
                    icon = Icons.Filled.DarkMode,
                    title = "Dark Mode",
                    subtitle = "Always on (Light mode coming soon)",
                    trailing = {
                        Text("On", color = accent, fontWeight = FontWeight.Medium)
                    }
                )
                SettingsDivider()
                SettingsInfoItem(
                    icon = Icons.Filled.Contrast,
                    title = "Pure Black",
                    subtitle = "Use pure black background for OLED",
                    trailing = {
                        Text("On", color = accent, fontWeight = FontWeight.Medium)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StartupSettingsScreen(
    defaultStartPage: String,
    onDefaultStartPageChange: (String) -> Unit,
    onBack: () -> Unit
) {
    val accent = LocalAccentColor.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NothingColors.PureBlack)
    ) {
        TopAppBar(
            title = { Text("Startup", color = NothingColors.PureWhite) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = NothingColors.PureWhite)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = NothingColors.PureBlack)
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            SettingsSection(title = "Launch", accentColor = accent) {
                val pages = listOf(
                    "dial" to "Dial Pad",
                    "recents" to "Recents",
                    "contacts" to "Contacts",
                    "favorites" to "Favorites"
                )
                pages.forEachIndexed { index, (value, label) ->
                    if (index > 0) SettingsDivider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .nothingClickable { onDefaultStartPageChange(value) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = defaultStartPage == value,
                            onClick = { onDefaultStartPageChange(value) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = accent,
                                unselectedColor = NothingColors.SilverGray
                            )
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(label, color = NothingColors.PureWhite)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactsSettingsScreen(
    scrollbarPosition: ScrollbarPosition,
    onScrollbarPositionChange: (ScrollbarPosition) -> Unit,
    onBack: () -> Unit
) {
    val accent = LocalAccentColor.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NothingColors.PureBlack)
    ) {
        TopAppBar(
            title = { Text("Contacts", color = NothingColors.PureWhite) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = NothingColors.PureWhite)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = NothingColors.PureBlack)
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            SettingsSection(title = "Scrollbar", accentColor = accent) {
                SettingsToggleRow(
                    icon = Icons.Filled.SwapHoriz,
                    title = "Position",
                    subtitle = "Alphabet scrollbar side",
                    value = if (scrollbarPosition == ScrollbarPosition.RIGHT) "Right" else "Left",
                    onClick = {
                        onScrollbarPositionChange(
                            if (scrollbarPosition == ScrollbarPosition.RIGHT)
                                ScrollbarPosition.LEFT else ScrollbarPosition.RIGHT
                        )
                    },
                    accentColor = accent
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            SettingsSection(title = "Display", accentColor = accent) {
                SettingsInfoItem(
                    icon = Icons.Filled.SortByAlpha,
                    title = "Sort Order",
                    subtitle = "Sort by first name (Coming soon)",
                    trailing = { Text("First Name", color = NothingColors.SilverGray) }
                )
                SettingsDivider()
                SettingsInfoItem(
                    icon = Icons.Filled.Image,
                    title = "Show Photos",
                    subtitle = "Display contact photos",
                    trailing = { Text("On", color = accent, fontWeight = FontWeight.Medium) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchSettingsScreen(
    showRecommendations: Boolean,
    onShowRecommendationsChange: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    val accent = LocalAccentColor.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NothingColors.PureBlack)
    ) {
        TopAppBar(
            title = { Text("Search", color = NothingColors.PureWhite) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = NothingColors.PureWhite)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = NothingColors.PureBlack)
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            SettingsSection(title = "T9 Search", accentColor = accent) {
                SettingsSwitch(
                    icon = Icons.Filled.TipsAndUpdates,
                    title = "Show Recommendations",
                    subtitle = "Show frequent contacts when no results",
                    checked = showRecommendations,
                    onCheckedChange = onShowRecommendationsChange,
                    accentColor = accent
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            SettingsSection(title = "Options", accentColor = accent) {
                SettingsInfoItem(
                    icon = Icons.Filled.History,
                    title = "Search History",
                    subtitle = "Save recent searches (Coming soon)",
                    trailing = { Text("Off", color = NothingColors.SilverGray) }
                )
                SettingsDivider()
                SettingsInfoItem(
                    icon = Icons.Filled.Tune,
                    title = "Match Threshold",
                    subtitle = "Minimum characters for T9",
                    trailing = { Text("1", color = accent, fontWeight = FontWeight.Medium) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CallsSettingsScreen(
    defaultSimSlot: Int = -1,
    dualSimEnabled: Boolean = false,
    sim1Name: String = "SIM 1",
    sim2Name: String = "SIM 2",
    onDefaultSimSlotChange: (Int) -> Unit = {},
    onBack: () -> Unit
) {
    val accent = LocalAccentColor.current
    var showSimPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NothingColors.PureBlack)
    ) {
        TopAppBar(
            title = { Text("Calls", color = NothingColors.PureWhite) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = NothingColors.PureWhite)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = NothingColors.PureBlack)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // SIM preference (only on dual-SIM devices)
            if (dualSimEnabled) {
                SettingsSection(title = "Dual SIM", accentColor = accent) {
                    val simLabel = when (defaultSimSlot) {
                        0    -> sim1Name
                        1    -> sim2Name
                        else -> "Always ask"
                    }
                    SettingsToggleRow(
                        icon = Icons.Filled.SimCard,
                        title = "Default SIM for calls",
                        subtitle = "Which SIM to use when dialing",
                        value = simLabel,
                        onClick = { showSimPicker = true },
                        accentColor = accent
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            SettingsSection(title = "Speed Dial", accentColor = accent) {
                SettingsInfoItem(
                    icon = Icons.Filled.Speed,
                    title = "Configure Speed Dial",
                    subtitle = "Long-press numbers 2-9 (Coming soon)",
                    trailing = {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null,
                            tint = NothingColors.SilverGray, modifier = Modifier.size(20.dp))
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsSection(title = "Blocking", accentColor = accent) {
                SettingsInfoItem(
                    icon = Icons.Filled.Block,
                    title = "Blocked Numbers",
                    subtitle = "Manage blocked callers (Coming soon)",
                    trailing = {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null,
                            tint = NothingColors.SilverGray, modifier = Modifier.size(20.dp))
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsSection(title = "Call Options", accentColor = accent) {
                SettingsInfoItem(
                    icon = Icons.Filled.Vibration,
                    title = "Vibrate on Connect",
                    subtitle = "Vibrate when call connects (Coming soon)",
                    trailing = { Text("Off", color = NothingColors.SilverGray) }
                )
                SettingsDivider()
                SettingsInfoItem(
                    icon = Icons.Filled.RecordVoiceOver,
                    title = "Call Recording",
                    subtitle = "Auto-record calls (Coming soon)",
                    trailing = { Text("Off", color = NothingColors.SilverGray) }
                )
            }
        }
    }

    // SIM picker bottom sheet
    if (showSimPicker) {
        androidx.compose.material3.ModalBottomSheet(
            onDismissRequest = { showSimPicker = false },
            containerColor = NothingColors.CharcoalBlack
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    "Default SIM for calls",
                    style = MaterialTheme.typography.titleMedium,
                    color = NothingColors.PureWhite,
                    fontWeight = FontWeight.Bold,
                    modifier = androidx.compose.ui.Modifier.padding(bottom = 12.dp)
                )
                listOf(
                    -1 to "Always ask",
                    0  to sim1Name,
                    1  to sim2Name
                ).forEach { (slot, label) ->
                    TextButton(
                        onClick = { onDefaultSimSlotChange(slot); showSimPicker = false },
                        modifier = androidx.compose.ui.Modifier.fillMaxWidth()
                    ) {
                        if (slot == defaultSimSlot) {
                            Icon(Icons.Filled.Check, null, tint = accent,
                                modifier = androidx.compose.ui.Modifier.size(18.dp))
                            Spacer(androidx.compose.ui.Modifier.width(8.dp))
                        } else {
                            Spacer(androidx.compose.ui.Modifier.width(26.dp))
                        }
                        Text(
                            label,
                            color = if (slot == defaultSimSlot) accent else NothingColors.PureWhite,
                            modifier = androidx.compose.ui.Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AboutScreen(
    onBack: () -> Unit
) {
    val accent = LocalAccentColor.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NothingColors.PureBlack)
    ) {
        TopAppBar(
            title = { Text("About", color = NothingColors.PureWhite) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = NothingColors.PureWhite)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = NothingColors.PureBlack)
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // App info card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NothingColors.SurfaceCard)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(accent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Dialpad,
                        contentDescription = null,
                        tint = NothingColors.PureWhite,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    "GlyphDial",
                    style = MaterialTheme.typography.headlineSmall,
                    color = NothingColors.PureWhite,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Version 1.0.0",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NothingColors.SilverGray
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            SettingsSection(title = "Information", accentColor = accent) {
                InfoRow(icon = Icons.Filled.Code, title = "Developer", value = "EvoDart")
                SettingsDivider()
                InfoRow(icon = Icons.Filled.Build, title = "Build", value = "Debug")
                SettingsDivider()
                InfoRow(icon = Icons.Filled.Android, title = "Platform", value = "Android 12+")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            SettingsSection(title = "Links", accentColor = accent) {
                SettingsInfoItem(
                    icon = Icons.Filled.Description,
                    title = "Privacy Policy",
                    subtitle = "View privacy information",
                    trailing = {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, 
                            tint = NothingColors.SilverGray, modifier = Modifier.size(20.dp))
                    }
                )
                SettingsDivider()
                SettingsInfoItem(
                    icon = Icons.Filled.Gavel,
                    title = "Terms of Service",
                    subtitle = "View terms and conditions",
                    trailing = {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, 
                            tint = NothingColors.SilverGray, modifier = Modifier.size(20.dp))
                    }
                )
            }
        }
    }
}

// ============ SHARED COMPONENTS ============

@Composable
private fun SettingsSection(
    title: String,
    accentColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = accentColor,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(NothingColors.SurfaceCard)
        ) { content() }
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = NothingColors.DarkGray.copy(alpha = 0.3f)
    )
}

@Composable
private fun SettingsNavItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .nothingClickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = NothingColors.SilverGray, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = NothingColors.PureWhite)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = NothingColors.SilverGray)
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, 
            tint = NothingColors.SilverGray, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun SettingsInfoItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = NothingColors.SilverGray, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = NothingColors.PureWhite)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = NothingColors.SilverGray)
        }
        trailing()
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    value: String,
    onClick: () -> Unit,
    accentColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth().nothingClickable(onClick = onClick).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = NothingColors.SilverGray, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = NothingColors.PureWhite)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = NothingColors.SilverGray)
        }
        Text(value, style = MaterialTheme.typography.bodyMedium, color = accentColor, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SettingsSwitch(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    accentColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth().nothingClickable { onCheckedChange(!checked) }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = NothingColors.SilverGray, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = NothingColors.PureWhite)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = NothingColors.SilverGray)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = accentColor,
                checkedTrackColor = accentColor.copy(alpha = 0.3f),
                uncheckedThumbColor = NothingColors.SilverGray,
                uncheckedTrackColor = NothingColors.DarkGray
            )
        )
    }
}

@Composable
private fun ColorPickerFull(
    selectedColor: AccentColor,
    onColorSelected: (AccentColor) -> Unit
) {
    Column(Modifier.fillMaxWidth().padding(16.dp)) {
        Text("Accent Color", style = MaterialTheme.typography.bodyLarge, 
            color = NothingColors.PureWhite, modifier = Modifier.padding(bottom = 4.dp))
        Text("Choose your theme color", style = MaterialTheme.typography.bodyMedium, 
            color = NothingColors.SilverGray, modifier = Modifier.padding(bottom = 16.dp))
        
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            AccentColor.entries.forEach { color ->
                ColorSwatch(
                    color = Color(color.hex),
                    isSelected = color == selectedColor,
                    onClick = { onColorSelected(color) }
                )
            }
        }
    }
}

@Composable
private fun ColorSwatch(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(36.dp).clip(CircleShape).background(color)
            .then(if (isSelected) Modifier.border(3.dp, NothingColors.PureWhite, CircleShape) else Modifier)
            .nothingClickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(Icons.Filled.Check, "Selected",
                tint = if (color == Color(AccentColor.WHITE.hex)) NothingColors.PureBlack else NothingColors.PureWhite,
                modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, title: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = NothingColors.SilverGray, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Text(title, style = MaterialTheme.typography.bodyLarge, color = NothingColors.PureWhite, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyMedium, color = NothingColors.SilverGray)
    }
}

