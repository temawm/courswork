package com.example.reccomendation_app_courswork.Screens

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavController
import com.example.reccomendation_app_courswork.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val firestore = Firebase.firestore
    val scope = rememberCoroutineScope()
    var isLoadingContext by remember { mutableStateOf(false) }
    val auth = Firebase.auth
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var showPopup by remember { mutableStateOf(false) }
    var signInorUp by remember { mutableStateOf(true) }
    var connectionInternet by remember { mutableStateOf(true) }
    var enabledContinueButton by remember { mutableStateOf(true) }
    var timer by remember { mutableIntStateOf(0) }
    var failedSignUp by remember { mutableStateOf<Boolean?>(null) }
    var failedSignIn by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(failedSignIn) {
        if (failedSignIn == true) {
            delay(4000)
            failedSignIn = null
        }
    }
    LaunchedEffect(connectionInternet) {
        if (!connectionInternet) {
            for (i in 1 downTo 0) {
                timer = i
                delay(1000)
            }
            enabledContinueButton = connectionInternet
        }
        if (timer == 0) {
            connectionInternet = true
            enabledContinueButton = true
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = if (signInorUp) "Авторизация" else "Регистрация",
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .padding(top = 20.dp),
            fontSize = 38.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(32.dp))
        TextField(
            value = email,
            onValueChange = {
                email = it
                emailError = validateEmail(email)
            },
            label = { Text("Email", color = Color.Gray) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp)
                .border(
                    1.dp,
                    if (emailError) colorResource(R.color.authorizationMark) else Color.Gray,
                    RoundedCornerShape(15.dp)
                ),
            placeholder = { Text(text = "example@gmail.com", color = Color.Gray) },
            colors = TextFieldDefaults.textFieldColors
                (
                containerColor = Color.White,
                cursorColor = colorResource(id = R.color.authorizationMark),
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.White,

                focusedTextColor = if (!emailError) colorResource(id = R.color.redLowOpacity) else Color.Black,
                unfocusedTextColor = if (!emailError) colorResource(id = R.color.redLowOpacity) else Color.Black
            )

        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = validatePassword(password)
            },
            label = { Text("Password", color = Color.Gray) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp)
                .border(
                    1.dp,
                    if (passwordError) colorResource(R.color.authorizationMark) else Color.Gray,
                    RoundedCornerShape(15.dp)
                ),
            placeholder = { Text(text = "********", color = Color.Gray) },
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.textFieldColors
                (
                containerColor = Color.White,
                cursorColor = colorResource(id = R.color.authorizationMark),
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.White,

                focusedTextColor = if (!passwordError) colorResource(id = R.color.redLowOpacity) else Color.Black,
                unfocusedTextColor = if (!passwordError) colorResource(id = R.color.redLowOpacity) else Color.Black
            )

        )
        Text(
            text = when {
                signInorUp && failedSignIn == null-> "Забыли пароль?"
                failedSignUp == true && !signInorUp -> "Email адрес уже используется"
                failedSignUp == false && !signInorUp -> "На вашу почту отправлено письмо с подтверждением"
                failedSignIn == true && signInorUp -> "Неверный логин или пароль"
                else -> "Уже есть аккаунт?"
            },
            modifier = Modifier
                .height(80.dp)
                .wrapContentWidth()
                .padding(12.dp)
                .clickable {
                    if (signInorUp) showPopup = true else signInorUp = true
                },
            textAlign = TextAlign.Center,
            color = when {
                failedSignUp == false && !signInorUp -> Color.Gray
                (failedSignIn == true && signInorUp) || (failedSignUp == true && !signInorUp)-> colorResource(id = R.color.redLowOpacity)
                else -> colorResource(id = R.color.authorizationMark)
            },
            style = TextStyle(
                fontSize = 16.sp,
                textDecoration = when {
                    (failedSignUp == false && !signInorUp) || (failedSignUp == true && !signInorUp) -> null
                    else -> TextDecoration.Underline
                },
            )


        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        )
        {
            Button(
                onClick = {
                    signInorUp = true
                    failedSignUp = null
                },
                modifier = Modifier
                    .width(175.dp)
                    .height(55.dp)
                    .padding(start = 12.dp, end = 12.dp)
                    .border(
                        1.dp,
                        if (signInorUp) colorResource(id = R.color.authorizationMark) else Color.LightGray,
                        RoundedCornerShape(15.dp)
                    ),
                shape = RoundedCornerShape(15.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = if (signInorUp) colorResource(id = R.color.authorizationMark) else Color.White,
                    contentColor = if (signInorUp) Color.White else Color.LightGray
                )
            ) {
                Text("Вход")
            }
            Button(
                onClick = {
                    signInorUp = false
                    failedSignUp = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .padding(start = 12.dp, end = 12.dp)
                    .border(
                        1.dp,
                        if (signInorUp) Color.LightGray else colorResource(id = R.color.authorizationMark),
                        RoundedCornerShape(15.dp)
                    ),
                shape = RoundedCornerShape(15.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = if (signInorUp) Color.White else colorResource(id = R.color.authorizationMark),
                    contentColor = if (signInorUp) Color.LightGray else Color.White
                )
            ) {
                Text("Регистрация")
            }

        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = {
                if (emailError && passwordError) {
                    connectionInternet = isNetworkAvailable(context)
                    if (connectionInternet) {
                        enabledContinueButton = true
                        if (!signInorUp) {
                            scope.launch {
                                signUpAndVerifyEmail(auth, email, password) { success ->
                                    if (success) {
                                        failedSignUp = false
                                        createDatabase(firestore, email)
                                        Log.d("CreateDatabase", "Succesful")
                                    }
                                    else {
                                        failedSignUp = true
                                    }

                                }
                            }

                        } else {
                            signIn(auth, email, password, navController) { success ->
                                if (!success) {
                                    failedSignIn = true
                                    Log.d("FailedSignIn", "SignIn: $failedSignIn")
                                } else {
                                    failedSignIn = false
                                    Log.d("FailedSignIn", "SignIn: $failedSignIn")
                                }
                            }


                        }
                    } else {
                        enabledContinueButton = false
                    }
                }
            },
            enabled = enabledContinueButton,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .padding(start = 12.dp, end = 12.dp)
                .border(
                    1.dp,
                    colorResource(id = R.color.authorizationMark),
                    RoundedCornerShape(15.dp)
                ),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = colorResource(id = R.color.authorizationMark)
            )
        ) {
            Text(
                text = if (enabledContinueButton) "Продолжить" else "Нет подключения к сети.\nПовторное подключение через $timer секунд.",
                color = if (enabledContinueButton) colorResource(id = R.color.authorizationMark) else Color.Black,
                textAlign = TextAlign.Center
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Privacy policy",
                fontSize = 16.sp,
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 185.dp)
                    .clickable {
                        showPopup = true
                    },
                style = TextStyle(
                    textDecoration = TextDecoration.Underline,
                ),
                color = Color.LightGray
            )
        }

        if (showPopup) {
            Popup(
                alignment = Alignment.Center,
                onDismissRequest = { showPopup = false }
            ) {
                Box(
                    modifier = Modifier
                        .width(300.dp)
                        .height(150.dp)
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                        .border(3.dp, Color.Black, RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column {
                        Text(text = "read readme.txt to continue", fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        BasicText(
                            text = "Visit my GitHub",
                            modifier = Modifier
                                .clickable {
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse("https://github.com/temawm")
                                    }
                                    context.startActivity(intent)
                                }
                                .padding(8.dp),
                            style = TextStyle(
                                color = Color.Blue,
                                fontSize = 16.sp,
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    }
                }
            }
        }
    }
}

private fun validateEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

private fun validatePassword(password: String): Boolean {
    return password.length >= 8
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}

fun createDatabase(firestore: FirebaseFirestore, email: String): Boolean {
    return try {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val userRef = firestore.collection("Users").document(userId!!)

        userRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                val userProfile = hashMapOf(
                    "name" to "",
                    "birthDate" to "",
                    "email" to email,
                    "profileImageUrl" to ""
                )
                userRef.set(userProfile)
            }
        }
        true
    } catch (e: Exception) {
        Log.d("CreateDatabase", "$e")
        false
    }
}

suspend fun signUpAndVerifyEmail(auth: FirebaseAuth, email: String, password: String, callback: (Boolean) -> Unit) {
    try {
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            auth.currentUser?.sendEmailVerification()?.addOnSuccessListener {
                callback(true)
            }
        }.await()
    } catch (e: Exception) {
        Log.d("MyAuthLog", "SignUp failed: ${e.message}")
        callback(false)
    }
}

private fun signIn(
    auth: FirebaseAuth,
    email: String,
    password: String,
    navController: NavController,
    callback: (Boolean) -> Unit
) {
    try {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        Log.d("MyAuthLog", "SignIn is successful and email is verified!")
                        navController.navigate("HomeScreen") {
                            popUpTo("LoginScreen") {
                                inclusive = true
                            }
                            callback(true)
                        }
                    } else {
                        Log.d("MyAuthLog", "SignIn is failure!")
                        callback(false)
                    }
                } else {
                    callback(false)
                }

            }

    } catch (e: Exception) {
        Log.d("MyAuthLog", "Exception: $e")

    }
}
