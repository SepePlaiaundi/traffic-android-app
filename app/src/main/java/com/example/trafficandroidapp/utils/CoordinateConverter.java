package com.example.trafficandroidapp.utils;

import org.locationtech.proj4j.*;

public class CoordinateConverter {
    public static double[] utmToLatLon(double x, double y) {
        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CRSFactory csFactory = new CRSFactory();

        // Asumiendo que la API usa EPSG:3857 o UTM zona 30N (común en Euskadi)
        // Si el resultado sale en el océano, prueba con "EPSG:25830"
        CoordinateReferenceSystem fromCRS = csFactory.createFromName("EPSG:25830");
        CoordinateReferenceSystem toCRS = csFactory.createFromName("EPSG:4326");

        CoordinateTransform trans = ctFactory.createTransform(fromCRS, toCRS);
        ProjCoordinate result = new ProjCoordinate();
        trans.transform(new ProjCoordinate(x, y), result);

        return new double[]{result.y, result.x}; // [Lat, Lon]
    }
}