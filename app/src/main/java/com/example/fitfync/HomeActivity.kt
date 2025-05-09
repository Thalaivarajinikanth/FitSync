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
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.location.LocationServices

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitfync.room.MealLog
import com.example.fitfync.room.SleepLog
import com.example.fitfync.viewmodel.MealViewModel
import com.example.fitfync.viewmodel.SleepViewModel
import com.example.fitfync.viewmodel.WorkoutViewModel


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
                0 -> HomeScreen(onNavigate = { selectedIndex = it })
                1 -> WorkoutScreen()
                2 -> MealLoggerScreen()
                3 -> SleepTrackerScreen()
            }
        }
    }
}


// -------- Screens --------

@Composable
fun HomeScreen(onNavigate: (Int) -> Unit) {
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
                text = "Welcome, $userName",
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

            //  Feature Cards Now Navigate to Bottom Screens!
            FeatureCard(" Workout Tracker") { onNavigate(1) }
            FeatureCard(" Meal Logger") { onNavigate(2) }
            FeatureCard(" Sleep Tracker") { onNavigate(3) }

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
fun WorkoutScreen(
    viewModel: WorkoutViewModel = viewModel(),
    onBack: () -> Unit = {}  // You can pass this from the navigation handler
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var workoutType by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var caloriesBurned by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var locationText by remember { mutableStateOf<String>("Unknown") }

    val workouts by viewModel.workouts.collectAsState()

    // Permission launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        location?.let {
                            locationText = "${it.latitude}, ${it.longitude}"
                        } ?: run {
                            locationText = "Location not available"
                        }
                    }
                }
            } else {
                locationText = "Permission denied"
            }
        }
    )

    // Launch permission request
    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F4F4))
    ) {
        TopBarWithBack(title = "Workout Tracker", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = workoutType,
                onValueChange = { workoutType = it },
                label = { Text("Workout Type (e.g., Running)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it },
                label = { Text("Duration (mins)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = caloriesBurned,
                onValueChange = { caloriesBurned = it },
                label = { Text("Calories Burned") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (workoutType.isNotBlank() && duration.isNotBlank() && caloriesBurned.isNotBlank()) {
                        val workoutLog = WorkoutLog(
                            workoutType = workoutType,
                            duration = duration,
                            caloriesBurned = caloriesBurned,
                            location = locationText
                        )
                        viewModel.insertWorkout(workoutLog)
                        workoutType = ""
                        duration = ""
                        caloriesBurned = ""
                        message = "Workout Saved!"
                    } else {
                        message = "Please fill in all fields."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log Workout")
            }

            Spacer(modifier = Modifier.height(16.dp))

            message?.let {
                Text(it, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Previous Workouts", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            if (workouts.isEmpty()) {
                Text("No logs yet")
            } else {
                workouts.forEach { workout ->
                    Text(
                        text = "• ${workout.workoutType} - ${workout.duration} mins (${workout.caloriesBurned} kcal)",
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}






@Composable
fun MealLoggerScreen(
    viewModel: MealViewModel = viewModel(),
    onBack: () -> Unit = {}  // Pass in your back navigation lambda
) {
    var mealType by remember { mutableStateOf("") }
    var foodItem by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    val meals by viewModel.meals.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F4F4))
    ) {
        TopBarWithBack(title = "Meal Logger", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = mealType,
                onValueChange = { mealType = it },
                label = { Text("Meal Type (e.g., Breakfast)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = foodItem,
                onValueChange = { foodItem = it },
                label = { Text("Food Item") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = calories,
                onValueChange = { calories = it },
                label = { Text("Calories") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (mealType.isNotBlank() && foodItem.isNotBlank() && calories.isNotBlank()) {
                        val log = MealLog(
                            mealType = mealType,
                            foodItem = foodItem,
                            calories = calories
                        )
                        viewModel.insertMeal(log)

                        mealType = ""
                        foodItem = ""
                        calories = ""
                        message = "Meal Logged!"
                    } else {
                        message = "Please complete all fields."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log Meal")
            }

            Spacer(modifier = Modifier.height(16.dp))

            message?.let {
                Text(it, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("🍽️ Previous Meals", fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(8.dp))
            if (meals.isEmpty()) {
                Text("No meals logged yet.")
            } else {
                meals.forEach {
                    Text("• ${it.mealType}: ${it.foodItem} (${it.calories} kcal)", fontSize = 14.sp)
                }
            }
        }
    }
}




@Composable
fun SleepTrackerScreen(
    viewModel: SleepViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    var hours by remember { mutableStateOf("") }
    var quality by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    val sleepLogs by viewModel.sleeps.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F4F4))
    ) {
        TopBarWithBack(title = "Sleep Tracker", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = hours,
                onValueChange = { hours = it },
                label = { Text("Hours Slept") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = quality,
                onValueChange = { quality = it },
                label = { Text("Sleep Quality (e.g., Good)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (hours.isNotBlank() && quality.isNotBlank()) {
                        val log = SleepLog(
                            hours = hours,
                            quality = quality
                        )
                        viewModel.insertSleep(log)

                        hours = ""
                        quality = ""
                        message = "Sleep Logged!"
                    } else {
                        message = "Please complete all fields."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log Sleep")
            }

            Spacer(modifier = Modifier.height(16.dp))

            message?.let {
                Text(it, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("Previous Logs", fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(8.dp))
            if (sleepLogs.isEmpty()) {
                Text("No logs yet.")
            } else {
                sleepLogs.forEach {
                    Text("• ${it.hours} hrs, Quality: ${it.quality}", fontSize = 14.sp)
                }
            }
        }
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
fun FeatureCard(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithBack(title: String, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = { onBack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.Black
        )
    )
}

