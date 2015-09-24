package com.serli.overquizz.metier;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class ClosestThingFinder {

    private static final double earthRadius = 6371000;

    public static LinkedList<QuizzDataDistanceWrapper> retrieveClosest(double latitude, double longitude, List<QuizzData> fullData) {
        LinkedList<QuizzDataDistanceWrapper> result = new LinkedList<>();

        for(QuizzData data : fullData) {
            float distance = distFrom(latitude, longitude, data.getLatitude(), data.getLongitude());

            QuizzDataDistanceWrapper newQddw = new QuizzDataDistanceWrapper(data, distance);


            boolean inserted = false;
            ListIterator<QuizzDataDistanceWrapper> it = result.listIterator(0);
            while(it.hasNext())
            {
                if(distance < it.next().getDistance()) {
                    it.previous();
                    it.add(newQddw);
                    inserted = true;
                    break;
                }
            }

            it.add(newQddw);
        }

        return result;
    }


    public static float distFrom(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }
}

