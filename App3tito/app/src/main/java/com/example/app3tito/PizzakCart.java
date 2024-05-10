package com.example.app3tito;

public class PizzakCart {
    private String PizzaId;
    private String name;
    private String ar;
    private float csillag;
    private int kep;

    public PizzakCart(String PizzaId, String name, String ar, float csillag, int kep) {
        this.PizzaId=PizzaId;
        this.name = name;
        this.ar = ar;
        this.csillag = csillag;
        this.kep = kep;
    }

    public PizzakCart(){}


    public String getPizzaId() {
        return PizzaId;
    }

    public void setPizzaId(String PizzaId) {
        PizzaId = PizzaId;
    }

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

