package com.example.paideia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.UUID;

public class aggiungiLibro extends AppCompatActivity {

    //Codice di autorizzazione per accedere alla galleria
    private final int GALLERY_REQ_CODE = 1000;

    //Gestori dati Firebase
    DatabaseReference ref;
    FirebaseDatabase rootNode;

    //Imageview che conterrà la foto del libro da aggiungere
    ImageView image;

    //Indirizzo dell'immagine da inserire
    Uri imageURI;

    //Email dell'account loggato
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggiungi_libro);

        //Assegnazione dell'immagine placeholder alla view
        image = (ImageView) findViewById(R.id.imageView6);

        //Assegnazione dell'email loggata
        email = getIntent().getStringExtra("EMAIL");

        //Inizializzazione dei gestori dati di Firebase
        rootNode = FirebaseDatabase.getInstance("https://paideia-fa077-default-rtdb.europe-west1.firebasedatabase.app");
        ref = rootNode.getReference("database/libri");
    }


    //Metodo che permette di scegliere una foto dalla galleria
    public void openGallery(View v){
        Intent iGallery = new Intent(Intent.ACTION_PICK);
        iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(iGallery, GALLERY_REQ_CODE);
    }


    //Metodo che assegna l'immagine scelta alla view
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == GALLERY_REQ_CODE){
            image.setImageURI(data.getData());
            imageURI = data.getData();
        }
    }

    //Transizione da aggiungiLibro a Carrello
    public void toBasket(View v) {
        Intent intent = new Intent(this, Carrello.class);
        intent.putExtra("EMAIL", email);
        startActivity(intent);
        //Definizione delle animazioni in entrata e in uscita
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        finish();
    }

    //Transizione da aggiungiLibro a Account
    public void toAccount(View v) {
        Intent intent = new Intent(this, Account.class);
        intent.putExtra("EMAIL", email);
        startActivity(intent);
        //Definizione delle animazioni in entrata e in uscita
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        finish();
    }

    //Transizione da aggiungiLibro a Content
    public void gridClick(View v){
        Intent intent = new Intent(this, Content.class);
        intent.putExtra("EMAIL", email);
        startActivity(intent);

        //Definizione delle animazioni in entrata e in uscita
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    //Creazione e invio del nuovo libro al database remoto Firebase
    public void addItem(View v){
        String titolo = ((EditText) findViewById(R.id.titolo)).getText().toString();
        String autore = ((EditText) findViewById(R.id.autore)).getText().toString();
        String prezzo = ((EditText) findViewById(R.id.prezzo)).getText().toString().replace(".",",") + "€";
        String descrizione = ((EditText) findViewById(R.id.descrizione)).getText().toString();
        String immagine = getBase64String();

        ref.child(titolo).setValue(new Libro(titolo, autore, prezzo, immagine, descrizione, "0", "0"));
        Toast.makeText(this, titolo + " è stato aggiunto al catalogo", Toast.LENGTH_SHORT).show();
        gridClick(v);
    }

    //Encoding della foto inserita in Stringa Base64
    private String getBase64String() {
        //Creazione di un bitmap a partire dalla foto caricata
        BitmapDrawable drawable = (BitmapDrawable) ((ImageView) findViewById(R.id.imageView6)).getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        //Trasformazione in bytestream e codifica in stringa
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

}