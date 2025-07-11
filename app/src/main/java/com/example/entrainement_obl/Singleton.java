package com.example.entrainement_obl;

public class Singleton {
    private Singleton(){

    }

    private static final Singleton instance = new Singleton();

    public static Singleton getInstance(){
        return instance;
    }

    public String name = "";

}
