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
package org.dyn4j.samples;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.imageio.ImageIO;

import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.joint.DistanceJoint;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.samples.framework.SimulationFrame;
import org.dyn4j.world.listener.BoundsListener;
import org.dyn4j.world.listener.BoundsListenerAdapter;

/**
 * A scene where the user can play basket ball by configuring the trajectory.
 * @author William Bittle
 * @version 4.2.0
 * @since 4.1.1
 */
public class BasketBall extends SimulationFrame {
	/** Generated serial version id */
	private static final long serialVersionUID = 8357585473409415833L;
	
	// images

	/** The basketball image */
	private static final BufferedImage BASKETBALL = getImageSuppressExceptions("/org/dyn4j/samples/resources/Basketball.png");

	/** Helper function to read the images from the class path */
	private static final BufferedImage getImageSuppressExceptions(String pathOnClasspath) {
		try {
			return ImageIO.read(BasketBall.class.getResource(pathOnClasspath));
		} catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * A custom body that uses an image instead.
	 * @author William Bittle
	 * @version 3.2.1
	 * @since 3.2.0
	 */
	private final class ImageBody extends SimulationBody {
		/** The image to use, if required */
		public BufferedImage image;
		
		/* (non-Javadoc)
		 * @see org.dyn4j.samples.SimulationBody#renderFixture(java.awt.Graphics2D, double, org.dyn4j.dynamics.BodyFixture, java.awt.Color)
		 */
		@Override
		protected void renderFixture(Graphics2D g, double scale, BodyFixture fixture, Color color) {
			// do we need to render an image?
			if (this.image != null) {
				// get the shape on the fixture
				Convex convex = fixture.getShape();
				// check the shape type
				if (convex instanceof Circle) {
					// cast the shape to get the radius
					Circle c = (Circle) convex;
					double r = c.getRadius();
					Vector2 cc = c.getCenter();
					int x = (int)Math.ceil((cc.x - r) * scale);
					int y = (int)Math.ceil((cc.y - r) * scale);
					int w = (int)Math.ceil(r * 2 * scale);
						// lets us an image instead
						g.drawImage(BASKETBALL, x, y, w, w, null);
				}
			} else {
				// default rendering
				super.renderFixture(g, scale, fixture, color);
			}
		}
	}
	
	// controls
	
	private final AtomicBoolean left = new AtomicBoolean(false);
	private final AtomicBoolean right = new AtomicBoolean(false);
	private final AtomicBoolean up = new AtomicBoolean(false);
	private final AtomicBoolean down = new AtomicBoolean(false);
	
	private final AtomicBoolean plus = new AtomicBoolean(false);
	private final AtomicBoolean minus = new AtomicBoolean(false);
	
	private final AtomicBoolean shoot = new AtomicBoolean(false);
	
	/**
	 * Custom key adapter to listen for key events.
	 * @author William Bittle
	 * @version 3.2.1
	 * @since 3.2.0
	 */
	private class CustomKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_LEFT:
					left.set(true);
					break;
				case KeyEvent.VK_RIGHT:
					right.set(true);
					break;
				case KeyEvent.VK_UP:
					up.set(true);
					break;
				case KeyEvent.VK_DOWN:
					down.set(true);
					break;
				case KeyEvent.VK_ADD:
				case KeyEvent.VK_PLUS:
					plus.set(true);
					break;
				case KeyEvent.VK_MINUS:
				case KeyEvent.VK_SUBTRACT:
					minus.set(true);
					break;
			}
			
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_LEFT:
					left.set(false);
					break;
				case KeyEvent.VK_RIGHT:
					right.set(false);
					break;
				case KeyEvent.VK_UP:
					up.set(false);
					break;
				case KeyEvent.VK_DOWN:
					down.set(false);
					break;
				case KeyEvent.VK_PLUS:
				case KeyEvent.VK_ADD:
					plus.set(false);
					break;
				case KeyEvent.VK_MINUS:
				case KeyEvent.VK_SUBTRACT:
					minus.set(false);
					break;
				case KeyEvent.VK_S:
					shoot.set(true);
					break;
			}
		}
	}
	
	private static final long ALL = Long.MAX_VALUE;
	private static final long BALL = 1;
	private static final long RIM = 2;
	private static final long OTHER = 4;
	
	private static final CategoryFilter ballFilter = new CategoryFilter(BALL, ALL ^ RIM);
	private static final CategoryFilter rimFilter = new CategoryFilter(RIM, ALL ^ BALL);
	private static final CategoryFilter allFilter = new CategoryFilter(OTHER, ALL);
	
	private final Vector2 start = new Vector2(-10.0, -3.0);
	private final Vector2 direction = new Vector2(Math.toRadians(45));
	private double power = 15.0;
	
	private final List<SimulationBody> toRemove = new ArrayList<SimulationBody>();
	
	/**
	 * Default constructor.
	 */
	public BasketBall() {
		super("BasketBall", 24.0);
		
		this.setOffsetY(-100);
		
		KeyListener listener = new CustomKeyListener();
		this.addKeyListener(listener);
		this.canvas.addKeyListener(listener);
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 */
	protected void initializeWorld() {
		AxisAlignedBounds bounds = new AxisAlignedBounds(30, 30);
		bounds.translate(0.0, 8.0);
		this.world.setBounds(bounds);
		
		// create the floor
		SimulationBody floor = new SimulationBody(new Color(222, 184, 135));
		BodyFixture bf = floor.addFixture(Geometry.createRectangle(24, 0.5));
		bf.setFilter(allFilter);
		floor.setMass(MassType.INFINITE);
		// move the floor down a bit
		floor.translate(0.0, -4.0);
		this.world.addBody(floor);
		
		// create the pole
		SimulationBody pole = new SimulationBody(new Color(50, 50, 50));
		bf = pole.addFixture(Geometry.createRectangle(0.2, 8));
		bf.setFilter(allFilter);
		bf = pole.addFixture(Geometry.createRectangle(0.2, 1.0));
		bf.getShape().rotate(Math.toRadians(30));
		bf.getShape().translate(-0.2, 4.2);
		bf.setFilter(allFilter);
		pole.setMass(MassType.INFINITE);
		pole.translate(11.0, 0.0);
		this.world.addBody(pole);
		
		// create the backboard
		SimulationBody backboard = new SimulationBody(new Color(50, 50, 50));
		bf = backboard.addFixture(Geometry.createRectangle(0.2, 2.5));
		bf.setFilter(allFilter);
		backboard.setMass(MassType.INFINITE);
		backboard.translate(10.5, 5.25);
		this.world.addBody(backboard);
		
		// create the rim
		SimulationBody rim = new SimulationBody(new Color(255, 69, 0));
		bf = rim.addFixture(Geometry.createRectangle(2.0, 0.2));
		bf.setFilter(rimFilter);
		bf = rim.addFixture(Geometry.createRectangle(0.2, 0.2));
		bf.setFilter(allFilter);
		bf.getShape().translate(-1.0, 0.0);
		rim.setMass(MassType.INFINITE);
		rim.translate(9.5, 4.0);
		this.world.addBody(rim);
		
		// create the net from joints and bodies
		SimulationBody prevL = rim;
		SimulationBody prevR = rim;
		double y = 3.6;
		for (int i = 0; i < 3; i++) {
			SimulationBody ropeL = new SimulationBody(Color.WHITE);
			bf = ropeL.addFixture(Geometry.createRectangle(0.1, 0.4));
			bf.setFilter(allFilter);
			ropeL.setMass(MassType.NORMAL);
			ropeL.translate(8.8, y);
			ropeL.setLinearDamping(0.8);
			this.world.addBody(ropeL);
			
			SimulationBody ropeR = new SimulationBody(Color.WHITE);
			bf = ropeR.addFixture(Geometry.createRectangle(0.1, 0.4));
			bf.setFilter(allFilter);
			ropeR.setMass(MassType.NORMAL);
			ropeR.translate(10.2, y);
			ropeR.setLinearDamping(0.8);
			this.world.addBody(ropeR);
			
			// links
			
			RevoluteJoint<SimulationBody> rjl = new RevoluteJoint<SimulationBody>(prevL, ropeL, new Vector2(8.8, y + 0.2));
			this.world.addJoint(rjl);
			
			RevoluteJoint<SimulationBody> rjr = new RevoluteJoint<SimulationBody>(prevR, ropeR, new Vector2(10.2, y + 0.2));
			this.world.addJoint(rjr);
			
			// string
			
			DistanceJoint<SimulationBody> dj = new DistanceJoint<SimulationBody>(ropeL, ropeR, new Vector2(8.8, y - 0.2), new Vector2(10.2, y - 0.2));
			dj.setRestDistance(dj.getRestDistance() - 0.2);
			dj.setDampingRatio(0.8);
			dj.setFrequency(8.0);
			this.world.addJoint(dj);
			
			prevL = ropeL;
			prevR = ropeR;
			
			y-=0.5;
		}
		
		// listen for basketballs leaving the bounds
		BoundsListener<SimulationBody, BodyFixture> bl = new BoundsListenerAdapter<SimulationBody, BodyFixture>() {
			@Override
			public void outside(SimulationBody body) {
				toRemove.add(body);
			}
		};
		this.world.addBoundsListener(bl);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#render(java.awt.Graphics2D, double)
	 */
	@Override
	protected void render(Graphics2D g, double elapsedTime) {
		super.render(g, elapsedTime);
		
		// draw the trajectory based on the current
		// start position, direction, and power
		final double scale = this.getScale();
		
		double x = start.x * scale;
		double y = start.y * scale;

		double vx = direction.x * power;
		double vy = direction.y * power;
		
		double gy = this.world.getGravity().y;
		
		double t = this.world.getSettings().getStepFrequency();
		g.setColor(new Color(150, 150, 150, 100));
		for(int i = 0; i < 1000; i++) {
			g.fill(new Ellipse2D.Double(x - 2, y - 2, 4, 4));
			
			// integrate to get new velocity
			vy += gy * t;
			
			// integrate to get new position
			x += vx * t * scale;
			y += vy * t * scale;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#handleEvents()
	 */
	@Override
	protected void handleEvents() {
		super.handleEvents();
		
		if (this.left.get()) {
			this.start.x -= 0.05;
		}
		if (this.right.get()) {
			this.start.x += 0.05;
		}
		if (this.up.get()) {
			this.direction.rotate(0.01);
		}
		if (this.down.get()) {
			this.direction.rotate(-0.01);
		}
		if (this.plus.get()) {
			this.power += 0.05;
		}
		if (this.minus.get()) {
			this.power -= 0.05;
		}
		
		if (this.shoot.get()) {
			this.shoot.set(false);
			
			// create a circle
			ImageBody circle = new ImageBody();
			circle.image = BASKETBALL;
			BodyFixture bf = circle.addFixture(Geometry.createCircle(0.5), 1.0, 0.2, 0.5);
			bf.setFilter(ballFilter);
			circle.setMass(MassType.NORMAL);
			circle.translate(start);
			circle.setLinearVelocity(this.direction.x * this.power, this.direction.y * this.power);
			this.world.addBody(circle);
		}
		
		for (SimulationBody b : this.toRemove) {
			this.world.removeBody(b);
		}
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		BasketBall simulation = new BasketBall();
		simulation.run();
	}
}
