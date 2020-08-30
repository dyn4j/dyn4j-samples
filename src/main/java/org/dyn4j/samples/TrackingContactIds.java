/*
 * Copyright (c) 2010-2016 William Bittle  http://www.dyn4j.org/
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.contact.Contact;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.samples.framework.SimulationFrame;
import org.dyn4j.world.ContactCollisionData;
import org.dyn4j.world.listener.ContactListenerAdapter;

/**
 * A simple scene with a few shape types that tracks the creation,
 * persistence and removal of contacts by using their unique ids.
 * @author William Bittle
 * @version 3.2.1
 * @since 3.0.0
 */
public class TrackingContactIds extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = -7551190289570564575L;

	// contact listening
	
	/** A mapping of contact id to UUID */
	private Map<Contact, UUID> ids2 = new HashMap<Contact, UUID>();

	/**
	 * A custom contact listener for tracking contacts.
	 * @author William Bittle
	 * @version 3.2.1
	 * @since 3.2.1
	 */
	private class CustomContactListener extends ContactListenerAdapter<SimulationBody> {
		@Override
		public void begin(ContactCollisionData<SimulationBody> collision, Contact contact) {
			UUID id = UUID.randomUUID();
			ids2.put(contact, id);
			System.out.println("BEGIN: " + id);
		}
		
		@Override
		public void persist(ContactCollisionData<SimulationBody> collision, Contact oldContact, Contact newContact) {
			UUID id = ids2.get(oldContact);
			if (id == null) {
				System.err.println("Shouldn't happen");
			}
			// since the contact object itself changes between iterations
			// remove the old contact and add the new contact with the same id
			ids2.remove(oldContact);
			ids2.put(newContact, id);
		}
		
		@Override
		public void end(ContactCollisionData<SimulationBody> collision, Contact contact) {
			UUID id = ids2.get(contact);
			if (id == null) {
				System.err.println("Shouldn't happen");
			}
			System.out.println("END: " + id);
			ids2.remove(contact);
		}
	}
	
	/**
	 * Default constructor.
	 */
	public TrackingContactIds() {
		super("Tracking Contact IDs", 45.0);
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 */
	protected void initializeWorld() {
		// create all your bodies/joints
		
		// create the floor
		SimulationBody floor = new SimulationBody();
		floor.addFixture(Geometry.createRectangle(15, 1));
		floor.setMass(MassType.INFINITE);
		// move the floor down a bit
		floor.translate(0.0, -4.0);
		this.world.addBody(floor);
		
		// create a triangle object
		SimulationBody triangle = new SimulationBody();
		triangle.addFixture(Geometry.createTriangle(
				new Vector2(0.0, 0.5), 
				new Vector2(-0.5, -0.5), 
				new Vector2(0.5, -0.5)));
		triangle.setMass(MassType.NORMAL);
		triangle.translate(-1.0, 2.0);
		// test having a velocity
		triangle.getLinearVelocity().set(5.0, 0.0);
		this.world.addBody(triangle);
		
		// create a circle
		SimulationBody circle = new SimulationBody();
		circle.addFixture(Geometry.createCircle(0.5));
		circle.setMass(MassType.NORMAL);
		circle.translate(2.0, 2.0);
		// test adding some force
		circle.applyForce(new Vector2(-100.0, 0.0));
		// set some linear damping to simulate rolling friction
		circle.setLinearDamping(0.05);
		this.world.addBody(circle);
		
		// try a rectangle
		SimulationBody rectangle = new SimulationBody();
		rectangle.addFixture(Geometry.createRectangle(1, 1));
		rectangle.setMass(MassType.NORMAL);
		rectangle.translate(0.0, 2.0);
		rectangle.getLinearVelocity().set(-5.0, 0.0);
		this.world.addBody(rectangle);
		
		// try a polygon with lots of vertices
		SimulationBody polygon = new SimulationBody();
		polygon.addFixture(Geometry.createUnitCirclePolygon(10, 1));
		polygon.setMass(MassType.NORMAL);
		polygon.translate(-2.5, 2.0);
		// set the angular velocity
		polygon.setAngularVelocity(Math.toRadians(-20.0));
		this.world.addBody(polygon);
		
		// try a compound object (Capsule)
		BodyFixture c1Fixture = new BodyFixture(Geometry.createCircle(0.5));
		BodyFixture c2Fixture = new BodyFixture(Geometry.createCircle(0.5));
		c1Fixture.setDensity(0.5);
		c2Fixture.setDensity(0.5);
		// translate the circles in local coordinates
		c1Fixture.getShape().translate(-1.0, 0.0);
		c2Fixture.getShape().translate(1.0, 0.0);
		SimulationBody capsule = new SimulationBody();
		capsule.addFixture(c1Fixture);
		capsule.addFixture(c2Fixture);
		capsule.addFixture(Geometry.createRectangle(2, 1));
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
		
		// attach the contact listener
		this.world.addContactListener(new CustomContactListener());
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		TrackingContactIds simulation = new TrackingContactIds();
		simulation.run();
	}
}
