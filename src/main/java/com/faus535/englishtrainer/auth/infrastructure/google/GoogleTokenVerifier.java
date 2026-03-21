package com.faus535.englishtrainer.auth.infrastructure.google;

import com.faus535.englishtrainer.auth.domain.error.GoogleAuthException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoogleTokenVerifier {

    private final GoogleIdTokenVerifier verifier;

    GoogleTokenVerifier(@Value("${google.client-id}") String clientId) {
        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance()
        )
                .setAudience(List.of(clientId))
                .build();
    }

    public GoogleUserInfo verify(String idToken) throws GoogleAuthException {
        try {
            GoogleIdToken token = verifier.verify(idToken);
            if (token == null) {
                throw new GoogleAuthException("Invalid Google ID token");
            }
            GoogleIdToken.Payload payload = token.getPayload();
            return new GoogleUserInfo(
                    payload.getEmail(),
                    (String) payload.get("name"),
                    payload.getEmailVerified()
            );
        } catch (GoogleAuthException e) {
            throw e;
        } catch (Exception e) {
            throw new GoogleAuthException("Failed to verify Google token: " + e.getMessage());
        }
    }
}
