package controller;

import java.lang.Comparable;

class SeabedData implements Comparable {

    public double dist, depth;

    public SeabedData(double dist, double depth) {
        this.dist = dist;
        this.depth = depth;
    }

    public int compareTo(Object other) {
        SeabedData otherData = (SeabedData)other;
        if(dist == otherData.dist)
            return 0;
        return dist < otherData.dist ? -1 : 1;
    }
}
