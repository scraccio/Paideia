package com.example.paideia;

public class Libro {
    public String titolo;
    public String autore;
    public String prezzo;
    public String immagine;
    public String descrizione;
    public String numvalutazioni;
    public String valutazione;

    public Libro(String titolo, String autore, String prezzo, String immagine, String descrizione, String numvalutazioni, String valutazione){
        this.titolo = titolo;
        this.autore = autore;
        this.prezzo = prezzo;
        this.immagine = immagine;
        this.descrizione = descrizione;
        this.numvalutazioni = numvalutazioni;
        this.valutazione = valutazione;

    }

    public Libro(){}

    public String getTitolo(){
        return titolo;
    }

    public String getAutore(){
        return autore;
    }

    public String getPrezzo(){
        return prezzo;
    }

    public String getImmagine(){
        return immagine;
    }

    public String getDescrizione(){
        return descrizione;
    }

    public String getNumvalutazioni(){
        return numvalutazioni;
    }

    public String getValutazione(){
        return valutazione;
    }
}
