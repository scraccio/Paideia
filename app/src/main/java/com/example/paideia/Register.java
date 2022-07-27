package com.example.paideia;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {

    //Gestori dati Firebase
    DatabaseReference ref;
    FirebaseDatabase rootNode;

    //Gestore dell'autenticazione di Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Avvio della schermata principale (login)
        setContentView(R.layout.activity_register);

        //Inizializzazione dei gestore dati di Firebase
        rootNode = FirebaseDatabase.getInstance("https://paideia-fa077-default-rtdb.europe-west1.firebasedatabase.app");
        ref = rootNode.getReference("database/utenti");

        //Inizializzazione autenticazione Firebase
        mAuth = FirebaseAuth.getInstance();
    }

    //Metodo che definisce il comportamento in uscita dell'Activity
    @Override
    public void finish(){
        super.finish();

        //Definizione delle animazioni in entrata e in uscita
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    //Transizione da Register a Content
    public void toContent(View V){
        Intent intent = new Intent(this, Content.class);
        startActivity(intent);

        //Definizione delle animazioni in entrata e in uscita
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    //Metodo che permette di registrare l'utente
    public void register(View V){
        //Estrazione stringhe email, password e conferma password dai campi compilati dall'utente
        String email = ((EditText) findViewById(R.id.emailregister)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordregister)).getText().toString();
        String password2 = ((EditText) findViewById(R.id.password2register)).getText().toString();

        //Controlli sugli input
        if(email.equals("") || password.equals("") || password2.equals("")){
            Toast.makeText(this, "Compila tutti i campi", Toast.LENGTH_LONG).show();
        }
        else{
            //Controllo sulla validità dell'e-mail inserita
            if(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() == true){
                //Controllo sulla lunghezza della password
                if(password.length() >= 8){
                    //Controllo sull'esattezza delle due password inserite
                    if(password.equals(password2)){

                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                //Se esiste già un utente con la stessa email, annulla la procedura
                                if (dataSnapshot.child(email.replace("@", "").replace(".", "")).exists()) {
                                    Toast.makeText(Register.this, "Account già esistente", Toast.LENGTH_SHORT).show();
                                }
                                else {

                                    //Registrazione Firebase
                                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {

                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {

                                                //Creazione di un nuovo utente date le credenziali e inserimento su Firebase
                                                FirebaseDatabase.getInstance().getReference("utenti")
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .setValue(new Utente(email, password)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        finish();
                                                    }
                                                });

                                                //Inserimento dell'utente nella tabella "utenti" di Firebase
                                                //La tabella sarà poi utile ad associare un carrello ad ogni utente, oltre che alla lista di libri valutati
                                                ref.child(email.replace("@", "").replace(".", "")).setValue(new Utente(email, password));

                                                Toast.makeText(Register.this, "Registrazione avvenuta con successo", Toast.LENGTH_LONG).show();

                                                //Chiusura della schermata di Registrazione
                                                finish();
                                            }
                                            else { //Registrazione fallita
                                                Toast.makeText(getApplicationContext(), "Registrazione fallita", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    else{
                        Toast.makeText(this, "Le password non coincidono", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(this, "Inserisci una password di almeno 8 caratteri", Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(this, "Inserisci un indirizzo e-mail valido", Toast.LENGTH_LONG).show();
            }

        }
    }

}