package com.estrada.newsuty.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.estrada.newsuty.databinding.ActivityAddNewsBinding
import com.estrada.newsuty.db.AppDatabase
import com.estrada.newsuty.db.News
import com.estrada.newsuty.db.NewsDAO
import com.estrada.newsuty.ogtagparser.LinkSourceContent
import com.estrada.newsuty.ogtagparser.LinkViewCallback
import com.estrada.newsuty.ogtagparser.OgTagParser

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