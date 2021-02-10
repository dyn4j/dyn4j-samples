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
import org.dyn4j.dynamics.joint.PrismaticJoint;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.samples.framework.SimulationFrame;

/**
 * A scene that replicates a piston in an ICE.
 * @author William Bittle
 * @since 4.1.1
 * @version 4.1.1
 */
public class Crank extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = -602182516495632849L;

	/**
	 * Default constructor.
	 */
	public Crank() {
		super("Crank", 32.0);
		
		this.setOffsetY(-200);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#initializeWorld()
	 */
	protected void initializeWorld() {
		// Floor
		SimulationBody body1 = new SimulationBody();
		body1.addFixture(Geometry.createRectangle(10.0, 0.5));
	    body1.setMass(MassType.INFINITE);
	    world.addBody(body1);

	    // Crank
	    SimulationBody body2 = new SimulationBody();
	    {// Fixture1
	      Convex c = Geometry.createRectangle(0.5, 2.0);
	      BodyFixture bf = new BodyFixture(c);
	      bf.setDensity(2.0);
	      body2.addFixture(bf);
	    }
	    body2.translate(new Vector2(0.0, 5.0));
	    body2.setMass(MassType.NORMAL);
	    world.addBody(body2);

	    // Follower
	    SimulationBody body3 = new SimulationBody();
	    {// Fixture2
	      Convex c = Geometry.createRectangle(0.5, 4.0);
	      BodyFixture bf = new BodyFixture(c);
	      bf.setDensity(2.0);
	      body3.addFixture(bf);
	    }
	    body3.translate(new Vector2(0.0, 7.5));
	    body3.setMass(MassType.NORMAL);
	    world.addBody(body3);

	    // Piston
	    SimulationBody body4 = new SimulationBody();
	    {// Fixture3
	      Convex c = Geometry.createRectangle(1.5, 1.5);
	      BodyFixture bf = new BodyFixture(c);
	      bf.setDensity(2.0);
	      body4.addFixture(bf);
	    }
	    body4.translate(new Vector2(0.0, 9.75));
	    body4.setMass(MassType.FIXED_ANGULAR_VELOCITY);
	    world.addBody(body4);

	    // CrankJoint
	    RevoluteJoint<SimulationBody> joint1 = new RevoluteJoint<SimulationBody>(body1, body2, new Vector2(0.0, 4.25));
	    joint1.setLimitEnabled(false);
	    joint1.setLimits(Math.toRadians(0.0), Math.toRadians(0.0));
	    joint1.setReferenceAngle(Math.toRadians(0.0));
	    joint1.setMotorEnabled(true);
	    joint1.setMotorSpeed(Math.toRadians(180.0));
	    joint1.setMaximumMotorTorque(10000.0);
	    joint1.setCollisionAllowed(false);
	    world.addJoint(joint1);
	    
	    // FollowerJoint
	    RevoluteJoint<SimulationBody> joint2 = new RevoluteJoint<SimulationBody>(body2, body3, new Vector2(0.0, 5.75));
	    joint2.setLimitEnabled(false);
	    joint2.setLimits(Math.toRadians(0.0), Math.toRadians(0.0));
	    joint2.setReferenceAngle(Math.toRadians(0.0));
	    joint2.setMotorEnabled(false);
	    joint2.setMotorSpeed(Math.toRadians(0.0));
	    joint2.setMaximumMotorTorque(0.0);
	    joint2.setCollisionAllowed(false);
	    world.addJoint(joint2);
	    
	    // PistonJoint1
	    RevoluteJoint<SimulationBody> joint3 = new RevoluteJoint<SimulationBody>(body3, body4, new Vector2(0.0, 9.25));
	    joint3.setLimitEnabled(false);
	    joint3.setLimits(Math.toRadians(0.0), Math.toRadians(0.0));
	    joint3.setReferenceAngle(Math.toRadians(0.0));
	    joint3.setMotorEnabled(false);
	    joint3.setMotorSpeed(Math.toRadians(0.0));
	    joint3.setMaximumMotorTorque(0.0);
	    joint3.setCollisionAllowed(false);
	    world.addJoint(joint3);
	    
	    // PistonJoint2
	    PrismaticJoint<SimulationBody> joint4 = new PrismaticJoint<SimulationBody>(body1, body4, new Vector2(0.0, 9.75), new Vector2(0.0, 1.0));
	    joint4.setLimitEnabled(false);
	    joint4.setLimits(0.0, 0.0);
	    joint4.setReferenceAngle(Math.toRadians(0.0));
	    joint4.setMotorEnabled(false);
	    joint4.setMotorSpeed(0.0);
	    joint4.setMaximumMotorForce(0.0);
	    joint4.setCollisionAllowed(false);
	    world.addJoint(joint4);
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		Crank simulation = new Crank();
		simulation.run();
	}
}
