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

import java.util.ArrayList;
import java.util.List;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.contact.Contact;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.WeldJoint;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.geometry.Wound;
import org.dyn4j.samples.framework.Camera;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.samples.framework.SimulationFrame;
import org.dyn4j.world.ContactCollisionData;
import org.dyn4j.world.listener.ContactListener;
import org.dyn4j.world.listener.ContactListenerAdapter;

/**
 * An example of destruction of bodies and joints
 * @author William Bittle
 * @since 5.0.0
 * @version 4.1.1
 */
public class Destructible extends SimulationFrame {
	private static final long serialVersionUID = -3302959427805814281L;

	public Destructible() {
		super("Destructible");
	}

	/**
	 * Extends the contact counter to implement the destruction
	 * of a joint when a contact is encountered.  Normally you would just
	 * extend the {@link ContactListener} interface.
	 * @author William Bittle
	 */
	public class Destructor extends ContactListenerAdapter<SimulationBody> {
		/** Used to flag that the joint has been removed */
		private boolean removed = false;
		
		/** Used to flag that the test body has been destroyed */
		private boolean broken = false;
		
		@Override
		public void begin(ContactCollisionData<SimulationBody> collision, Contact contact) {
			super.begin(collision, contact);
			
			// when a contact is added
			if (!this.removed) {
				Body jb1 = joint.getBody1();
				Body jb2 = joint.getBody2();
				Body b1 = collision.getBody1();
				Body b2 = collision.getBody2();
				
				// check the bodies involved
				if (b2 == jb1 || b2 == jb2 || b1 == jb1 || b1 == jb2) {
					// remove the joint
					toDeleteJoints.add(joint);
					this.removed = true;
				}
			}
			
			if (!this.broken) {
				Body b1 = collision.getBody1();
				Body b2 = collision.getBody2();
				
				// check the bodies involved
				if (b2 == icosigon || b1 == icosigon) {
					// remove the body from the world
					toDeleteBodies.add(icosigon);
					
					// make the test body into triangles
					
					// get the velocity
					Vector2 v = icosigon.getLinearVelocity().copy();
					// half the velocity to give the effect of a broken body
					v.multiply(0.5);
					
					Convex convex = icosigon.getFixture(0).getShape();
					Transform tx = icosigon.getTransform();
					Vector2 center = convex.getCenter();
					// we assume its a unit circle polygon to make
					// tessellation easy
					Vector2[] vertices = ((Wound) convex).getVertices();
					int size = vertices.length;
					for (int i = 0; i < size; i++) {
						// get the first and second vertices
						Vector2 p1 = vertices[i];
						Vector2 p2 = vertices[i + 1 == size ? 0 : i + 1];
						// create a body for the triangle
						SimulationBody b = new SimulationBody(icosigon.getColor());
						b.addFixture(Geometry.createTriangle(p1, p2, center), 1.0, 0.3, 0.0);
						b.setMass(MassType.NORMAL);
						// copy over the transform
						b.setTransform(tx.copy());
						// copy over the velocity
						b.setLinearVelocity(v.copy());
						b.setUserData("Piece" + (i + 1));
						// add the new body to the world
						toAddBodies.add(b);
					}
					
					this.broken = true;
					
					// set the requires setMass flag
					world.setUpdateRequired(true);
					
					// don't allow the contact
					collision.getContactConstraint().setEnabled(false);
				}
			}
		}
	}
	
	/** The floor body */
	private SimulationBody floor;
	
	/** The 20 sided body */
	private SimulationBody icosigon;
	
	/** The weld joint */
	private WeldJoint<SimulationBody> joint;

	/** The destrution class */
	private Destructor destructor;
	
	// buffer the deletes/adds
	
	private List<Joint<SimulationBody>> toDeleteJoints = new ArrayList<Joint<SimulationBody>>();
	private List<SimulationBody> toDeleteBodies = new ArrayList<SimulationBody>();
	private List<SimulationBody> toAddBodies = new ArrayList<SimulationBody>();
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#initializeWorld()
	 */
	@Override
	public void initializeWorld() {
		
		// create the floor
		this.floor = new SimulationBody();
		this.floor.addFixture(Geometry.createRectangle(15.0, 1.0));
		this.floor.setMass(MassType.INFINITE);
		this.floor.setUserData("Floor");
		
		// create the weld joint bodies
		SimulationBody top = new SimulationBody();
		top.addFixture(Geometry.createRectangle(0.5, 1.0));
		top.setMass(MassType.NORMAL);
		top.translate(0.0, 3.0);
		top.getLinearVelocity().set(2.0, 0.0);
		top.setUserData("Top");
		
		SimulationBody bot = new SimulationBody();
		bot.addFixture(Geometry.createRectangle(0.5, 1.0));
		bot.setMass(MassType.NORMAL);
		bot.translate(0.0, 2.0);
		bot.setUserData("Bottom");
		
		this.joint = new WeldJoint<SimulationBody>(top, bot, new Vector2(0.0, 2.5));
		this.joint.setUserData("WeldJoint1");
		
		this.icosigon = new SimulationBody();
		this.icosigon.addFixture(Geometry.createUnitCirclePolygon(20, 1.0));
		this.icosigon.setMass(MassType.NORMAL);
		this.icosigon.translate(-2.5, 2.0);
		this.icosigon.setUserData("Icosigon");
		
		this.destructor = new Destructor();
		
		this.world.addBody(this.floor);
		this.world.addBody(this.icosigon);
		this.world.addBody(top);
		this.world.addBody(bot);
		this.world.addJoint(this.joint);
		this.world.addContactListener(this.destructor);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#initializeCamera(org.dyn4j.samples.framework.Camera)
	 */
	@Override
	protected void initializeCamera(Camera camera) {
		super.initializeCamera(camera);
		camera.scale = 64.0;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#handleEvents()
	 */
	@Override
	protected void handleEvents() {
		super.handleEvents();
		
		for (Joint<SimulationBody> joint : this.toDeleteJoints) {
			this.world.removeJoint(joint);
		}
		this.toDeleteJoints.clear();
		
		for (SimulationBody body : this.toDeleteBodies) {
			this.world.removeBody(body);
		}
		this.toDeleteBodies.clear();
		
		for (SimulationBody body : this.toAddBodies) {
			this.world.addBody(body);
		}
		this.toAddBodies.clear();
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		Destructible simulation = new Destructible();
		simulation.run();
	}
}