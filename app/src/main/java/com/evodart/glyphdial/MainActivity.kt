package com.evodart.glyphdial

import android.Manifest
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telecom.TelecomManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.evodart.glyphdial.data.model.CallLogEntry
import com.evodart.glyphdial.data.model.Contact
import com.evodart.glyphdial.data.settings.AccentColor
import com.evodart.glyphdial.data.settings.ScrollbarPosition
import com.evodart.glyphdial.data.settings.SettingsDataStore
import com.evodart.glyphdial.ui.components.dialpad.SimPreference
import com.evodart.glyphdial.ui.components.navigation.NothingBottomNav
import com.evodart.glyphdial.ui.components.pager.SwipeablePagePager
import com.evodart.glyphdial.ui.components.pager.SwipeablePages
import com.evodart.glyphdial.ui.components.pager.rememberAppPagerState
import com.evodart.glyphdial.ui.screens.contacts.ContactDetailScreen
import com.evodart.glyphdial.ui.screens.contacts.ContactsScreen
import com.evodart.glyphdial.ui.screens.dialpad.DialPadScreen
import com.evodart.glyphdial.ui.screens.favorites.FavoritesScreen
import com.evodart.glyphdial.ui.screens.recents.CallDetailScreen
import com.evodart.glyphdial.ui.screens.recents.RecentsScreen
import com.evodart.glyphdial.ui.screens.settings.SettingsScreen
import com.evodart.glyphdial.ui.theme.GlyphDialTheme
import com.evodart.glyphdial.ui.theme.LocalAccentColor
import com.evodart.glyphdial.ui.theme.NothingColors
import com.evodart.glyphdial.ui.theme.toColor
import com.evodart.glyphdial.ui.viewmodel.CallLogViewModel
import com.evodart.glyphdial.ui.viewmodel.ContactsViewModel
import com.evodart.glyphdial.ui.viewmodel.DialerViewModel
import com.evodart.glyphdial.ui.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            val accentColor by settingsDataStore.accentColor.collectAsState(initial = AccentColor.RED)
            
            CompositionLocalProvider(LocalAccentColor provides accentColor.toColor()) {
                GlyphDialTheme {
                    GlyphDialContent()
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GlyphDialContent(
    contactsViewModel: ContactsViewModel = hiltViewModel(),
    callLogViewModel: CallLogViewModel = hiltViewModel(),
    dialerViewModel: DialerViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
    // Settings state
    val defaultStartPage by settingsViewModel.defaultStartPage.collectAsState()
    val scrollbarPosition by settingsViewModel.scrollbarPosition.collectAsState()
    val showRecommendations by settingsViewModel.showRecommendations.collectAsState()
    val accentColor by settingsViewModel.accentColor.collectAsState()
    
    // Pager state with default from settings
    val pagerState = rememberAppPagerState(defaultStartPage)
    
    // Contact state
    val contacts by contactsViewModel.contacts.collectAsState()
    val contactsLoading by contactsViewModel.isLoading.collectAsState()
    val hasContactsPermission by contactsViewModel.hasPermission.collectAsState()
    
    // CallLog state
    val recentCalls by callLogViewModel.recentCalls.collectAsState()
    val callLogLoading by callLogViewModel.isLoading.collectAsState()
    val hasCallLogPermission by callLogViewModel.hasPermission.collectAsState()
    
    // Dialer state
    val hasPhonePermission by dialerViewModel.hasPhonePermission.collectAsState()
    val t9Suggestions by dialerViewModel.t9Suggestions.collectAsState()
    
    // Detail screen states
    var selectedContact by remember { mutableStateOf<Contact?>(null) }
    var selectedCall by remember { mutableStateOf<CallLogEntry?>(null) }
    
    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { 
        contactsViewModel.checkPermission()
        callLogViewModel.checkPermission()
        dialerViewModel.checkPermission()
    }
    
    // Default dialer launcher
    val defaultDialerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { /* Result handled by system */ }
    
    // Request permissions on launch
    LaunchedEffect(Unit) {
        val needed = mutableListOf<String>()
        if (!hasContactsPermission) needed.add(Manifest.permission.READ_CONTACTS)
        if (!hasCallLogPermission) {
            needed.add(Manifest.permission.READ_CALL_LOG)
            needed.add(Manifest.permission.WRITE_CALL_LOG)
        }
        needed.add(Manifest.permission.CALL_PHONE)
        needed.add(Manifest.permission.READ_PHONE_STATE)
        if (needed.isNotEmpty()) permissionLauncher.launch(needed.toTypedArray())
    }
    
    val dualSimEnabled = false
    val simPreference = SimPreference.ALWAYS_ASK
    
    // Helper function to make call
    fun makeCall(number: String) {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))
        context.startActivity(intent)
    }
    
    // Helper function to send SMS
    fun sendSms(number: String) {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$number"))
        context.startActivity(intent)
    }
    
    // Helper to request default dialer
    fun requestDefaultDialer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = context.getSystemService(Context.ROLE_SERVICE) as RoleManager
            if (roleManager.isRoleAvailable(RoleManager.ROLE_DIALER)) {
                val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
                defaultDialerLauncher.launch(intent)
            }
        } else {
            val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            if (context.packageName != telecomManager.defaultDialerPackage) {
                val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
                    .putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, context.packageName)
                context.startActivity(intent)
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        // Main content
        SwipeablePagePager(
            pagerState = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 92.dp)
        ) { route ->
            when (route) {
                "dial" -> DialPadScreen(
                    dualSimEnabled = dualSimEnabled,
                    simPreference = simPreference,
                    t9Suggestions = t9Suggestions,
                    onSearchQueryChange = { dialerViewModel.updateSearchQuery(it) },
                    modifier = Modifier.fillMaxSize()
                )
                "recents" -> {
                    if (!hasCallLogPermission) {
                        PermissionRequiredScreen(
                            title = "Call Log Permission",
                            message = "Grant permission to view recent calls",
                            onRequestPermission = {
                                permissionLauncher.launch(arrayOf(Manifest.permission.READ_CALL_LOG))
                            }
                        )
                    } else {
                        RecentsScreen(
                            calls = recentCalls,
                            onCallClick = { call -> selectedCall = call },
                            onRedialClick = { number -> makeCall(number) },
                            isLoading = callLogLoading
                        )
                    }
                }
                "contacts" -> {
                    if (!hasContactsPermission) {
                        PermissionRequiredScreen(
                            title = "Contacts Permission",
                            message = "Grant permission to view contacts",
                            onRequestPermission = {
                                permissionLauncher.launch(arrayOf(Manifest.permission.READ_CONTACTS))
                            }
                        )
                    } else {
                        ContactsScreen(
                            contacts = contacts,
                            onContactClick = { contact -> selectedContact = contact },
                            onCallClick = { number -> makeCall(number) },
                            isLoading = contactsLoading,
                            scrollbarPosition = scrollbarPosition
                        )
                    }
                }
                "favorites" -> {
                    if (!hasContactsPermission) {
                        PermissionRequiredScreen(
                            title = "Contacts Permission",
                            message = "Grant permission to view favorites",
                            onRequestPermission = {
                                permissionLauncher.launch(arrayOf(Manifest.permission.READ_CONTACTS))
                            }
                        )
                    } else {
                        FavoritesScreen(
                            contacts = contactsViewModel.getStarredContacts(),
                            onContactClick = { contact -> selectedContact = contact },
                            onCallClick = { number -> makeCall(number) },
                            isLoading = contactsLoading
                        )
                    }
                }
                "settings" -> SettingsScreen(
                    defaultStartPage = defaultStartPage,
                    scrollbarPosition = scrollbarPosition,
                    showRecommendations = showRecommendations,
                    accentColor = accentColor,
                    onDefaultStartPageChange = { page -> settingsViewModel.setDefaultStartPage(page) },
                    onScrollbarPositionChange = { pos -> settingsViewModel.setScrollbarPosition(pos) },
                    onShowRecommendationsChange = { show -> settingsViewModel.setShowRecommendations(show) },
                    onAccentColorChange = { color -> settingsViewModel.setAccentColor(color) },
                    onSetDefaultDialerClick = { requestDefaultDialer() }
                )
            }
        }
        
        // Bottom nav
        NothingBottomNav(
            pagerState = pagerState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        
        // Contact Detail Overlay
        AnimatedVisibility(
            visible = selectedContact != null,
            enter = slideInHorizontally { it } + fadeIn(),
            exit = slideOutHorizontally { it } + fadeOut()
        ) {
            selectedContact?.let { contact ->
                ContactDetailScreen(
                    contact = contact,
                    onBackClick = { selectedContact = null },
                    onCallClick = { number -> makeCall(number) },
                    onSmsClick = { number -> sendSms(number) },
                    onFavoriteToggle = { /* TODO: toggle favorite */ },
                    onEditClick = { /* TODO: edit contact */ },
                    onShareClick = { /* TODO: share contact */ },
                    onDeleteClick = { /* TODO: delete contact */ }
                )
            }
        }
        
        // Call Detail Overlay
        AnimatedVisibility(
            visible = selectedCall != null,
            enter = slideInHorizontally { it } + fadeIn(),
            exit = slideOutHorizontally { it } + fadeOut()
        ) {
            selectedCall?.let { call ->
                CallDetailScreen(
                    call = call,
                    onBackClick = { selectedCall = null },
                    onCallClick = { makeCall(call.number) },
                    onSmsClick = { sendSms(call.number) },
                    onAddToContactsClick = { /* TODO: add to contacts */ },
                    onBlockClick = { /* TODO: block number */ }
                )
            }
        }
    }
}

@Composable
private fun PermissionRequiredScreen(
    title: String,
    message: String,
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium, color = NothingColors.PureWhite)
        Spacer(Modifier.height(8.dp))
        Text(message, style = MaterialTheme.typography.bodyLarge, color = NothingColors.SilverGray)
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onRequestPermission,
            colors = ButtonDefaults.buttonColors(containerColor = LocalAccentColor.current)
        ) { Text("Grant Permission") }
    }
}
