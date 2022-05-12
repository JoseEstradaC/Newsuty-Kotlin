package com.estrada.newsuty.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.estrada.newsuty.R
import com.estrada.newsuty.databinding.ActivityLoginBinding
import com.estrada.newsuty.db.AppDatabase
import com.estrada.newsuty.db.User
import com.estrada.newsuty.db.UserDAO
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private var db: AppDatabase? = null
    private var userdao: UserDAO? = null
    private lateinit var binding: ActivityLoginBinding
    private lateinit var authFirebase: FirebaseAuth
    private lateinit var gsic: GoogleSignInClient
    private lateinit var cuenta: GoogleSignInAccount
    private val respuestaMain =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                Toast.makeText(this, "Registro correcto", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Registro incorrecto", Toast.LENGTH_SHORT).show()
            }
        }
    private val respuestaGoogle =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                try {
                    val tarea = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                    cuenta = tarea.getResult(ApiException::class.java)!!
                    gsic.signOut()
                    val credenciales = GoogleAuthProvider.getCredential(cuenta.idToken, null)
                    authFirebase.signInWithCredential(credenciales).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Registro correcto", Toast.LENGTH_SHORT).show()
                            goMain()
                        } else {
                            Toast.makeText(this, "Registro incorrecto", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: ApiException) {
                    Toast.makeText(this, "Fallo en el servicio de google", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(this, "Registro incorrecto, google", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authFirebase = Firebase.auth

        binding.LoginBtnGoogle.setSize(SignInButton.SIZE_STANDARD)
        binding.LoginBtnGoogle.setOnClickListener {
            google(it)
        }
        binding.LoginBtnLogin.setOnClickListener {
            login(it)
        }
        binding.LoginBtnRegistro.setOnClickListener {
            registro(it)
        }
    }

    private fun login(it: View) {
        val email = binding.LoginEmail.text.toString().trim()
        val password = binding.LoginPassword.text.toString().trim()

        authFirebase.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                goMain()
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (authFirebase.currentUser != null) {
            goMain()
        }
    }

    private fun goMain() {
        if (authFirebase.currentUser != null) {
            db = AppDatabase.getDatabase(this);
            userdao = db?.userDAO()
            if (userdao?.obtenerUser(authFirebase.currentUser!!.uid) == null) {
                userdao?.insertarUser(
                    User(
                        authFirebase.currentUser!!.uid,
                        false,
                        authFirebase.currentUser!!.email.toString()
                    )
                )
            }
        }
        val intencion = Intent(this, MainActivity::class.java)
        intencion.putExtra("userID", authFirebase.currentUser!!.uid)
        startActivity(intencion)
    }

    private fun registro(view: View) {
        val intencion = Intent(this, RegisterActivity::class.java)
        respuestaMain.launch(intencion)
    }

    private fun google(view: View) {
        val gsio = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()
        gsic = GoogleSignIn.getClient(this, gsio)
        respuestaGoogle.launch(gsic.signInIntent)
    }
}