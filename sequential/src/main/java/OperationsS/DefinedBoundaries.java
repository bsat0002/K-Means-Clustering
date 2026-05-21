package OperationsS;

import java.util.List;
import java.util.ArrayList;
/* I added some country boundaries manually since the Italy json file has limited number of points*/
public class DefinedBoundaries {
    public static List<Boundaries> getBoundaries() {
        List<Boundaries> squareish_boundary = new ArrayList<>();

        squareish_boundary.add(new Boundaries(46.3, 9.5, 49.0, 17.2)); // Austria
        squareish_boundary.add(new Boundaries(51.0, 23.5, 56.0, 30.5)); // Belgium
        squareish_boundary.add(new Boundaries(45.8, 5.9, 47.8, 10.5)); // Switzerland
        squareish_boundary.add(new Boundaries(41.5, 19.0, 46.5, 23.0)); // Serbia
        squareish_boundary.add(new Boundaries(45.5, 13.3, 46.8, 16.7)); // Slovenia
        squareish_boundary.add(new Boundaries(62.1, 6.4,59.5 , 11.2)); // Norway
        squareish_boundary.add(new Boundaries(57.9, 11.9, 56.3, 15.8)); // Sweden
        squareish_boundary.add(new Boundaries(51.0, 23.5, 56.0, 30.5)); // Belarus
        squareish_boundary.add(new Boundaries(45.8, 16.0, 48.5, 22.6));// Hungary
        squareish_boundary.add(new Boundaries(56.1, 21.5, 54.6, 25.2)); // Lithuania
        squareish_boundary.add(new Boundaries(53.7,15.4 , 49.7, 22.0)); // Poland
        squareish_boundary.add(new Boundaries(53.3, 7.7, 47.8, 11.6)); // Germany
        




        return squareish_boundary;
    }
}
