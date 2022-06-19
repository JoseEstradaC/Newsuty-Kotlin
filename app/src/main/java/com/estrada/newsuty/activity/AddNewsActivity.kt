package com.estrada.newsuty.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.estrada.newsuty.databinding.ActivityAddNewsBinding
import com.estrada.newsuty.db.AppDatabase
import com.estrada.newsuty.db.News
import com.estrada.newsuty.db.NewsDAO
import com.estrada.newsuty.ogtagparser.LinkSourceContent
import com.estrada.newsuty.ogtagparser.LinkViewCallback
import com.estrada.newsuty.ogtagparser.OgTagParser
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*


class AddNewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNewsBinding
    private lateinit var userID: String
    private var db: AppDatabase? = null
    private var newsDAO: NewsDAO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bundle: Bundle = intent.extras!!
        userID = bundle.getString("userID")!!

        binding.AddNewBtn.setOnClickListener {
            createNews(binding.AddNewURL.text.toString().trim())
        }
        binding.AddNewBtnCamera.setOnClickListener {
            if (binding.AddNewTitle.text.isNullOrEmpty())
                Toast.makeText(this, "Debes añadir un título antes de hacer la foto", Toast.LENGTH_SHORT).show()
            else
            {
                if (ContextCompat.checkSelfPermission(binding.root.context, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(binding.root.context as Activity, android.Manifest.permission.CAMERA)) {
                    } else
                        ActivityCompat.requestPermissions(binding.root.context as Activity, arrayOf(android.Manifest.permission.CAMERA), 200)
                } else {
                    capturePhoto()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 200 && data != null){
            val bitmap = (data.extras?.get("data") as Bitmap)
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference
            val name = UUID.randomUUID().toString() + ".jpg"
            val Ref = storageRef.child(name)

            var uploadTask = Ref.putBytes(data)
            uploadTask.addOnFailureListener {
            }.addOnSuccessListener { taskSnapshot ->
                Ref.downloadUrl.addOnSuccessListener {
                    db = AppDatabase.getDatabase(this)
                    newsDAO = db?.newsDAO()
                    val intencion = Intent()
                    try {
                        newsDAO?.insertarNews(
                            News(
                                0,
                                null,
                                it.toString(),
                                binding.AddNewTitle.text.toString(),
                                userID,
                                !(binding.checkBoxTitle.isChecked)
                            )
                        )
                    } catch (ex: Exception) {
                        setResult(666, intencion)
                        Log.e("newsuty-ERROR", ex.toString())
                    }
                    setResult(RESULT_OK, intencion)
                    finish()
                }

            }
        }
    }

    private fun capturePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, 200)
    }

    private fun createNews(url: String) {
        db = AppDatabase.getDatabase(this)
        newsDAO = db?.newsDAO()

        OgTagParser().getContents(
            url,
            object : LinkViewCallback {
                override fun onAfterLoading(linkSourceContent: LinkSourceContent) {
                    val intencion = Intent()
                    if (linkSourceContent.ogTitle.isNotEmpty() || linkSourceContent.images.isNotEmpty()) {
                        if (newsDAO?.obtenerNewsByURL(url) !== null) {
                            setResult(RESULT_CANCELED, intencion)
                            finish()
                            return
                        }

                        try {
                            newsDAO?.insertarNews(
                                News(
                                    0,
                                    url,
                                    linkSourceContent.images,
                                    linkSourceContent.ogTitle,
                                    userID,
                                    !(binding.checkBox.isChecked)
                                )
                            )
                        } catch (ex: Exception) {
                            setResult(666, intencion)
                            Log.e("newsuty-ERROR", ex.toString())
                        }

                        setResult(RESULT_OK, intencion)
                    } else {
                        setResult(666, intencion)
                    }
                    finish()
                }
            }
        )
    }
}