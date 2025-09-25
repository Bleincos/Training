package com.example.entrainement_obl;

public class EntrainementType {
    String name;
    int repetitions;
    int minutes;
    int secondes;
    int recuperation;

    public EntrainementType(String name, int repetitions, int minutes, int secondes, int recuperation){
        this.name=name;
        this.repetitions=repetitions;
        this.minutes=minutes;
        this.secondes=secondes;
        this.recuperation=recuperation;
    }
    /**
     * Functions explicits
     * @param name
     */
    public void setName(String name){
        this.name=name;
    }
    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }
    public void setSecondes(int secondes) {
        this.secondes = secondes;
    }
    public void setRecuperation(int recuperation) {
        this.recuperation = recuperation;
    }
    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }
    public String getName() {
        return name;
    }
    public int getRepetitions() {
        return repetitions;
    }
    public int getMinutes() {
        return minutes;
    }
    public int getSecondes() {
        return secondes;
    }
    public int getRecuperation() {
        return recuperation;
    }

    public void setAll(String name, int repetitions, int minutes, int secondes, int recuperation){
        setName(name);
        setRepetitions(repetitions);
        setMinutes(minutes);
        setSecondes(secondes);
        setRecuperation(recuperation);
    }
}
