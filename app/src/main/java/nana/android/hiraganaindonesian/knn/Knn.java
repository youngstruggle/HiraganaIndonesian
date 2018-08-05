package nana.android.hiraganaindonesian.knn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Knn {

    // the data
    static double[][] instances = {{0.35, 0.91, 0.86, 0.42, 0.71},
            {0.21, 0.12, 0.76, 0.22, 0.92},
            {0.41, 0.58, 0.73, 0.21, 0.09},
            {0.71, 0.34, 0.55, 0.19, 0.80},
            {0.79, 0.45, 0.79, 0.21, 0.44},
            {0.61, 0.37, 0.34, 0.81, 0.42},
            {0.78, 0.12, 0.31, 0.83, 0.87},
            {0.52, 0.23, 0.73, 0.45, 0.78},
            {0.53, 0.17, 0.63, 0.29, 0.72},};

    public static void main(String args[]) {

        //Algoritma Knn

        /* 1) Menentukan Nilai K */
        int k = 1;// # of neighbours

        // list to save city data
        List<City> cityList = new ArrayList<City>();
        // list to save distance result
        List<Result> resultList = new ArrayList<Result>();
        // add city data to cityList
        cityList.add(new City(instances[0], "London"));
        cityList.add(new City(instances[1], "Leeds"));
        cityList.add(new City(instances[2], "Arsenal"));
        cityList.add(new City(instances[3], "London"));
        cityList.add(new City(instances[4], "Arsenal"));
        cityList.add(new City(instances[5], "Leeds"));
        cityList.add(new City(instances[6], "London"));
        cityList.add(new City(instances[7], "Arsenal"));
        cityList.add(new City(instances[8], "Leeds"));

        // data about unknown city (Data yang di uji)
        double[] query = {0.65, 0.78, 0.21, 0.29, 0.58};

        /* 2) Menghitung nilai distance untuk data yang di uji dengan data training */
        for (City city : cityList) {
            double dist = 0.0;
            for (int j = 0; j < city.cityAttributes.length; j++) {
                dist = dist + Math.pow(city.cityAttributes[j] - query[j], 2);
                //System.out.println("Nama" + city.cityAttributes[j]+" ");
            }
            /* 3) Menghitung akar */
            double distance = Math.sqrt(dist);
            resultList.add(new Result(distance, city.cityName));
            //System.out.println(distance);
            //System.out.println("Output "+city.cityName);
        }//end of For


        //System.out.println(resultList);
        Collections.sort(resultList, new DistanceComparator());
        String[] ss = new String[k];
        for (int x = 0; x < k; x++) {
            System.out.println(resultList.get(x).cityName + "\t" + "\t" + resultList.get(x).distance);

            // get classes of k nearest instances (city names) from the list
            // into an array
            ss[x] = resultList.get(x).cityName;
        }

    }// end main

    // simple class to model instances (features + class)
    static class City {
        double[] cityAttributes;
        String cityName;

        public City(double[] cityAttributes, String cityName) {
            this.cityName = cityName;
            this.cityAttributes = cityAttributes;
        }
    }

    // simple class to model results (distance + class)
    static class Result {
        double distance;
        String cityName;

        public Result(double distance, String cityName) {
            this.cityName = cityName;
            this.distance = distance;
        }
    }

    // simple comparator class used to compare results via distances
    static class DistanceComparator implements Comparator<Result> {
        @Override
        public int compare(Result a, Result b) {
            return a.distance < b.distance ? -1 : a.distance == b.distance ? 0 : 1;
        }
    }

    /*
     * if a.distance < b.distance {
     * -1 :
     * } else {
     * a.distance == b.distance ?
     *
     */
}