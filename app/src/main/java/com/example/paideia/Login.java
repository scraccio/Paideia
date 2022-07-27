package com.example.paideia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    //Gestore dell'autenticazione di Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Avvio della schermata principale (login)
        setContentView(R.layout.activity_main);

        //Inizializzazione autorizzazione Firebase
        mAuth = FirebaseAuth.getInstance();

        //Assegnazione del comportamento al click del bottone login
        Button button = (Button) findViewById(R.id.logintext);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        //Se sono già loggato, passo direttamente al Catalogo
        if(mAuth.getCurrentUser() != null){
            toContent(mAuth.getCurrentUser().getEmail());
            finish();
        }
    }

    //Transizione di activity, da Login a Content
    public void toContent(String email){
        Intent intent = new Intent(this, Content.class);
        //Inserimento l'email nell'intent per passarla alla prossima activity
        intent.putExtra("EMAIL", email);
        startActivity(intent);
        //Definizione delle animazioni in entrata e in uscita
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    //Transizione di activity, da Login a Register
    public void toRegister(View v){
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
        //Definizione delle animazioni in entrata e in uscita
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    //Login dell'utente
    public void login(){
        //Estrazione stringhe email e password dai campi compilati dall'utente
        String email = ((EditText) findViewById(R.id.emaillogin)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordlogin)).getText().toString();

        //Controlli sugli input
        if(email.equals("") || password.equals("")){
            Toast.makeText(this, "Compila tutti i campi", Toast.LENGTH_LONG).show();
        }
        else{
            //Procedura di login
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //Transizione verso il catalogo
                        toContent(email);
                    } else {
                        Toast.makeText(getApplicationContext(), "Autenticazione fallita", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}