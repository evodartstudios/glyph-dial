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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.tween
import com.evodart.glyphdial.ui.theme.NothingMotion
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
    
    val telecomManager = remember { context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager }
    var phoneAccounts by remember { mutableStateOf<List<android.telecom.PhoneAccountHandle>>(emptyList()) }
    var dualSimEnabled by remember { mutableStateOf(false) }
    var sim1Name by remember { mutableStateOf("SIM 1") }
    var sim2Name by remember { mutableStateOf("SIM 2") }
    val simPreference = SimPreference.ALWAYS_ASK
    
    LaunchedEffect(hasPhonePermission) {
        if (hasPhonePermission) {
            try {
                @Suppress("MissingPermission")
                val accounts = telecomManager.callCapablePhoneAccounts
                phoneAccounts = accounts
                if (accounts.size > 1) {
                    dualSimEnabled = true
                    val acc1 = telecomManager.getPhoneAccount(accounts[0])
                    val acc2 = telecomManager.getPhoneAccount(accounts[1])
                    sim1Name = acc1?.label?.toString() ?: "SIM 1"
                    sim2Name = acc2?.label?.toString() ?: "SIM 2"
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
    }
    
    // Helper function to make call (emergency-aware)
    fun makeCall(number: String, simSlot: Int? = null) {
        // Emergency numbers MUST route via system, not our dialer
        if (com.evodart.glyphdial.utils.EmergencyNumbers.isEmergencyNumber(number)) {
            val emergencyIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))
            try { context.startActivity(emergencyIntent) } catch (_: Exception) {}
            return
        }

        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))
        if (simSlot != null && simSlot < phoneAccounts.size) {
            intent.putExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccounts[simSlot])
        }
        try {
            context.startActivity(intent)
        } catch (e: SecurityException) {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
            context.startActivity(dialIntent)
        }
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
            val tm = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            if (context.packageName != tm.defaultDialerPackage) {
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
                    modifier = Modifier.fillMaxSize(),
                    dualSimEnabled = dualSimEnabled,
                    simPreference = simPreference,
                    sim1Name = sim1Name,
                    sim2Name = sim2Name,
                    t9Suggestions = t9Suggestions,
                    onSearchQueryChange = { dialerViewModel.updateSearchQuery(it) },
                    onCall = { number, slot -> makeCall(number, slot) }
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
                            onDeleteGroup = { group ->
                                group.calls.forEach { callLogViewModel.deleteEntry(it.id) }
                            },
                            onClearAll = { callLogViewModel.clearAll() },
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
                "settings" -> {
                    val defaultSimSlot by settingsViewModel.defaultSimSlot.collectAsState()
                    SettingsScreen(
                        defaultStartPage = defaultStartPage,
                        scrollbarPosition = scrollbarPosition,
                        showRecommendations = showRecommendations,
                        accentColor = accentColor,
                        defaultSimSlot = defaultSimSlot,
                        dualSimEnabled = dualSimEnabled,
                        sim1Name = sim1Name,
                        sim2Name = sim2Name,
                        onDefaultStartPageChange = { page -> settingsViewModel.setDefaultStartPage(page) },
                        onScrollbarPositionChange = { pos -> settingsViewModel.setScrollbarPosition(pos) },
                        onShowRecommendationsChange = { show -> settingsViewModel.setShowRecommendations(show) },
                        onAccentColorChange = { color -> settingsViewModel.setAccentColor(color) },
                        onSetDefaultDialerClick = { requestDefaultDialer() },
                        onDefaultSimSlotChange = { slot -> settingsViewModel.setDefaultSimSlot(slot) }
                    )
                }
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
            enter = slideInHorizontally(animationSpec = tween(NothingMotion.CallAnimations.acceptExplosionDurationMs, easing = NothingMotion.Easing.emphasizedDecelerate)) { it / 2 } + fadeIn(animationSpec = tween(NothingMotion.CallAnimations.acceptExplosionDurationMs)) + scaleIn(initialScale = 0.95f, animationSpec = tween(NothingMotion.CallAnimations.acceptExplosionDurationMs, easing = NothingMotion.Easing.emphasizedDecelerate)),
            exit = slideOutHorizontally(animationSpec = tween(NothingMotion.CallAnimations.declineImplodeDurationMs, easing = NothingMotion.Easing.emphasizedAccelerate)) { it } + fadeOut(animationSpec = tween(NothingMotion.CallAnimations.declineImplodeDurationMs)) + scaleOut(targetScale = 0.95f, animationSpec = tween(NothingMotion.CallAnimations.declineImplodeDurationMs, easing = NothingMotion.Easing.emphasizedAccelerate))
        ) {
            selectedContact?.let { contact ->
                val contactHistory = recentCalls.filter { call -> 
                    contact.phoneNumbers.any { it.number == call.number } 
                }
                ContactDetailScreen(
                    contact = contact,
                    history = contactHistory,
                    onBackClick = { selectedContact = null },
                    onCallClick = { number -> makeCall(number) },
                    onSmsClick = { number -> sendSms(number) },
                    onFavoriteToggle = {
                        // Bridge to system contacts — in-app favorite toggle is a roadmap item
                        com.evodart.glyphdial.utils.ContactIntents.openContact(context, contact)
                    },
                    onEditClick = {
                        com.evodart.glyphdial.utils.ContactIntents.editContact(context, contact)
                    },
                    onShareClick = {
                        com.evodart.glyphdial.utils.ContactIntents.shareContact(context, contact)
                    },
                    onDeleteClick = {
                        com.evodart.glyphdial.utils.ContactIntents.openContact(context, contact)
                    }
                )
            }
        }
        
        // Call Detail Overlay
        AnimatedVisibility(
            visible = selectedCall != null,
            enter = slideInHorizontally(animationSpec = tween(NothingMotion.CallAnimations.acceptExplosionDurationMs, easing = NothingMotion.Easing.emphasizedDecelerate)) { it / 2 } + fadeIn(animationSpec = tween(NothingMotion.CallAnimations.acceptExplosionDurationMs)) + scaleIn(initialScale = 0.95f, animationSpec = tween(NothingMotion.CallAnimations.acceptExplosionDurationMs, easing = NothingMotion.Easing.emphasizedDecelerate)),
            exit = slideOutHorizontally(animationSpec = tween(NothingMotion.CallAnimations.declineImplodeDurationMs, easing = NothingMotion.Easing.emphasizedAccelerate)) { it } + fadeOut(animationSpec = tween(NothingMotion.CallAnimations.declineImplodeDurationMs)) + scaleOut(targetScale = 0.95f, animationSpec = tween(NothingMotion.CallAnimations.declineImplodeDurationMs, easing = NothingMotion.Easing.emphasizedAccelerate))
        ) {
            selectedCall?.let { call ->
                val callHistory = recentCalls.filter { it.number == call.number }
                CallDetailScreen(
                    call = call,
                    history = callHistory,
                    onBackClick = { selectedCall = null },
                    onCallClick = { makeCall(call.number) },
                    onSmsClick = { sendSms(call.number) },
                    onAddToContactsClick = {
                        com.evodart.glyphdial.utils.ContactIntents.createContact(context, call.number)
                    },
                    onBlockClick = {
                        // Opens the system call-log detail for this number where the block option lives
                        val blockIntent = Intent(Intent.ACTION_VIEW).apply {
                            data = android.net.Uri.parse("tel:${call.number}")
                        }
                        if (blockIntent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(blockIntent)
                        }
                    }
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
