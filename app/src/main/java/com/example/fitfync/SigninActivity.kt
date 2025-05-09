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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitfync.ui.theme.FitFyncTheme
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.text.font.FontWeight

class SigninActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitFyncTheme {
                SignInScreen()
            }
        }
    }
}

@Composable
fun SignInScreen() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var firebaseError by remember { mutableStateOf<String?>(null) }
    var showGdprDialog by remember { mutableStateOf(true) }
    var consentGiven by remember { mutableStateOf(false) }

    // GDPR Dialog
    if (showGdprDialog && !consentGiven) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Privacy & GDPR Consent", fontWeight = FontWeight.Bold) },
            text = {
                Text("We use your data to personalize your experience and keep your account secure. By proceeding, you agree to our Privacy Policy and Terms of Service.")
            },
            confirmButton = {
                Button(onClick = {
                    consentGiven = true
                    showGdprDialog = false
                }) {
                    Text("I Agree")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    Toast.makeText(context, "Consent required to use the app", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Decline")
                }
            }
        )
    }

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
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.fitsync_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null
                    firebaseError = null
                },
                label = { Text("Email", color = Color.Black) },
                isError = emailError != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (emailError != null) {
                Text(emailError ?: "", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                    firebaseError = null
                },
                label = { Text("Password", color = Color.Black) },
                isError = passwordError != null,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (passwordError != null) {
                Text(passwordError ?: "", color = Color.Red, fontSize = 12.sp)
            }

            if (firebaseError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(firebaseError ?: "", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    var valid = true

                    if (email.isBlank()) {
                        emailError = "Email cannot be empty"
                        valid = false
                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailError = "Enter a valid email"
                        valid = false
                    }

                    if (password.isBlank()) {
                        passwordError = "Password cannot be empty"
                        valid = false
                    } else if (password.length < 6) {
                        passwordError = "Password must be at least 6 characters"
                        valid = false
                    }

                    if (valid && consentGiven) {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                                    context.startActivity(Intent(context, HomeActivity::class.java))
                                } else {
                                    firebaseError = task.exception?.message ?: "Login failed"
                                }
                            }
                    } else if (!consentGiven) {
                        Toast.makeText(context, "You must accept GDPR policy to continue", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text("Sign In")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Text("Don't have an account?", color = Color.Black)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Sign Up",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        context.startActivity(Intent(context, SignupActivity::class.java))
                    }
                )
            }
        }
    }
}
