package com.evodart.glyphdial.ui.screens.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.evodart.glyphdial.data.model.Contact
import com.evodart.glyphdial.ui.theme.NothingColors

/**
 * Favorites screen - 2 column grid with profile pictures
 */
@Composable
fun FavoritesScreen(
    contacts: List<Contact>,
    onContactClick: (Contact) -> Unit,
    onCallClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = NothingColors.NothingRed
            )
        } else if (contacts.isEmpty()) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No favorites yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = NothingColors.SilverGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Star contacts to add them here",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NothingColors.Gray
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = contacts,
                    key = { it.id }
                ) { contact ->
                    FavoriteGridItem(
                        contact = contact,
                        onClick = { onContactClick(contact) },
                        onCallClick = { contact.primaryNumber?.let { onCallClick(it) } }
                    )
                }
            }
        }
    }
}

/**
 * Grid item - circle or square with profile picture, no bg on call icon
 */
@Composable
private fun FavoriteGridItem(
    contact: Contact,
    onClick: () -> Unit,
    onCallClick: () -> Unit
) {
    val isCircle = remember(contact.id) { contact.id % 3 == 0L }
    
    Column(
        modifier = Modifier
            .aspectRatio(0.85f)
            .clip(if (isCircle) CircleShape else RoundedCornerShape(20.dp))
            .background(NothingColors.SurfaceCard)
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Profile picture or initials
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(NothingColors.CharcoalBlack),
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
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 28.sp),
                    color = NothingColors.NothingRed,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Name
        Text(
            text = contact.displayName,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
            color = NothingColors.PureWhite,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Call button - NO background
        Icon(
            imageVector = Icons.Filled.Call,
            contentDescription = "Call",
            tint = NothingColors.PureWhite,
            modifier = Modifier
                .size(22.dp)
                .clickable(onClick = onCallClick)
        )
    }
}
