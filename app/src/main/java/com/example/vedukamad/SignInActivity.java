package com.example.vedukamad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.*;

public class SignInActivity extends AppCompatActivity {

    FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;
    EditText emailInput, passwordInput;
    Button loginButton, phoneButton;
    SignInButton googleSignInBtn;

    private final ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            Task<GoogleSignInAccount> accountTask =
                                    GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                            try {
                                GoogleSignInAccount signInAccount =
                                        accountTask.getResult(ApiException.class);
                                AuthCredential authCredential = GoogleAuthProvider
                                        .getCredential(signInAccount.getIdToken(), null);

                                auth.signInWithCredential(authCredential)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SignInActivity.this,
                                                        "Google Sign-In Successful", Toast.LENGTH_SHORT).show();

                                                // Save user info in SharedPreferences after successful sign-in
                                                saveUserInfo(signInAccount.getDisplayName(), signInAccount.getEmail());

                                                redirectToRoleBasedDashboard(); // ✅ updated
                                            } else {
                                                Toast.makeText(SignInActivity.this,
                                                        "Google Sign-In Failed: " + task.getException(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            } catch (ApiException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        googleSignInBtn = findViewById(R.id.googleButton);
        phoneButton = findViewById(R.id.phoneButton);

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(SignInActivity.this, options);

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignInActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Save user info in SharedPreferences after successful sign-in
                            saveUserInfo(email, email); // Store email as fallback username

                            redirectToRoleBasedDashboard(); // ✅ updated
                        } else {
                            Toast.makeText(SignInActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        googleSignInBtn.setOnClickListener(v -> {
            Intent intent = googleSignInClient.getSignInIntent();
            activityResultLauncher.launch(intent);
        });

        TextView signupRedirect = findViewById(R.id.signupText);
        signupRedirect.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void saveUserInfo(String username, String email) {
        SharedPreferences prefs = getSharedPreferences("VedukaPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("username", username);  // Save username
        editor.putString("email", email);        // Save email
        editor.apply();
    }

    private void redirectToRoleBasedDashboard() {
        SharedPreferences prefs = getSharedPreferences("VedukaPrefs", MODE_PRIVATE);
        String role = prefs.getString("userRole", "none");

        Log.d("SignInActivity", "Redirecting with role: " + role);

        if ("organizer".equals(role)) {
            startActivity(new Intent(SignInActivity.this, OrganizerDashboardActivity.class));
        } else if ("user".equals(role)) {
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null) {
                String username = currentUser.getDisplayName();
                if (username == null || username.isEmpty()) {
                    username = currentUser.getEmail(); // fallback
                }

                // Pass the username to the HomePageActivity
                Intent intent = new Intent(SignInActivity.this, HomePageActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            } else {
                Toast.makeText(this, "User info not available", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No role selected. Please try again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SignInActivity.this, UserRoleActivity.class));
        }

        finish();
    }
}
