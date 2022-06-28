package org.example.epam.jdbc;

import org.example.epam.jdbc.entity.Line;
import org.example.epam.jdbc.entity.Point;
import org.example.epam.jdbc.service.PointService;
import org.example.epam.jdbc.utils.PointsUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PointsTest {
    private static final PointService pointService = new PointService();
    @Test
    void testPoints() {
        assertTrue(pointService.dropTable());
        assertTrue(pointService.createTable());
        assertTrue(PointsUtils.fillTableRandom(pointService, 20, 1000));
        assertEquals(1000, pointService.count());
        assertTrue(pointService.dropTable());
        assertTrue(pointService.createTable());
        assertTrue(PointsUtils.fillTableRandomBatch(pointService, 20, 1000));
        assertEquals(1000, pointService.count());
    }

    @Test
    void testNearest() {
        System.out.println(PointsUtils.findNearest(pointService, new Point(0,19)));
    }

    @Test
    void testFarthest() {
        System.out.println(PointsUtils.findFarthest(pointService, new Point(20,20)));
    }

    @Test
    void testLine() {
        Line line = new Line(2,3, 7);
        Point[] points = PointsUtils.findAllPoints(pointService, line);
        System.out.println("Points :");
        Arrays.stream(points).forEach(System.out::println);
        System.out.println("lie on the line " + line);
    }

}
