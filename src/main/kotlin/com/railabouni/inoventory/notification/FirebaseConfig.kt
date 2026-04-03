package com.railabouni.inoventory.notification

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.io.ByteArrayInputStream
import java.util.Base64

@Configuration
class FirebaseConfig {

    @Value("\${FIREBASE_ADMIN_CREDENTIALS_JSON:}")
    private lateinit var credentialsJson: String

    @PostConstruct
    fun initFirebase() {
        if (FirebaseApp.getApps().isEmpty()) {
            val optionsBuilder = FirebaseOptions.builder()

            if (credentialsJson.isNotBlank()) {
                try {
                    // Decode from Base64
                    val decodedBytes = Base64.getDecoder().decode(credentialsJson.trim())
                    val credentialsStream = ByteArrayInputStream(decodedBytes)
                    
                    optionsBuilder.setCredentials(GoogleCredentials.fromStream(credentialsStream))
                    println("Firebase initialized successfully via Base64 credentials.")
                } catch (e: Exception) {
                    println("Error decoding or loading Firebase credentials: \${e.message}")
                }
            } else {
                // Fallback for local development if you have a local JSON file path set in GOOGLE_APPLICATION_CREDENTIALS
                try {
                    optionsBuilder.setCredentials(GoogleCredentials.getApplicationDefault())
                } catch (e: Exception) {
                    println("Warning: No Firebase credentials provided. Push notifications will fail.")
                }
            }

            try {
                FirebaseApp.initializeApp(optionsBuilder.build())
            } catch (e: Exception) {
                println("Failed to initialize FirebaseApp: \${e.message}")
            }
        }
    }
}