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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.samples.framework.SimulationFrame;
import org.dyn4j.world.ContactCollisionData;
import org.dyn4j.world.PhysicsWorld;
import org.dyn4j.world.listener.ContactListenerAdapter;
import org.dyn4j.world.listener.StepListenerAdapter;

/**
 * A simple scene of a circle that is controlled by the left and
 * right arrow keys that is moved by applying torques and forces.
 * <p>
 * Also illustrated here is how to track whether the body is in
 * contact with the "ground."
 * <p>
 * Always keep in mind that this is just an example, production
 * code should be more robust and better organized.
 * @author William Bittle
 * @since 4.1.1
 * @version 3.2.0
 */
public class Platformer extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = -313391186714427055L;

	/**
	 * Default constructor for the window
	 */
	public Platformer() {
		super("Platformer", 32.0);
		
		KeyListener listener = new CustomKeyListener();
		this.addKeyListener(listener);
		this.canvas.addKeyListener(listener);
	}
	
	private SimulationBody wheel;
	
	// controls
	private final AtomicBoolean goLeftPressed = new AtomicBoolean(false);
	private final AtomicBoolean goRightPressed = new AtomicBoolean(false);
	private final AtomicBoolean goDownPressed = new AtomicBoolean(false);
	private final AtomicBoolean jumpPressed = new AtomicBoolean(false);
	
	private final AtomicBoolean isOnGround = new AtomicBoolean(false);
	
	private static final Color WHEEL_OFF_COLOR = Color.MAGENTA;
	private static final Color WHEEL_ON_COLOR = Color.GREEN;
	
	private static final Object CHARACTER = new Object();
	private static final Object FLOOR = new Object();
	private static final Object ONE_WAY_PLATFORM = new Object();
	
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
					goLeftPressed.set(true);
					break;
				case KeyEvent.VK_RIGHT:
					goRightPressed.set(true);
					break;
			}
			
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_LEFT:
					goLeftPressed.set(false);
					break;
				case KeyEvent.VK_RIGHT:
					goRightPressed.set(false);
					break;
				case KeyEvent.VK_UP:
					jumpPressed.set(true);
					break;
				case KeyEvent.VK_DOWN:
					if (isOnGround.get()) {
						goDownPressed.set(true);
					}
					break;
			}
		}
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 */
	protected void initializeWorld() {
		// the floor
		SimulationBody floor = new SimulationBody();
		floor.addFixture(Geometry.createRectangle(50.0, 0.2));
		floor.setMass(MassType.INFINITE);
		floor.translate(0, -3);
		floor.setUserData(FLOOR);
		this.world.addBody(floor);
		
		// some obstacles
		final int n = 5;
		for (int i = 0; i < n; i++) {
			SimulationBody sb = new SimulationBody();
			double w = 1.0;
			double h = Math.random() * 0.3 + 0.1;
			sb.addFixture(Geometry.createIsoscelesTriangle(w, h));
			sb.translate((Math.random() > 0.5 ? -1 : 1) * Math.random() * 5.0, h * 0.5 - 2.9);
			sb.setMass(MassType.INFINITE);
			sb.setUserData(FLOOR);
			this.world.addBody(sb);
		}
		
		// the platform
		SimulationBody platform = new SimulationBody();
		platform.addFixture(Geometry.createRectangle(10.0, 0.2));
		platform.setMass(MassType.INFINITE);
		platform.translate(0, 0);
		platform.setUserData(ONE_WAY_PLATFORM);
		this.world.addBody(platform);
		
		// some bounding shapes
		SimulationBody right = new SimulationBody();
		right.addFixture(Geometry.createRectangle(0.2, 20));
		right.setMass(MassType.INFINITE);
		right.translate(10, 7);
		this.world.addBody(right);
		
		SimulationBody left = new SimulationBody();
		left.addFixture(Geometry.createRectangle(0.2, 20));
		left.setMass(MassType.INFINITE);
		left.translate(-10, 7);
		this.world.addBody(left);
		
		// the wheel
		wheel = new SimulationBody(WHEEL_OFF_COLOR);
		// NOTE: lots of friction to simulate a sticky tire
		wheel.addFixture(Geometry.createCircle(0.5), 1.0, 20.0, 0.1);
		wheel.setMass(MassType.NORMAL);
		wheel.translate(0.0, -2.0);
		wheel.setUserData(CHARACTER);
		wheel.setAtRestDetectionEnabled(false);
		this.world.addBody(wheel);
		
		// Use a number of concepts here to support movement, jumping, and one-way
		// platforms - this is by no means THE solution to these problems, but just
		// and example to provide some ideas on how you might
		
		// One consideration might be to use a sensor body to get less accurate
		// on-ground detection so that it's not frustrating to the user.  dyn4j
		// will detect them in collision, but small bouncing or other things could
		// cause it to look/feel wrong
		
		// SETP 1: 
		// at the beginning of each world step, check if the body is in
		// contact with any of the floor bodies
		this.world.addStepListener(new StepListenerAdapter<SimulationBody>() {
			@Override
			public void begin(TimeStep step, PhysicsWorld<SimulationBody, ?> world) {
				super.begin(step, world);
				
				boolean isGround = false;
				List<ContactConstraint<SimulationBody>> contacts = world.getContacts(wheel);
				for (ContactConstraint<SimulationBody> cc : contacts) {
					if (is(cc.getOtherBody(wheel), FLOOR, ONE_WAY_PLATFORM) && cc.isEnabled()) {
						isGround = true;
					}
				}
				if (!isGround) {
					isOnGround.set(false);					
				}
			}
		});
		
		// STEP 2:
		// when contacts are processed, we need to check if we're colliding with either
		// the one-way platform or the ground
		this.world.addContactListener(new ContactListenerAdapter<SimulationBody>() {
			@Override
			public void collision(ContactCollisionData<SimulationBody> collision) {
				ContactConstraint<SimulationBody> cc = collision.getContactConstraint();
				
				// set the other body to one-way if necessary
				setOneWay(cc);
				
				// track on the on-ground status
				setOnGround(cc);
				
				super.collision(collision);
			}
		});
	}
	
	/**
	 * Helper method to determine if a body is one of the given types assuming
	 * the type is stored in the user data.
	 * @param body the body
	 * @param types the set of types
	 * @return boolean
	 */
	private boolean is(SimulationBody body, Object... types) {
		for (Object type : types) {
			if (body.getUserData() == type) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if the given platform should be toggled as one-way
	 * given the position of the character body.
	 * @param character the character body
	 * @param platform the platform body
	 * @return boolean
	 */
	private boolean isOneWay(SimulationBody character, SimulationBody platform) {
		AABB wAABB = character.createAABB();
		AABB pAABB = platform.createAABB();
		
		// NOTE: this would need to change based on the shape of the platform and it's orientation
		// 
		// one thought might be to store the allowed normal of the platform on the platform body
		// and check that against the ContactConstraint normal to see if they are pointing in the
		// same direction
		//
		// another option might be to project both onto the platform normal to see where they are overlapping
		if (wAABB.getMinY() < pAABB.getMinY() || goDownPressed.get()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Disables the constraint if it's between the character and platform and it
	 * the scenario meets the condition for one-way.
	 * @param contactConstraint the constraint
	 */
	private void setOneWay(ContactConstraint<SimulationBody> contactConstraint) {
		SimulationBody b1 = contactConstraint.getBody1();
		SimulationBody b2 = contactConstraint.getBody2();
		
		if (is(b1, CHARACTER) && is(b2, ONE_WAY_PLATFORM)) {
			if (isOneWay(b1, b2)) {
				contactConstraint.setEnabled(false);
			}
		} else if (is(b1, ONE_WAY_PLATFORM) && is(b2, CHARACTER)) {
			if (isOneWay(b2, b1)) {
				contactConstraint.setEnabled(false);
			}
		}
	}
	
	/**
	 * Sets the isOnGround flag if the given contact constraint is between
	 * the character body and a floor or one-way platform.
	 * @param contactConstraint
	 */
	private void setOnGround(ContactConstraint<SimulationBody> contactConstraint) {
		SimulationBody b1 = contactConstraint.getBody1();
		SimulationBody b2 = contactConstraint.getBody2();
		
		if (is(b1, CHARACTER) && 
			is(b2, FLOOR, ONE_WAY_PLATFORM) &&
			contactConstraint.isEnabled()) {
			isOnGround.set(true);
			goDownPressed.set(false);
		} else if (is(b1, FLOOR, ONE_WAY_PLATFORM) && 
				   is(b2, CHARACTER) &&
				contactConstraint.isEnabled()) {
			isOnGround.set(true);
			goDownPressed.set(false);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#handleEvents()
	 */
	@Override
	protected void handleEvents() {
		super.handleEvents();
		
		// apply a torque based on key input
		if (this.goLeftPressed.get()) {
			wheel.applyTorque(Math.PI / 2);
		}
		if (this.goRightPressed.get()) {
			wheel.applyTorque(-Math.PI / 2);
		}
		
		// only allow jumping if the body is on the ground
		if (this.jumpPressed.get()) {
			if (this.isOnGround.get()) {
				wheel.applyImpulse(new Vector2(0.0, 7));
			}
			this.jumpPressed.set(false);
		}
		
		// color the body green if it's on the ground
		if (this.isOnGround.get()) {
			wheel.setColor(WHEEL_ON_COLOR);
		} else {
			wheel.setColor(WHEEL_OFF_COLOR);
		}
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		Platformer simulation = new Platformer();
		simulation.run();
	}
}
