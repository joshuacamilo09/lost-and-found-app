import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.lostandfound.frontend_app.data.remote.dto.item.ItemStatus
import com.lostandfound.frontend_app.ui.viewmodel.ItemDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    itemId: Long,
    viewModel: ItemDetailViewModel,
    onBack: () -> Unit,
    onEditClick: (Long) -> Unit,
    onContactClick: (Long) -> Unit
) {
    LaunchedEffect(itemId) {
        viewModel.loadItem(itemId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalhes do Objeto", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    // SÓ MOSTRA EDITAR/APAGAR SE O ITEM PERTENCER AO USER LOGADO
                    if (viewModel.item?.userId == viewModel.currentUserId) {
                        IconButton(onClick = { onEditClick(itemId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }

                    }
                }
            )
        }
    ) { padding ->
        val item = viewModel.item

        if (viewModel.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (item != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF8F9FA))
                    .verticalScroll(rememberScrollState())
            ) {
                // --- IMAGEM DE CAPA COM BADGE ---
                Box {
                    AsyncImage(
                        model = item.imageUrl ?: "https://via.placeholder.com/600",
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentScale = ContentScale.Crop
                    )

                    Surface(
                        color = if (item.status == ItemStatus.LOST) Color(0xFFE57373) else Color(0xFF0D0D12),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(16.dp).align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = if (item.status == ItemStatus.LOST) "Perdido" else "Encontrado",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // --- TÍTULO E STATUS DE RESOLUÇÃO ---
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            if (item.resolved) {
                                Badge(containerColor = Color(0xFF4CAF50)) {
                                    Text("Resolvido", color = Color.White, modifier = Modifier.padding(4.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = Color(0xFFF2F2F7),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = item.categoryName,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }

                    // --- CARD: DESCRIÇÃO ---
                    InfoCard(title = "Descrição") {
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.DarkGray
                        )
                    }

                    // --- CARD: INFORMAÇÕES ---
                    InfoCard(title = "Informações") {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            InfoRow(Icons.Default.LocationOn, "Localização", item.locationName ?: "Não especificado")
                            InfoRow(Icons.Default.AccessTime, "Data", item.itemDateTime.toString())
                            InfoRow(
                                Icons.Default.Person,
                                if (item.status == ItemStatus.LOST) "Perdido por" else "Encontrado por",
                                item.username
                            )
                        }
                    }

                    // --- SEÇÃO DE ACÇÕES (CONTACTO OU MARCAR COMO RESOLVIDO) ---
                    if (item.userId == viewModel.currentUserId) {
                        // SE FOR O DONO: Botão para resolver
                        if (!item.resolved) {
                            Button(
                                onClick = { viewModel.markAsResolved(item.id) },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                            ) {
                                Icon(Icons.Default.CheckCircleOutline, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Marcar como Resolvido / Entregue")
                            }
                        }
                    } else {
                        // SE NÃO FOR O DONO: Botão de Contacto
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Entrar em Contacto", fontWeight = FontWeight.Bold)

                            Button(
                                onClick = { onContactClick(item.userId) },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D0D12))
                            ) {
                                Icon(Icons.Default.ChatBubbleOutline, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Iniciar Conversa")
                            }

                            // MENSAGEM IMPORTANTE
                            Surface(
                                color = Color(0xFFE3F2FD),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Importante: Ao contactar o utilizador, esteja preparado para fornecer uma descrição detalhada do objeto para comprovar a propriedade.",
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF1565C0)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun InfoCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(20.dp), tint = Color.Gray)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
    }
}