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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.List;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.joint.FrictionJoint;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Interval;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.samples.framework.Camera;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.samples.framework.SimulationFrame;
import org.dyn4j.samples.framework.input.BooleanStateKeyboardInputHandler;
import org.dyn4j.world.DetectFilter;
import org.dyn4j.world.World;
import org.dyn4j.world.result.RaycastResult;

/**
 * A scene were a player controled tank raycasts against the world.
 * @author William Bittle
 * @version 5.0.1
 * @since 3.0.0
 */
public class Tank extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = 1462952703366297615L;

	private static final Object INDESTRUCTIBLE = new Object();
	
	// controls
	private final BooleanStateKeyboardInputHandler driveForward;
	private final BooleanStateKeyboardInputHandler driveBackward;
	private final BooleanStateKeyboardInputHandler rotateLeft;
	private final BooleanStateKeyboardInputHandler rotateRight;
	private final BooleanStateKeyboardInputHandler rotateTurretLeft;
	private final BooleanStateKeyboardInputHandler rotateTurretRight;
	private final BooleanStateKeyboardInputHandler shoot;
	
	private SimulationBody tank;
	private SimulationBody barrel;
	
	/**
	 * Default constructor.
	 */
	public Tank() {
		super("Tank");
		
		this.driveForward = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_W);
		this.driveBackward = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_S);
		this.rotateLeft = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_A);
		this.rotateRight = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_D);
		this.rotateTurretLeft = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_LEFT);
		this.rotateTurretRight = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_RIGHT);
		this.shoot = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_E);
		
		this.driveForward.install();
		this.driveBackward.install();
		this.rotateLeft.install();
		this.rotateRight.install();
		this.rotateTurretLeft.install();
		this.rotateTurretRight.install();
		this.shoot.install();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#initializeCamera(org.dyn4j.samples.framework.Camera)
	 */
	@Override
	protected void initializeCamera(Camera camera) {
		super.initializeCamera(camera);
		camera.scale = 48.0;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#printControls()
	 */
	@Override
	protected void printControls() {
		super.printControls();
		
		printControl("Move Forward", "w", "Use the w key to move forward");
		printControl("Move Backward", "s", "Use the s key to move backward");
		printControl("Rotate Left", "a", "Use the a key to rotate left");
		printControl("Rotate Right", "d", "Use the d key to rotate right");
		printControl("Barrel Left", "Left", "Use the left key to rotate the barrel left");
		printControl("Barrel Right", "Right", "Use the right key to rotate the barrel right");
		printControl("Shoot", "e", "Use the e key to shoot");
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 * <p>
	 * Basically the same shapes from the Shapes test in
	 * the TestBed.
	 */
	protected void initializeWorld() {
	    this.world.setGravity(World.ZERO_GRAVITY);

	    // Triangle
	    SimulationBody triangle = new SimulationBody();
	    triangle.addFixture(Geometry.createTriangle(new Vector2(0.0, 0.5), new Vector2(-0.5, -0.5), new Vector2(0.5, -0.5)));
	    triangle.translate(new Vector2(-2.5, 3));
	    triangle.setMass(MassType.INFINITE);
	    this.world.addBody(triangle);

	    // Circle
	    SimulationBody circle = new SimulationBody();
	    circle.setUserData(INDESTRUCTIBLE);
	    circle.addFixture(Geometry.createCircle(0.5));
	    circle.translate(new Vector2(3.2, 3.5));
	    circle.setMass(MassType.INFINITE);
	    this.world.addBody(circle);

	    // Segment
	    SimulationBody segment = new SimulationBody();
	    segment.addFixture(Geometry.createSegment(new Vector2(0.5, 0.5), new Vector2(-0.5, 0)));
	    segment.translate(new Vector2(-4.2, 4));
	    segment.setMass(MassType.INFINITE);
	    this.world.addBody(segment);

	    // Square
	    SimulationBody square = new SimulationBody();
	    square.addFixture(Geometry.createSquare(1.0));
	    square.translate(new Vector2(1.5, -2.0));
	    square.setMass(MassType.INFINITE);
	    this.world.addBody(square);

	    // Polygon
	    SimulationBody polygon = new SimulationBody();
	    polygon.addFixture(Geometry.createUnitCirclePolygon(5, 0.5));
	    polygon.translate(new Vector2(2.0, 0));
	    polygon.setMass(MassType.INFINITE);
	    this.world.addBody(polygon);

	    // Capsule
	    SimulationBody capsule = new SimulationBody();
	    capsule.addFixture(Geometry.createCapsule(2, 1));
	    capsule.translate(new Vector2(-4.5, -5.0));
	    capsule.setMass(MassType.INFINITE);
	    this.world.addBody(capsule);
	    
	    tank = new SimulationBody();
	    tank.addFixture(Geometry.createRectangle(1.0, 1.5));
	    tank.addFixture(Geometry.createCircle(0.35));
	    tank.setMass(MassType.NORMAL);
	    this.world.addBody(tank);
	    
	    barrel = new SimulationBody();
	    // NOTE: make the mass of the barrel less so that driving doesn't turn the barrel
	    barrel.addFixture(Geometry.createRectangle(0.15, 1.0), 0.2);
	    barrel.setMass(MassType.NORMAL);
	    barrel.translate(0.0, 0.5);
	    this.world.addBody(barrel);
	    
	    // make the barrel pivot about the tank
	    RevoluteJoint<SimulationBody> rj = new RevoluteJoint<SimulationBody>(tank, barrel, tank.getWorldCenter());
	    this.world.addJoint(rj);
	    
	    // add friction to the motion of the tank driving
	    FrictionJoint<SimulationBody> fj2 = new FrictionJoint<SimulationBody>(tank, circle, tank.getWorldCenter());
	    fj2.setMaximumForce(2);
	    fj2.setMaximumTorque(1);
	    fj2.setCollisionAllowed(true);
	    this.world.addJoint(fj2);
	    
	    // add fricition to the motion of the barrel
	    FrictionJoint<SimulationBody> fj = new FrictionJoint<SimulationBody>(circle, barrel, tank.getWorldCenter());
	    fj.setMaximumForce(0);
	    fj.setMaximumTorque(0.2);
	    fj.setCollisionAllowed(true);
	    this.world.addJoint(fj);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.SimulationFrame#render(java.awt.Graphics2D, double)
	 */
	protected void render(Graphics2D g, double elapsedTime) {
		super.render(g, elapsedTime);
		
		final double r = 4.0;
		final double scale = this.getCameraScale();
		final double length = 100;
		
		Vector2 start = this.barrel.getTransform().getTransformed(new Vector2(0.0, 0.55));
		Vector2 direction = this.barrel.getTransform().getTransformedR(new Vector2(0.0, 1.0));
		
		Ray ray = new Ray(start, direction);
		g.setColor(Color.RED);
		g.draw(new Line2D.Double(
				ray.getStart().x * scale, 
				ray.getStart().y * scale, 
				ray.getStart().x * scale + ray.getDirectionVector().x * length * scale, 
				ray.getStart().y * scale + ray.getDirectionVector().y * length * scale));
		
		List<RaycastResult<SimulationBody, BodyFixture>> results = this.world.raycast(ray, length, new DetectFilter<SimulationBody, BodyFixture>(true, true, null));
		for (RaycastResult<SimulationBody, BodyFixture> result : results) {
			// draw the intersection
			Vector2 point = result.getRaycast().getPoint();
			g.setColor(Color.GREEN);
			g.fill(new Ellipse2D.Double(
					point.x * scale - r * 0.5, 
					point.y * scale - r * 0.5, 
					r, 
					r));
			g.setColor(Color.BLUE);
			g.draw(new Line2D.Double(
					point.x * scale, 
					point.y * scale, 
					point.x * scale + result.getRaycast().getNormal().x * scale, 
					point.y * scale + result.getRaycast().getNormal().y * scale));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#handleEvents()
	 */
	@Override
	protected void handleEvents() {
		super.handleEvents();
		
		if (this.rotateTurretLeft.isActive()) {
			Vector2 normal = this.barrel.getTransform().getTransformedR(new Vector2(-1.0, 0.0));
			normal.multiply(0.1);
			
			Vector2 point = this.barrel.getTransform().getTransformed(new Vector2(0.0, 1.0));
			this.barrel.applyForce(normal, point);
		}
		
		if (this.rotateTurretRight.isActive()) {
			Vector2 normal = this.barrel.getTransform().getTransformedR(new Vector2(1.0, 0.0));
			normal.multiply(0.1);
			
			Vector2 point = this.barrel.getTransform().getTransformed(new Vector2(0.0, 1.0));
			this.barrel.applyForce(normal, point);
		}
		
		if (this.driveForward.isActive()) {
			Vector2 normal = this.tank.getTransform().getTransformedR(new Vector2(0.0, 1.0));
			normal.multiply(5);
			
			tank.applyForce(normal);
		}
		
		if (this.driveBackward.isActive()) {
			Vector2 normal = this.tank.getTransform().getTransformedR(new Vector2(0.0, 1.0));
			normal.multiply(-5);
			
			tank.applyForce(normal);
		}
		
		if (this.rotateLeft.isActive()) {
			tank.applyTorque(Math.PI / 2);
		}
		
		if (this.rotateRight.isActive()) {
			tank.applyTorque(-Math.PI / 2);
		}
		
		// make sure the linear velocity is already in the direction of the tank front
		Vector2 normal = this.tank.getTransform().getTransformedR(new Vector2(0.0, 1.0));
		double defl = tank.getLinearVelocity().dot(normal);
		// clamp the velocity
		defl = Interval.clamp(defl, -2, 2);
		tank.setLinearVelocity(normal.multiply(defl));
		
		// clamp the angular velocity
		double av = tank.getAngularVelocity();
		av = Interval.clamp(av, -1, 1);
		tank.setAngularVelocity(av);
		
		// clamp the angular velocity of the barrel
		av = barrel.getAngularVelocity();
		av = Interval.clamp(av, -1, 1);
		barrel.setAngularVelocity(av);
		
		if (this.shoot.isActiveButNotHandled()) {
			this.shoot.setHasBeenHandled(true);
			final double length = 100;
			
			Vector2 start = this.barrel.getTransform().getTransformed(new Vector2(0.0, 0.55));
			Vector2 direction = this.barrel.getTransform().getTransformedR(new Vector2(0.0, 1.0));
			
			Ray ray = new Ray(start, direction);
			RaycastResult<SimulationBody, BodyFixture> result = 
					this.world.raycastClosest(ray, length, new DetectFilter<SimulationBody, BodyFixture>(true, true, null) {
						@Override
						public boolean isAllowed(SimulationBody body, BodyFixture fixture) {
							boolean isAllowed = super.isAllowed(body, fixture);
							return isAllowed && body.getUserData() != INDESTRUCTIBLE;
						}
					});
			
			if (result != null) {
				this.world.removeBody(result.getBody());
			}
		}
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		Tank simulation = new Tank();
		simulation.run();
	}
}
