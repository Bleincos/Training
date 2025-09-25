package com.example.entrainement_obl;

import java.util.ArrayList;

public class Singleton {
    private static final Singleton instance = new Singleton();
    private Singleton(){

    }
    public static Singleton getInstance(){
        return instance;
    }

    public ArrayList<EntrainementType> entrainements = new ArrayList<>();
    public Boolean state = Boolean.FALSE; // False to create, True to edit
    public int i; //position in the arraylist

}
