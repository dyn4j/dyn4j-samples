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

import org.dyn4j.collision.Filter;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.samples.framework.SimulationFrame;
import org.dyn4j.world.World;

/**
 * A simple scene of two billiard balls colliding with one another
 * and a wall.
 * <p>
 * Primarily used to illustrate the computation of the mass and size
 * of the balls.  See the {@link Billiards#initializeWorld()} method.
 * @author William Bittle
 * @version 4.1.1
 * @since 3.2.0
 */
public final class Billiards extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = -8518496343422955267L;

	/**
	 * Default constructor.
	 */
	public Billiards() {
		super("Billiards", 250.0);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.SimulationFrame#initializeWorld()
	 */
	@Override
	protected void initializeWorld() {
		// no gravity on a top-down view of a billiards game
		this.world.setGravity(World.ZERO_GRAVITY);
		this.world.getSettings().setRestitutionVelocity(0.0);
		
		// create all your bodies/joints
		
		final double edgeDepth = 0.29 / 2.0;
		final double tableWidth = 1.83;
		final double tableHeight = 1.12;
		
		final double halfTableWidth = tableWidth / 2.0;
		final double halfTableHeight = tableHeight / 2.0;
		final double halfEdgeDepth = edgeDepth / 2.0;
		
		// 2.25 in diameter = 0.028575 m radius
		final double ballRadius = 0.028575;
		
		// 0.126 oz/in^3 = 217.97925 kg/m^3
		final double ballDensity = 217.97925;
		
		final double ballFriction = 0.08;
		final double ballRestitution = 0.9;
		
		// I wouldn't do this in practice - I'm being lazy and using a body
		// to render a green bottom to the billiards table.  Instead, you should
		// just render the green bottom and not use a body.
		SimulationBody bottom = new SimulationBody(new Color(60, 164, 114));
		BodyFixture bf = bottom.addFixture(Geometry.createRectangle(tableWidth, tableHeight), 1.0, 0.0, 0.0);
		bf.setFilter(new Filter() {
			@Override
			public boolean isAllowed(Filter filter) {
				return false;
			}
		});
		bottom.setMass(MassType.INFINITE);
		world.addBody(bottom);
		
		SimulationBody wallRight = new SimulationBody(new Color(150, 75, 0));
		wallRight.addFixture(Geometry.createRectangle(edgeDepth, tableHeight), 1.0, 0.4, 0.3);
		wallRight.translate(halfTableWidth - halfEdgeDepth, 0);
		wallRight.setMass(MassType.INFINITE);
		world.addBody(wallRight);
		
		SimulationBody wallLeft = new SimulationBody(new Color(150, 75, 0));
		wallLeft.addFixture(Geometry.createRectangle(edgeDepth, tableHeight), 1.0, 0.4, 0.3);
		wallLeft.translate(-halfTableWidth + halfEdgeDepth, 0);
		wallLeft.setMass(MassType.INFINITE);
		world.addBody(wallLeft);

		SimulationBody wallTop = new SimulationBody(new Color(150, 75, 0));
		wallTop.addFixture(Geometry.createRectangle(tableWidth, edgeDepth), 1.0, 0.4, 0.3);
		wallTop.translate(0, halfTableHeight - halfEdgeDepth);
		wallTop.setMass(MassType.INFINITE);
		world.addBody(wallTop);
		
		SimulationBody wallBottom = new SimulationBody(new Color(150, 75, 0));
		wallBottom.addFixture(Geometry.createRectangle(tableWidth, edgeDepth), 1.0, 0.4, 0.3);
		wallBottom.translate(0, -halfTableHeight + halfEdgeDepth);
		wallBottom.setMass(MassType.INFINITE);
		world.addBody(wallBottom);
		
		SimulationBody cueBall = new SimulationBody(new Color(255, 255, 255));
		cueBall.addFixture(Geometry.createCircle(ballRadius), ballDensity, ballFriction, ballRestitution);
		cueBall.translate(-0.25, 0.0);
		cueBall.setLinearVelocity(2.0, 0.0);
		cueBall.setLinearDamping(0.3);
		cueBall.setAngularDamping(0.8);
		cueBall.setMass(MassType.NORMAL);
		this.world.addBody(cueBall);
		
		// billiard colors
		Color[] colors = new Color[] {
			// solid
			new Color(255, 215, 0),	
			new Color(0, 0, 255),
			new Color(255, 0, 0),
			new Color(75, 0, 130),
			new Color(255, 69, 0),
			new Color(34, 139, 34),
			new Color(128, 0, 0),
			new Color(0, 0, 0),
			
			// striped (just do a lighter color)
			new Color(255, 215, 0).darker(),	
			new Color(0, 0, 255).darker(),
			new Color(255, 0, 0).darker(),
			new Color(75, 0, 130).brighter(),
			new Color(255, 69, 0).darker(),
			new Color(34, 139, 34).brighter(),
			new Color(128, 0, 0).brighter(),
			new Color(0, 0, 0).brighter()
		};
		
		final int rackSize = 5;
		final double sx = 0.45;
		final double sy = 0.0;
		
		// 5 columns
		int n = 0;
		for (int i = 0; i < rackSize; i++) {
			double x = sx - (ballRadius * 2.0 * (double)i);
			double columnHeight = ballRadius * 2.0 * (rackSize - i); 
			double csy = columnHeight / 2.0;
			// 5 - i rows
			for (int j = 0; j < rackSize - i; j++) {
				double y = sy + csy - (ballRadius * 2.0 * j);
				
				SimulationBody ball = new SimulationBody(colors[n]);
				ball.addFixture(Geometry.createCircle(ballRadius), ballDensity, ballFriction, ballRestitution);
				ball.translate(x, y);
				ball.setLinearDamping(0.4);
				ball.setAngularDamping(0.8);
				ball.setMass(MassType.NORMAL);
				this.world.addBody(ball);
				
				n++;
			}
		}
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		Billiards simulation = new Billiards();
		simulation.run();
	}
}
