package com.serli.overquizz.metier;

import java.io.Serializable;

public class QuizzDataDistanceWrapper implements Serializable{
    private QuizzData data;
    private float distance;

    public QuizzData getData() {
        return data;
    }

    public float getDistance() {
        return distance;
    }

    public QuizzDataDistanceWrapper(QuizzData data, float distance) {
        this.data = data;
        this.distance = distance;
    }
}
