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
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.dyn4j.dynamics.joint.MotorJoint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.samples.framework.Camera;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.samples.framework.SimulationFrame;
import org.dyn4j.world.World;

/**
 * Use the mouse to move the green body to the blue body. Notice how
 * the green body tries to work around the obstacles, but does not go
 * through them.
 * @author William Bittle
 * @since 5.0.0
 * @version 4.1.1
 */
public class Maze extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = -4132057742762298086L;

	/** The controller body */
	private SimulationBody controller;
	
	/** The current mouse drag point */
	private Point point;
	
	/**
	 * A custom mouse adapter to track mouse drag events.
	 * @author William Bittle
	 */
	private final class CustomMouseAdapter extends MouseAdapter {
		@Override
		public void mouseMoved(MouseEvent e) {
			// just create a new point and store it locally
			// later, on the next update we'll check for it
			point = new Point(e.getX(), e.getY());
		}
	}

	/**
	 * Default constructor for the window
	 */
	public Maze() {
		super("Maze");
		
		this.setMousePickingEnabled(false);
		
		// setup the mouse listening
		MouseAdapter ml = new CustomMouseAdapter();
		this.canvas.addMouseMotionListener(ml);
		this.canvas.addMouseWheelListener(ml);
		this.canvas.addMouseListener(ml);
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 */
	protected void initializeWorld() {
		// no gravity please
		this.world.setGravity(World.ZERO_GRAVITY);
		
		// player control setup
		
		this.controller = new SimulationBody(Color.CYAN);
	    this.controller.addFixture(Geometry.createCircle(0.25));
	    this.controller.setMass(MassType.INFINITE);
	    this.controller.setAtRestDetectionEnabled(false);
	    this.world.addBody(this.controller);
	    
	    SimulationBody player = new SimulationBody(Color.GREEN);
	    player.addFixture(Geometry.createCircle(0.25));
	    player.setMass(MassType.NORMAL);
	    player.setAtRestDetectionEnabled(false);
	    this.world.addBody(player);
	    
	    MotorJoint<SimulationBody> control = new MotorJoint<SimulationBody>(player, this.controller);
	    control.setCollisionAllowed(false);
	    control.setMaximumForce(1000.0);
	    control.setMaximumTorque(1000.0);
	    this.world.addJoint(control);
	    
	    // maze
	    
	    double wallWidth = 0.5;
	    double mazeSize = 30;
	    double mazeOffset = mazeSize / 2.0 - wallWidth / 2.0;
	    double pathWidth = 1.5;
	    
	    // outer walls
	    
	    SimulationBody wall = new SimulationBody();
	    wall.addFixture(Geometry.createRectangle(wallWidth, mazeSize));
	    wall.setMass(MassType.INFINITE);
	    wall.translate(mazeOffset, 0);
	    this.world.addBody(wall);
	    
	    SimulationBody wall2 = new SimulationBody();
	    wall2.addFixture(Geometry.createRectangle(mazeSize - pathWidth, wallWidth));
	    wall2.setMass(MassType.INFINITE);
	    wall2.translate(pathWidth * 0.5, mazeOffset);
	    this.world.addBody(wall2);
	    
	    SimulationBody wall3 = new SimulationBody();
	    wall3.addFixture(Geometry.createRectangle(mazeSize - pathWidth, wallWidth));
	    wall3.setMass(MassType.INFINITE);
	    wall3.translate(-pathWidth * 0.5, -mazeOffset);
	    this.world.addBody(wall3);
	    
	    SimulationBody wall4 = new SimulationBody();
	    wall4.addFixture(Geometry.createRectangle(wallWidth, mazeSize));
	    wall4.setMass(MassType.INFINITE);
	    wall4.translate(-mazeOffset, 0);
	    this.world.addBody(wall4);
	    
	    // inner walls
	    
	    for (int i = 0; i < 7; i++) {
		    SimulationBody wall5 = new SimulationBody();
		    wall5.addFixture(Geometry.createRectangle(wallWidth, mazeSize - pathWidth));
		    wall5.setMass(MassType.INFINITE);
		    wall5.translate(-mazeOffset + (i + 1) * pathWidth, (i % 2 == 0 ? 1 : -1) * pathWidth / 2.0);
		    this.world.addBody(wall5);
	    }
	    
	    for (int i = 0; i < 18; i++) {
		    SimulationBody wall5 = new SimulationBody();
		    wall5.addFixture(Geometry.createRectangle(mazeSize / 2 - pathWidth, wallWidth));
		    wall5.setMass(MassType.INFINITE);
		    wall5.translate(-mazeOffset + 12 * pathWidth + (i % 2 == 0 ? 1 : -1) * pathWidth / 2.0, -mazeOffset + (i + 1) * pathWidth);
		    this.world.addBody(wall5);
	    }
	    
	    for (int i = 0; i < 3; i++) {
		    SimulationBody wall5 = new SimulationBody();
		    wall5.addFixture(Geometry.createRectangle(wallWidth, mazeSize - pathWidth));
		    wall5.setMass(MassType.INFINITE);
		    wall5.translate(0 + (i + 7) * pathWidth, (i % 2 == 0 ? -1 : 1) * pathWidth / 2.0);
		    this.world.addBody(wall5);
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
	 * @see org.dyn4j.samples.framework.SimulationFrame#handleEvents()
	 */
	@Override
	protected void handleEvents() {
		super.handleEvents();
		
		// check if the mouse has moved/dragged
		if (this.point != null) {
			Vector2 v = this.toWorldCoordinates(this.point);
			
			// reset the transform of the controller body
			Transform tx = new Transform();
			tx.translate(v.x, v.y);
			this.controller.setTransform(tx);
			
			// clear the point
			this.point = null;
		}
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		Maze simulation = new Maze();
		simulation.run();
	}
}
