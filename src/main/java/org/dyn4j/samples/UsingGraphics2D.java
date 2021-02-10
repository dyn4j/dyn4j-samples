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

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Capsule;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Slice;
import org.dyn4j.geometry.Triangle;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.samples.framework.SimulationFrame;

/**
 * Class used to show a simple example of using the dyn4j project using
 * Java2D for rendering.
 * <p>
 * This class can be used as a starting point for projects.
 * @author William Bittle
 * @version 4.1.1
 * @since 3.0.0
 */
public class UsingGraphics2D extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = 5663760293144882635L;
	
	/**
	 * Default constructor for the window
	 */
	public UsingGraphics2D() {
		super("Graphics2D Example", 45.0);
		
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 * <p>
	 * Basically the same shapes from the Shapes test in
	 * the TestBed.
	 */
	protected void initializeWorld() {
		// create all your bodies/joints
		
		// create the floor
		Rectangle floorRect = new Rectangle(15.0, 1.0);
		SimulationBody floor = new SimulationBody();
		floor.addFixture(new BodyFixture(floorRect));
		floor.setMass(MassType.INFINITE);
		// move the floor down a bit
		floor.translate(0.0, -4.0);
		this.world.addBody(floor);
		
		// create a triangle object
		Triangle triShape = new Triangle(
				new Vector2(0.0, 0.5), 
				new Vector2(-0.5, -0.5), 
				new Vector2(0.5, -0.5));
		SimulationBody triangle = new SimulationBody();
		triangle.addFixture(triShape);
		triangle.setMass(MassType.NORMAL);
		triangle.translate(-1.0, 2.0);
		// test having a velocity
		triangle.getLinearVelocity().set(5.0, 0.0);
		this.world.addBody(triangle);
		
		// create a circle
		Circle cirShape = new Circle(0.5);
		SimulationBody circle = new SimulationBody();
		circle.addFixture(cirShape);
		circle.setMass(MassType.NORMAL);
		circle.translate(2.0, 2.0);
		// test adding some force
		circle.applyForce(new Vector2(-100.0, 0.0));
		// set some linear damping to simulate rolling friction
		circle.setLinearDamping(0.05);
		this.world.addBody(circle);
		
		// try a rectangle
		Rectangle rectShape = new Rectangle(1.0, 1.0);
		SimulationBody rectangle = new SimulationBody();
		rectangle.addFixture(rectShape);
		rectangle.setMass(MassType.NORMAL);
		rectangle.translate(0.0, 2.0);
		rectangle.getLinearVelocity().set(-5.0, 0.0);
		this.world.addBody(rectangle);
		
		// try a polygon with lots of vertices
		Polygon polyShape = Geometry.createUnitCirclePolygon(10, 1.0);
		SimulationBody polygon = new SimulationBody();
		polygon.addFixture(polyShape);
		polygon.setMass(MassType.NORMAL);
		polygon.translate(-2.5, 2.0);
		// set the angular velocity
		polygon.setAngularVelocity(Math.toRadians(-20.0));
		this.world.addBody(polygon);
		
		// try a compound object
		Circle c1 = new Circle(0.5);
		BodyFixture c1Fixture = new BodyFixture(c1);
		c1Fixture.setDensity(0.5);
		Circle c2 = new Circle(0.5);
		BodyFixture c2Fixture = new BodyFixture(c2);
		c2Fixture.setDensity(0.5);
		Rectangle rm = new Rectangle(2.0, 1.0);
		// translate the circles in local coordinates
		c1.translate(-1.0, 0.0);
		c2.translate(1.0, 0.0);
		SimulationBody capsule = new SimulationBody();
		capsule.addFixture(c1Fixture);
		capsule.addFixture(c2Fixture);
		capsule.addFixture(rm);
		capsule.setMass(MassType.NORMAL);
		capsule.translate(0.0, 4.0);
		this.world.addBody(capsule);
		
		SimulationBody issTri = new SimulationBody();
		issTri.addFixture(Geometry.createIsoscelesTriangle(1.0, 3.0));
		issTri.setMass(MassType.NORMAL);
		issTri.translate(2.0, 3.0);
		this.world.addBody(issTri);
		
		SimulationBody equTri = new SimulationBody();
		equTri.addFixture(Geometry.createEquilateralTriangle(2.0));
		equTri.setMass(MassType.NORMAL);
		equTri.translate(3.0, 3.0);
		this.world.addBody(equTri);
		
		SimulationBody rightTri = new SimulationBody();
		rightTri.addFixture(Geometry.createRightTriangle(2.0, 1.0));
		rightTri.setMass(MassType.NORMAL);
		rightTri.translate(4.0, 3.0);
		this.world.addBody(rightTri);
		
		SimulationBody cap = new SimulationBody();
		cap.addFixture(new Capsule(1.0, 0.5));
		cap.setMass(MassType.NORMAL);
		cap.translate(-3.0, 3.0);
		this.world.addBody(cap);
		
		SimulationBody slice = new SimulationBody();
		slice.addFixture(new Slice(0.5, Math.toRadians(120)));
		slice.setMass(MassType.NORMAL);
		slice.translate(-3.0, 3.0);
		this.world.addBody(slice);
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		UsingGraphics2D simulation = new UsingGraphics2D();
		simulation.run();
	}
}
