package com.github.ybecker.epforuml.sensor

import android.Manifest
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.widget.Toast
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.Preview
import androidx.camera.core.CameraSelector
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageButton
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCaptureException
import androidx.camera.video.*
import androidx.camera.view.PreviewView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.PermissionChecker
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.databinding.ActivityCameraBinding
import com.google.android.gms.maps.model.LatLng
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class CameraActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityCameraBinding

    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null

    private var recording: Recording? = null

    private lateinit var cameraExecutor: ExecutorService

    private var isRecording = false

    private lateinit var viewFinder: PreviewView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        // Set up the listeners for take photo and video capture buttons
        val captureButton = viewBinding.imageCaptureButton


        captureButton.setOnClickListener {
            (it as ImageButton).setColorFilter(ContextCompat.getColor(MainActivity.context, R.color.shade3), PorterDuff.Mode.SRC_IN)
            viewBinding.imageCaptureButton.removeCallbacks(null)
            if (isRecording) {
                stopRecordingVideo()
            } else {
                takePhoto()
            }
        }

        captureButton.setOnLongClickListener {
            (it as ImageButton).setColorFilter(ContextCompat.getColor(MainActivity.context, R.color.shade2), PorterDuff.Mode.SRC_IN)
            startRecordingVideo()
            true
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        viewFinder = findViewById(R.id.viewFinder)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun takePhoto() {

        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                    goToEdit(output.savedUri.toString())
                }

            }
        )

    }

    private fun startRecordingVideo() {

        val videoCapture = this.videoCapture ?: return

        isRecording=true

        val curRecording = recording
        if (curRecording != null) {
            curRecording.stop()
            recording = null
            return
        }

        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
            }
        }

        val context = this
        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()

        // launch a new Recording
        recording = videoCapture.output
            .prepareRecording(this, mediaStoreOutputOptions)
            .apply {
                if (PermissionChecker.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO
                    ) ==
                    PermissionChecker.PERMISSION_GRANTED
                ) {
                    withAudioEnabled()
                }
            // start the new recording
            }.start(ContextCompat.getMainExecutor(context)) { recordEvent ->
            when (recordEvent) {
                is VideoRecordEvent.Start -> {
                    isRecording=true
                    Log.d(TAG, "Start recording")

                }
                //when the video ends
                is VideoRecordEvent.Finalize -> {
                    isRecording=false
                    val uri = recordEvent.outputResults.outputUri
                    //when no errors the video is saved correctly, we can go back the the question
                    if (!recordEvent.hasError()) {
                        val msg = "Video capture succeeded: " +
                                "$uri"
                        goBackToQuestion(uri.toString())
                        Log.d(TAG, msg)
                    //if there is an error stop recording, and stay on the activity so the user can retake a video
                    } else {
                        recording?.close()
                        recording = null
                        Toast.makeText(this, "Error during the recording", LENGTH_SHORT).show()
                        Log.e(TAG, "Video capture ends with error: " +
                                "${recordEvent.error}")
                    }
                }
            }
        }
    }

    private fun stopRecordingVideo() {
        recording?.close()
    }



    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }


            imageCapture = ImageCapture.Builder()
                .build()

            //Set up a new recorder in videoCapture with low quality (the free Firebase bandwidth is limited)
            val recorder = Recorder.Builder()
                .setExecutor(ContextCompat.getMainExecutor(this))
                .setQualitySelector(QualitySelector.from(Quality.LOWEST))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, videoCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun goToEdit(uri : String){
        val intent  = Intent(this, EditPhotoActivity::class.java)
        intent.putExtra("uri", uri)
        intent.putExtra("questionTitle", getIntent().getStringExtra("questionTitle"))
        intent.putExtra("questionDetails", getIntent().getStringExtra("questionDetails"))
        startActivity(intent)

    }

    private fun goBackToQuestion(uri : String){
        val intent  = Intent(this, MainActivity::class.java)
        intent.putExtra("uri", uri)
        intent.putExtra("fragment", "NewQuestionFragment")
        intent.putExtra("questionTitle", getIntent().getStringExtra("questionTitle"))
        intent.putExtra("questionDetails", getIntent().getStringExtra("questionDetails"))
        startActivity(intent)

    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}