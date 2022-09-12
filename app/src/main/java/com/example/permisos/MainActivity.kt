package com.example.permisos

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    enum class CODE {
        CAMARA
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnTakePhoto.setOnClickListener {
            CapturePicture()
        }
        btnGallery.setOnClickListener {
            GetPhotoGallery()
        }
        btnTakeScreen.setOnClickListener {
            Handler().postDelayed({
                var view = window.decorView.rootView as View
                view.isDrawingCacheEnabled = true
                imgFoto.setImageBitmap(view.drawingCache)
                view.isDrawingCacheEnabled = false
            }, 1000)

        }
        btnSaveImage.setOnClickListener {

        }
    }

    private fun GetPhotoGallery() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED -> {
                registerGallery.launch(Intent.createChooser(Intent().apply {

                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                }, "Seleccione una imagen"))
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                registerOpenSetting.launch(Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", this@MainActivity.packageName, null)
                })
            }
            else -> {
                registerPermissionGallery.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun CapturePicture() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED -> {
                registerCamera.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))

            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {

                registerOpenSetting.launch(Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", this@MainActivity.packageName, null)
                })
            }
            else -> {
                registerPermission.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private val registerCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                var imagen = it.data?.extras?.get("data") as Bitmap
                imgFoto.setImageBitmap(imagen)
            }
        }

    private val registerGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                var bitmap = try {
                    MediaStore.Images.Media.getBitmap(
                        this@MainActivity.contentResolver,
                        it.data?.data
                    )

                } catch (e: Exception) {
                }
                imgFoto.setImageBitmap(bitmap as Bitmap?)
            }
        }

    private val registerOpenSetting =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        }
    private val registerPermissionGallery =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                GetPhotoGallery()
                Toast.makeText(this, "Permiso aceptado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()

            }
        }
    private val registerPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                CapturePicture()
                Toast.makeText(this, "Permiso aceptado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()

            }
        }


}