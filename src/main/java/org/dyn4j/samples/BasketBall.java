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
package org.dyn4j.samples;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.contact.Contact;
import org.dyn4j.dynamics.joint.DistanceJoint;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.samples.framework.Camera;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.samples.framework.SimulationFrame;
import org.dyn4j.samples.framework.input.BooleanStateKeyboardInputHandler;
import org.dyn4j.samples.framework.input.Key;
import org.dyn4j.samples.framework.input.ToggleStateKeyboardInputHandler;
import org.dyn4j.world.ContactCollisionData;
import org.dyn4j.world.NarrowphaseCollisionData;
import org.dyn4j.world.listener.BoundsListener;
import org.dyn4j.world.listener.BoundsListenerAdapter;
import org.dyn4j.world.listener.CollisionListener;
import org.dyn4j.world.listener.CollisionListenerAdapter;
import org.dyn4j.world.listener.ContactListener;
import org.dyn4j.world.listener.ContactListenerAdapter;

/**
 * A scene where the user can play basket ball by configuring the trajectory.
 * @author William Bittle
 * @version 5.0.0
 * @since 5.0.0
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
		private final BufferedImage image;
		
		public ImageBody(BufferedImage image) {
			this.image = image;
		}
		
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
						g.drawImage(this.image, x, y, w, w, null);
				}
			} else {
				// default rendering
				super.renderFixture(g, scale, fixture, color);
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
	
	// input control
	
	private final BooleanStateKeyboardInputHandler up;
	private final BooleanStateKeyboardInputHandler down;
	private final BooleanStateKeyboardInputHandler angleUp;
	private final BooleanStateKeyboardInputHandler angleDown;
	private final BooleanStateKeyboardInputHandler left;
	private final BooleanStateKeyboardInputHandler right;
	
	private final BooleanStateKeyboardInputHandler plus;
	private final BooleanStateKeyboardInputHandler minus;
	
	private final BooleanStateKeyboardInputHandler shoot;
	private final ToggleStateKeyboardInputHandler path;
	
	private final Vector2 start = new Vector2();
	private final Vector2 direction = new Vector2();
	private double power = 0.0;
	private int score = 0;
	
	private SimulationBody rim;
	
	// cache of bodies to remove
	private final List<SimulationBody> toRemove = new ArrayList<SimulationBody>();
	
	private final Object SCORE_BEGIN_IDENTIFIER = new Object();
	private final Object SCORE_COMPLETE_IDENTIFIER = new Object();
	
	private final class BallUserData {
		public final Vector2 start = new Vector2();
		public boolean enteredScoreBegin = false;
		public boolean enteredScoreComplete = false;
		public boolean scored = false;
	}
	
	/**
	 * Default constructor.
	 */
	public BasketBall() {
		super("BasketBall");
		
		this.up = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_UP);
		this.down = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_DOWN);
		this.left = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_LEFT);
		this.right = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_RIGHT);
		this.angleUp = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_PAGE_UP);
		this.angleDown = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_PAGE_DOWN);
		this.plus = new BooleanStateKeyboardInputHandler(this.canvas, new Key(KeyEvent.VK_PLUS), new Key(KeyEvent.VK_ADD), new Key(KeyEvent.VK_EQUALS, KeyEvent.SHIFT_DOWN_MASK));
		this.minus = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_MINUS, KeyEvent.VK_SUBTRACT);
		this.shoot = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_S);
		this.path = new ToggleStateKeyboardInputHandler(this.canvas, KeyEvent.VK_P);
		
		this.up.install();
		this.down.install();
		this.left.install();
		this.right.install();
		this.angleDown.install();
		this.angleUp.install();
		this.plus.install();
		this.minus.install();
		this.shoot.install();
		this.path.install();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#printControls()
	 */
	@Override
	protected void printControls() {
		super.printControls();
		
		printControl("Move Up", "Up", "Use the up key to move the shoot position up");
		printControl("Move Down", "Down", "Use the down key to move the shoot position down");
		printControl("Move Left", "Left", "Use the left key to move the shoot position left");
		printControl("Move Right", "Right", "Use the right key to move the shoot position right");
		printControl("Angle Up", "Pg Up", "Use the page up key to increase the shoot angle");
		printControl("Angle Down", "Pg Down", "Use the page down key to decrease the shoot angle");
		printControl("Increase Power", "+", "Use the + key to increase the shoot power");
		printControl("Decrease Power", "-", "Use the - key to decrease the shoot power");
		printControl("Shoot", "s", "Use the s key to shoot a basketball");
		printControl("Show Path", "p", "Use the p key to show the travel path");
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 */
	protected void initializeWorld() {
		this.start.set(-10.0, -3.0);
		this.direction.set(new Vector2(Math.toRadians(45)));
		this.power = 15.0;
		
		AxisAlignedBounds bounds = new AxisAlignedBounds(50, 30);
		bounds.translate(-5.0, 8.0);
		this.world.setBounds(bounds);
		
		// create the floor
		SimulationBody court = new SimulationBody(new Color(222, 184, 135));
		BodyFixture bf = court.addFixture(Geometry.createRectangle(40, 0.5));
		bf.setFilter(allFilter);
		court.setMass(MassType.INFINITE);
		// move the floor down a bit
		court.translate(-5.0, -4.0);
		this.world.addBody(court);
		
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
		
		// save for rendering later 
		// NOTE: in the real world you'd implement rendering a smarter
		// way by grouping objects into layers
		this.rim = rim;
		
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
			dj.setSpringEnabled(true);
			dj.setSpringDamperEnabled(true);
			dj.setSpringDampingRatio(0.8);
			dj.setSpringFrequency(8.0);
			this.world.addJoint(dj);
			
			prevL = ropeL;
			prevR = ropeR;
			
			y-=0.5;
		}
		
		// for scoring setup some sensor bodies
		SimulationBody sensorScoreBegin = new SimulationBody(new Color(255, 0, 0, 0));
		sensorScoreBegin.setUserData(SCORE_BEGIN_IDENTIFIER);
		bf = sensorScoreBegin.addFixture(Geometry.createRectangle(2.0, 2.0));
		bf.setSensor(true);
		sensorScoreBegin.setMass(MassType.INFINITE);
		sensorScoreBegin.translate(9.5, 5.0);
		this.world.addBody(sensorScoreBegin);
		
		SimulationBody sensorScoreAdd = new SimulationBody(new Color(0, 255, 0, 0));
		sensorScoreAdd.setUserData(SCORE_COMPLETE_IDENTIFIER);
		bf = sensorScoreAdd.addFixture(Geometry.createRectangle(1.7, 1.25));
		bf.setSensor(true);
		sensorScoreAdd.setMass(MassType.INFINITE);
		sensorScoreAdd.translate(9.5, 3.0);
		this.world.addBody(sensorScoreAdd);
		
		// listen for basketballs leaving the bounds
		BoundsListener<SimulationBody, BodyFixture> bl = new BoundsListenerAdapter<SimulationBody, BodyFixture>() {
			@Override
			public void outside(SimulationBody body) {
				toRemove.add(body);
			}
		};
		this.world.addBoundsListener(bl);
		
		// use a CollisionListener to detect when the body is in the scoring zones
		CollisionListener<SimulationBody, BodyFixture> cl = new CollisionListenerAdapter<SimulationBody, BodyFixture>() {
			@Override
			public boolean collision(NarrowphaseCollisionData<SimulationBody, BodyFixture> collision) {
				SimulationBody b1 = collision.getBody1();
				SimulationBody b2 = collision.getBody2();
				
				if (isBall(b1) && isScoreBegin(b2)) {
					BallUserData bud = (BallUserData)b1.getUserData();
					if (!bud.scored) {
						bud.enteredScoreBegin = true;
						bud.enteredScoreComplete = false;
					}
				} else if (isBall(b1) && isScoreComplete(b2)) {
					BallUserData bud = (BallUserData)b1.getUserData();
					if (!bud.scored && bud.enteredScoreBegin) {
						bud.enteredScoreComplete = true;
					} else {
						bud.enteredScoreBegin = false;
					}
				}
				return super.collision(collision);
			}
		};
		this.world.addCollisionListener(cl);
		
		// use a ContactListener to detect when the body leaves the scoring zone
		ContactListener<SimulationBody> ccl = new ContactListenerAdapter<SimulationBody>() {
			@Override
			public void end(ContactCollisionData<SimulationBody> collision, Contact contact) {
				SimulationBody b1 = collision.getBody1();
				SimulationBody b2 = collision.getBody2();
				
				if (isBall(b1) && isScoreComplete(b2)) {
					BallUserData bud = (BallUserData)b1.getUserData();
					// 1. if the ball hasn't been scored yet
					// 2. if the ball entered the score begin region
					// 3. if the ball entered the score complete region
					// 4. if it's now exiting the score complete region
					// then count it as a score
					if (!bud.scored && bud.enteredScoreBegin && bud.enteredScoreComplete) {
						bud.scored = true;
						
						// was it a two pointer or three pointer?
						if (bud.start.x < -14) 
							score += 3;
						else
							score += 2;
					} else {
						bud.enteredScoreBegin = false;
						bud.enteredScoreComplete = false;
					}
				}
				super.end(collision, contact);
			}
		};
		this.world.addContactListener(ccl);
	}
	
	private boolean isBall(SimulationBody body) {
		return body.getUserData() instanceof BallUserData;
	}
	
	private boolean isScoreBegin(SimulationBody body) {
		return body.getUserData() == SCORE_BEGIN_IDENTIFIER;
	}
	
	private boolean isScoreComplete(SimulationBody body) {
		return body.getUserData() == SCORE_COMPLETE_IDENTIFIER;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#reset()
	 */
	@Override
	public void reset() {
		super.reset();
		this.score = 0;
		// other stuff gets reset in initializeWorld
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#initializeCamera(org.dyn4j.samples.framework.Camera)
	 */
	@Override
	protected void initializeCamera(Camera camera) {
		super.initializeCamera(camera);
		camera.scale = 24.0;
		camera.offsetX = 0.0;
		camera.offsetY = -100.0;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#initializeSettings()
	 */
	@Override
	protected void initializeSettings() {
		super.initializeSettings();
		this.start.set(-10.0, -3.0);
		this.direction.set(new Vector2(Math.toRadians(45)));
		this.power = 15.0;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#render(java.awt.Graphics2D, double)
	 */
	@Override
	protected void render(Graphics2D g, double elapsedTime) {
		super.render(g, elapsedTime);
		
		AffineTransform tx = g.getTransform();
		g.scale(1, -1);
		g.translate(-this.getWidth() * 0.5 - this.getCameraOffsetX(), -this.getHeight() * 0.5 + this.getCameraOffsetY());
		
		// render the score
		g.setColor(Color.BLACK);
		g.setFont(new Font("SansSerif", Font.PLAIN, 20));
		g.drawString("Score: " + this.score, 20, 45);
		
		// render the power and angle
		g.setColor(Color.BLACK);
		g.setFont(new Font("SansSerif", Font.PLAIN, 12));
		g.drawString(String.format("Power: %1$.2f", this.power), 20, 70);
		g.drawString(String.format("Angle: %1$.2f", Math.toDegrees(this.direction.getDirection())), 20, 85);
		g.drawString(String.format("Position: (%1$.2f, %2$.2f)", this.start.x, this.start.y), 20, 100);
		
		g.setTransform(tx);
		
		// draw the trajectory based on the current
		// start position, direction, and power
		final double scale = this.getCameraScale();
		
		double x = start.x * scale;
		double y = start.y * scale;

		double vx = direction.x * power;
		double vy = direction.y * power;
		
		double gy = this.world.getGravity().y;
		double t = this.world.getSettings().getStepFrequency();
		
		// draw the helper angle, power, position vector
		g.setColor(Color.RED);
		g.draw(new Line2D.Double(x, y, vx * scale * t * 20 + x, vy * scale * t * 20 + y));
		g.fill(new Ellipse2D.Double(x - 2, y - 2, 4, 4));
		
		if (this.path.isActive()) {
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
		
		// render the rim again because we want it to 
		// look like the bballs are going through the hoop
		this.render(g, elapsedTime, this.rim);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#handleEvents()
	 */
	@Override
	protected void handleEvents() {
		super.handleEvents();
		
		if (this.left.isActive()) {
			this.start.x -= 0.05;
		}
		if (this.right.isActive()) {
			this.start.x += 0.05;
			if (this.start.x >= 7.5) {
				this.start.x = 7.5;
			}
		}
		if (this.down.isActive()) {
			this.start.y -= 0.05;
		}
		if (this.up.isActive()) {
			this.start.y += 0.05;
			if (this.start.y >= 3.0) {
				this.start.y = 3.0;
			}
		}
		if (this.angleUp.isActive()) {
			this.direction.rotate(0.01);
		}
		if (this.angleDown.isActive()) {
			this.direction.rotate(-0.01);
		}
		if (this.plus.isActive()) {
			this.power += 0.05;
		}
		if (this.minus.isActive()) {
			this.power -= 0.05;
		}
		
		if (this.shoot.isActiveButNotHandled()) {
			this.shoot.setHasBeenHandled(true);
			
			// create a circle
			BallUserData data = new BallUserData();
			data.start.x = start.x;
			data.start.y = start.y;
			
			ImageBody circle = new ImageBody(BASKETBALL);
			circle.setUserData(data);
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
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#onPickingStart(org.dyn4j.samples.framework.SimulationBody)
	 */
	@Override
	protected void onBodyMousePickingStart(SimulationBody body) {
		super.onBodyMousePickingStart(body);
		
		// if the user picks up a ball using the mouse
		// disable scoring for that ball
		if (isBall(body)) {
			BallUserData bud = (BallUserData)body.getUserData();
			bud.scored = true;
			bud.enteredScoreBegin = false;
			bud.enteredScoreComplete = false;
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
