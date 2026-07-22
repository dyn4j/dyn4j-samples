/*
 * Copyright (c) 2010-2026 William Bittle  http://www.dyn4j.org/
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
import java.awt.event.KeyEvent;

import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.dynamics.joint.WeldJoint;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.samples.framework.Camera;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.samples.framework.SimulationFrame;
import org.dyn4j.samples.framework.input.BooleanStateKeyboardInputHandler;

/**
 * Class used to show how angular limits are interpreted.
 * @author William Bittle
 * @version 6.0.0
 * @since 6.0.0
 */
public class AngularLimits extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = 4353559329206556448L;

	private int jointType = 0;
	private final BooleanStateKeyboardInputHandler cycleJointType;
	
	/**
	 * Default constructor for the window
	 */
	public AngularLimits() {
		super("AngularLimits");
		
		this.cycleJointType = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_S);
		this.cycleJointType.install();
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 */
	protected void initializeWorld() {
		// easy scenarios
		createScenario(5.0, 0.0, -Math.toRadians(45), Math.toRadians(45), 0.0);		// lower < upper centered around 0 (STANDARD scenario)
		createScenario(-2.0, 0.0, Math.toRadians(10), Math.toRadians(90), 0.0);		// lower < upper both positive (STANDARD scenario)
		createScenario(2.0, 0.0, -Math.toRadians(90), -Math.toRadians(10), 0.0);	// lower < upper both negative (STANDARD scenario)
		createScenario(-5.0, 0.0, Math.toRadians(45), -Math.toRadians(45), 0.0);	// lower > upper opposite signs (REVERSED scenario)
		
		// larger values
		createScenario(5.0, 3.0, -Math.toRadians(120), Math.toRadians(120), 0.0);	// lower < upper centered around 0 (STANDARD scenario)
		createScenario(-2.0, 3.0, Math.toRadians(60), Math.toRadians(120), 0.0);	// lower < upper both positive (STANDARD scenario)
		createScenario(2.0, 3.0, -Math.toRadians(120), -Math.toRadians(60), 0.0);	// lower < upper both negative (STANDARD scenario)
		createScenario(-5.0, 3.0, Math.toRadians(120), -Math.toRadians(120), 0.0);	// lower > upper opposite signs (REVERSED scenario)

		// reference angle adjusted
		createScenario(5.0, 6.0, -Math.toRadians(120), Math.toRadians(120), -0.5 * Math.PI);	// lower < upper centered around 0 w/ reference angle offset (STANDARD scenario)
		createScenario(-2.0, 6.0, Math.toRadians(60), Math.toRadians(120), -0.5 * Math.PI);		// lower < upper upper both positive w/ reference angle offset (STANDARD scenario)
		createScenario(2.0, 6.0, -Math.toRadians(120), -Math.toRadians(60), -0.5 * Math.PI);	// lower < upper both negative w/ reference angle offset (STANDARD scenario)
		createScenario(-5.0, 6.0, Math.toRadians(120), -Math.toRadians(120), -0.5 * Math.PI);	// lower < upper opposite signs w/ reference angle offset (REVERSED scenario)
		
		// exact same angle
		createScenario(-5.0, 9.0, -Math.toRadians(45), -Math.toRadians(45), 0.0);	// lower = upper negative
		createScenario(-2.0, 9.0, Math.toRadians(45), Math.toRadians(45), 0.0);		// lower = upper positive
		createScenario(2.0, 9.0, -Math.toRadians(160), -Math.toRadians(160), 0.0);	// lower = upper negative
		createScenario(5.0, 9.0, Math.toRadians(160), Math.toRadians(160), 0.0);	// lower = upper positive
		
		// crossed angles
		createScenario(-5.0, 12.0, Math.toRadians(135), Math.toRadians(90), 0.0);	// lower > upper both positive (CROSSED scenario)
		createScenario(-2.0, 12.0, -Math.toRadians(90), -Math.toRadians(135), 0.0);	// lower > upper both negative (CROSSED scenario)
		createScenario(2.0, 12.0, -Math.toRadians(90), Math.toRadians(225), 0.0);	// lower < upper different signs w/ upper > pi (CROSSED scenario)
		createScenario(5.0, 12.0, -Math.toRadians(225), Math.toRadians(90), 0.0);	// lower < upper different signs w/ lower < -pi (CROSSED scenario)
				
		// Failure cases
//		// close to PI (barely works)
//		createScenario(-5.0, 15.0, 0.95*Math.PI, 0.95*Math.PI, 0.5*Math.PI);
//		// close to PI (failure if fast enough)
//		createScenario(-2.0, 15.0, -Math.toRadians(175), Math.toRadians(175), 0.0);
//		// negative PI (failure)
//		createScenario(2.0, 15.0, -Math.PI, -Math.PI, 0.0);
//		// positive PI (failure)
//		createScenario(5.0, 15.0, Math.PI, Math.PI, 0.0);
	}
	
	private void createScenario(double x, double y, double cwl, double ccwl, double ra) {
		Rectangle shapeA = new Rectangle(1.0, 1.0);
		SimulationBody bodyA = new SimulationBody();
		bodyA.setColor(Color.BLUE);
		bodyA.addFixture(shapeA);
		bodyA.setMass(MassType.NORMAL);
//		bodyA.rotate(Math.toRadians(45));
		bodyA.translate(x, y - 2.5);
		this.world.addBody(bodyA);
		
		Rectangle shapeB = new Rectangle(1.0, 1.0);
		SimulationBody bodyB = new SimulationBody();
		bodyB.setColor(Color.RED);
		bodyB.addFixture(shapeB);
		bodyB.setMass(MassType.INFINITE);
		bodyB.translate(x - 0.5, y - 2.0);
		this.world.addBody(bodyB);
		
		if (this.jointType == 0) {
			RevoluteJoint<SimulationBody> jointA = new RevoluteJoint<SimulationBody>(bodyA, bodyB, bodyB.getWorldCenter());
			jointA.setCollisionAllowed(false);
			jointA.setLimits(cwl, ccwl);
			jointA.setLimitsReferenceAngle(ra);
			jointA.setLimitsEnabled(true);
			this.world.addJoint(jointA);
		} else {
			WeldJoint<SimulationBody> jointA = new WeldJoint<SimulationBody>(bodyA, bodyB, bodyB.getWorldCenter());
			jointA.setCollisionAllowed(false);
			jointA.setLimits(cwl, ccwl);
			jointA.setLimitsReferenceAngle(ra);
			jointA.setLimitsEnabled(true);
			jointA.setSpringEnabled(true);
			jointA.setSpringDamperEnabled(true);
			jointA.setSpringDampingRatio(0.3);
			jointA.setSpringFrequency(8.0);
			jointA.setMaximumSpringTorque(100);
			jointA.setMaximumSpringTorqueEnabled(true);
			this.world.addJoint(jointA);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#initializeCamera(org.dyn4j.samples.framework.Camera)
	 */
	@Override
	protected void initializeCamera(Camera camera) {
		super.initializeCamera(camera);
		camera.scale = 30.0;
		camera.offsetY = -100;
	}
	
	@Override
	protected void printControls() {
		super.printControls();
		printControl("Switch Joints", "S", "Use the s key to switch between Revolute and Weld joints");
	}
	
	@Override
	protected void handleEvents() {
		super.handleEvents();
		if (this.cycleJointType.isActiveButNotHandled()) {
			this.cycleJointType.setHasBeenHandled(true);
			if (this.jointType == 0) {
				this.jointType = 1;
			} else {
				this.jointType = 0;
			}
			this.reset();
		}
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		AngularLimits simulation = new AngularLimits();
		simulation.run();
	}
}
