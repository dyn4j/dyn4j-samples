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

import java.awt.event.KeyEvent;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.ContinuousDetectionMode;
import org.dyn4j.dynamics.joint.WheelJoint;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.samples.framework.Camera;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.samples.framework.SimulationFrame;
import org.dyn4j.samples.framework.input.BooleanStateKeyboardInputHandler;

/**
 * A scene where a truck is carrying other objects.
 * @author William Bittle
 * @since 5.0.1
 * @version 4.1.1
 */
public class Truck extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = 4610026750399376114L;

	private final BooleanStateKeyboardInputHandler left;
	private final BooleanStateKeyboardInputHandler right;
	private final BooleanStateKeyboardInputHandler stop;
	
	private double speed;
	private WheelJoint<SimulationBody> wj1;
	private WheelJoint<SimulationBody> wj2;
	
	/**
	 * Default constructor.
	 */
	public Truck() {
		super("Truck");
		
		this.left = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_LEFT);
		this.right = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_RIGHT);
		this.stop = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_S);
		
		this.left.install();
		this.right.install();
		this.stop.install();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#printControls()
	 */
	@Override
	protected void printControls() {
		super.printControls();
		
		printControl("Move Left", "Left", "Use the left key to accelerate left");
		printControl("Move Right", "Right", "Use the right key to accelerate right");
		printControl("Stop", "s", "Use the s key to stop");
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 */
	protected void initializeWorld() {
		this.world.getSettings().setContinuousDetectionMode(ContinuousDetectionMode.NONE);
		
	    // Ground
		SimulationBody ground = new SimulationBody();
		ground.addFixture(Geometry.createRectangle(50.0, 1.0), 1.0, 0.5, 0.0);
	    ground.translate(new Vector2(-2.0, -4.0));
	    ground.setMass(MassType.INFINITE);
	    world.addBody(ground);

	    // Ramp
	    SimulationBody ramp = new SimulationBody();
	    ramp.addFixture(Geometry.createRectangle(10.0, 0.2));
	    ramp.rotate(Math.toRadians(10.0));
	    ramp.translate(new Vector2(0.0, -3.0));
	    ramp.setMass(MassType.INFINITE);
	    world.addBody(ramp);
		
	    // Frame
	    SimulationBody truck = new SimulationBody();
	    {// drive shaft
	    	truck.addFixture(Geometry.createRectangle(5.0, 0.25));
	    }
	    {// body
	    	Convex c = Geometry.createRectangle(5.2, 0.5);
	      	c.translate(new Vector2(0.0, 0.5));
	      	BodyFixture bf = new BodyFixture(c);
	      	truck.addFixture(bf);
	    }
	    {// tailgate
	    	Convex c = Geometry.createRectangle(0.25, 0.5);
	    	c.translate(new Vector2(-2.4, 1.0));
	    	BodyFixture bf = new BodyFixture(c);
	    	truck.addFixture(bf);
	    }
	    {// cab
	    	Convex c = Geometry.createRectangle(2.0, 2.0);
	    	c.translate(new Vector2(2.4, 1.0));
		    BodyFixture bf = new BodyFixture(c);
		    truck.addFixture(bf);
	    }
	    truck.translate(new Vector2(-23.0, -3.0));
	    truck.setMass(MassType.NORMAL);
	    world.addBody(truck);

	    // Rear Wheel
	    SimulationBody rearWheel = new SimulationBody();
	    rearWheel.addFixture(Geometry.createCircle(0.5), 1.0, 0.5, 0.0);
	    rearWheel.translate(-25.0, -3.0);
	    rearWheel.setMass(MassType.NORMAL);
	    world.addBody(rearWheel);

	    // Front Wheel
	    SimulationBody frontWheel = new SimulationBody();
	    frontWheel.addFixture(Geometry.createCircle(0.5), 1.0, 0.5, 0.0);
	    frontWheel.translate(-21.0, -3.0);
	    frontWheel.setMass(MassType.NORMAL);
	    world.addBody(frontWheel);

	    // Rear Motor
	    WheelJoint<SimulationBody> rearWheelJoint = new WheelJoint<SimulationBody>(truck, rearWheel, new Vector2(-25.0, -3.0), new Vector2(0.0, -1.0));
	    rearWheelJoint.setMotorEnabled(true);
	    rearWheelJoint.setMotorSpeed(0.0);
	    rearWheelJoint.setMaximumMotorTorqueEnabled(true);
	    rearWheelJoint.setMaximumMotorTorque(1000.0);
	    world.addJoint(rearWheelJoint);
	    this.wj1 = rearWheelJoint;
	    
	    // Front Motor
	    WheelJoint<SimulationBody> frontWheelJoint = new WheelJoint<SimulationBody>(truck, frontWheel, new Vector2(-21.0, -3.0), new Vector2(0.0, -1.0));
	    frontWheelJoint.setMotorEnabled(true);
	    frontWheelJoint.setMotorSpeed(0.0);
	    frontWheelJoint.setMaximumMotorTorqueEnabled(true);
	    frontWheelJoint.setMaximumMotorTorque(1000);
	    world.addJoint(frontWheelJoint);
	    this.wj2 = frontWheelJoint;
	    
	    // put some stuff in the back of the truck
	    double x = -24;
	    double y = -2.0;
	    double s = 0.25;
	    for (int i = 0; i < 10; i++) {
	    	for (int j = 0; j < 3; j++) {
	    	    SimulationBody box = new SimulationBody();
	    	    box.addFixture(Geometry.createSquare(s), 1.0, 0.2, 0.0);
	    	    box.translate(new Vector2(x + (i * s), y + (j * s)));
	    	    box.setMass(MassType.NORMAL);
	    	    world.addBody(box);
	    	}
	    }
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#initializeCamera(org.dyn4j.samples.framework.Camera)
	 */
	@Override
	protected void initializeCamera(Camera camera) {
		super.initializeCamera(camera);
		camera.scale = 16.0;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#reset()
	 */
	@Override
	public void reset() {
		super.reset();
		this.speed = 0.0;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#handleEvents()
	 */
	@Override
	protected void handleEvents() {
		super.handleEvents();
		
		if (this.left.isActive()) {
			this.speed += Math.PI / 100;
		}
		if (this.right.isActive()) {
			this.speed -= Math.PI / 100;
		}
		if (this.stop.isActive()) {
			this.speed = 0.0;
		}
		
		if (this.wj1 != null) {
			this.wj1.setMotorSpeed(this.speed);
		}
		if (this.wj2 != null) {
			this.wj2.setMotorSpeed(this.speed);
		}
		
		
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		Truck simulation = new Truck();
		simulation.run();
	}
}
