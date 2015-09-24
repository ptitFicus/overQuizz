package com.serli.overquizz.metier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ferrilata on 24/09/15.
 */
public class Session implements Serializable{
    private List<QuizzDataDistanceWrapper> datas;
    private int correctAnswers;
    private List<QuizzData> answeredQuestions;

    public Session() {
        answeredQuestions = new ArrayList<>();
        datas = new LinkedList<>();
    }

    public List<QuizzDataDistanceWrapper> getDatas() {
        return datas;
    }

    public void setDatas(List<QuizzDataDistanceWrapper> datas) {
        this.datas = datas;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void addGoodAnswer() {
        correctAnswers++;
    }

    public int getHowManyAnsweredQuestions() {
        return answeredQuestions.size();
    }

    public void addAnsweredQuestion(QuizzData data) {
        answeredQuestions.add(data);
    }
}
