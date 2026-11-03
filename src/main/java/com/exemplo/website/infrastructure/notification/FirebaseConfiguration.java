package com.exemplo.website.infrastructure.notification;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/** Inicializa o Firebase Admin de forma preguiçosa, permitindo configurar credenciais por ambiente. */
@Configuration
public class FirebaseConfiguration {
    private final String projectId;
    private final String credentialsJson;
    private final String credentialsPath;

    public FirebaseConfiguration(
            @Value("${firebase.project-id:fixa-fead2}") String projectId,
            @Value("${firebase.credentials-json:}") String credentialsJson,
            @Value("${firebase.credentials-path:}") String credentialsPath
    ) {
        this.projectId = projectId;
        this.credentialsJson = credentialsJson;
        this.credentialsPath = credentialsPath;
    }

    public synchronized FirebaseApp getApp() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) return FirebaseApp.getInstance();

        FirebaseOptions.Builder options = FirebaseOptions.builder().setProjectId(projectId);
        if (!credentialsJson.isBlank()) {
            options.setCredentials(GoogleCredentials.fromStream(new ByteArrayInputStream(
                    credentialsJson.getBytes(StandardCharsets.UTF_8))));
            return FirebaseApp.initializeApp(options.build());
        }
        if (credentialsPath.isBlank()) {
            throw new IllegalStateException("Configure FIREBASE_CREDENTIALS_JSON ou FIREBASE_CREDENTIALS_PATH");
        }
        try (FileInputStream credentials = new FileInputStream(credentialsPath)) {
            options.setCredentials(GoogleCredentials.fromStream(credentials));
            return FirebaseApp.initializeApp(options.build());
        }
    }
}
