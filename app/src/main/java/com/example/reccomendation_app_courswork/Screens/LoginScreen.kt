package com.example.reccomendation_app_courswork.Screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel
) {
    val uiState by loginViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var timer = 0

    LaunchedEffect(uiState.connectionInternet) {
        if (!uiState.connectionInternet) {
            for (i in 5 downTo 0) {
                delay(1000)
                timer = i
            }
            loginViewModel.updateConnectionState(true)
            loginViewModel.updateContinueButtonState(true)
        }
    }

    if (!uiState.isLoadingContext) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = if (uiState.signInOrUp) "Авторизация" else "Регистрация",
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 20.dp),
                fontSize = 38.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))
            TextField(
                value = uiState.email,
                onValueChange = {
                    loginViewModel.onEmailChanged(it)
                },
                label = { Text("Email", color = Color.Gray) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp)
                    .border(
                        1.dp,
                        if (uiState.emailError) colorResource(R.color.redLowOpacity) else Color.Gray,
                        RoundedCornerShape(15.dp)
                    ),
                placeholder = { Text(text = "example@gmail.com", color = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor = colorResource(id = R.color.authorizationMark),
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White,
                    focusedTextColor = if (uiState.emailError) colorResource(id = R.color.redLowOpacity) else Color.Black,
                    unfocusedTextColor = if (uiState.emailError) colorResource(id = R.color.redLowOpacity) else Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = uiState.password,
                onValueChange = {
                    loginViewModel.onPasswordChanged(it)
                },
                label = { Text("Password", color = Color.Gray) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp)
                    .border(
                        1.dp,
                        if (uiState.passwordError) colorResource(R.color.redLowOpacity) else Color.Gray,
                        RoundedCornerShape(15.dp)
                    ),
                placeholder = { Text(text = "********", color = Color.Gray) },
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    cursorColor = colorResource(id = R.color.authorizationMark),
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White,
                    focusedTextColor = if (uiState.passwordError) colorResource(id = R.color.redLowOpacity) else Color.Black,
                    unfocusedTextColor = if (uiState.passwordError) colorResource(id = R.color.redLowOpacity) else Color.Black
                )
            )

            Text(
                text = if (uiState.signInOrUp) "Забыли пароль?" else if (uiState.failedSignUp == true) "Несуществующий email-адрес" else if (uiState.failedSignUp == false) "На вашу почту отправлено письмо с подтверждением" else "Уже есть аккаунт?",
                modifier = Modifier
                    .wrapContentHeight()
                    .wrapContentWidth()
                    .padding(12.dp)
                    .clickable {
                        if (uiState.signInOrUp) loginViewModel.showPopup() else loginViewModel.toggleSignInOrUp(
                            true
                        )
                    },
                textAlign = TextAlign.Center,
                color = if (uiState.failedSignUp == false && !uiState.signInOrUp) Color.Gray else colorResource(id = R.color.authorizationMark),
                style = TextStyle(
                    fontSize = 16.sp,
                    textDecoration = if (uiState.failedSignUp == false && !uiState.signInOrUp) null else TextDecoration.Underline
                )
            )

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { loginViewModel.toggleSignInOrUp(true) },
                    modifier = Modifier
                        .width(175.dp)
                        .height(55.dp)
                        .padding(start = 12.dp, end = 12.dp)
                        .border(
                            1.dp,
                            if (uiState.signInOrUp) colorResource(id = R.color.authorizationMark) else Color.LightGray,
                            RoundedCornerShape(15.dp)
                        ),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.signInOrUp) colorResource(id = R.color.authorizationMark) else Color.White,
                        contentColor = if (uiState.signInOrUp) Color.White else Color.LightGray
                    )
                ) {
                    Text("Вход")
                }
                Button(
                    onClick = { loginViewModel.toggleSignInOrUp(false) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .padding(start = 12.dp, end = 12.dp)
                        .border(
                            1.dp,
                            if (uiState.signInOrUp) Color.LightGray else colorResource(id = R.color.authorizationMark),
                            RoundedCornerShape(15.dp)
                        ),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.signInOrUp) Color.White else colorResource(id = R.color.authorizationMark),
                        contentColor = if (uiState.signInOrUp) Color.LightGray else Color.White
                    )
                ) {
                    Text("Регистрация")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    loginViewModel.onContinueButtonClick(context, navController)
                },
                enabled = uiState.enabledContinueButton,
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
                    text = if (uiState.enabledContinueButton) "Продолжить" else "Нет подключения к сети.\nПовторное подключение через $timer секунд.",
                    color = colorResource(id = R.color.authorizationMark),
                    textAlign = TextAlign.Center
                )
            }

            if (uiState.showPopup) {
                Popup(
                    alignment = Alignment.Center,
                    onDismissRequest = { loginViewModel.dismissPopup() }
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
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Загрузка...",
                modifier = Modifier
                    .wrapContentHeight()
                    .wrapContentWidth(),
                textAlign = TextAlign.Center,
                color = Color.Gray,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = colorResource(id = R.color.authorizationMark)
            )
        }
    }
}
