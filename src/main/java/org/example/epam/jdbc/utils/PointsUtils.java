package org.example.epam.jdbc.utils;

import org.example.epam.jdbc.entity.Line;
import org.example.epam.jdbc.entity.Point;
import org.example.epam.jdbc.service.PointService;

import java.util.ArrayList;

public class PointsUtils {
    public static boolean fillTableRandom(PointService pointService, int maxValue, int totalPoints) {
        try {
            for (int i = 0; i < totalPoints; i++) {
                pointService.insert(new Point((int) (Math.random() * 2 * maxValue - maxValue), (int) (Math.random() * 2 * maxValue - maxValue)));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean fillTableRandomBatch(PointService pointService, int maxValue, int totalPoints) {
        return pointService.fillRandom(maxValue, totalPoints);
    }

    public static Point findNearest(PointService pointService, Point to) {
        Point[] points = pointService.selectAll();
        Point nearest = null;
        double minDistance = Double.MAX_VALUE;
        for (Point from : points) {
            double distance = from.distanceTo(to);
            if (Double.compare(minDistance, distance) > 0) {
                nearest = from;
                minDistance = distance;
            }
        }
        System.out.println("Minimum distance is " + minDistance);
        return nearest;
    }

    public static Point findFarthest(PointService pointService, Point to) {
        Point[] points = pointService.selectAll();
        Point farthest = null;
        double maxDistance = Double.MIN_VALUE;
        for (Point from : points) {
            double distance = from.distanceTo(to);
            if (Double.compare(maxDistance, distance) < 0) {
                farthest = from;
                maxDistance = distance;
            }
        }
        System.out.println("Maximum distance is " + maxDistance);
        return farthest;
    }

    public static Point[] findAllPoints(PointService pointService, Line line) {
        ArrayList<Point> result = new ArrayList<>();
        Point[] points = pointService.selectAll();
        for (Point point : points) {
            if (line.contains(point)) {
                result.add(point);
            }
        }
        return result.toArray(Point[]::new);
    }
}
