package org.dyn4j.samples.framework;

import java.awt.Point;

import org.dyn4j.geometry.Vector2;

public class Camera {
	public double scale;
	public double offsetX;
	public double offsetY;

	public final Vector2 toWorldCoordinates(double width, double height, Point p) {
 		if (p != null) {
 			Vector2 v = new Vector2();
 			// convert the screen space point to world space
 			v.x =  (p.getX() - width * 0.5 - this.offsetX) / this.scale;
 			v.y = -(p.getY() - height * 0.5 + this.offsetY) / this.scale;
 			return v;
 		}
 		
 		return null;
	}
}
