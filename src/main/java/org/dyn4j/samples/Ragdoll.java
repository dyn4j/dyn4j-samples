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

import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.samples.framework.SimulationFrame;

/**
 * A somewhat complex scene with a ragdoll.
 * @author William Bittle
 * @since 3.2.1
 * @version 3.2.0
 */
public class Ragdoll extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = -2350301592218819726L;

	/**
	 * Default constructor.
	 */
	public Ragdoll() {
		super("Ragdoll", 64.0);
		this.setOffsetY(300);
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 */
	protected void initializeWorld() {		
		// Ground
		SimulationBody ground = new SimulationBody();
		ground.addFixture(Geometry.createRectangle(100.0, 1.0));
	    ground.translate(new Vector2(0.6875, -8.75));
	    ground.setMass(MassType.INFINITE);
	    world.addBody(ground);

	    // the ragdoll
	    
	    // Head
	    SimulationBody head = new SimulationBody();
	    head.addFixture(Geometry.createCircle(0.25));
	    head.setMass(MassType.NORMAL);
	    world.addBody(head);

	    // Torso
	    SimulationBody torso = new SimulationBody();
	    torso.addFixture(Geometry.createRectangle(0.5, 1.0));
	    {
	      Convex c = Geometry.createRectangle(1.0, 0.25);
	      c.translate(new Vector2(0.00390625, 0.375));
	      torso.addFixture(c);
	    }
	    torso.translate(new Vector2(0.0234375, -0.8125));
	    torso.setMass(MassType.NORMAL);
	    world.addBody(torso);

	    // Right Humerus
	    SimulationBody rightHumerus = new SimulationBody();
	    rightHumerus.addFixture(Geometry.createRectangle(0.25, 0.5));
	    rightHumerus.translate(new Vector2(0.4375, -0.609375));
	    rightHumerus.setMass(MassType.NORMAL);
	    world.addBody(rightHumerus);

	    // Right Ulna
	    SimulationBody rightUlna = new SimulationBody();
	    rightUlna.addFixture(Geometry.createRectangle(0.25, 0.4));
	    rightUlna.translate(new Vector2(0.44140625, -0.98828125));
	    rightUlna.setMass(MassType.NORMAL);
	    world.addBody(rightUlna);

	    // Neck
	    SimulationBody neck = new SimulationBody();
	    neck.addFixture(Geometry.createRectangle(0.15, 0.2));
	    neck.translate(new Vector2(0.015625, -0.2734375));
	    neck.setMass(MassType.NORMAL);
	    world.addBody(neck);

	    // Left Humerus
	    SimulationBody leftHumerus = new SimulationBody();
	    leftHumerus.addFixture(Geometry.createRectangle(0.25, 0.5));
	    leftHumerus.translate(new Vector2(-0.3828125, -0.609375));
	    leftHumerus.setMass(MassType.NORMAL);
	    world.addBody(leftHumerus);

	    // Left Ulna
	    SimulationBody leftUlna = new SimulationBody();
	    leftUlna.addFixture(Geometry.createRectangle(0.25, 0.4));
	    leftUlna.translate(new Vector2(-0.3828125, -0.9765625));
	    leftUlna.setMass(MassType.NORMAL);
	    world.addBody(leftUlna);

	    // Right Femur
	    SimulationBody rightFemur = new SimulationBody();
	    rightFemur.addFixture(Geometry.createRectangle(0.25, 0.75));
	    rightFemur.translate(new Vector2(0.1796875, -1.5703125));
	    rightFemur.setMass(MassType.NORMAL);
	    world.addBody(rightFemur);

	    // Left Femur
	    SimulationBody leftFemur = new SimulationBody();
	    leftFemur.addFixture(Geometry.createRectangle(0.25, 0.75));
	    leftFemur.translate(new Vector2(-0.1328125, -1.5703125));
	    leftFemur.setMass(MassType.NORMAL);
	    world.addBody(leftFemur);

	    // Right Tibia
	    SimulationBody rightTibia = new SimulationBody();
	    rightTibia.addFixture(Geometry.createRectangle(0.25, 0.5));
	    rightTibia.translate(new Vector2(0.18359375, -2.11328125));
	    rightTibia.setMass(MassType.NORMAL);
	    world.addBody(rightTibia);

	    // Left Tibia
	    SimulationBody leftTibia = new SimulationBody();
	    leftTibia.addFixture(Geometry.createRectangle(0.25, 0.5));
	    leftTibia.translate(new Vector2(-0.1328125, -2.1171875));
	    leftTibia.setMass(MassType.NORMAL);
	    world.addBody(leftTibia);

	    // Head to Neck
	    RevoluteJoint<SimulationBody> headToNeck = new RevoluteJoint<SimulationBody>(head, neck, new Vector2(0.01, -0.2));
	    world.addJoint(headToNeck);
	    
	    // Neck to Torso
	    RevoluteJoint<SimulationBody> neckToTorso = new RevoluteJoint<SimulationBody>(neck, torso, new Vector2(0.01, -0.35));
	    world.addJoint(neckToTorso);
	    
	    // Torso to Left Humerus
	    RevoluteJoint<SimulationBody> torsoToLeftHumerus = new RevoluteJoint<SimulationBody>(torso, leftHumerus, new Vector2(-0.4, -0.4));
	    world.addJoint(torsoToLeftHumerus);
	    
	    // Torso to Right Humerus
	    RevoluteJoint<SimulationBody> torsoToRightHumerus = new RevoluteJoint<SimulationBody>(torso, rightHumerus, new Vector2(0.4, -0.4));
	    world.addJoint(torsoToRightHumerus);
	    
	    // Right Humerus to Right Ulna
	    RevoluteJoint<SimulationBody> rightHumerusToRightUlna = new RevoluteJoint<SimulationBody>(rightHumerus, rightUlna, new Vector2(0.43, -0.82));
	    world.addJoint(rightHumerusToRightUlna);
	    
	    // Left Humerus to Left Ulna
	    RevoluteJoint<SimulationBody> leftHumerusToLeftUlna = new RevoluteJoint<SimulationBody>(leftHumerus, leftUlna, new Vector2(-0.4, -0.81));
	    world.addJoint(leftHumerusToLeftUlna);
	    
	    // Torso to Right Femur
	    RevoluteJoint<SimulationBody> torsoToRightFemur = new RevoluteJoint<SimulationBody>(torso, rightFemur, new Vector2(0.16, -1.25));
	    world.addJoint(torsoToRightFemur);
	    
	    // Torso to Left Femur
	    RevoluteJoint<SimulationBody> torsoToLeftFemur = new RevoluteJoint<SimulationBody>(torso, leftFemur, new Vector2(-0.13, -1.25));
	    world.addJoint(torsoToLeftFemur);
	    
	    // Right Femur to Right Tibia
	    RevoluteJoint<SimulationBody> rightFemurToRightTibia = new RevoluteJoint<SimulationBody>(rightFemur, rightTibia, new Vector2(0.17, -1.9));
	    world.addJoint(rightFemurToRightTibia);
	    
	    // Left Femur to Left Tibia
	    RevoluteJoint<SimulationBody> leftFemurToLeftTibia = new RevoluteJoint<SimulationBody>(leftFemur, leftTibia, new Vector2(-0.14, -1.9));
	    world.addJoint(leftFemurToLeftTibia);
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		Ragdoll simulation = new Ragdoll();
		simulation.run();
	}
}
