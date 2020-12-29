package com.thirafikaz.mapsintermediate.model;

import java.util.ArrayList;

/**
 * Created by fiqri on 11/01/18.
 */

//TODO 1 : CREATE OBJECT DASAR

public class ResponseRoute {

    ArrayList<Object0> routes;
    // TODO 3 : GETER
    public ArrayList<Object0> getRoutes() {
        return routes;
    }

    public class Object0 {

        ArrayList<Legs> legs;

        public ArrayList<Legs> getLegs() {
            return legs;
        }

        //TODO 4 : OBJECT 0
        OverView overview_polyline ;

        // TODO 5 GETTER OBJECT 0
        public OverView getOverview_polyline() {
            return overview_polyline;
        }

        // TODO 6 : create object overview polyline
        public class OverView {

            // TODO 7 : create string point
            String points;

            // TODO 8 : create string getter point
            public String getPoints(){
                return points;
            }
        }

        public class Legs {
            Distance distance;
            Duration duration;

            public Distance getDistance() {
                return distance;
            }

            public Duration getDuration() {
                return duration;
            }

            public class Distance {
                String text;
                Double value;

                public String getText() {
                    return text;
                }

                public Double getValue() {
                    return value;
                }
            }

            public class Duration {
                String text;
                Double value;

                public String getText() {
                    return text;
                }

                public Double getValue() {
                    return value;
                }
            }
        }
    }
}
