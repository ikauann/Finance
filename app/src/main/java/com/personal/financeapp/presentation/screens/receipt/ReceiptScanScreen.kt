package com.personal.financeapp.presentation.screens.receipt

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScanScreen(
    navController: NavController,
    viewModel: ReceiptViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = try {
                if (Build.VERSION.SDK_INT < 28) {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                        decoder.isMutableRequired = true
                    }
                }
            } catch (e: Exception) {
                null
            }
            bitmap?.let { b ->
                capturedImage = b
                viewModel.onImageSelected(b)
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            capturedImage = it
            viewModel.onImageSelected(it)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escanear Cupom") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                }
            )
        },
        bottomBar = {
            com.personal.financeapp.presentation.components.BottomNavigationBar(navController)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is ReceiptUiState.Idle -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.AutoMirrored.Filled.ReceiptLong,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                when (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)) {
                                    PackageManager.PERMISSION_GRANTED -> {
                                        cameraLauncher.launch(null)
                                    }
                                    else -> {
                                        permissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.CameraAlt, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Tirar Foto")
                        }
                        OutlinedButton(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.PhotoLibrary, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Escolher da Galeria")
                        }
                    }
                }
                is ReceiptUiState.Loading -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Gemini analisando o cupom...")
                    }
                }
                is ReceiptUiState.Success -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        capturedImage?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .height(200.dp)
                                    .fillMaxWidth()
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Informações Extraídas", style = MaterialTheme.typography.titleMedium)
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                Text("Local: ${state.transaction.description}")
                                Text("Valor: ${NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(state.transaction.amount)}")
                                Text("Data: ${state.transaction.date}")
                                Text("Categoria: ${state.transaction.category.name}")
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { viewModel.reset() }, modifier = Modifier.weight(1f)) {
                                Text("Tentar Novamente")
                            }
                            Button(
                                onClick = { viewModel.confirmTransaction(state.transaction) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Confirmar")
                            }
                        }
                    }
                }
                is ReceiptUiState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Error, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(64.dp))
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.reset() }) {
                            Text("Voltar")
                        }
                    }
                }
                is ReceiptUiState.Saved -> {
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}
