package com.example.paideia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class Account extends AppCompatActivity{

    //Dichiarazione della TextView che conterrà l'email loggata
    TextView email;

    //Gestore dell'autenticazione di Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Avvio della schermata del catalogo
        setContentView(R.layout.activity_account);

        //Inizializzazione autorizzazione Firebase
        mAuth = FirebaseAuth.getInstance();

        //Assegnazione della mail loggata alla TextView
        email = findViewById(R.id.email);
        email.setText(mAuth.getCurrentUser().getEmail());

    }

    //Metodo che definisce il comportamento di uscita dall'activity
    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    //Transizione da Account a Carrello
    public void toBasket(View v) {
        Intent intent = new Intent(this, Carrello.class);
        intent.putExtra("EMAIL", mAuth.getCurrentUser().getEmail());
        startActivity(intent);

        //Definizione delle animazioni in entrata e in uscita
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    //Transizione da Account a Account, visualizza un toast
    public void toUser(View v) {
        Toast.makeText(this, "Sei già nella schermata dell'account", Toast.LENGTH_LONG).show();
    }

    //Transizione da Account a Catalogo
    public void gridClick(View v){
        Intent intent = new Intent(this, Content.class);
        intent.putExtra("EMAIL", mAuth.getCurrentUser().getEmail());
        startActivity(intent);

        //Definizione delle animazioni in entrata e in uscita
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    //Procedura di logout e transizione verso Login
    public void logout(View v){

        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(this, Login.class);
        startActivity(intent);

        //Definizione delle animazioni in entrata e in uscita
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

}