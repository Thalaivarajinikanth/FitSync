package com.example.fitfync

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitfync.ui.theme.FitFyncTheme
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            startActivity(Intent(this, SigninActivity::class.java))
            finish()
        }

        setContent {
            FitFyncTheme {
                FitnessAppHome()
            }
        }
    }
}

@Composable
fun FitnessAppHome() {
    var selectedIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                BottomNavItem.entries.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedIndex) {
                0 -> HomeScreen()
                1 -> WorkoutScreen()
                2 -> MealLoggerScreen()
                3 -> SleepTrackerScreen()
            }
        }
    }
}

// -------- Screens --------

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val userName = auth.currentUser?.email ?: "User"

    var steps by remember { mutableIntStateOf(0) }
    val calories = remember(steps) { steps * 0.04 }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x80FFFFFF))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome, $userName 👋",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Today's Stats", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatCard("Steps", steps.toString())
                StatCard("Calories", String.format("%.0f", calories))
                StatCard("Workouts", "2")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { if (steps >= 100) steps -= 100 },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("-100")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text("Adjust Steps", fontWeight = FontWeight.Medium, fontSize = 16.sp)

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = { steps += 100 },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("+100", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            FeatureCard("🏋️ Workout Tracker")
            FeatureCard("🥗 Meal Logger")
            FeatureCard("🛌 Sleep Tracker")

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    auth.signOut()
                    Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                    context.startActivity(Intent(context, SigninActivity::class.java))
                },
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text("Logout")
            }
        }
    }
}

@Composable
fun WorkoutScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F4F4)),
        contentAlignment = Alignment.Center
    ) {
        Text("🏋️ Workout Tracker (Coming Soon)", fontSize = 18.sp)
    }
}

@Composable
fun MealLoggerScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F4F4)),
        contentAlignment = Alignment.Center
    ) {
        Text("🥗 Meal Logger (Coming Soon)", fontSize = 18.sp)
    }
}

@Composable
fun SleepTrackerScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F4F4)),
        contentAlignment = Alignment.Center
    ) {
        Text("🛌 Sleep Tracker (Coming Soon)", fontSize = 18.sp)
    }
}

// -------- Components --------

@Composable
fun StatCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 14.sp, color = Color.Gray)
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

@Composable
fun FeatureCard(title: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = title, fontSize = 16.sp, color = Color.Black)
        }
    }
}

// -------- Bottom Nav Items --------

enum class BottomNavItem(val title: String, val icon: ImageVector) {
    Home("Home", Icons.Default.Home),
    Workout("Workout", Icons.Default.FitnessCenter),
    Meal("Meal", Icons.Default.Restaurant),
    Sleep("Sleep", Icons.Default.Bedtime)
}
