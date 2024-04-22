 package com.example.app3tito;

 import android.content.Intent;
 import android.content.SharedPreferences;
 import android.os.Bundle;
 import android.util.Log;
 import android.view.View;
 import android.widget.AdapterView;
 import android.widget.EditText;
 import android.widget.Toast;

 import androidx.annotation.NonNull;
 import androidx.appcompat.app.AppCompatActivity;

 import com.google.android.gms.tasks.OnCompleteListener;
 import com.google.android.gms.tasks.Task;
 import com.google.firebase.auth.AuthResult;
 import com.google.firebase.auth.FirebaseAuth;

 public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
     private static final String LOG_TAG = RegisterActivity.class.getName();
     private static final String PREF_KEY = RegisterActivity.class.getPackage().toString();
     private static final int SECRET_KEY = 99;
     EditText userEmailET;
     EditText passwordET;
     EditText passwordAgainET;
     private SharedPreferences preferences;
     private FirebaseAuth mAuth;


     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        int secretkey = getIntent().getIntExtra("SECRET_KEY", 0);

        if (secretkey != 99){
            finish();
        }

        userEmailET = findViewById(R.id.userEmali);
        passwordET = findViewById(R.id.passwordEditText);
        passwordAgainET = findViewById(R.id.passwordAgainEditText);
        preferences = getSharedPreferences (PREF_KEY, MODE_PRIVATE);

         String userEmail = preferences.getString("userEmail", "");
         String password = preferences.getString( "password", "");


         userEmailET.setText(userEmail);
         passwordET.setText(password);
         passwordAgainET.setText(password);

         mAuth = FirebaseAuth.getInstance();

         Log.i(LOG_TAG, "onCreate");

    }

     public void register(View view) {
         String email = userEmailET.getText().toString();
         String password = passwordET.getText().toString();
         String passwordConfirm = passwordAgainET.getText().toString();

         // Ellenőrizzük, hogy az e-mail cím és a jelszó mezők nem üresek
         if (email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
             Log.e(LOG_TAG, "Minden mező kitöltése kötelező!");
             Toast.makeText(RegisterActivity.this, "Minden mező kitöltése kötelező!", Toast.LENGTH_SHORT).show();
             return;
         }

         // Ellenőrizzük, hogy a jelszavak megegyeznek-e
         if (!password.equals(passwordConfirm)) {
             Log.e(LOG_TAG, "A két jelszó nem egyezik meg!");
             Toast.makeText(RegisterActivity.this, "A két jelszó nem egyezik meg!", Toast.LENGTH_SHORT).show();
             return;
         }

         // Ha minden mező kitöltve, és a jelszavak megegyeznek, folytathatjuk a regisztrációt
         mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
             @Override
             public void onComplete(@NonNull Task<AuthResult> task) {
                 if (task.isSuccessful()) {
                     Log.d(LOG_TAG, "Sikeres regisztráció");
                     startShopping();
                 } else {
                     Log.d(LOG_TAG, "Regisztrációs hiba");
                     Toast.makeText(RegisterActivity.this, "Regisztrációs hiba: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                 }
             }
         });
     }



     public void cancel(View view) {
        finish();
     }

     private void startShopping(){
         Intent intent = new Intent( this, PizzaListActivity.class);
         startActivity(intent);
     }

     @Override
     protected void onStart() {
         super.onStart();
         Log.i(LOG_TAG, "onStart");
     }

     @Override
     protected void onStop() {
         super.onStop();
         Log.i(LOG_TAG, "onStop");
     }

     @Override
     protected void onDestroy() {
         super.onDestroy();
         Log.i(LOG_TAG, "onDestroy");
     }

     @Override
     protected void onPause() {
         super.onPause();
         Log.i(LOG_TAG, "onPause");
     }

     protected void onResume() {
         super.onResume();
         Log.i(LOG_TAG, "onResume");
     }

     @Override
     public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
         String selectedItem = parent.getItemAtPosition(position).toString();
         Log.i(LOG_TAG, selectedItem);
     }

     @Override
     public void onNothingSelected(AdapterView<?> parent) {
     }
 }