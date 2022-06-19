package com.estrada.newsuty.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.estrada.newsuty.R
import com.estrada.newsuty.databinding.ActivityMainBinding
import com.estrada.newsuty.dialog.IdiomaDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userID: String
    private val respuestaNewNews =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                Toast.makeText(this, "Noticia añadida", Toast.LENGTH_SHORT).show()
                openFragment(DestacadosFragment.newInstance())
            } else {
                if (it.resultCode != RESULT_CANCELED) {
                    Toast.makeText(this, "Error al añadir la noticia", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        val bundle: Bundle = intent.extras!!
        userID = bundle.getString("userID")!!
        openFragment(DestacadosFragment.newInstance())
        bottomNavigationListener()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.topbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.topBarSalir -> {
                auth.signOut()
                finish()
                true
            }
            R.id.topBarIdioma -> {
                var dialog = IdiomaDialogFragment()
                dialog.show(supportFragmentManager, "IdiomaDialog")
                true
            }
            R.id.topBarAddNews -> {
                val intencion = Intent(this, AddNewsActivity::class.java)
                intencion.putExtra("userID", userID)
                respuestaNewNews.launch(intencion)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun bottomNavigationListener() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Destacados -> {
                    val destacadosFragment = DestacadosFragment.newInstance()
                    openFragment(destacadosFragment)
                    return@setOnItemSelectedListener true
                }
                R.id.UltimaHora -> {
                    val ultimaHoraFragment = UltimaHoraFragment.newInstance()
                    openFragment(ultimaHoraFragment)
                    return@setOnItemSelectedListener true
                }
                R.id.MisNoticias -> {
                    val misNoticiasFragment = MisNoticiasFragment.newInstance()
                    openFragment(misNoticiasFragment)
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    fun openFragment(fragment: Fragment) {
        val bundle = Bundle()
        bundle.putString("USERUID", userID)

        fragment.arguments = bundle
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}