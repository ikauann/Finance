package com.personal.financeapp.presentation.screens.connect

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.CookieManager
import android.webkit.ConsoleMessage
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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

    LaunchedEffect(itemId) {
        if (itemId != null) {
            viewModel.onConnectionSuccess(itemId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Conectar Banco") })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is ConnectBankUiState.ReadyToConnect -> {
                    val connectToken = (uiState as ConnectBankUiState.ReadyToConnect).connectToken
                    val includeSandbox = (uiState as ConnectBankUiState.ReadyToConnect).includeSandbox
                    val context = LocalContext.current

                    AndroidView(
                        factory = { ctx ->
                            WebView(ctx).apply {
                                // Habilitar debug remoto via chrome://inspect
                                WebView.setWebContentsDebuggingEnabled(true)

                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                settings.databaseEnabled = true
                                settings.loadWithOverviewMode = true
                                settings.useWideViewPort = true
                                settings.cacheMode = WebSettings.LOAD_DEFAULT
                                settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                                settings.setSupportMultipleWindows(false)
                                settings.javaScriptCanOpenWindowsAutomatically = true
                                settings.mediaPlaybackRequiresUserGesture = false

                                setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)

                                settings.userAgentString = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36"

                                // Habilitar cookies (necessário para OAuth flows dos bancos)
                                CookieManager.getInstance().setAcceptCookie(true)
                                CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

                                webChromeClient = object : WebChromeClient() {
                                    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                                        Log.d("PLUGGY_JS", "${consoleMessage?.message()}")
                                        return true
                                    }
                                }

                                webViewClient = object : WebViewClient() {
                                    override fun shouldOverrideUrlLoading(
                                        view: WebView?,
                                        request: WebResourceRequest?
                                    ): Boolean {
                                        val requestUrl = request?.url?.toString() ?: ""
                                        Log.d("PLUGGY_DEBUG", "Navigating to: $requestUrl")

                                        if (requestUrl.startsWith("financeapp://callback")) {
                                            val uri = Uri.parse(requestUrl)
                                            val cbItemId = uri.getQueryParameter("itemId")
                                            if (cbItemId != null) {
                                                viewModel.onConnectionSuccess(cbItemId)
                                            }
                                            return true
                                        }

                                        if (requestUrl.startsWith("financeapp://error")) {
                                            val uri = Uri.parse(requestUrl)
                                            val msg = uri.getQueryParameter("message") ?: "Erro no widget"
                                            viewModel.onWebViewError(msg)
                                            return true
                                        }

                                        if (requestUrl.startsWith("financeapp://close")) {
                                            navController.popBackStack()
                                            return true
                                        }

                                        // URLs externas (OAuth de banco) → abrir no navegador do sistema
                                        if (!requestUrl.startsWith("https://cdn.pluggy.ai") &&
                                            !requestUrl.startsWith("https://connect.pluggy.ai") &&
                                            !requestUrl.startsWith("https://api.pluggy.ai") &&
                                            !requestUrl.startsWith("file://") &&
                                            (requestUrl.startsWith("http://") || requestUrl.startsWith("https://"))
                                        ) {
                                            // OAuth redirect: abrir no navegador do sistema
                                            // Após autenticação, o banco redireciona para financeapp://callback
                                            try {
                                                ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(requestUrl)))
                                            } catch (e: Exception) {
                                                Log.e("PLUGGY_DEBUG", "Erro ao abrir navegador: ${e.message}")
                                            }
                                            return true
                                        }

                                        return false
                                    }

                                    override fun onReceivedError(
                                        view: WebView?,
                                        request: WebResourceRequest?,
                                        error: WebResourceError?
                                    ) {
                                        Log.e("PLUGGY_DEBUG", "WebView error: code=${error?.errorCode}, desc=${error?.description}, url=${request?.url}")
                                        if (request?.isForMainFrame == true) {
                                            viewModel.onWebViewError("Erro ao carregar: ${error?.description} (${error?.errorCode})")
                                        }
                                    }

                                    override fun onReceivedSslError(
                                        view: WebView?,
                                        handler: SslErrorHandler?,
                                        error: android.net.http.SslError?
                                    ) {
                                        Log.e("PLUGGY_DEBUG", "SSL error: $error")
                                        handler?.cancel()
                                        viewModel.onWebViewError("Erro de certificado SSL")
                                    }

                                    override fun onPageFinished(view: WebView?, url: String?) {
                                        Log.d("PLUGGY_DEBUG", "Page finished: $url")
                                        // Quando o HTML local terminar de carregar, inicializar o Pluggy
                                        if (url?.startsWith("file://") == true) {
                                            val js = "initPluggy('${connectToken}', ${includeSandbox});"
                                            Log.d("PLUGGY_DEBUG", "Injecting JS: $js")
                                            view?.evaluateJavascript(js, null)
                                        }
                                    }
                                }

                                // Carregar o HTML local com a SDK da Pluggy
                                loadUrl("file:///android_asset/pluggy_connect.html")
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is ConnectBankUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ConnectBankUiState.Syncing -> {
                    Column(
                        Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Sincronizando suas transações...", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
                is ConnectBankUiState.Success -> {
                    Column(
                        Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("✅ Banco conectado!", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = { navController.popBackStack() }) {
                            Text("Voltar ao Dashboard")
                        }
                    }
                }
                is ConnectBankUiState.Error -> {
                    Column(
                        Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("❌ Erro: ${(uiState as ConnectBankUiState.Error).message}", color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.onConnectClick() }) {
                            Text("Tentar Novamente")
                        }
                    }
                }
                else -> {
                    Column(
                        Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Conecte sua conta bancária via Open Finance.",
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = { viewModel.onConnectClick() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Conectar com Pluggy")
                        }
                    }
                }
            }
        }
    }
}
