package com.personal.financeapp.presentation.screens.connect

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectBankScreen(
    navController: NavController,
    itemId: String? = null,
    viewModel: ConnectBankViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(itemId) {
        if (itemId != null) {
            viewModel.onConnectionSuccess(itemId)
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is ConnectBankUiState.ReadyToConnect) {
            val url = (uiState as ConnectBankUiState.ReadyToConnect).url
            val intent = CustomTabsIntent.Builder().build()
            intent.launchUrl(context, Uri.parse(url))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Conectar Banco") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Conecte sua conta bancária via Open Finance para importar transações automaticamente.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            when (uiState) {
                is ConnectBankUiState.Loading -> CircularProgressIndicator()
                is ConnectBankUiState.Syncing -> {
                    Text("Sincronizando transações...")
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                is ConnectBankUiState.Success -> {
                    Text("✅ Banco conectado com sucesso!", color = MaterialTheme.colorScheme.primary)
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Voltar ao Dashboard")
                    }
                }
                is ConnectBankUiState.Error -> {
                    Text("❌ Erro: ${(uiState as ConnectBankUiState.Error).message}", color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.onConnectClick() }) {
                        Text("Tentar Novamente")
                    }
                }
                else -> {
                    Button(
                        onClick = { viewModel.onConnectClick() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Conectar Banco com Pluggy")
                    }
                }
            }
        }
    }
}
