/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package turtle;

import java.util.List;
import java.util.ArrayList;

public class TurtleSoup {

    /**
     * Draw a square.
     * 
     * @param turtle the turtle context
     * @param sideLength length of each side
     */
    public static void drawSquare(Turtle turtle, int sideLength) {
        for (int i = 0; i<4; i++) {
            turtle.forward(sideLength);
            turtle.turn(90);
        }
    }

    /**
     * Determine inside angles of a regular polygon.
     * 
     * There is a simple formula for calculating the inside angles of a polygon;
     * you should derive it and use it here.
     * 
     * @param sides number of sides, where sides must be > 2
     * @return angle in degrees, where 0 <= angle < 360
     */
    public static double calculateRegularPolygonAngle(int sides) {
        return -360.0/(double) sides + 180.0;
    }

    /**
     * Determine number of sides given the size of interior angles of a regular polygon.
     * 
     * There is a simple formula for this; you should derive it and use it here.
     * Make sure you *properly round* the answer before you return it (see java.lang.Math).
     * HINT: it is easier if you think about the exterior angles.
     * 
     * @param angle size of interior angles in degrees, where 0 < angle < 180
     * @return the integer number of sides
     */
    public static int calculatePolygonSidesFromAngle(double angle) {
        return (int) Math.round(-360.0 / (angle - 180.0));
    }

    /**
     * Given the number of sides, draw a regular polygon.
     * 
     * (0,0) is the lower-left corner of the polygon; use only right-hand turns to draw.
     * 
     * @param turtle the turtle context
     * @param sides number of sides of the polygon to draw
     * @param sideLength length of each side
     */
    public static void drawRegularPolygon(Turtle turtle, int sides, int sideLength) {
        // the angle which the turtle turns is the outside angle of the polygon, since we
        // make only right hand turns, which is calculated by: outsideAngle = 180 degrees - insideAngle
        double outsideAngle = 180 - calculateRegularPolygonAngle(sides);
        System.out.println(outsideAngle);
        for (int side = 0; side < sides; side++) {
            turtle.forward(sideLength);
            turtle.turn(outsideAngle);
        }
    }

    /**
     * Given the current direction, current location, and a target location, calculate the heading
     * towards the target point.
     * 
     * The return value is the angle input to turn() that would point the turtle in the direction of
     * the target point (targetX,targetY), given that the turtle is already at the point
     * (currentX,currentY) and is facing at angle currentHeading. The angle must be expressed in
     * degrees, where 0 <= angle < 360. 
     *
     * HINT: look at http://en.wikipedia.org/wiki/Atan2 and Java's math libraries
     * 
     * @param currentHeading current direction as clockwise from north
     * @param currentX current location x-coordinate
     * @param currentY current location y-coordinate
     * @param targetX target point x-coordinate
     * @param targetY target point y-coordinate
     * @return adjustment to heading (right turn amount) to get to target point,
     *         must be 0 <= angle < 360
     */
    public static double calculateHeadingToPoint(double currentHeading, int currentX, int currentY,
                                                 int targetX, int targetY) {
        int dx = targetX - currentX;
        int dy = targetY - currentY;
        double absoluteAngle = Math.toDegrees(Math.atan2(dx,dy));
        // for a % b the % operator gives the rest after dividing a by b. This thus gives a value
        // between -b and +b. To get a value between 0 and b we need to calculate (a % b) + b) % b
        // (see also https://stackoverflow.com/a/4412200)
        double relativeAngle = ((absoluteAngle - currentHeading) % 360.0 + 360) % 360.0;
        return relativeAngle;
    }

