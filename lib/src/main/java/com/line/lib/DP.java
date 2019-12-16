package com.line.lib;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenliu on 2019-12-16.
 */
public class DP {

    public static List<Point> dpData(List<Point> data, float threshold) {
        List<Point> importantPints;
        if (data.size() > 2) {
            final List<Point> originPoints = new ArrayList<>(data);
            importantPints = new ArrayList<>();
            compress(originPoints, threshold, originPoints.get(0), originPoints.get(originPoints.size() - 1));
            for (Point p : originPoints) {
                if (p.isImportant()) {
                    importantPints.add(p);
                }
            }
        } else {
            importantPints = data;
        }
        return importantPints;
    }

    /**
     * Ax + By + C = 0
     *
     * @param points
     * @param threshold
     * @param from
     * @param to
     */
    private static void compress(List<Point> points, float threshold, Point from, Point to) {
        boolean switchvalue = false;
        /**
         * 由起始点和终止点构成直线方程一般式的系数
         */
        double fromLat = from.x;
        double fromLng = from.y;
        double toLat = to.x;
        double toLng = to.y;
        double A = (fromLat - toLat)
                / Math.sqrt(Math.pow((fromLat - toLat), 2)
                + Math.pow((fromLng - toLng), 2));
        /**
         * 由起始点和终止点构成直线方程一般式的系数
         */
        double B = (toLng - fromLng)
                / Math.sqrt(Math.pow((fromLat - toLat), 2)
                + Math.pow((fromLng - toLng), 2));
        /**
         * 由起始点和终止点构成直线方程一般式的系数
         */
        double C = (fromLng * toLat - toLng * fromLat)
                / Math.sqrt(Math.pow((fromLat - toLat), 2)
                + Math.pow((fromLng - toLng), 2));

        double d = 0;
        double dmax = 0;
        int m = points.indexOf(from);
        int n = points.indexOf(to);
        if (n == m + 1)
            return;
        Point middle = null;
        List<Double> distance = new ArrayList<>();
        for (int i = m + 1; i < n; i++) {
            double blng = (points.get(i).y);
            double blat = (points.get(i).x);
            d = Math.abs(A * (blng) + B * (blat) + C) / Math.sqrt(Math.pow(A, 2) + Math.pow(B, 2));
            distance.add(d);
        }
        dmax = distance.get(0);
        for (int j = 1; j < distance.size(); j++) {
            if (distance.get(j) > dmax)
                dmax = distance.get(j);
        }

        switchvalue = dmax > threshold;

        if (!switchvalue) {
            //删除Points(m,n)内的坐标
            for (int i = m + 1; i < n; i++) {
                points.get(i).setImportant(false);
            }
        } else {
            for (int i = m + 1; i < n; i++) {
                double blng = (points.get(i).y);
                double blat = (points.get(i).x);
                if ((Math.abs(A * (blng) + B
                        * (blat) + C)
                        / Math.sqrt(Math.pow(A, 2) + Math.pow(B, 2)) == dmax))
                    middle = points.get(i);
            }
            compress(points, threshold, from, middle);
            compress(points, threshold, middle, to);
        }
    }
}
