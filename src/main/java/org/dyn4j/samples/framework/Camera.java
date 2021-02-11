/*
 * Copyright (c) 2010-2021 William Bittle  http://www.dyn4j.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of dyn4j nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dyn4j.samples.framework;

import java.awt.Point;

import org.dyn4j.geometry.Vector2;

/**
 * Stores the zoom and panning state of the camera.
 * @author William Bittle
 * @version 4.1.1
 * @since 4.1.1
 */
public class Camera {
	/** The scale (zoom) in pixels per meter */
	public double scale;
	
	/** The pan-x in pixels */
	public double offsetX;
	
	/** The pan-y in pixels */
	public double offsetY;

	/**
	 * Returns World coordinates for the given point given the width/height of the viewport.
	 * @param width the viewport width
	 * @param height the viewport height
	 * @param p the point
	 * @return Vector2
	 */
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