    /**
     * Calculate the distance between the current position and the target position.
     *
     * @param currentX current location x-coordinate
     * @param currentY current location y-coordinate
     * @param targetX target point x-coordinate
     * @param targetY target point y-coordinate
     * @return the Euclidean distance between the two points
     */
    public  static double calculateDistanceToPoint(int currentX, int currentY, int targetX, int targetY) {
        int dx = targetX - currentX;
        int dy = targetY - currentY;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Given a sequence of points, calculate the heading adjustments needed to get from each point
     * to the next.
     * 
     * Assumes that the turtle starts at the first point given, facing up (i.e. 0 degrees).
     * For each subsequent point, assumes that the turtle is still facing in the direction it was
     * facing when it moved to the previous point.
     * You should use calculateHeadingToPoint() to implement this function.
     * 
     * @param xCoords list of x-coordinates (must be same length as yCoords)
     * @param yCoords list of y-coordinates (must be same length as xCoords)
     * @return list of heading adjustments between points, of size 0 if (# of points) == 0,
     *         otherwise of size (# of points) - 1
     */
    public static List<Double> calculateHeadings(List<Integer> xCoords, List<Integer> yCoords) {
        List<Double> headings = new ArrayList<>();
        if (xCoords.size() == 0){
            return headings;
        }
        int curX = 0;
        int curY = 0;
        double curHeading = 0.0;
        double nextHeading;
        for (int i = 1; i < xCoords.size(); i ++) {
            int nextX = xCoords.get(i);
            int nextY = yCoords.get(i);

            nextHeading = calculateHeadingToPoint(curHeading, curX, curY, nextX, nextY);
            headings.add(nextHeading);
            System.out.println("from ("+curX+","+curY+") to ("+nextX+","+nextY+") has heading:"+nextHeading);
            curX = nextX;
            curY = nextY;
            curHeading += nextHeading %360;
        }
        return headings;
    }

    /**
     * Given a sequence of points, calculate the distances between them.
     * Assumes that the turtle starts at the first point given.
     *
     * @param xCoords list of x-coordinates (must be same length as yCoords)
     * @param yCoords list of y-coordinates (must be same length as xCoords)
     * @return list of the distances between points, of size 0 if (# of points) == 0,
     *         otherwise of size (# of points) - 1
     */
    public static List<Integer> calculateDistances(List<Integer> xCoords, List<Integer> yCoords) {
        List<Integer> distances = new ArrayList<>();
        if (xCoords.size() == 0){
            return distances;
        }
        int curX = 0;
        int curY = 0;
        int distance;
        for (int i = 1; i < xCoords.size(); i ++) {
            int nextX = xCoords.get(i);
            int nextY = yCoords.get(i);
            distance = (int) calculateDistanceToPoint(curX, curY, nextX, nextY);
            distances.add(distance);
            curX = nextX;
            curY = nextY;
        }
        return distances;
    }



    /**
     * Draw your personal, custom art.
     * 
     * Many interesting images can be drawn using the simple implementation of a turtle.  For this
     * function, draw something interesting; the complexity can be as little or as much as you want.
     * 
     * @param turtle the turtle context
     */
    public static void drawPersonalArt(Turtle turtle) {
        List<Integer> xpoints = new ArrayList<>();
        List<Integer> ypoints = new ArrayList<>();
        xpoints.add(0);
        ypoints.add(0);

        xpoints.add(50);
        ypoints.add(0);

        xpoints.add(50);
        ypoints.add(0);

        xpoints.add(0);
        ypoints.add(50);

        xpoints.add(0);
        ypoints.add(50);

        xpoints.add(-50);
        ypoints.add(-50);

        xpoints.add(-100);
        ypoints.add(-100);

        xpoints.add(-30);
        ypoints.add(-60);

        xpoints.add(0);
        ypoints.add(0);

        List<Double> headings = calculateHeadings(xpoints,ypoints);
        List<Integer> distances = calculateDistances(xpoints,ypoints);
        for (int i = 0; i<xpoints.size() - 1; i++){
            double curHeading = headings.get(i);
            int curDistance = distances.get(i);
            turtle.turn(curHeading);
            turtle.forward(curDistance);
        }

    }

    /**
     * Main method.
     * 
     * This is the method that runs when you run "java TurtleSoup".
     * 
     * @param args unused
     */
    public static void main(String args[]) {
        DrawableTurtle turtle = new DrawableTurtle();

//        drawRegularPolygon(turtle, 5, 40);
        turtle.color(PenColor.RED);
        drawPersonalArt(turtle);
        turtle.draw();
    }

}
