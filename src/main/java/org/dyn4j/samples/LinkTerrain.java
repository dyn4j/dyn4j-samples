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

import java.util.List;

import org.dyn4j.collision.narrowphase.Sat;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Link;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.samples.framework.SimulationFrame;

/**
 * A simple scene of a terrain made using the {@link Link}s to avoid
 * the internal edge collision problem.
 * @author William Bittle
 * @version 3.2.2
 * @since 3.2.2
 */
public class LinkTerrain extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = -3675099977835892473L;

	/**
	 * Default constructor for the window
	 */
	public LinkTerrain() {
		super("LinkTerrain", 64.0);
		
		this.pause();
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 */
	protected void initializeWorld() {
//		this.world.setGravity(this.world.getGravity().negate());
		this.world.setNarrowphaseDetector(new Sat());
		
		// the terrain
		List<Link> links = Geometry.createLinks(
				new Vector2[] {
					// clockwise winding
					new Vector2(-6.0,  0.5),
					new Vector2( 0.0,  0.0),
					new Vector2( 2.0,  0.0),
					new Vector2( 4.0,  0.2),
					new Vector2( 4.5,  0.3),
					new Vector2( 6.0, -0.5)
					// upside down
//					new Vector2(-6.0, -0.5),
//					new Vector2( 0.0, -0.0),
//					new Vector2( 2.0, -0.0),
//					new Vector2( 4.0, -0.2),
//					new Vector2( 4.5, -0.3),
//					new Vector2( 6.0,  0.5)
					// counter-clockwise winding
//					new Vector2( 6.0, -0.5),
//					new Vector2( 4.5,  0.3),
//					new Vector2( 4.0,  0.2),
//					new Vector2( 2.0,  0.0),
//					new Vector2( 0.0,  0.0),
//					new Vector2(-6.0,  0.5)
					// another terrain
//					new Vector2(-5.0,  0.5),
//		    		new Vector2(-0.0,  0.0),
//		    		new Vector2( 1.0,  0.0),
//		    		new Vector2( 1.5,  0.2),
//		    		new Vector2( 2.5,  0.0),
//		    		new Vector2( 3.5, -0.5),
//		    		new Vector2( 6.0, -0.4),
//		    		new Vector2( 7.0, -0.3)
		    		// reverse winding
//					new Vector2( 7.0, -0.3),
//					new Vector2( 6.0, -0.4),
//					new Vector2( 3.5, -0.5),
//					new Vector2( 2.5,  0.0),
//					new Vector2( 1.5,  0.2),
//					new Vector2( 1.0,  0.0),
//					new Vector2(-0.0,  0.0),
//		    		new Vector2(-5.0,  0.5)
		    		// cliff
//					new Vector2(-5.0,  0.0),
//		    		new Vector2( 1.0,  0.0),
//		    		new Vector2(-3.0, -1.0)
					// cliff (reversed winding)
//					new Vector2(-5.0,  0.0),
//		    		new Vector2( 1.0,  0.0),
//		    		new Vector2(-3.0, -1.0)
				}, false);
		
		SimulationBody floor = new SimulationBody();
		for (Link link : links) {
			floor.addFixture(link);
		}
		floor.setMass(MassType.INFINITE);
		this.world.addBody(floor);
		
		// the body
		SimulationBody slider = new SimulationBody();
		slider.addFixture(Geometry.createSquare(0.25));
//		List<Link> sLinks = Geometry.createLinks(new Vector2[] {
//				new Vector2(0.5, 0.0),
//				new Vector2(0.0, 1.0),
//				new Vector2(-0.5, 0.0)
//		}, true);
//		for (Link link : sLinks) {
//			slider.addFixture(link);
//		}
		slider.setMass(MassType.NORMAL);
		slider.setLinearVelocity(6.2, 0);
//		slider.setLinearVelocity(5, 0);
		slider.translate(-5.5, 1.0);
//		slider.translate(-5.5, -1.0);
		this.world.addBody(slider);
	}

	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		LinkTerrain simulation = new LinkTerrain();
		simulation.run();
	}
}
