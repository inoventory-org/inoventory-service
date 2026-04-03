package com.railabouni.inoventory.notification

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.io.ByteArrayInputStream

@Configuration
class FirebaseConfig {

    @Value("\${FIREBASE_ADMIN_CREDENTIALS_JSON:}")
    private lateinit var credentialsJson: String

    @PostConstruct
    fun initFirebase() {
        if (FirebaseApp.getApps().isEmpty()) {
            val optionsBuilder = FirebaseOptions.builder()

            if (credentialsJson.isNotBlank()) {
                val credentialsStream = ByteArrayInputStream(credentialsJson.toByteArray())
                optionsBuilder.setCredentials(GoogleCredentials.fromStream(credentialsStream))
            } else {
                try {
                    optionsBuilder.setCredentials(GoogleCredentials.getApplicationDefault())
                } catch (e: Exception) {
                    println("Warning: Firebase credentials not found. Defaulting without secure auth. Push notifications will fail if not resolved.")
                }
            }

            try {
                FirebaseApp.initializeApp(optionsBuilder.build())
            } catch (e: Exception) {
                println("Failed to initialize Firebase: \${e.message}")
            }
        }
    }
}
