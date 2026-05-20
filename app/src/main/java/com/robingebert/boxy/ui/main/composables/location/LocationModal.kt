package com.robingebert.boxy.ui.main.composables.location

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil3.compose.AsyncImage
import com.robingebert.boxy.domain.models.Location
import com.robingebert.boxy.ui.common.composables.IconRow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationModal(modifier: Modifier = Modifier, location: Location, onDismiss: () -> Unit, onSave: (Location) -> Unit) {

    var name by rememberSaveable { mutableStateOf(location.name) }
    var capturedImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (!success) {
            capturedImageUri = null
        }
    }
    fun save() {
        scope.launch {
            val targetFileName = "${location.id}.jpg"

            val finalPictureName = if (capturedImageUri != null) {
                val savedFile = saveImageToInternalStorage(context, capturedImageUri!!, targetFileName)

                if (savedFile != null) targetFileName else location.picture
            } else {
                location.picture
            }

            onSave(location.copy(name = name, picture = finalPictureName))
            onDismiss()
        }
    }


    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = { onDismiss() },
        dragHandle = {Spacer(Modifier.height(16.dp))}
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 20.dp),
        ) {
            IconRow(Icons.Default.Title) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("name") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(Modifier.height(16.dp))
            IconRow(Icons.Default.Camera) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                        val uri = getTmpFileUri(context)
                        capturedImageUri = uri
                        cameraLauncher.launch(uri)
                    }) {
                        Icon(Icons.Rounded.Save, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Take picture")
                    }

                    val imageModel = capturedImageUri
                        ?: location.picture?.let { fileName ->
                            File(File(context.filesDir, "images"), fileName)
                        }
                    imageModel?.let {
                        AsyncImage(
                            model = it,
                            contentDescription = "Picture of inventory",
                            modifier = Modifier.size(48.dp).padding(start = 8.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(Modifier.padding(start = 46.dp)) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { save() }
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Save")
                }
            }
        }
    }
}

suspend fun saveImageToInternalStorage(context: Context, tempUri: Uri, fileName: String): File? {
    return withContext(Dispatchers.IO) {
        try {
            val imagesFolder = File(context.filesDir, "images")
            if (!imagesFolder.exists()) {
                imagesFolder.mkdirs()
            }

            val destinationFile = File(imagesFolder, fileName)

            context.contentResolver.openInputStream(tempUri)?.use { inputStream ->
                FileOutputStream(destinationFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            destinationFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

fun getTmpFileUri(context: Context): Uri {
    val tempFile = File.createTempFile("temp_image_", ".jpg", context.cacheDir).apply {
        createNewFile()
    }

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        tempFile
    )
}