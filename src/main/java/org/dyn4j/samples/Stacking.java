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

import java.awt.event.MouseEvent;

import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.samples.framework.Camera;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.samples.framework.SimulationFrame;
import org.dyn4j.samples.framework.input.BooleanStateMouseInputHandler;

/**
 * A simple scene where you use Mouse Button 3 to create boxes.
 * @author William Bittle
 * @version 5.0.1
 * @since 3.2.0
 */
public class Stacking extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = -1366264828445805140L;

	private final BooleanStateMouseInputHandler create;
	
	/**
	 * Default constructor.
	 */
	public Stacking() {
		super("Stacking");
		
		this.create = new BooleanStateMouseInputHandler(this.canvas, MouseEvent.BUTTON3);
		
		this.create.install();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#initializeCamera(org.dyn4j.samples.framework.Camera)
	 */
	@Override
	protected void initializeCamera(Camera camera) {
		super.initializeCamera(camera);
		camera.scale = 32.0;
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 */
	protected void initializeWorld() {
		SimulationBody floor = new SimulationBody();
	    floor.addFixture(Geometry.createRectangle(20, 1), 1.0, 0.5, 0.0);
	    floor.setMass(MassType.INFINITE);
	    this.world.addBody(floor);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#printControls()
	 */
	@Override
	protected void printControls() {
		super.printControls();
		
		this.printControl("Create", "RMB", "Click the right mouse button to create a square");
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#handleEvents()
	 */
	@Override
	protected void handleEvents() {
		super.handleEvents();
		
		// see if the user clicked
		if (this.create.isActiveButNotHandled()) {
			this.create.setHasBeenHandled(true);
			// convert from screen space to world space coordinates
			Vector2 v = this.toWorldCoordinates(this.create.getMouseLocation());
			
			// create a new body
			SimulationBody no = new SimulationBody();
			no.addFixture(Geometry.createSquare(0.5), 1.0, 0.8, 0.0);
			no.translate(v.x, v.y);
			no.setMass(MassType.NORMAL);
			this.world.addBody(no);
		}
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		Stacking simulation = new Stacking();
		simulation.run();
	}
}
