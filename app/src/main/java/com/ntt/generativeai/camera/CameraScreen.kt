package com.ntt.generativeai.camera

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors

@Composable
fun CameraScreen(modifier: Modifier, scanned: MutableList<File>, goToAnalyze: () -> Unit) {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var imageAnalysis by remember { mutableStateOf<ImageAnalysis?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(android.Manifest.permission.CAMERA)
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)

                val preview = androidx.camera.core.Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                imageCapture = ImageCapture.Builder().build()
                imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(
                            Executors.newSingleThreadExecutor(), MyImageAnalyzer(
                                TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS))
                        )
                    }

                runCatching {
                    val cameraProvider = cameraProviderFuture.get()
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        context as ComponentActivity,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageCapture,
                        //imageAnalysis
                    )
                }.onFailure { exc ->
                    Log.e("CameraPreview", "Init error", exc)
                }
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        Button(
            onClick = {
                takePicture(context, imageCapture) {
                    scanned.add(it)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            shape = ButtonDefaults.outlinedShape
        ) {
            Text("SCAN")
        }

        if (scanned.isNotEmpty()) Button(
            onClick = {
                goToAnalyze.invoke()
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            shape = ButtonDefaults.outlinedShape
        ) {
            Text("Analyze (#${scanned.size})")
        }
    }
}

private fun takePicture(context: Context, imageCapture: ImageCapture?, cb: (file: File) -> Unit) {
    val outputDirectory = context.filesDir
    val photoFile = File(
        outputDirectory,
        SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis()) + ".jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture?.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                cb.invoke(photoFile)
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraPreview", "Save photo error: ${exception.message}", exception)
            }
        }
    )
}

private class MyImageAnalyzer(val recognizer: TextRecognizer) : ImageAnalysis.Analyzer {

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    // Task completed successfully
                    // ...
                    Log.e("TEXT", visionText.text)
                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                    // ...
                }
                // When the image is from CameraX analysis use case, must call image.close() on received
                // images when finished using them. Otherwise, new images may not be received or the camera
                // may stall.
                .addOnCompleteListener { imageProxy.close() }
        }
    }
}