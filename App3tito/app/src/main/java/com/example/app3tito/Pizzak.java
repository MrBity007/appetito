package com.example.app3tito;



public class Pizzak {
    private String name;
    private String ar;
    private float csillag;
    private int kep;

    public Pizzak(String name, String ar, float csillag, int kep) {
        this.name = name;
        this.ar = ar;
        this.csillag = csillag;
        this.kep = kep;
    }

    public Pizzak(){}

    public String getName() {
        return name;
    }

    public String getAr() {
        return ar;
    }

    public float getCsillag() {
        return csillag;
    }

    public int getKep() {
        return kep;
    }
}
