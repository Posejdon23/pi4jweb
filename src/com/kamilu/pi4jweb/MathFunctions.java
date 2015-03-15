package com.kamilu.pi4jweb;

public class MathFunctions {

	public static double[] getCoord(double alfa, int r, int a, int b) {
		double tangAlfa = Math.tan(Math.toRadians(alfa));
		double x1 = r / Math.sqrt(Math.pow(tangAlfa, 2) + 1);
		double y1 = tangAlfa * x1;
		double x2 = a - x1;
		double y2 = b - y1;
		return new double[]{x1 + a, y1 + b, x2, y2};
	}
	public static void main(String[] args) {
		double[] res = getCoord(45, 150, 0, 0);
		System.out.println("x1 = " + res[0] + ", y1 = " + res[1] + "\nx2 = " + res[2] + ", y2 = " + res[3]);
	}
}
