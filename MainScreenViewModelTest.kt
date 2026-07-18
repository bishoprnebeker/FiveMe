package com.example.fiveme.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fiveme.*
import com.example.fiveme.data.HighFive
import com.example.fiveme.ui.FiveMeViewModel
import java.text.SimpleDateFormat
import java.util.*

// Styling Tokens for Neon Dark Theme
val DeepDarkBlue = Color(0xFF0F172A)
val CardSlate = Color(0xFF1E293B)
val NeonOrange = Color(0xFFF97316)
val NeonCyan = Color(0xFF06B6D4)
val NeonGold = Color(0xFFF59E0B)
val BorderColor = Color(0xFF334155)
val TextGray = Color(0xFF94A3B8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    viewModel: FiveMeViewModel,
    onNavigate: (Any) -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val confirmedFeed by viewModel.confirmedHighFives.collectAsState()
    val pendingHighFives by viewModel.pendingHighFives.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "FiveMe",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color.White
                    )
                },
                actions = {
                    // Sandbox Shortcut
                    IconButton(
                        onClick = { onNavigate(Sandbox) },
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = "Sandbox",
                            tint = NeonCyan
                        )
                    }

                    // Profile Shortcut
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clickable { onNavigate(Profile) }
                            .size(36.dp)
                            .background(CardSlate, CircleShape)
                            .border(1.5.dp, NeonOrange, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser?.avatarEmoji ?: "👤",
                            fontSize = 18.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepDarkBlue,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onNavigate(GiveFive()) },
                containerColor = NeonOrange,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Give")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Give High-Five", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = DeepDarkBlue
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Profile & Notifications Status Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .background(
                        brush = Brush.horizontalGradient(listOf(CardSlate, DeepDarkBlue)),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Hello, ${currentUser?.username ?: "Guest"}!",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Scan the vicinity to start high-fiving!",
                        color = TextGray,
                        fontSize = 12.sp
                    )
                }

                // Notification Center Button
                BadgedBox(
                    badge = {
                        if (pendingHighFives.isNotEmpty()) {
                            Badge(
                                containerColor = NeonOrange,
                                contentColor = Color.White
                            ) {
                                Text(pendingHighFives.size.toString())
                            }
                        }
                    },
                    modifier = Modifier.clickable { onNavigate(Notifications) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Pending HighFives",
                        tint = if (pendingHighFives.isNotEmpty()) NeonGold else Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Feed Section
            Text(
                text = "Recent High-Fives",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (confirmedFeed.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BackHand,
                            contentDescription = null,
                            tint = BorderColor,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No High-Fives yet!",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Find someone nearby, give them a high-five, and request confirmation to see it here!",
                            color = TextGray,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(confirmedFeed) { highFive ->
                        HighFiveFeedCard(
                            highFive = highFive,
                            currentUserId = currentUser?.id ?: -1,
                            onViewChain = { onNavigate(ChainView(highFive.id)) },
                            onPassItOn = { onNavigate(GiveFive(parentHighFiveId = highFive.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HighFiveFeedCard(
    highFive: HighFive,
    currentUserId: Long,
    onViewChain: () -> Unit,
    onPassItOn: () -> Unit
) {
    val sdf = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
    val dateString = sdf.format(Date(highFive.timestamp))
    
    // Check if the current user is the receiver of this high-five
    val isRecipientOfThis = highFive.receiverId == currentUserId

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = CardSlate),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Giver ✋ Receiver
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.BackHand,
                    contentDescription = null,
                    tint = NeonOrange,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = highFive.senderName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "high-fived",
                    color = TextGray,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = highFive.receiverName,
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = dateString,
                    color = TextGray,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Body: High-five Comment
            Text(
                text = "\"${highFive.comment}\"",
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DeepDarkBlue, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            )

            // Optional Image Attachment (Mock Visual)
            if (!highFive.imagePath.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.DarkGray)
                        .border(1.dp, BorderColor, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Attached Image",
                        tint = TextGray,
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = "📷 Photo Commemoration Attached",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(6.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer / Actions: Ancestry chain tag + View Chain + Pass It On
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Chain Badge indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onViewChain() }
                        .background(
                            if (highFive.parentHighFiveId != null) NeonGold.copy(alpha = 0.15f)
                            else Color.Transparent,
                            RoundedCornerShape(20.dp)
                        )
                        .border(
                            1.dp,
                            if (highFive.parentHighFiveId != null) NeonGold.copy(alpha = 0.5f)
                            else Color.Transparent,
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = if (highFive.parentHighFiveId != null) NeonGold else TextGray,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (highFive.parentHighFiveId != null) "Inspiration Chain" else "View Chain",
                        color = if (highFive.parentHighFiveId != null) NeonGold else TextGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // If user was recipient, allow passing the positive energy!
                if (isRecipientOfThis) {
                    Button(
                        onClick = onPassItOn,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonCyan.copy(alpha = 0.2f),
                            contentColor = NeonCyan
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(28.dp)
                            .border(1.dp, NeonCyan, RoundedCornerShape(12.dp)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Pass it on!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
