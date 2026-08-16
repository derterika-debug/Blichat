package com.blichat.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF050507)
private val Card = Color(0xFF111116)
private val Burgundy = Color(0xFF7A1738)
private val Burgundy2 = Color(0xFFB22A55)
private val TextMain = Color(0xFFF4F1F2)
private val TextMuted = Color(0xFF9B969B)

data class Chat(val name: String, val message: String, val time: String, val online: Boolean)

private val chats = listOf(
    Chat("Алекс", "Привет! Как тебе Blichat?", "22:41", true),
    Chat("Марина", "Созвонимся вечером?", "21:18", false),
    Chat("Даша", "Фото отправила 📸", "20:04", true),
    Chat("Иван", "До завтра!", "Вчера", false)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { BlichatApp() }
    }
}

@Composable
fun BlichatApp() {
    var screen by remember { mutableStateOf("chats") }
    var selectedChat by remember { mutableStateOf<Chat?>(null) }

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Bg,
            surface = Card,
            primary = Burgundy2,
            onBackground = TextMain,
            onSurface = TextMain
        )
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = Bg) {
            if (selectedChat != null) {
                ChatScreen(selectedChat!!, onBack = { selectedChat = null })
            } else {
                when (screen) {
                    "profile" -> ProfileScreen()
                    "calls" -> CallsScreen()
                    else -> HomeScreen(
                        onChat = { selectedChat = it },
                        onProfile = { screen = "profile" },
                        onCalls = { screen = "calls" }
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreen(onChat: (Chat) -> Unit, onProfile: () -> Unit, onCalls: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp, 22.dp, 20.dp, 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Blichat", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Text("Общение без лишнего шума", color = TextMuted, fontSize = 13.sp)
            }
            IconButton(onClick = onProfile) {
                Icon(Icons.Default.AccountCircle, "Профиль", tint = TextMain, modifier = Modifier.size(32.dp))
            }
        }

        Row(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
            FilterChip(
                selected = true,
                onClick = {},
                label = { Text("Все") }
            )
            Spacer(Modifier.width(8.dp))
            FilterChip(
                selected = false,
                onClick = onCalls,
                label = { Text("Звонки") }
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(12.dp, 8.dp, 12.dp, 100.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chats) { chat ->
                ChatRow(chat, onClick = { onChat(chat) })
            }
        }

        Box(Modifier.fillMaxSize()) {
            FloatingActionButton(
                onClick = { },
                containerColor = Burgundy,
                contentColor = Color.White,
                modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp)
            ) {
                Icon(Icons.Default.Edit, "Новое сообщение")
            }
        }
    }
}

@Composable
fun ChatRow(chat: Chat, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Card)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            Box(
                Modifier.size(54.dp).clip(CircleShape).background(Burgundy),
                contentAlignment = Alignment.Center
            ) {
                Text(chat.name.take(1), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            if (chat.online) {
                Box(
                    Modifier.size(13.dp).clip(CircleShape).background(Color(0xFF52C878))
                        .align(Alignment.BottomEnd)
                )
            }
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(chat.name, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
            Spacer(Modifier.height(3.dp))
            Text(chat.message, color = TextMuted, maxLines = 1, fontSize = 14.sp)
        }
        Text(chat.time, color = TextMuted, fontSize = 12.sp)
    }
}

@Composable
fun ChatScreen(chat: Chat, onBack: () -> Unit) {
    var message by remember { mutableStateOf("") }
    var sent by remember { mutableStateOf(listOf<String>()) }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(10.dp, 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Назад")
            }
            Box(
                Modifier.size(42.dp).clip(CircleShape).background(Burgundy),
                contentAlignment = Alignment.Center
            ) { Text(chat.name.take(1), fontWeight = FontWeight.Bold) }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(chat.name, fontWeight = FontWeight.Bold)
                Text(if (chat.online) "в сети" else "был(а) недавно", color = TextMuted, fontSize = 12.sp)
            }
            IconButton(onClick = {}) { Icon(Icons.Default.Phone, "Позвонить") }
            IconButton(onClick = {}) { Icon(Icons.Default.Videocam, "Видеозвонок") }
        }

        HorizontalDivider(color = Color(0xFF202026))

        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                MessageBubble("Привет! Это Blichat 👋", false)
                MessageBubble("Новый мессенджер. Пока это тестовый экран.", false)
            }
            items(sent) { text -> MessageBubble(text, true) }
        }

        Row(
            Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Сообщение…") },
                shape = RoundedCornerShape(22.dp),
                singleLine = true
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (message.isNotBlank()) {
                        sent = sent + message.trim()
                        message = ""
                    }
                },
                modifier = Modifier.size(50.dp).clip(CircleShape).background(Burgundy)
            ) {
                Icon(Icons.Default.Send, "Отправить", tint = Color.White)
            }
        }
    }
}

@Composable
fun MessageBubble(text: String, mine: Boolean) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (mine) Burgundy else Card,
            shape = RoundedCornerShape(18.dp)
        ) {
            Text(text, Modifier.padding(13.dp, 9.dp), fontSize = 15.sp)
        }
    }
}

@Composable
fun CallsScreen() {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Звонки", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))
        Text("История звонков появится здесь.", color = TextMuted)
        Spacer(Modifier.height(20.dp))
        Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Burgundy)) {
            Icon(Icons.Default.Phone, null)
            Spacer(Modifier.width(8.dp))
            Text("Начать звонок")
        }
    }
}

@Composable
fun ProfileScreen() {
    Column(
        Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(30.dp))
        Box(
            Modifier.size(100.dp).clip(CircleShape).background(Burgundy),
            contentAlignment = Alignment.Center
        ) {
            Text("B", fontSize = 42.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(14.dp))
        Text("Мой профиль", fontSize = 25.sp, fontWeight = FontWeight.Bold)
        Text("Настройки аккаунта Blichat", color = TextMuted)
        Spacer(Modifier.height(30.dp))
        ProfileItem("🔔", "Уведомления")
        ProfileItem("🔒", "Конфиденциальность")
        ProfileItem("🎨", "Оформление")
        ProfileItem("ℹ️", "О Blichat")
    }
}

@Composable
fun ProfileItem(icon: String, title: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 7.dp)
            .clip(RoundedCornerShape(16.dp)).background(Card).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, fontSize = 20.sp)
        Spacer(Modifier.width(14.dp))
        Text(title, fontSize = 16.sp)
    }
}
