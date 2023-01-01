/*
 * Copyright (c) 2010-2022 William Bittle  http://www.dyn4j.org/
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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Convex;

/**
 * Custom Body class to add drawing functionality.
 * @author William Bittle
 * @version 3.2.1
 * @since 3.0.0
 */
public class SimulationBody extends Body {
	/** The color of the object */
	protected Color color;
	
	/**
	 * Default constructor.
	 */
	public SimulationBody() {
		this.color = Graphics2DRenderer.getRandomColor();
	}
	
	/**
	 * Constructor.
	 * @param color a set color
	 */
	public SimulationBody(Color color) {
		this.color = color;
	}

	/**
	 * Draws the body.
	 * <p>
	 * Only coded for polygons and circles.
	 * @param g the graphics object to render to
	 * @param scale the scaling factor
	 */
	public void render(Graphics2D g, double scale) {
		this.render(g, scale, this.color);
	}
	
	/**
	 * Draws the body.
	 * <p>
	 * Only coded for polygons and circles.
	 * @param g the graphics object to render to
	 * @param scale the scaling factor
	 * @param color the color to render the body
	 */
	public void render(Graphics2D g, double scale, Color color) {
		// point radius
		final int pr = 4;
		
		// save the original transform
		AffineTransform ot = g.getTransform();
		
		// transform the coordinate system from world coordinates to local coordinates
		AffineTransform lt = new AffineTransform();
		lt.translate(this.transform.getTranslationX() * scale, this.transform.getTranslationY() * scale);
		lt.rotate(this.transform.getRotationAngle());
		
		// apply the transform
		g.transform(lt);
		
		// loop over all the body fixtures for this body
		for (BodyFixture fixture : this.fixtures) {
			this.renderFixture(g, scale, fixture, color);
		}
		
		// draw a center point
		Ellipse2D.Double ce = new Ellipse2D.Double(
				this.getLocalCenter().x * scale - pr * 0.5,
				this.getLocalCenter().y * scale - pr * 0.5,
				pr,
				pr);
		g.setColor(Color.WHITE);
		g.fill(ce);
		g.setColor(Color.DARK_GRAY);
		g.draw(ce);
		
		// set the original transform
		g.setTransform(ot);
	}
	
	/**
	 * Renders the given fixture.
	 * @param g the graphics object to render to
	 * @param scale the scaling factor
	 * @param fixture the fixture to render
	 * @param color the color to render the fixture
	 */
	protected void renderFixture(Graphics2D g, double scale, BodyFixture fixture, Color color) {
		// get the shape on the fixture
		Convex convex = fixture.getShape();
		
		// brighten the color if asleep
		if (this.isAtRest()) {
			color = color.brighter();
		}
		
		// render the fixture
		Graphics2DRenderer.render(g, convex, scale, color);
	}
	
	/**
	 * Returns this body's color.
	 * @return Color
	 */
	public Color getColor() {
		return this.color;
	}
	
	/**
	 * Sets the body's color
	 * @param color the color
	 */
	public void setColor(Color color) {
		this.color = color;
	}
}