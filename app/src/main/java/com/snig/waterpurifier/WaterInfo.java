package com.snig.waterpurifier;

public class WaterInfo {
    public int[] tds_1;
    public int[] tds_2;
    public double[] pH;
    public int[] water_usage;

    public WaterInfo() {
        tds_1 = new int[100];
        tds_2 = new int[100];
        water_usage = new int[7];
        pH = new double[100];
        for (int i = 0; i < 100; i++){
            tds_1[i] = 0;
            tds_2[i] = 0;
            pH[i] = 0.0;
        }
        for (int i = 0; i < 7; i++) water_usage[i] = 0;
    }

}
