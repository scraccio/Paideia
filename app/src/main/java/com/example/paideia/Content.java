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
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Content extends AppCompatActivity implements Adapter.ItemClickListener {

    //Oggetto che riceverà gli oggetti "libro" e li disporrà sulla RecyclerView sottoforma di griglia
    Adapter adapter;

    //Gestori dati Firebase
    DatabaseReference ref;
    FirebaseDatabase rootNode;

    //Lista dei libri da visualizzare su schermo
    ArrayList<Libro> listaLibri;

    //Email dell'account loggato
    String email;

    //Gestore delle autorizzazioni di Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Avvio della schermata del catalogo
        setContentView(R.layout.activity_content);

        //Inizializzazione dell'email loggata tramite il dato inserito nell'intent
        email = getIntent().getStringExtra("EMAIL");

        //Inizializzazione dei gestori dati di Firebase
        rootNode = FirebaseDatabase.getInstance("https://paideia-fa077-default-rtdb.europe-west1.firebasedatabase.app");
        ref = rootNode.getReference("database/libri");

        //Inizializzazione della lista di libri
        listaLibri = new ArrayList<Libro>();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int x = 0;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Libro libro = dataSnapshot.getValue(Libro.class);
                    x = 0;
                    for(int i=0; i<listaLibri.size(); i++){
                        if(libro.getTitolo().equals(listaLibri.get(i).getTitolo())){
                            x = 1;
                        }
                    }
                    if(x == 0) {
                        listaLibri.add(libro);
                    }

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
        adapter = new Adapter(this, listaLibri);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        //Inizializzazione autorizzazione Firebase
        mAuth = FirebaseAuth.getInstance();

        //Controllo sull'account superuser
        //Se l'email corrisponde a quella del superuser, viene aggiunto un bottone per inserire un nuovo libro
        if(mAuth.getCurrentUser().getEmail().equals("admin@gmail.com")){
            LayoutInflater inflater = (LayoutInflater) findViewById(R.id.activity_content).getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewGroup parent = (ViewGroup)findViewById(R.id.activity_content);
            inflater.inflate(R.layout.plus_button, parent);

            View button = findViewById(R.id.plus_button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Quando il bottone viene premuto, la funzione richiamata porterà alla schermata di inserimento libro
                    toItemAdd(new View(getApplicationContext()));
                }
            });
        }

        //Definizione del comportamento assegnato alla SearchView
        SearchView searchView = (SearchView) findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //Appena viene scritto qualcosa nella SearchView, viene applicato un filtro all'adapter
                filter(s);
                return false;
            }
        });

    }

    //Metodo che filtra gli item della RecyclerView in base alla stringa inserita nella SearchView
    //Verranno visualizzati soltanto gli item che contengono la stringa data come argomento
    //Il metodo non è case sensitive
    private void filter(String s){
        ArrayList<Libro> filteredList = new ArrayList<>();
        for(Libro libro : listaLibri){
            if(libro.getTitolo().toLowerCase().contains(s.toLowerCase())){
                filteredList.add(libro);
            }
        }
        adapter.filterList(filteredList);
    }

    //Metodo che definisce il comportamento in uscita dell'Activity
    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    //Transizione da Content a Carrello
    public void toBasket(View v) {
        Intent intent = new Intent(this, Carrello.class);
        //Inserimento dell'email loggata nell'intent
        intent.putExtra("EMAIL", email);
        startActivity(intent);
        //Definizione delle animazioni in entrata e in uscita
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        finish();
    }

    //Transizione da Content a Account
    public void toUser(View v) {
        Intent intent = new Intent(this, Account.class);
        startActivity(intent);
        //Definizione delle animazioni in entrata e in uscita
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        finish();
    }


    //Transizione da Content a Content, non fa nulla perché ci si trova già lì
    public void gridClick(View v){
        Toast.makeText(this, "Sei già nel catalogo", Toast.LENGTH_LONG).show();
    }

    //Definizione del comportamento al click di un elemento della RecyclerView
    public void onItemClick(View v, int position) {
        toItem(adapter.getItem(position));
    }


    //Transizione da Content a itemActivity, dato un oggetto Libro
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

    //Transizione da Content a aggiungiLibro
    public void toItemAdd(View v){
        Intent intent = new Intent(getApplicationContext(), aggiungiLibro.class);
        //Inserimento dell'email loggata nell'intent
        intent.putExtra("EMAIL", email);
        startActivity(intent);
        //Definizione delle animazioni in entrata e in uscita
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

}