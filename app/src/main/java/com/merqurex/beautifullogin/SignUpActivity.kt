package com.merqurex.beautifullogin

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

// Define the User data class
data class User(val name: String = "", val email: String = "")

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set your custom color
        val lightPurple = Color.parseColor("#F7F0FD") // Replace with your desired color value

        // Set the UI visibility flags
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

        // Set the status bar and navigation bar color
        window.statusBarColor = lightPurple
        window.navigationBarColor = lightPurple

        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        val nameLayout = findViewById<TextInputLayout>(R.id.name_signup_layout)
        val nameEditText = findViewById<TextInputEditText>(R.id.name_signup_edittext)

        val emailLayout = findViewById<TextInputLayout>(R.id.email_signup_layout)
        val emailEditText = findViewById<TextInputEditText>(R.id.email_signup_edittext)

        val passwordLayout = findViewById<TextInputLayout>(R.id.password_signup_layout)
        val passwordEditText = findViewById<TextInputEditText>(R.id.password_signup_edittext)

        val signUpButton = findViewById<Button>(R.id.signup_button)
        val loginAccountIntent = findViewById<Button>(R.id.login_intent_button)

        signUpButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            var isValid = true

            if (!isNameValid(name)) {
                nameLayout.error = "Please enter a valid name"
                isValid = false
            } else {
                nameLayout.error = null
            }

            if (!isEmailValid(email)) {
                emailLayout.error = "Please enter a valid email address"
                isValid = false
            } else {
                emailLayout.error = null
            }

            if (!isPasswordValid(password)) {
                passwordLayout.error = "Password should be at least 6 characters long"
                isValid = false
            } else {
                passwordLayout.error = null
            }

            if (isValid) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val userId = user?.uid

                            val userData = User(name, email)
                            userId?.let {
                                usersRef.child(it).setValue(userData)
                            }

                            Toast.makeText(this, "Account creation successful", Toast.LENGTH_SHORT).show()
                            val blankIntent = Intent(this, BlankActivity::class.java)
                            startActivity(blankIntent)
                        } else {
                            Toast.makeText(this, "Account creation failed. ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            }

        }

        loginAccountIntent.setOnClickListener {
            val loginAccIntent = Intent(this, MainActivity::class.java)
            startActivity(loginAccIntent)
        }
    }

    private fun isNameValid(name: String): Boolean {
        // You can implement your name validation logic here
        return name.isNotEmpty()
    }

    private fun isEmailValid(email: String): Boolean {
        // Validate email criteria using Patterns.EMAIL_ADDRESS matcher
        val emailPattern = android.util.Patterns.EMAIL_ADDRESS
        return emailPattern.matcher(email).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        // Validate password criteria (at least 6 characters long)
        return password.length >= 6
    }
}
