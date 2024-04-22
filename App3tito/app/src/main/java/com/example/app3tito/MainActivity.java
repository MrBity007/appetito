package com.example.app3tito;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private static final int RC_SIGN_IN = 123;
    private static final int SECRET_KEY = 99;
    private static final int PERMISSION_REQUEST_CODE = 1001;
    EditText userEmailET;
    EditText passwordET;

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userEmailET = findViewById(R.id.editUserEmail);
        passwordET = findViewById(R.id.editTextPassword);

        preferences = getSharedPreferences (PREF_KEY, MODE_PRIVATE);
        mAuth=FirebaseAuth.getInstance();

        //Notiti
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Bejelentkezés";
            String description = "Bejelentkezés értesítés";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Bejelentkezés", name, importance);
            channel.setDescription(description);
            // Regisztráljuk a Notification Channel-t a Notification Manager-ben
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(LOG_TAG, "firebaseAuthWithGoogle:" + account.getId());
        }catch (ApiException e){
                Log.w(LOG_TAG, "Google sign in failed", e);
            }
        }
    }


    public void login(View view) {
        String email = userEmailET.getText().toString();
        String password = passwordET.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Kérlek tölts ki minden mezőt!", Toast.LENGTH_SHORT).show();
        } else {
            // Ha mindkét mező kitöltve, akkor folytathatod a bejelentkezési műveletet
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        //siker
                        beleptet();

                    } else {
                        //sikertelen+hiba
                        Toast.makeText (MainActivity.this, "Sikertelen bejelentkezes " +task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
    private void beleptet(){
        sendNotification("Sikeres bejelentkezés!");//értesítés küldése
        Intent intent = new Intent( this, PizzaListActivity.class);
        startActivity(intent);
    }

    public void loginAsGuest (View view) {
        mAuth.signInAnonymously().addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //siker
                    beleptet();
                } else {
                    //hiba+hiba kiírás
                    Toast.makeText (MainActivity.this, "Sikertelen bejelentkezes " +task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public void register(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userEmail", userEmailET. getText().toString());
        editor.putString("password", passwordET. getText().toString());
        editor.apply();

    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void sendNotification(String message) {
        // Ellenőrizzük, hogy van-e értesítési engedély
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED) == PackageManager.PERMISSION_GRANTED) {
            // Az értesítés építése
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Bejelentkezés")
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle("Bejelentkezés")
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            // Az értesítési menedzser eléréséhez
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(1, builder.build());
        } else {
            // Az engedély hiányában megjelenített üzenet
            Toast.makeText(this, "Az értesítési engedély hiányzik", Toast.LENGTH_SHORT).show();
            // Engedélykérési logika
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED}, PERMISSION_REQUEST_CODE);
        }

    }


}