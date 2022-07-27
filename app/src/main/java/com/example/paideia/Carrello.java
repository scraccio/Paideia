package com.example.paideia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Carrello extends AppCompatActivity implements Adapter.ItemClickListener {

    //Oggetto che riceverà gli oggetti "libro" e li disporrà sulla RecyclerView sottoforma di griglia
    Adapter adapter;

    //Gestori dati Firebase
    DatabaseReference ref;
    FirebaseDatabase rootNode;

    //Lista dei libri da visualizzare su schermo
    ArrayList<Libro> listaCarrello;

    //Email dell'account loggato
    String email;

    //Somma dei prezzi dei libri nel carrello
    double subtotale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Avvio della schermata del catalogo
        setContentView(R.layout.activity_basket);

        //Inizializzazione dell'email loggata tramite il dato inserito nell'intent
        email = getIntent().getStringExtra("EMAIL");

        //Inizializzazione della lista di libri
        listaCarrello = new ArrayList<Libro>();

        //Inizializzazione del subtotale a 0
        subtotale = 0;

        //Variabile che definisce le cifre significative del subtotale
        DecimalFormat df = new DecimalFormat("#.##");

        //Inizializzazione delle view con i valori default
        ((TextView)findViewById(R.id.totArticoli)).setText("0 articoli nel carrello");
        ((TextView)findViewById(R.id.subtotale)).setText("Subtotale: 0.00€");

        //Inizializzazione dei gestori dati di Firebase
        rootNode = FirebaseDatabase.getInstance("https://paideia-fa077-default-rtdb.europe-west1.firebasedatabase.app");
        ref = rootNode.getReference("database/utenti/" + email.replace("@","").replace(".","") + "/carrello");

        //Inserimento dei libri dal database remoto alla lista di libri da visualizzare
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Libro libro = dataSnapshot.getValue(Libro.class);
                    listaCarrello.add(libro);
                    if(listaCarrello.size() == 1){
                        ((TextView)findViewById(R.id.totArticoli)).setText(listaCarrello.size() + " articolo nel carrello");
                    }
                    else{
                        ((TextView)findViewById(R.id.totArticoli)).setText(listaCarrello.size() + " articoli nel carrello");
                    }

                    subtotale += Double.parseDouble(libro.getPrezzo().replace("€","").replace(",","."));
                    ((TextView)findViewById(R.id.subtotale)).setText("Subtotale: " + df.format(subtotale) + "€");
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Setup di RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        int numberOfColumns = 3;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        //Inizializzazione dell'oggetto adapter, viene fornita la lista dei libri
        adapter = new Adapter(this, listaCarrello);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    //Metodo che definisce il comportamento in uscita dell'Activity
    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    //Transizione da Carrello a Carrello, non fa nulla perché ci si trova già lì
    public void toBasket(View v) {
        Toast.makeText(this, "Sei già nel carrello", Toast.LENGTH_LONG).show();
    }

    //Transizione da Carrello a Account
    public void toAccount(View v) {
        Intent intent = new Intent(this, Account.class);
        //Inserimento dell'email loggata nell'intent
        intent.putExtra("EMAIL", email);
        startActivity(intent);
        //Definizione delle animazioni in entrata e in uscita
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        finish();
    }

    //Transizione da Carrello a Content
    public void gridClick(View v){
        Intent intent = new Intent(this, Content.class);
        //Inserimento dell'email loggata nell'intent
        intent.putExtra("EMAIL", email);
        startActivity(intent);
        //Definizione delle animazioni in entrata e in uscita
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    //Definizione del comportamento al click di un elemento della RecyclerView
    @Override
    public void onItemClick(View v, int position) {
        toItem(adapter.getItem(position));
    }

    //Transizione da Carrello a itemActivity, dato un oggetto Libro
    private void toItem(Libro libro) {
        Intent intent = new Intent(this, itemActivity.class);

        //Inserimento dell'email loggata e del libro selezionato nell'intent
        String[] data = {libro.getTitolo(), libro.getAutore(), libro.getPrezzo(), libro.getImmagine(), libro.getDescrizione(), libro.getNumvalutazioni(), libro.getValutazione()};
        intent.putExtra("OBJ", data);
        intent.putExtra("EMAIL", email);
        startActivity(intent);

        //Definizione delle animazioni in entrata e in uscita
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

}