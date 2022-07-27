package com.example.paideia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class itemActivity extends AppCompatActivity {

    //Gestori dati Firebase
    DatabaseReference ref;
    FirebaseDatabase rootNode;

    //Email dell'account loggato
    String email;

    //Array contenente i dati del libro da visualizzare su schermo
    String[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        //Inizializzazione dell'email loggata tramite il dato inserito nell'intent
        email = getIntent().getStringExtra("EMAIL");

        //Inizializzazione dell'array con le informazioni del libro tramite il dato inserito nell'intent
        data = getIntent().getStringArrayExtra("OBJ");
        System.out.println(data[0]);


        //Assegnazione delle informazioni del libro alle rispettive view
        ((TextView) findViewById(R.id.textView7)).setText("Valutazione: " + Math.floor(Double.parseDouble(data[6])*100)/100);

        ((TextView) findViewById(R.id.textView4)).setText(data[0]);
        ((TextView) findViewById(R.id.textView)).setText("di " + data[1]);
        ((TextView) findViewById(R.id.textView5)).setText("Prezzo: " + data[2]);
        ((TextView) findViewById(R.id.textView2)).setText(data[4]);

        ((ImageView) findViewById(R.id.imageView5)).setImageResource(R.drawable.starempty);
        ((ImageView) findViewById(R.id.imageView11)).setImageResource(R.drawable.starempty);
        ((ImageView) findViewById(R.id.imageView12)).setImageResource(R.drawable.starempty);
        ((ImageView) findViewById(R.id.imageView13)).setImageResource(R.drawable.starempty);
        ((ImageView) findViewById(R.id.imageView8)).setImageResource(R.drawable.starempty);

        //Calcolo e set delle stelline
        double val = Double.parseDouble(data[6]);
        calculateAndSetStars(val);

        //Assegnazione del comportamento sul click delle stelline
        //Verrà aperto un popup per chiedere se si vuole proseguire con la votazione scelta
        ((ImageView) findViewById(R.id.imageView5)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vote(new View(itemActivity.this), 1);
            }
        });
        ((ImageView) findViewById(R.id.imageView11)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vote(new View(itemActivity.this), 2);
            }
        });
        ((ImageView) findViewById(R.id.imageView12)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vote(new View(itemActivity.this), 3);
            }
        });
        ((ImageView) findViewById(R.id.imageView13)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vote(new View(itemActivity.this), 4);
            }
        });
        ((ImageView) findViewById(R.id.imageView8)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vote(new View(itemActivity.this), 5);
            }
        });

        //Decodifica e assegnazione dell'immagine alla ImageView
        decodeBase64AndSetImage(data[3], (ImageView) findViewById(R.id.imageView4));


        //Ricerca del libro nel carrello utente
        //Se l'elemento viene trovato, spunterà un tasto "Rimuovi dal carrello", a cui verrà assegnato il comportamento corretto
        rootNode = FirebaseDatabase.getInstance("https://paideia-fa077-default-rtdb.europe-west1.firebasedatabase.app");
        ref = rootNode.getReference("database/utenti/" + email.replace("@","").replace(".","") + "/carrello");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(data[0]).exists()){
                    LayoutInflater inflater = (LayoutInflater) findViewById(R.id.activity_item).getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    ViewGroup parent = (ViewGroup)findViewById(R.id.layout10);
                    inflater.inflate(R.layout.removebasket, parent);

                    View button = findViewById(R.id.removebasketbutton);

                    //Assegnazione del comportamento al button
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Eliminazione del libro dai database
                            ref.child(data[0]).removeValue();
                            Toast.makeText(itemActivity.this, data[0] + " è stato rimosso dal carrello", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Se si è loggati con l'account superuser, inizializza un bottone per rimuovere l'elemento dal catalogo
        if(email.equals("admin@gmail.com")){
            LayoutInflater inflater = (LayoutInflater) findViewById(R.id.activity_item).getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewGroup parent = (ViewGroup)findViewById(R.id.activity_item);
            inflater.inflate(R.layout.minus_button, parent);

            View button = findViewById(R.id.minus_button);

            //Assegnazione del comportamento al click del bottone
            //Verrà aperto un AlertDialog in cui l'utente potrà confermare o annullare la rimozione
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog alertDialog = new AlertDialog.Builder(itemActivity.this).create();
                    alertDialog.setTitle("Conferma rimozione");
                    alertDialog.setMessage("Vuoi rimuovere l'elemento?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Annulla", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Conferma", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            //Rimozione del libro dal carrello
                            rootNode = FirebaseDatabase.getInstance("https://paideia-fa077-default-rtdb.europe-west1.firebasedatabase.app");
                            ref = rootNode.getReference("database/utenti/" + email.replace("@","").replace(".","") + "/carrello");
                            ref.child(data[0]).removeValue();

                            //Rimozione del libro da Firebase
                            ref = rootNode.getReference("database/libri");
                            ref.child(data[0]).removeValue();

                            dialogInterface.dismiss();

                            //Transizione verso il Catalogo
                            gridClick(new View(getApplicationContext()));
                        }
                    });
                    alertDialog.show();
                }
            });
        }
    }

    //Metodo che calcola e imposta le immagini con le stelline corrette
    private void calculateAndSetStars(double val) {
        val = Math.round(val*2)/2.0;
        if(val == 0.5){
            ((ImageView) findViewById(R.id.imageView5)).setImageResource(R.drawable.starhalf);
        }
        if(val == 1){
            ((ImageView) findViewById(R.id.imageView5)).setImageResource(R.drawable.starfull);
        }
        if(val == 1.5){
            ((ImageView) findViewById(R.id.imageView5)).setImageResource(R.drawable.starfull);
            ((ImageView) findViewById(R.id.imageView11)).setImageResource(R.drawable.starhalf);
        }
        if(val == 2){
            ((ImageView) findViewById(R.id.imageView5)).setImageResource(R.drawable.starfull);
            ((ImageView) findViewById(R.id.imageView11)).setImageResource(R.drawable.starfull);
        }
        if(val == 2.5){
            ((ImageView) findViewById(R.id.imageView5)).setImageResource(R.drawable.starfull);
            ((ImageView) findViewById(R.id.imageView11)).setImageResource(R.drawable.starfull);
            ((ImageView) findViewById(R.id.imageView12)).setImageResource(R.drawable.starhalf);
        }
        if(val == 3){
            ((ImageView) findViewById(R.id.imageView5)).setImageResource(R.drawable.starfull);
            ((ImageView) findViewById(R.id.imageView11)).setImageResource(R.drawable.starfull);
            ((ImageView) findViewById(R.id.imageView12)).setImageResource(R.drawable.starfull);
        }
        if(val == 3.5){
            ((ImageView) findViewById(R.id.imageView5)).setImageResource(R.drawable.starfull);
            ((ImageView) findViewById(R.id.imageView11)).setImageResource(R.drawable.starfull);
            ((ImageView) findViewById(R.id.imageView12)).setImageResource(R.drawable.starfull);
            ((ImageView) findViewById(R.id.imageView13)).setImageResource(R.drawable.starhalf);
        }
        if(val == 4){
            ((ImageView) findViewById(R.id.imageView5)).setImageResource(R.drawable.starfull);
            ((ImageView) findViewById(R.id.imageView11)).setImageResource(R.drawable.starfull);
            ((ImageView) findViewById(R.id.imageView12)).setImageResource(R.drawable.starfull);
            ((ImageView) findViewById(R.id.imageView13)).setImageResource(R.drawable.starfull);
        }
        if(val == 4.5){
            ((ImageView) findViewById(R.id.imageView5)).setImageResource(R.drawable.starfull);
            ((ImageView) findViewById(R.id.imageView11)).setImageResource(R.drawable.starfull);
            ((ImageView) findViewById(R.id.imageView12)).setImageResource(R.drawable.starfull);
            ((ImageView) findViewById(R.id.imageView13)).setImageResource(R.drawable.starfull);
            ((ImageView) findViewById(R.id.imageView8)).setImageResource(R.drawable.starhalf);
        }
        if(val == 5){
            ((ImageView) findViewById(R.id.imageView5)).setImageResource(R.drawable.starfull);
            ((ImageView) findViewById(R.id.imageView11)).setImageResource(R.drawable.starfull);
            ((ImageView) findViewById(R.id.imageView12)).setImageResource(R.drawable.starfull);
            ((ImageView) findViewById(R.id.imageView13)).setImageResource(R.drawable.starfull);
            ((ImageView) findViewById(R.id.imageView8)).setImageResource(R.drawable.starfull);
        }
    }

    //Transizione da itemActivity a Carrello
    public void toBasket(View v) {
        Intent intent = new Intent(this, Carrello.class);
        intent.putExtra("EMAIL", email);
        startActivity(intent);
        //Definizione delle animazioni in entrata e in uscita
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        finish();
    }

    //Transizione da itemActivity a Account
    public void toUser(View v) {
        Intent intent = new Intent(this, Account.class);
        intent.putExtra("EMAIL", email);
        startActivity(intent);
        //Definizione delle animazioni in entrata e in uscita
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        finish();
    }

    //Transizione da itemActivity a Catalogo
    public void gridClick(View v){
        System.out.println("SUNFONI");
        Intent intent = new Intent(this, Content.class);
        intent.putExtra("EMAIL", email);
        startActivity(intent);
        //Definizione delle animazioni in entrata e in uscita
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        finish();
    }

    //Metodo che aggiunge un libro al carrello in Firebase
    public void aggiungiAlCarrello(View v){
        Toast.makeText(this, "Hai aggiunto " + data[0] + " al carrello", Toast.LENGTH_SHORT).show();

        ref = rootNode.getReference("database/utenti/" + email.replace("@","").replace(".","") + "/carrello");
        ref.child(data[0]).setValue(new Libro(data[0], data[1], data[2], data[3], data[4], data[5], data[6]));
    }

    //Metodo che decodifica l'immagine a partire da una stringa in Base64 e la imposta in una ImageView
    private void decodeBase64AndSetImage(String completeImageData, ImageView imageView) {
        InputStream stream = new ByteArrayInputStream(Base64.decode(completeImageData.getBytes(), Base64.DEFAULT));

        Bitmap bitmap = BitmapFactory.decodeStream(stream);

        imageView.setImageBitmap(bitmap);
    }

    //Metodo che definisce il comportamento in uscita dell'Activity
    @Override
    public void finish(){
        super.finish();

        //Definizione delle animazioni in entrata e in uscita
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    //Metodo per pubblicare una valutazione su un libro
    //Genera un AlertDialog, l'utente può confermare la valutazione oppure annullare l'operazione
    public void vote(View v, int num){
        AlertDialog alertDialog = new AlertDialog.Builder(itemActivity.this).create();
        alertDialog.setTitle("Voto");
        alertDialog.setMessage("Vuoi dare una valutazione di " + num +  "?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Annulla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Conferma", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Pubblicazione della valutazione sul database remoto Firebase
                rootNode = FirebaseDatabase.getInstance("https://paideia-fa077-default-rtdb.europe-west1.firebasedatabase.app");
                ref = rootNode.getReference("database/utenti/" + email.replace("@","").replace(".","") + "/valutati");

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int x=0;
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            if(dataSnapshot.getKey().equals(data[0])){
                                x++;
                                break;
                            }
                        }
                        if(x == 0){
                            //Se il libro non era stato valutato in precedenza, adesso viene valutato
                            ref.child(data[0]).setValue(num);

                            rootNode = FirebaseDatabase.getInstance("https://paideia-fa077-default-rtdb.europe-west1.firebasedatabase.app");
                            ref = rootNode.getReference("database/libri");

                            //Aggiornamento dei campi "numvalutazioni" e "valutazione" sul libro appena valutato
                            //Il primo campo verrà aumentato di uno, per il secondo verrà ricalcolata la media
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                                        if(dataSnapshot.child("titolo").getValue().equals(data[0])){
                                            int numval = Integer.parseInt(dataSnapshot.child("numvalutazioni").getValue().toString());
                                            double val = Double.parseDouble(dataSnapshot.child("valutazione").getValue().toString());
                                            double newVal = ((val*numval)+num)/(numval+1);

                                            //Aggiorno le stelle visualizzate in schermata
                                            calculateAndSetStars(newVal);
                                            String newValue = String.valueOf(newVal);
                                            ((TextView) findViewById(R.id.textView7)).setText("Valutazione: " + Math.floor(newVal*100)/100);

                                            dataSnapshot.child("valutazione").getRef().setValue(newValue);
                                            dataSnapshot.child("numvalutazioni").getRef().setValue(String.valueOf(numval+1));
                                            break;
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        else{
                            Toast.makeText(itemActivity.this, "Hai già valutato questo libro", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }
}