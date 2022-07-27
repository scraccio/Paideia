package com.example.paideia;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    //ArrayList che conterrà gli elementi da visualizzare
    private ArrayList<Libro> mData;

    //Oggetto che inserisce una View in una Activity dinamicamente
    private LayoutInflater mInflater;

    //Oggetto che catturerà ogni interazione che l'utente ha con un elemento dello schermo
    private ItemClickListener mClickListener;

    //Costruttore della classe Adapter
    //Prende in input un contesto e l'ArrayList contenente i libri da visualizzare
    Adapter(Context context, ArrayList<Libro> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    //Metodo che inserisce una view nell'Activity
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    //Inizializza ogni cella del nostro GridLayout con le informazioni di un oggetto
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.titolo.setText((mData.get(position)).getTitolo());
        holder.prezzo.setText((mData.get(position)).getPrezzo());

        //Decodifica della string image da Base64
        decodeBase64AndSetImage(mData.get(position).getImmagine(), holder.immagine);

        //Inizializzazione delle stelline vuote
        holder.valutazione.setImageResource(R.drawable.starempty);
        holder.valutazione2.setImageResource(R.drawable.starempty);
        holder.valutazione3.setImageResource(R.drawable.starempty);
        holder.valutazione4.setImageResource(R.drawable.starempty);
        holder.valutazione5.setImageResource(R.drawable.starempty);

        double val = Double.parseDouble((mData.get(position)).getValutazione());
        //Arrotondamento della valutazione al "mezzo più vicino" (0.5, 1, 1.5, 2, etc...)
        val = Math.round(val*2)/2.0;
        //Override delle stelline in base alla valutazione
        if(val == 0.5){
            holder.valutazione.setImageResource(R.drawable.starhalf);
        }
        if(val == 1){
            holder.valutazione.setImageResource(R.drawable.starfull);
        }
        if(val == 1.5){
            holder.valutazione.setImageResource(R.drawable.starfull);
            holder.valutazione2.setImageResource(R.drawable.starhalf);
        }
        if(val == 2){
            holder.valutazione.setImageResource(R.drawable.starfull);
            holder.valutazione2.setImageResource(R.drawable.starfull);
        }
        if(val == 2.5){
            holder.valutazione.setImageResource(R.drawable.starfull);
            holder.valutazione2.setImageResource(R.drawable.starfull);
            holder.valutazione3.setImageResource(R.drawable.starhalf);
        }
        if(val == 3){
            holder.valutazione.setImageResource(R.drawable.starfull);
            holder.valutazione2.setImageResource(R.drawable.starfull);
            holder.valutazione3.setImageResource(R.drawable.starfull);
        }
        if(val == 3.5){
            holder.valutazione.setImageResource(R.drawable.starfull);
            holder.valutazione2.setImageResource(R.drawable.starfull);
            holder.valutazione3.setImageResource(R.drawable.starfull);
            holder.valutazione4.setImageResource(R.drawable.starhalf);
        }
        if(val == 4){
            holder.valutazione.setImageResource(R.drawable.starfull);
            holder.valutazione2.setImageResource(R.drawable.starfull);
            holder.valutazione3.setImageResource(R.drawable.starfull);
            holder.valutazione4.setImageResource(R.drawable.starfull);
        }
        if(val == 4.5){
            holder.valutazione.setImageResource(R.drawable.starfull);
            holder.valutazione2.setImageResource(R.drawable.starfull);
            holder.valutazione3.setImageResource(R.drawable.starfull);
            holder.valutazione4.setImageResource(R.drawable.starfull);
            holder.valutazione5.setImageResource(R.drawable.starhalf);
        }
        if(val == 5){
            holder.valutazione.setImageResource(R.drawable.starfull);
            holder.valutazione2.setImageResource(R.drawable.starfull);
            holder.valutazione3.setImageResource(R.drawable.starfull);
            holder.valutazione4.setImageResource(R.drawable.starfull);
            holder.valutazione5.setImageResource(R.drawable.starfull);
        }
    }

    //Metodo che ritorna il numero di celle
    @Override
    public int getItemCount() {
        return mData.size();
    }

    //Metodo che aggiorna la lista dei libri visualizzati in base al filtro applicato dalla SearchView
    public void filterList(ArrayList<Libro> filteredList){
        mData = filteredList;
        notifyDataSetChanged();
    }


    //Oggetto che conserva e ricicla le view appena non sono più visibili su schermo
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView titolo;
        TextView prezzo;
        ImageView immagine;
        ImageView valutazione;
        ImageView valutazione2;
        ImageView valutazione3;
        ImageView valutazione4;
        ImageView valutazione5;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            titolo = itemView.findViewById(R.id.titolo);
            prezzo = itemView.findViewById(R.id.prezzo);
            immagine = itemView.findViewById(R.id.image);
            valutazione = itemView.findViewById(R.id.valutazione);
            valutazione2 = itemView.findViewById(R.id.valutazione2);
            valutazione3 = itemView.findViewById(R.id.valutazione3);
            valutazione4 = itemView.findViewById(R.id.valutazione4);
            valutazione5 = itemView.findViewById(R.id.valutazione5);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    //Metodo getter per restituire un elemento dell'ArrayList dato il suo indice
    Libro getItem(int id) {
        return mData.get(id);
    }

    //Inizializza mClickListener, permette quindi di catturare le azioni su un determinato elemento
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    //Interfaccia che definisce (ma non inizializza) la funzione onItemClick
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    //Metodo che decodifica l'immagine a partire da una stringa in Base64 e la imposta in una ImageView
    private void decodeBase64AndSetImage(String completeImageData, ImageView imageView) {
        InputStream stream = new ByteArrayInputStream(Base64.decode(completeImageData.getBytes(), Base64.DEFAULT));

        Bitmap bitmap = BitmapFactory.decodeStream(stream);

        imageView.setImageBitmap(bitmap);
    }
}