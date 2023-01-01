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

import java.util.List;

import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Link;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.samples.framework.Camera;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.samples.framework.SimulationFrame;

/**
 * A scene where a bowling ball hits a set of pins.
 * <p>
 * The pins can interact with anything in the ALL and BALL groups, but not with themselves.
 * @author William Bittle
 * @version 5.0.0
 * @since 3.2.1
 */
public class Bowling extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = 6102930425312889302L;

	private static final CategoryFilter ALL = new CategoryFilter(1, Long.MAX_VALUE);
	private static final CategoryFilter BALL = new CategoryFilter(2, Long.MAX_VALUE);
	private static final CategoryFilter PIN = new CategoryFilter(4, 1 | 2 | 8);
	private static final CategoryFilter NOT_BALL = new CategoryFilter(8, 1 | 4);
	
	/**
	 * Default constructor.
	 */
	public Bowling() {
		super("Bowling");
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 */
	protected void initializeWorld() {
		SimulationBody floor = new SimulationBody();
		BodyFixture fixture = floor.addFixture(Geometry.createRectangle(15.0, 0.2), 0.9);
		fixture.setFilter(ALL);
		floor.setMass(MassType.INFINITE);
		this.world.addBody(floor);
		
		SimulationBody wall = new SimulationBody();
		fixture = wall.addFixture(Geometry.createRectangle(0.2, 3.0));
		fixture.setFilter(NOT_BALL);
		wall.setMass(MassType.INFINITE);
		wall.translate(7.0, 1.5);
		this.world.addBody(wall);
		
		SimulationBody top = new SimulationBody();
		fixture = top.addFixture(Geometry.createRectangle(2.0, 0.2));
		fixture.setFilter(ALL);
		fixture = top.addFixture(Geometry.createRectangle(1.0, 0.2));
		fixture.setFilter(ALL);
		fixture.getShape().rotate(Math.toDegrees(60));
		fixture.getShape().translate(-1.25, -0.25);
		top.setMass(MassType.INFINITE);
		top.translate(6.0, 2.0);
		this.world.addBody(top);
		
		SimulationBody channel = new SimulationBody();
		Vector2[] verts = new Vector2[] {
			new Vector2(7.0, 0.5),
			new Vector2(8.0, 0.5),
			new Vector2(8.5, 0.25),
			new Vector2(9.0, 0.0),
			new Vector2(8.5, -0.25),
			new Vector2(8.0, -0.5),
			new Vector2(-8.0, -1.0),
			new Vector2(-9.0, -0.5),
			new Vector2(-10.0, 0.0),
			new Vector2(-9.0, 0.5),
			new Vector2(-8.0, 1.0)
		};
		List<Link> links = Geometry.createLinks(verts, false);
		for (Link link : links) {
			channel.addFixture(link);
		}
		channel.setMass(MassType.INFINITE);
		this.world.addBody(channel);
		
		SimulationBody bowlingBall = new SimulationBody();
		fixture = new BodyFixture(Geometry.createCircle(0.109));
		fixture.setDensity(194.82);
		fixture.setRestitution(0.5);
		fixture.setFilter(BALL);
		bowlingBall.addFixture(fixture);
		bowlingBall.setMass(MassType.NORMAL);
		bowlingBall.setLinearVelocity(new Vector2(10.0, 0.0));
		bowlingBall.setAngularDamping(0.5);
		bowlingBall.translate(-3.0, 0.1);
		this.world.addBody(bowlingBall);
		
		// create some pins
		double x = 6;
		for (int i = 0; i < 10; i++) {
			SimulationBody pin = new SimulationBody();
			BodyFixture bf = pin.addFixture(Geometry.createRectangle(0.1, 0.5), 2.0, 0.5, 0.6);
			bf.setFilter(PIN);
			pin.setMass(MassType.NORMAL);
			pin.translate(x, 0.1 + 0.25);
			this.world.addBody(pin);
			x += 0.06;
		}
	}
	
	@Override
	protected void initializeCamera(Camera camera) {
		super.initializeCamera(camera);
		camera.scale = 64.0;
		camera.offsetX = -150.0;
		camera.offsetY = -100.0;
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		Bowling simulation = new Bowling();
		simulation.run();
	}
}
