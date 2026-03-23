package com.evodart.glyphdial.ui.screens.contacts

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import com.evodart.glyphdial.ui.components.animation.nothingClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.evodart.glyphdial.data.model.Contact
import com.evodart.glyphdial.data.settings.ScrollbarPosition
import com.evodart.glyphdial.ui.components.dialpad.formatPhoneNumber
import com.evodart.glyphdial.ui.components.scrollbar.AnimatedAlphabetScrollbar
import com.evodart.glyphdial.ui.theme.NothingColors
import com.evodart.glyphdial.ui.theme.NothingTextStyles
import kotlinx.coroutines.launch
import com.evodart.glyphdial.ui.components.search.NothingSearchBar

/**
 * Contacts screen with search and alphabet scrollbar
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactsScreen(
    contacts: List<Contact>,
    onContactClick: (Contact) -> Unit,
    onCallClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    scrollbarPosition: ScrollbarPosition = ScrollbarPosition.RIGHT
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    // Search state
    var searchQuery by remember { mutableStateOf("") }
    
    // Filter contacts based on search
    val filteredContacts = remember(contacts, searchQuery) {
        if (searchQuery.isBlank()) {
            contacts
        } else {
            contacts.filter { contact ->
                contact.name.contains(searchQuery, ignoreCase = true) ||
                contact.phoneNumbers.any { it.number.contains(searchQuery) }
            }
        }
    }
    
    // Current visible letter
    var currentLetter by remember { mutableStateOf<Char?>(null) }
    
    Column(modifier = modifier.fillMaxSize()) {
        // Search bar
        com.evodart.glyphdial.ui.components.search.NothingSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = "Search contacts",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = NothingColors.NothingRed
                )
            } else if (filteredContacts.isEmpty()) {
                Text(
                    text = if (searchQuery.isNotBlank()) "No results for \"$searchQuery\"" 
                           else "No contacts found",
                    style = MaterialTheme.typography.bodyLarge,
                    color = NothingColors.SilverGray,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                val groupedContacts = filteredContacts.groupBy { 
                    it.name.firstOrNull()?.uppercaseChar() ?: '#' 
                }.toSortedMap()
                
                val letters = remember(groupedContacts) {
                    groupedContacts.keys.toList()
                }
                
                val letterIndexMap = remember(groupedContacts) {
                    var index = 0
                    groupedContacts.mapValues { (_, list) ->
                        val currentIndex = index
                        index += list.size + 1
                        currentIndex
                    }
                }
                
                LaunchedEffect(listState.firstVisibleItemIndex) {
                    var count = 0
                    for ((letter, list) in groupedContacts) {
                        if (listState.firstVisibleItemIndex <= count) {
                            currentLetter = letter
                            break
                        }
                        count += list.size + 1
                    }
                }
                
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                start = if (scrollbarPosition == ScrollbarPosition.LEFT) 24.dp else 0.dp,
                                end = if (scrollbarPosition == ScrollbarPosition.RIGHT) 24.dp else 0.dp
                            ),
                        state = listState,
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        groupedContacts.forEach { (letter, contactsInGroup) ->
                            stickyHeader(key = "header_$letter") {
                                SectionHeader(letter = letter.toString())
                            }
                            
                            items(
                                items = contactsInGroup,
                                key = { it.id }
                            ) { contact ->
                                ContactListItem(
                                    contact = contact,
                                    onClick = { onContactClick(contact) },
                                    onCallClick = { 
                                        contact.primaryNumber?.let { onCallClick(it) }
                                    }
                                )
                            }
                        }
                    }
                    
                    // Scrollbar - only show when not searching
                    if (searchQuery.isBlank()) {
                        AnimatedAlphabetScrollbar(
                            letters = letters,
                            currentLetter = currentLetter,
                            onLetterSelected = { letter ->
                                letterIndexMap[letter]?.let { index ->
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(index)
                                    }
                                }
                            },
                            position = scrollbarPosition,
                            modifier = Modifier
                                .align(
                                    if (scrollbarPosition == ScrollbarPosition.LEFT) 
                                        Alignment.CenterStart else Alignment.CenterEnd
                                )
                                .padding(
                                    start = if (scrollbarPosition == ScrollbarPosition.LEFT) 4.dp else 0.dp,
                                    end = if (scrollbarPosition == ScrollbarPosition.RIGHT) 4.dp else 0.dp
                                )
                        )
                    }
                }
            }
        }
    }
}



@Composable
private fun SectionHeader(letter: String) {
    Text(
        text = letter,
        style = NothingTextStyles.sectionHeader.copy(
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        ),
        color = NothingColors.NothingRed,
        modifier = Modifier
            .fillMaxWidth()
            .background(NothingColors.PureBlack)
            .padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

@Composable
private fun ContactListItem(
    contact: Contact,
    onClick: () -> Unit,
    onCallClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .nothingClickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(NothingColors.SurfaceCard),
            contentAlignment = Alignment.Center
        ) {
            if (contact.photoUri != null) {
                AsyncImage(
                    model = contact.photoUri,
                    contentDescription = contact.displayName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = contact.initials,
                    style = MaterialTheme.typography.titleMedium,
                    color = NothingColors.PureWhite,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = contact.displayName,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp),
                    color = NothingColors.PureWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                
                if (contact.starred) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Favorite",
                        tint = NothingColors.Warning,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            contact.primaryNumber?.let { number ->
                Spacer(modifier = Modifier.height(4.dp))
                
                val phoneType = contact.phoneNumbers.firstOrNull()?.type?.name?.lowercase()
                    ?.replaceFirstChar { it.uppercase() } ?: "Mobile"
                
                Text(
                    text = "${formatPhoneNumber(number)} · $phoneType",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    color = NothingColors.SilverGray,
                    maxLines = 1
                )
            }
        }
        
        if (contact.primaryNumber != null) {
            IconButton(
                onClick = onCallClick,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Call,
                    contentDescription = "Call",
                    tint = NothingColors.PureWhite,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
    
    HorizontalDivider(
        modifier = Modifier.padding(start = 84.dp),
        thickness = 0.5.dp,
        color = NothingColors.DarkGray.copy(alpha = 0.3f)
    )
}
