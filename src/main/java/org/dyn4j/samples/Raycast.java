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
import java.awt.geom.Line2D;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.joint.FrictionJoint;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Interval;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.samples.framework.SimulationFrame;
import org.dyn4j.world.DetectFilter;
import org.dyn4j.world.World;
import org.dyn4j.world.result.RaycastResult;

/**
 * A scene were a player controled tank raycasts against the world.
 * @author William Bittle
 * @version 4.1.1
 * @since 3.0.0
 */
public class Raycast extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = 1462952703366297615L;

	// controls
	private final AtomicBoolean driveForward = new AtomicBoolean(false);
	private final AtomicBoolean driveBackward = new AtomicBoolean(false);
	private final AtomicBoolean rotateLeft = new AtomicBoolean(false);
	private final AtomicBoolean rotateRight = new AtomicBoolean(false);
	private final AtomicBoolean rotateTurretLeft = new AtomicBoolean(false);
	private final AtomicBoolean rotateTurretRight = new AtomicBoolean(false);
	
	private SimulationBody tank;
	private SimulationBody barrel;
	
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
					rotateTurretLeft.set(true);
					break;
				case KeyEvent.VK_RIGHT:
					rotateTurretRight.set(true);
					break;
				case KeyEvent.VK_W:
					driveForward.set(true);
					break;
				case KeyEvent.VK_S:
					driveBackward.set(true);
					break;
				case KeyEvent.VK_A:
					rotateLeft.set(true);
					break;
				case KeyEvent.VK_D:
					rotateRight.set(true);
					break;
			}
			
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_LEFT:
					rotateTurretLeft.set(false);
					break;
				case KeyEvent.VK_RIGHT:
					rotateTurretRight.set(false);
					break;
				case KeyEvent.VK_W:
					driveForward.set(false);
					break;
				case KeyEvent.VK_S:
					driveBackward.set(false);
					break;
				case KeyEvent.VK_A:
					rotateLeft.set(false);
					break;
				case KeyEvent.VK_D:
					rotateRight.set(false);
					break;
			}
		}
	}
	
	/**
	 * Default constructor.
	 */
	public Raycast() {
		super("Raycast", 48.0);
		
		KeyListener listener = new CustomKeyListener();
		this.addKeyListener(listener);
		this.canvas.addKeyListener(listener);
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
		final double scale = this.getScale();
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
	
	@Override
	protected void handleEvents() {
		super.handleEvents();
		
		if (this.rotateTurretLeft.get()) {
			Vector2 normal = this.barrel.getTransform().getTransformedR(new Vector2(-1.0, 0.0));
			normal.multiply(0.08);
			
			Vector2 point = this.barrel.getTransform().getTransformed(new Vector2(0.0, 1.0));
			this.barrel.applyForce(normal, point);
		}
		
		if (this.rotateTurretRight.get()) {
			Vector2 normal = this.barrel.getTransform().getTransformedR(new Vector2(1.0, 0.0));
			normal.multiply(0.08);
			
			Vector2 point = this.barrel.getTransform().getTransformed(new Vector2(0.0, 1.0));
			this.barrel.applyForce(normal, point);
		}
		
		if (this.driveForward.get()) {
			Vector2 normal = this.tank.getTransform().getTransformedR(new Vector2(0.0, 1.0));
			normal.multiply(5);
			
			tank.applyForce(normal);
		}
		
		if (this.driveBackward.get()) {
			Vector2 normal = this.tank.getTransform().getTransformedR(new Vector2(0.0, 1.0));
			normal.multiply(-5);
			
			tank.applyForce(normal);
		}
		
		if (this.rotateLeft.get()) {
			tank.applyTorque(Math.PI / 2);
		}
		
		if (this.rotateRight.get()) {
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
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		Raycast simulation = new Raycast();
		simulation.run();
	}
}
