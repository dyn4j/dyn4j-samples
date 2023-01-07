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
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import org.dyn4j.collision.Filter;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.samples.framework.Camera;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.samples.framework.SimulationFrame;
import org.dyn4j.samples.framework.input.BooleanStateKeyboardInputHandler;
import org.dyn4j.samples.framework.input.Key;
import org.dyn4j.world.World;

/**
 * A simple scene of two billiard balls colliding with one another
 * and a wall.
 * <p>
 * Primarily used to illustrate the computation of the mass and size
 * of the balls.  See the {@link Billiards#initializeWorld()} method.
 * @author William Bittle
 * @version 5.0.0
 * @since 3.2.0
 */
public final class Billiards extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = -8518496343422955267L;

	private static final Object BALL_IDENTIFIER = new Object();
	private SimulationBody cueBall;
	private SimulationBody cueStick;
	private Vector2 stickLocation = new Vector2();
	
	private double angle;
	private double power;
	
	private final BooleanStateKeyboardInputHandler left;
	private final BooleanStateKeyboardInputHandler right;
	
	private final BooleanStateKeyboardInputHandler plus;
	private final BooleanStateKeyboardInputHandler minus;
	
	private final BooleanStateKeyboardInputHandler shoot;
	
	/**
	 * Default constructor.
	 */
	public Billiards() {
		super("Billiards");
		
		this.left = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_LEFT);
		this.right = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_RIGHT);
		this.plus = new BooleanStateKeyboardInputHandler(this.canvas, new Key(KeyEvent.VK_PLUS), new Key(KeyEvent.VK_ADD), new Key(KeyEvent.VK_EQUALS, KeyEvent.SHIFT_DOWN_MASK));
		this.minus = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_MINUS, KeyEvent.VK_SUBTRACT);
		this.shoot = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_S);

		this.left.install();
		this.right.install();
		this.plus.install();
		this.minus.install();
		this.shoot.install();	
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.SimulationFrame#initializeWorld()
	 */
	@Override
	protected void initializeWorld() {
		// no gravity on a top-down view of a billiards game
		this.world.setGravity(World.ZERO_GRAVITY);
		
		final double edgeDepth = 0.29 / 2.0;
		final double tableWidth = 1.83;
		final double tableHeight = 1.12;
		
		final double halfTableWidth = tableWidth / 2.0;
		final double halfTableHeight = tableHeight / 2.0;
		final double halfEdgeDepth = edgeDepth / 2.0;
		
		// 2.25 in diameter = 0.028575 m radius
		final double ballRadius = 0.028575;
		
		// 0.126 oz/in^3 = 217.97925 kg/m^3
		final double ballDensity = 217.97925;
		
		final double ballFriction = 0.08;
		final double ballRestitution = 0.9;
		
		// I wouldn't do this in practice - I'm being lazy and using a body
		// to render a green bottom to the billiards table.  Instead, you should
		// just render the green bottom and not use a body.
		SimulationBody bottom = new SimulationBody(new Color(60, 164, 114));
		BodyFixture bf = bottom.addFixture(Geometry.createRectangle(tableWidth, tableHeight), 1.0, 0.0, 0.0);
		bf.setFilter(new Filter() {
			@Override
			public boolean isAllowed(Filter filter) {
				return false;
			}
		});
		bottom.setMass(MassType.INFINITE);
		world.addBody(bottom);
		
		SimulationBody wallRight = new SimulationBody(new Color(150, 75, 0));
		BodyFixture fixture = wallRight.addFixture(Geometry.createRectangle(edgeDepth, tableHeight), 1.0, 0.4, 0.3);
		fixture.setRestitutionVelocity(0.0);
		wallRight.translate(halfTableWidth - halfEdgeDepth, 0);
		wallRight.setMass(MassType.INFINITE);
		wallRight.setAtRestDetectionEnabled(false);
		world.addBody(wallRight);
		
		SimulationBody wallLeft = new SimulationBody(new Color(150, 75, 0));
		fixture = wallLeft.addFixture(Geometry.createRectangle(edgeDepth, tableHeight), 1.0, 0.4, 0.3);
		fixture.setRestitutionVelocity(0.0);
		wallLeft.translate(-halfTableWidth + halfEdgeDepth, 0);
		wallLeft.setMass(MassType.INFINITE);
		wallLeft.setAtRestDetectionEnabled(false);
		world.addBody(wallLeft);

		SimulationBody wallTop = new SimulationBody(new Color(150, 75, 0));
		fixture = wallTop.addFixture(Geometry.createRectangle(tableWidth, edgeDepth), 1.0, 0.4, 0.3);
		fixture.setRestitutionVelocity(0.0);
		wallTop.translate(0, halfTableHeight - halfEdgeDepth);
		wallTop.setMass(MassType.INFINITE);
		wallTop.setAtRestDetectionEnabled(false);
		world.addBody(wallTop);
		
		SimulationBody wallBottom = new SimulationBody(new Color(150, 75, 0));
		fixture = wallBottom.addFixture(Geometry.createRectangle(tableWidth, edgeDepth), 1.0, 0.4, 0.3);
		fixture.setRestitutionVelocity(0.0);
		wallBottom.translate(0, -halfTableHeight + halfEdgeDepth);
		wallBottom.setMass(MassType.INFINITE);
		wallBottom.setAtRestDetectionEnabled(false);
		world.addBody(wallBottom);
		
		cueBall = new SimulationBody(new Color(255, 255, 255));
		fixture = cueBall.addFixture(Geometry.createCircle(ballRadius), ballDensity, ballFriction, ballRestitution);
		fixture.setRestitutionVelocity(0.001);
		cueBall.setUserData(BALL_IDENTIFIER);
		cueBall.translate(-0.25, 0.0);
		cueBall.setLinearDamping(0.8);
		cueBall.setAngularDamping(0.8);
		cueBall.setMass(MassType.NORMAL);
		cueBall.setBullet(true);
		this.world.addBody(cueBall);
		
		cueStick = new SimulationBody(new Color(180, 140, 50));
		cueStick.addFixture(new Polygon(
				new Vector2(0.0, 0.008),
				new Vector2(-1.0, 0.015),
				new Vector2(-1.0, -0.015),
				new Vector2(0.0, -0.008)));
		cueStick.setMass(MassType.NORMAL);
		stickLocation = new Vector2(-0.25, 0.0);
		
		// billiard colors
		Color[] colors = new Color[] {
			// solid
			new Color(255, 215, 0),	
			new Color(0, 0, 255),
			new Color(255, 0, 0),
			new Color(75, 0, 130),
			new Color(255, 69, 0),
			new Color(34, 139, 34),
			new Color(128, 0, 0),
			new Color(0, 0, 0),
			
			// striped (just do a lighter color)
			new Color(255, 215, 0).darker(),	
			new Color(0, 0, 255).darker(),
			new Color(255, 0, 0).darker(),
			new Color(75, 0, 130).brighter(),
			new Color(255, 69, 0).darker(),
			new Color(34, 139, 34).brighter(),
			new Color(128, 0, 0).brighter(),
			new Color(0, 0, 0).brighter()
		};
		
		final int rackSize = 5;
		final double sx = 0.45;
		final double sy = -ballRadius;
		
		// 5 columns
		int n = 0;
		for (int i = 0; i < rackSize; i++) {
			double x = sx - (ballRadius * 2.0 * (double)i);
			double columnHeight = ballRadius * 2.0 * (rackSize - i); 
			double csy = columnHeight / 2.0;
			// 5 - i rows
			for (int j = 0; j < rackSize - i; j++) {
				double y = sy + csy - (ballRadius * 2.0 * j);
				
				SimulationBody ball = new SimulationBody(colors[n]);
				fixture = ball.addFixture(Geometry.createCircle(ballRadius), ballDensity, ballFriction, ballRestitution);
				fixture.setRestitutionVelocity(0.001);
				ball.setUserData(BALL_IDENTIFIER);
				ball.translate(x, y);
				ball.setLinearDamping(0.8);
				ball.setAngularDamping(0.8);
				ball.setMass(MassType.NORMAL);
				ball.setBullet(true);
				this.world.addBody(ball);
				
				n++;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#initializeCamera(org.dyn4j.samples.framework.Camera)
	 */
	@Override
	protected void initializeCamera(Camera camera) {
		super.initializeCamera(camera);
		camera.scale = 250.0;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#printControls()
	 */
	@Override
	protected void printControls() {
		super.printControls();
		
		printControl("Rotate Left", "Left", "Use the left key to rotate counter-clockwise");
		printControl("Rotate Right", "Right", "Use the right key to rotate clockwise");
		printControl("Increase Power", "+", "Use the + key to increase the shoot power");
		printControl("Decrease Power", "-", "Use the - key to decrease the shoot power");
		printControl("Shoot", "s", "Use the s key to hit the cue ball");
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#render(java.awt.Graphics2D, double)
	 */
	@Override
	protected void render(Graphics2D g, double elapsedTime) {
		super.render(g, elapsedTime);
		
		// check if all balls are at rest, if so, then reset the cue stick position
		if (allBallsAtRest()) {
			this.stickLocation = this.cueBall.getWorldCenter();
		}
		
		Transform tx = new Transform();
		tx.setTranslation(stickLocation.sum(-this.power - 0.05, 0.0));
		tx.rotate(this.angle, stickLocation);
		
		this.cueStick.setTransform(tx);
		this.render(g, elapsedTime, cueStick);
	}
	
	private final boolean allBallsAtRest() {
		final int n = this.world.getBodyCount();
		for (int i = 0; i < n; i++) {
			SimulationBody b = this.world.getBody(i);
			if (b.getUserData() == BALL_IDENTIFIER) {
				if (!b.isAtRest()) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#reset()
	 */
	@Override
	public void reset() {
		super.reset();
		
		this.angle = 0.0;
		this.power = 0.0;
		this.stickLocation.zero();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#handleEvents()
	 */
	@Override
	protected void handleEvents() {
		super.handleEvents();
		
		if (allBallsAtRest()) {
			if (this.left.isActive()) {
				this.angle += 0.005;
			}
			
			if (this.right.isActive()) {
				this.angle -= 0.005;
			}
			
			if (this.plus.isActive()) {
				this.power += 0.005;
			}
			
			if (this.minus.isActive()) {
				this.power -= 0.005;
				if (this.power <= 0.0) {
					this.power = 0.0;
				}
			}
			
			if (this.shoot.isActiveButNotHandled()) {
				this.shoot.setHasBeenHandled(true);

				if (this.power > 0.0) {
					Vector2 v = new Vector2(this.cueStick.getTransform().getRotationAngle());
					v.multiply(this.power * 10.0);
					this.cueBall.setAtRest(false);
					this.cueBall.setLinearVelocity(v);
					this.power = 0.0;
				}
			}
		}
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		Billiards simulation = new Billiards();
		simulation.run();
	}
}
