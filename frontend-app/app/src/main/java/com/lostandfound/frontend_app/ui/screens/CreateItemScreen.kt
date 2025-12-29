package com.lostandfound.frontend_app.ui.screens

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.lostandfound.frontend_app.data.remote.dto.ItemStatus
import com.lostandfound.frontend_app.ui.viewmodel.CreateItemViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateItemScreen(viewModel: CreateItemViewModel, onItemCreated: () -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> viewModel.selectedImageUri = uri }

    LaunchedEffect(viewModel.isSuccess) {
        if (viewModel.isSuccess) onItemCreated()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Adicionar Objeto", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA)) // Fundo levemente cinza como na imagem
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- SEÇÃO: ESTADO DO OBJETO ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Estado do Objeto", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        // Botão ENCONTRADO
                        Button(
                            onClick = { viewModel.status = ItemStatus.FOUND },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (viewModel.status == ItemStatus.FOUND) Color(0xFF0D0D12) else Color.White,
                                contentColor = if (viewModel.status == ItemStatus.FOUND) Color.White else Color.Black
                            ),
                            border = if (viewModel.status == ItemStatus.FOUND) null else BorderStroke(1.dp, Color.LightGray)
                        ) {
                            Text("Encontrado")
                        }
                        // Botão PERDIDO
                        Button(
                            onClick = { viewModel.status = ItemStatus.LOST },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (viewModel.status == ItemStatus.LOST) Color(0xFF0D0D12) else Color.White,
                                contentColor = if (viewModel.status == ItemStatus.LOST) Color.White else Color.Black
                            ),
                            border = if (viewModel.status == ItemStatus.LOST) null else BorderStroke(1.dp, Color.LightGray)
                        ) {
                            Text("Perdido")
                        }
                    }
                }
            }

            // --- SEÇÃO: FOTO ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Fotografia do Objeto", modifier = Modifier.align(Alignment.Start), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color(0xFFF2F2F7), RoundedCornerShape(12.dp))
                            .clickable { galleryLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (viewModel.selectedImageUri == null) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(40.dp), tint = Color.Gray)
                                Text("Tirar ou carregar foto", color = Color.Gray)
                            }
                        } else {
                            AsyncImage(
                                model = viewModel.selectedImageUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            // --- SEÇÃO: DETALHES ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Título
                    Text("Título", fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = viewModel.title,
                        onValueChange = { viewModel.title = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ex: Carteira castanha") },
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF2F2F7), focusedContainerColor = Color(0xFFF2F2F7), unfocusedBorderColor = Color.Transparent)
                    )

                    // Categoria
                    Text("Categoria", fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = viewModel.categoryName,
                        onValueChange = { viewModel.categoryName = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Selecione uma categoria") },
                        trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, null) },
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF2F2F7), focusedContainerColor = Color(0xFFF2F2F7), unfocusedBorderColor = Color.Transparent)
                    )

                    // Descrição
                    Text("Descrição", fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = viewModel.description,
                        onValueChange = { viewModel.description = it },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        placeholder = { Text("Descreva o objeto com o máximo de detalhes possível...") },
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF2F2F7), focusedContainerColor = Color(0xFFF2F2F7), unfocusedBorderColor = Color.Transparent)
                    )
                }
            }

            // --- SEÇÃO: LOCALIZAÇÃO ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Localização", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = viewModel.locationName, // Certifique-se que esta var existe no ViewModel
                        onValueChange = { viewModel.locationName = it },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = Color.Gray) },
                        placeholder = { Text("Ex: Campus A - Biblioteca") },
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF2F2F7), focusedContainerColor = Color(0xFFF2F2F7), unfocusedBorderColor = Color.Transparent)
                    )
                    Text(
                        "Indique onde o objeto foi encontrado",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Botão Publicar
            Button(
                onClick = { viewModel.onCreateItemClick(context) },
                modifier = Modifier.fillMaxWidth().height(56.dp).padding(bottom = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E8E93)), // Cor do botão da imagem
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Publicar Anúncio", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}