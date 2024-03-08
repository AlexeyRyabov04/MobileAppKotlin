package com.example.mobileapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val linkToLogin : TextView = findViewById(R.id.linkToSignIn)
        linkToLogin.setOnClickListener{
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        val btn = findViewById<Button>(R.id.registerButton)
        btn.setOnClickListener {
            val email = findViewById<EditText>(R.id.emailEdit).text.toString()
            val password = findViewById<EditText>(R.id.passwordEdit).text.toString()
            val retypePassword = findViewById<EditText>(R.id.retypePasswordEdit).text.toString()
            if (email.isNotEmpty() && password.isNotEmpty() && retypePassword.isNotEmpty()) {
                if (password == retypePassword) {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val ref = Firebase.database.getReference().child("users")
                                val user = User(
                                    email = email,
                                )
                                ref.push().setValue(user)
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Имеются пустые поля", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart(){
        super.onStart()

        if(FirebaseAuth.getInstance().currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}