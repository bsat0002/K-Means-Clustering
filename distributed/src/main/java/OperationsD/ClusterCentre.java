package OperationsD;

import java.io.Serializable;
/* The reason for this class and not using Clusters is to send only the centre of the clusters between ranks and no
* need for all assigned records */
public class ClusterCentre implements Serializable {

    private double centre_latitude;
    private double centre_longitude;

    public ClusterCentre(double centre_latitude, double centre_longitude) {
        this.centre_latitude = centre_latitude;
        this.centre_longitude = centre_longitude;
    }

    public double getCentre_latitude() {
        return centre_latitude;
    }

    public double getCentre_longitude() {
        return centre_longitude;
    }

}