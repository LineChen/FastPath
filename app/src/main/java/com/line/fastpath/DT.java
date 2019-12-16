package com.line.fastpath;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenliu on 2019-12-16.
 */
public class DT {
    /**
     * 存储采样点数据链表
     */
    private static List<Point> points = new ArrayList<>();
    /**
     * 控制数据经度压缩的极差
     */
    private static final double D = 0.002;

    /**
     * 对矢量曲线进行压缩
     * Ax + By + C = 0
     *
     * @param from 曲线的起始点
     * @param to   曲线的终止点
     */
    private static void compress(Point from, Point to) {
        /**
         * 压缩算法的开关
         */
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

        switchvalue = dmax > D;

        if (!switchvalue) {
            //删除Points(m,n)内的坐标
            for (int i = m + 1; i < n; i++) {
                points.get(i).setIndex(-1);
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
            compress(from, middle);
            compress(middle, to);
        }
    }

    public static List<Point> douglasData(List<Point> source) {
        points = source;
        compress(points.get(0), points.get(points.size() - 1));
        List<Point> list = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            if (p.getIndex() > -1) {
                list.add(p);
            }
        }
        return list;
    }


    public static class Point {

        public float x;
        public float y;
        private int index;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }
}
