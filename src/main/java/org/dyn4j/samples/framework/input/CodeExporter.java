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
package org.dyn4j.samples.framework.input;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.dyn4j.Epsilon;
import org.dyn4j.Version;
import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.collision.Bounds;
import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.collision.Filter;
import org.dyn4j.collision.broadphase.AABBExpansionMethod;
import org.dyn4j.collision.broadphase.AABBProducer;
import org.dyn4j.collision.broadphase.BroadphaseDetector;
import org.dyn4j.collision.broadphase.BroadphaseFilter;
import org.dyn4j.collision.broadphase.CollisionItemAABBProducer;
import org.dyn4j.collision.broadphase.CollisionItemBroadphaseDetector;
import org.dyn4j.collision.broadphase.CollisionItemBroadphaseFilter;
import org.dyn4j.collision.broadphase.DynamicAABBTree;
import org.dyn4j.collision.broadphase.NullAABBExpansionMethod;
import org.dyn4j.collision.broadphase.Sap;
import org.dyn4j.collision.broadphase.StaticValueAABBExpansionMethod;
import org.dyn4j.collision.continuous.ConservativeAdvancement;
import org.dyn4j.collision.continuous.TimeOfImpactDetector;
import org.dyn4j.collision.manifold.ClippingManifoldSolver;
import org.dyn4j.collision.manifold.ManifoldSolver;
import org.dyn4j.collision.narrowphase.Gjk;
import org.dyn4j.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.collision.narrowphase.Sat;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.ContinuousDetectionMode;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.joint.AngleJoint;
import org.dyn4j.dynamics.joint.DistanceJoint;
import org.dyn4j.dynamics.joint.FrictionJoint;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.MotorJoint;
import org.dyn4j.dynamics.joint.PinJoint;
import org.dyn4j.dynamics.joint.PrismaticJoint;
import org.dyn4j.dynamics.joint.PulleyJoint;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.dynamics.joint.WeldJoint;
import org.dyn4j.dynamics.joint.WheelJoint;
import org.dyn4j.geometry.Capsule;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Ellipse;
import org.dyn4j.geometry.HalfEllipse;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Slice;
import org.dyn4j.geometry.Triangle;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.World;

/**
 * A simple example of how you might serialize the state of a world.
 * @author William Bittle
 * @version 5.0.0
 * @since 4.1.1
 */
public class CodeExporter {
	/** The line separator for the system */
	private static final String NEW_LINE = System.getProperty("line.separator");
	
	/** One tab */
	private static final String TAB1 = "  ";
	
	/** Two tabs */
	private static final String TAB2 = TAB1 + TAB1;
	
	/** Three tabs */
	private static final String TAB3 = TAB1 + TAB1 + TAB1;
	
	/**
	 * Exports the given world and settings to Java code.
	 * <p>
	 * Returns a string containing the code for the export.
	 * @param name the name of the generated class
	 * @param simulation the simulation to export
	 * @return String
	 */
	public static final String export(String name, World<?> world) {
		StringBuilder sb = new StringBuilder();
		// this map contains the id to output name for bodies
		Map<Object, String> idNameMap = new HashMap<Object, String>();
		
		sb
		// imports
		.append("import java.util.*;").append(NEW_LINE)
		.append("import org.dyn4j.collision.*;").append(NEW_LINE)
		.append("import org.dyn4j.collision.broadphase.*;").append(NEW_LINE)
		.append("import org.dyn4j.collision.continuous.*;").append(NEW_LINE)
		.append("import org.dyn4j.collision.manifold.*;").append(NEW_LINE)
		.append("import org.dyn4j.collision.narrowphase.*;").append(NEW_LINE)
		.append("import org.dyn4j.dynamics.*;").append(NEW_LINE)
		.append("import org.dyn4j.dynamics.joint.*;").append(NEW_LINE)
		.append("import org.dyn4j.geometry.*;").append(NEW_LINE).append(NEW_LINE)
		// class declaration
		.append("// ").append(world.getUserData()).append(NEW_LINE)
		.append("// generated for dyn4j v").append(Version.getVersion()).append(NEW_LINE)
		.append("public class ").append(name).append(" { ").append(NEW_LINE).append(NEW_LINE)
		// private constructor
		.append(TAB1).append("private ").append(name).append("() {}").append(NEW_LINE).append(NEW_LINE)
		// single static setup method
		.append(TAB1).append("public static final void setup(World world) {").append(NEW_LINE)
		// get the settings object from the world
		.append(TAB2).append("Settings settings = world.getSettings();").append(NEW_LINE);
		
		// output settings
		sb.append(export(world.getSettings()));
		
		// output world settings
		sb.append(NEW_LINE);
		Vector2 g = world.getGravity();
		if (g == World.EARTH_GRAVITY || g.equals(0.0, -9.8)) {
			// don't output anything since its the default
		} else if (g == World.ZERO_GRAVITY || g.isZero()) {
			sb.append(TAB2).append("world.setGravity(World.ZERO_GRAVITY);").append(NEW_LINE);
		} else {
			sb.append(TAB2).append("world.setGravity(").append(export(g)).append(");").append(NEW_LINE);
		}

		CollisionItemBroadphaseDetector<?, ?> bpd = world.getBroadphaseDetector();
		AABBProducer<?> ap = bpd.getAABBProducer();
		AABBExpansionMethod<?> em = bpd.getAABBExpansionMethod();
		BroadphaseFilter<?> bpf = bpd.getBroadphaseFilter();
		BroadphaseDetector<?> bp = bpd.getDecoratedBroadphaseDetector();
		
		if (ap instanceof CollisionItemAABBProducer) {
			sb.append(TAB2).append("AABBProducer<CollisionItem<Body, BodyFixture>> aabbProducer = new CollisionItemAABBProducer<Body, BodyFixture>();").append(NEW_LINE);
		} else {
			throw new UnsupportedOperationException("The class " + ap.getClass().getName() + " is not known.");
		}
		
		if (em instanceof StaticValueAABBExpansionMethod) {
			StaticValueAABBExpansionMethod<?> method = (StaticValueAABBExpansionMethod<?>)em;
			sb.append(TAB2).append("AABBExpansionMethod<CollisionItem<Body, BodyFixture>> aabbExpansionMethod = new StaticValueAABBExpansionMethod<CollisionItem<Body, BodyFixture>>(" + method.getExpansion() + ");").append(NEW_LINE);
		} else if (em instanceof NullAABBExpansionMethod) {
			sb.append(TAB2).append("AABBExpansionMethod<CollisionItem<Body, BodyFixture>> aabbExpansionMethod = new NullAABBExpansionMethod<CollisionItem<Body, BodyFixture>>();").append(NEW_LINE);
		} else {
			throw new UnsupportedOperationException("The class " + em.getClass().getName() + " is not known."); 
		}
		
		if (bpf instanceof CollisionItemBroadphaseFilter) {
			sb.append(TAB2).append("BroadphaseFilter<CollisionItem<Body, BodyFixture>> broadphaseFilter = new CollisionItemBroadphaseFilter<Body, BodyFixture>();").append(NEW_LINE);
		} else {
			throw new UnsupportedOperationException("The class " + bpf.getClass().getName() + " is not known."); 
		}
		
		if (bp instanceof Sap) {
			sb.append(TAB2).append("BroadphaseDetector<CollisionItem<Body, BodyFixture>> bp = new Sap<CollisionItem<Body, BodyFixture>>(broadphaseFilter, aabbProducer, aabbExpansionMethod);").append(NEW_LINE);
		} else if (bp instanceof DynamicAABBTree) {
			sb.append(TAB2).append("BroadphaseDetector<CollisionItem<Body, BodyFixture>> bp = new DynamicAABBTree<CollisionItem<Body, BodyFixture>>(broadphaseFilter, aabbProducer, aabbExpansionMethod);").append(NEW_LINE);
		} else {
			throw new UnsupportedOperationException("The class " + bp.getClass().getName() + " is not known.");
		}
		
		sb.append(TAB2).append("CollisionItemBroadphaseDetector<Body, BodyFixture> bpd = new CollisionItemBroadphaseDetectorAdapter<Body, BodyFixture>(bp);").append(NEW_LINE);
		sb.append(TAB2).append("world.setBroadphaseDetector(bpd);").append(NEW_LINE);
		
		NarrowphaseDetector npd = world.getNarrowphaseDetector();
		if (npd instanceof Sat) {
			sb.append(TAB2).append("world.setNarrowphaseDetector(new Sat());").append(NEW_LINE);
		} else if (npd instanceof Gjk) {
			// don't output anything since its the default
		} else {
			throw new UnsupportedOperationException("The class " + npd.getClass().getName() + " is not known.");
		}
		
		// don't output anything since its the default
		ManifoldSolver msr = world.getManifoldSolver();
		if (msr instanceof ClippingManifoldSolver) {
			sb.append(TAB2).append("world.setManifoldSolver(new ClippingManifoldSolver());").append(NEW_LINE);
		} else {
			throw new UnsupportedOperationException("The class " + msr.getClass().getName() + " is not known.");
		}
		
		// don't output anything since its the default
		TimeOfImpactDetector tid = world.getTimeOfImpactDetector();
		if (tid instanceof ConservativeAdvancement) {
			sb.append(TAB2).append("world.setTimeOfImpactDetector(new ConservativeAdvancement());").append(NEW_LINE);
		} else {
			throw new UnsupportedOperationException("The class " + tid.getClass().getName() + " is not known.");
		}
		
		Bounds bounds = world.getBounds();
		if (bounds == null) {
			// don't output anything since its the default
		} else if (bounds instanceof AxisAlignedBounds) {
			AxisAlignedBounds aab = (AxisAlignedBounds)bounds;
			double w = aab.getWidth();
			double h = aab.getHeight();
			sb.append(NEW_LINE)
			.append(TAB2).append("AxisAlignedBounds bounds = new AxisAlignedBounds(").append(w).append(", ").append(h).append(");").append(NEW_LINE);
			if (!aab.getTranslation().isZero()) {
				sb.append(TAB2).append("bounds.translate(").append(export(aab.getTranslation())).append(");").append(NEW_LINE);
			}
			sb.append(TAB2).append("world.setBounds(bounds);").append(NEW_LINE)
			.append(NEW_LINE);
		} else {
			throw new UnsupportedOperationException("The class " + bounds.getClass().getName() + " is not known.");
		}
		
		// output bodies
		int bSize = world.getBodyCount();
		for (int i = 1; i < bSize + 1; i++) {
			Body body = (Body)world.getBody(i - 1);
			// save the id+name
			idNameMap.put(body, "body" + i);
			Mass mass = body.getMass();
			// output the body settings
			sb.append(TAB2).append("// body user data: ").append(body.getUserData()).append(NEW_LINE)
			.append(TAB2).append("GameObject body").append(i).append(" = new GameObject();").append(NEW_LINE);
			// add all fixtures
			int fSize = body.getFixtureCount();
			for (int j = 0; j < fSize; j++) {
				BodyFixture bf = body.getFixture(j);
				sb.append(TAB2).append("{// fixture user data: ").append(bf.getUserData()).append(NEW_LINE)
				// create the shape
				.append(export(bf.getShape(), TAB3))
				// create the fixture
				.append(TAB3).append("BodyFixture bf = new BodyFixture(c);").append(NEW_LINE);
				// set the fixture properties
				if (bf.isSensor()) {
					sb.append(TAB3).append("bf.setSensor(").append(bf.isSensor()).append(");").append(NEW_LINE);
				} // by default fixtures are not sensors
				if (bf.getDensity() != BodyFixture.DEFAULT_DENSITY) {
					sb.append(TAB3).append("bf.setDensity(").append(bf.getDensity()).append(");").append(NEW_LINE);
				}
				if (bf.getFriction() != BodyFixture.DEFAULT_FRICTION) {
					sb.append(TAB3).append("bf.setFriction(").append(bf.getFriction()).append(");").append(NEW_LINE);
				}
				if (bf.getRestitution() != BodyFixture.DEFAULT_RESTITUTION) {
					sb.append(TAB3).append("bf.setRestitution(").append(bf.getRestitution()).append(");").append(NEW_LINE);
				}
				if (bf.getRestitutionVelocity() != BodyFixture.DEFAULT_RESTITUTION_VELOCITY) {
					sb.append(TAB3).append("bf.setRestitutionVelocity(").append(bf.getRestitutionVelocity()).append(");").append(NEW_LINE);
				}
				// set the filter properties
				sb.append(export(bf.getFilter(), TAB3))
				// add the fixture to the body
				.append(TAB3).append("body").append(i).append(".addFixture(bf);").append(NEW_LINE)
				.append(TAB2).append("}").append(NEW_LINE);
			}
			// set the transform
			if (Math.abs(body.getTransform().getRotationAngle()) > Epsilon.E) {
				sb.append(TAB2).append("body").append(i).append(".rotate(Math.toRadians(").append(Math.toDegrees(body.getTransform().getRotationAngle())).append("));").append(NEW_LINE);
			}
			if (!body.getTransform().getTranslation().isZero()) {
				sb.append(TAB2).append("body").append(i).append(".translate(").append(export(body.getTransform().getTranslation())).append(");").append(NEW_LINE);
			}
			// set velocity
			if (!body.getLinearVelocity().isZero()) {
				sb.append(TAB2).append("body").append(i).append(".setLinearVelocity(").append(export(body.getLinearVelocity())).append(");").append(NEW_LINE);
			}
			if (Math.abs(body.getAngularVelocity()) > Epsilon.E) {
				sb.append(TAB2).append("body").append(i).append(".setAngularVelocity(Math.toRadians(").append(Math.toDegrees(body.getAngularVelocity())).append("));").append(NEW_LINE);
			}
			// set state properties
			if (!body.isEnabled()) {
				sb.append(TAB2).append("body").append(i).append(".setEnabled(false);").append(NEW_LINE);
			} // by default the body is active
			if (body.isAtRest()) {
				sb.append(TAB2).append("body").append(i).append(".setAtRest(true);").append(NEW_LINE);
			} // by default the body is awake
			if (!body.isAtRestDetectionEnabled()) {
				sb.append(TAB2).append("body").append(i).append(".setAtRestDetectionEnabled(false);").append(NEW_LINE);
			} // by default auto sleeping is true
			if (body.isBullet()) {
				sb.append(TAB2).append("body").append(i).append(".setBullet(true);").append(NEW_LINE);
			} // by default the body is not a bullet
			// set damping
			if (body.getLinearDamping() != Body.DEFAULT_LINEAR_DAMPING) {
				sb.append(TAB2).append("body").append(i).append(".setLinearDamping(").append(body.getLinearDamping()).append(");").append(NEW_LINE);
			}
			if (body.getAngularDamping() != Body.DEFAULT_ANGULAR_DAMPING) {
				sb.append(TAB2).append("body").append(i).append(".setAngularDamping(").append(body.getAngularDamping()).append(");").append(NEW_LINE);
			}
			// set gravity scale
			if (body.getGravityScale() != 1.0) {
				sb.append(TAB2).append("body").append(i).append(".setGravityScale(").append(body.getGravityScale()).append(");").append(NEW_LINE);
			}
			// set mass properties last
			sb.append(TAB2).append("body").append(i).append(".setMass(").append(export(mass)).append(");").append(NEW_LINE)
			// set the mass type
			.append(TAB2).append("body").append(i).append(".setMassType(MassType.").append(mass.getType()).append(");").append(NEW_LINE);

			// set force/torque accumulators
			if (!body.getAccumulatedForce().isZero()) {
				sb.append(TAB2).append("body").append(i).append(".applyForce(").append(export(body.getAccumulatedForce())).append(");").append(NEW_LINE);
			}
			if (Math.abs(body.getAccumulatedTorque()) > Epsilon.E) {
				sb.append(TAB2).append("body").append(i).append(".applyTorque(").append(body.getAccumulatedTorque()).append(");").append(NEW_LINE);
			}
			
			// add the body to the world
			sb.append(TAB2).append("world.addBody(body").append(i).append(");").append(NEW_LINE).append(NEW_LINE);
		}
		
		// output joints
		int jSize = world.getJointCount();
		for (int i = 1; i < jSize + 1; i++) {
			Joint<?> joint = world.getJoint(i - 1);
			
			sb.append(TAB2).append("// ").append(joint.getUserData()).append(NEW_LINE);
			if (joint instanceof AngleJoint) {
				AngleJoint<?> aj = (AngleJoint<?>)joint;
				Body body1 = (Body)aj.getBody1();
				Body body2 = (Body)aj.getBody2();
				sb.append(TAB2).append("AngleJoint joint").append(i).append(" = new AngleJoint(").append(idNameMap.get(body1)).append(", ").append(idNameMap.get(body2)).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setLimits(Math.toRadians(").append(Math.toDegrees(aj.getLowerLimit())).append("), Math.toRadians(").append(Math.toDegrees(aj.getUpperLimit())).append("));").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setLimitEnabled(").append(aj.isLimitsEnabled()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setLimitsReferenceAngle(Math.toRadians(").append(Math.toDegrees(aj.getLimitsReferenceAngle())).append("));").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setRatio(").append(aj.getRatio()).append(");").append(NEW_LINE);
			} else if (joint instanceof DistanceJoint) {
				DistanceJoint<?> dj = (DistanceJoint<?>)joint;
				Body body1 = (Body)dj.getBody1();
				Body body2 = (Body)dj.getBody2();
				sb.append(TAB2).append("DistanceJoint joint").append(i).append(" = new DistanceJoint(").append(idNameMap.get(body1)).append(", ").append(idNameMap.get(body2)).append(", ").append(export(dj.getAnchor1())).append(", ").append(export(dj.getAnchor2())).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setSpringFrequency(").append(dj.getSpringFrequency()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setSpringDampingRatio(").append(dj.getSpringDampingRatio()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setRestDistance(").append(dj.getRestDistance()).append(");").append(NEW_LINE);
			} else if (joint instanceof FrictionJoint) {
				FrictionJoint<?> fj = (FrictionJoint<?>)joint;
				Body body1 = (Body)fj.getBody1();
				Body body2 = (Body)fj.getBody2();
				sb.append(TAB2).append("FrictionJoint joint").append(i).append(" = new FrictionJoint(").append(idNameMap.get(body1)).append(", ").append(idNameMap.get(body2)).append(", ").append(export(fj.getAnchor1())).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setMaximumForce(").append(fj.getMaximumForce()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setMaximumTorque(").append(fj.getMaximumTorque()).append(");").append(NEW_LINE);
			} else if (joint instanceof PinJoint) {
				PinJoint<?> mj = (PinJoint<?>)joint;
				Body body1 = (Body)mj.getBody();
				sb.append(TAB2).append("PinJoint joint").append(i).append(" = new PinJoint(").append(idNameMap.get(body1)).append(", ").append(export(mj.getAnchor())).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setSpringFrequency(").append(mj.getSpringFrequency()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setSpringDampingRatio(").append(mj.getSpringDampingRatio()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setMaximumSpringForce(").append(mj.getMaximumSpringForce()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setTarget(").append(export(mj.getAnchor())).append(");").append(NEW_LINE);
			} else if (joint instanceof PrismaticJoint) {
				PrismaticJoint<?> pj = (PrismaticJoint<?>)joint;
				Body body1 = (Body)pj.getBody1();
				Body body2 = (Body)pj.getBody2();
				sb.append(TAB2).append("PrismaticJoint joint").append(i).append(" = new PrismaticJoint(").append(idNameMap.get(body1)).append(", ").append(idNameMap.get(body2)).append(", ").append(export(pj.getAnchor1())).append(", ").append(export(pj.getAxis())).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setLowerLimitEnabled(").append(pj.isLowerLimitEnabled()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setUpperLimitEnabled(").append(pj.isUpperLimitEnabled()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setLimits(").append(pj.getLowerLimit()).append(", ").append(pj.getUpperLimit()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setReferenceAngle(Math.toRadians(").append(Math.toDegrees(pj.getReferenceAngle())).append("));").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setMotorEnabled(").append(pj.isMotorEnabled()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setMotorSpeed(").append(pj.getMotorSpeed()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setMaximumMotorForce(").append(pj.getMaximumMotorForce()).append(");").append(NEW_LINE);
			} else if (joint instanceof PulleyJoint) {
				PulleyJoint<?> pj = (PulleyJoint<?>)joint;
				Body body1 = (Body)pj.getBody1();
				Body body2 = (Body)pj.getBody2();
				sb.append(TAB2).append("PulleyJoint joint").append(i).append(" = new PulleyJoint(").append(idNameMap.get(body1)).append(", ").append(idNameMap.get(body2)).append(", ").append(export(pj.getPulleyAnchor1())).append(", ").append(export(pj.getPulleyAnchor2())).append(", ").append(export(pj.getAnchor1())).append(", ").append(export(pj.getAnchor2())).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setRatio(").append(pj.getRatio()).append(");").append(NEW_LINE);
			} else if (joint instanceof RevoluteJoint) {
				RevoluteJoint<?> rj = (RevoluteJoint<?>)joint;
				Body body1 = (Body)rj.getBody1();
				Body body2 = (Body)rj.getBody2();
				sb.append(TAB2).append("RevoluteJoint joint").append(i).append(" = new RevoluteJoint(").append(idNameMap.get(body1)).append(", ").append(idNameMap.get(body2)).append(", ").append(export(rj.getAnchor1())).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setLimitsEnabled(").append(rj.isLimitsEnabled()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setLimits(Math.toRadians(").append(Math.toDegrees(rj.getLowerLimit())).append("), Math.toRadians(").append(Math.toDegrees(rj.getUpperLimit())).append("));").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setLimitsReferenceAngle(Math.toRadians(").append(Math.toDegrees(rj.getLimitsReferenceAngle())).append("));").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setMotorEnabled(").append(rj.isMotorEnabled()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setMotorSpeed(Math.toRadians(").append(Math.toDegrees(rj.getMotorSpeed())).append("));").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setMaximumMotorTorque(").append(rj.getMaximumMotorTorque()).append(");").append(NEW_LINE);
			} else if (joint instanceof WeldJoint) {
				WeldJoint<?> wj = (WeldJoint<?>)joint;
				Body body1 = (Body)wj.getBody1();
				Body body2 = (Body)wj.getBody2();
				sb.append(TAB2).append("WeldJoint joint").append(i).append(" = new WeldJoint(").append(idNameMap.get(body1)).append(", ").append(idNameMap.get(body2)).append(", ").append(export(wj.getAnchor1())).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setSpringFrequency(").append(wj.getSpringFrequency()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setSpringDampingRatio(").append(wj.getSpringDampingRatio()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setLimitsReferenceAngle(Math.toRadians(").append(Math.toDegrees(wj.getLimitsReferenceAngle())).append("));").append(NEW_LINE);
			} else if (joint instanceof WheelJoint) {
				WheelJoint<?> wj = (WheelJoint<?>)joint;
				Body body1 = (Body)wj.getBody1();
				Body body2 = (Body)wj.getBody2();
				sb.append(TAB2).append("WheelJoint joint").append(i).append(" = new WheelJoint(").append(idNameMap.get(body1)).append(", ").append(idNameMap.get(body2)).append(", ").append(export(wj.getAnchor1())).append(", ").append(export(wj.getAxis())).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setSpringFrequency(").append(wj.getSpringFrequency()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setSpringDampingRatio(").append(wj.getSpringDampingRatio()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setMotorEnabled(").append(wj.isMotorEnabled()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setMotorSpeed(Math.toRadians(").append(Math.toDegrees(wj.getMotorSpeed())).append("));").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setMaximumMotorTorque(").append(wj.getMaximumMotorTorque()).append(");").append(NEW_LINE);
			} else if (joint instanceof MotorJoint) {
				MotorJoint<?> mj = (MotorJoint<?>)joint;
				Body body1 = (Body)mj.getBody1();
				Body body2 = (Body)mj.getBody2();
				sb.append(TAB2).append("MotorJoint joint").append(i).append(" = new MotorJoint(").append(idNameMap.get(body1)).append(", ").append(idNameMap.get(body2)).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setLinearTarget(").append(export(mj.getLinearTarget())).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setAngularTarget(Math.toRadians(").append(Math.toDegrees(mj.getAngularTarget())).append("));").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setCorrectionFactor(").append(mj.getCorrectionFactor()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setMaximumForce(").append(mj.getMaximumForce()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setMaximumTorque(").append(mj.getMaximumTorque()).append(");").append(NEW_LINE);
			} else {
				throw new UnsupportedOperationException("Unknown joint class: " + joint.getClass().getName());
			}
			
			sb.append(TAB2).append("joint").append(i).append(".setCollisionAllowed(").append(joint.isCollisionAllowed()).append(");").append(NEW_LINE);
			sb.append(TAB2).append("world.addJoint(joint").append(i).append(");");
			sb.append(NEW_LINE);
		}
		
		// end setup method
		sb.append(TAB1).append("}").append(NEW_LINE)
		// end class declaration
		.append("}").append(NEW_LINE);
		
		
		return sb.toString();
	}
	
	/**
	 * Exports the given settings.
	 * @param settings the settings
	 * @return String
	 */
	private static final String export(Settings settings) {
		StringBuilder sb = new StringBuilder();
		if (settings.getStepFrequency() != Settings.DEFAULT_STEP_FREQUENCY) {
			sb.append(TAB2).append("settings.setStepFrequency(").append(1.0 / settings.getStepFrequency()).append(");").append(NEW_LINE);
		}
		if (settings.getMaximumTranslation() != Settings.DEFAULT_MAXIMUM_TRANSLATION) {
			sb.append(TAB2).append("settings.setMaximumTranslation(").append(settings.getMaximumTranslation()).append(");").append(NEW_LINE);
		}
		if (settings.getMaximumRotation() != Settings.DEFAULT_MAXIMUM_ROTATION) {
			sb.append(TAB2).append("settings.setMaximumRotation(Math.toRadians(").append(Math.toDegrees(settings.getMaximumRotation())).append("));").append(NEW_LINE);
		}
		if (!settings.isAtRestDetectionEnabled()) {
			sb.append(TAB2).append("settings.setAtRestDetectionEnabled(false);").append(NEW_LINE);
		}
		if (settings.getMaximumAtRestLinearVelocity() != Settings.DEFAULT_MAXIMUM_AT_REST_LINEAR_VELOCITY) {
			sb.append(TAB2).append("settings.setMaximumAtRestLinearVelocity(").append(settings.getMaximumAtRestLinearVelocity()).append(");").append(NEW_LINE);
		}
		if (settings.getMaximumAtRestAngularVelocity() != Settings.DEFAULT_MAXIMUM_AT_REST_ANGULAR_VELOCITY) {
			sb.append(TAB2).append("settings.setMaximumAtRestAngularVelocity(Math.toRadians(").append(Math.toDegrees(settings.getMaximumAtRestAngularVelocity())).append("));").append(NEW_LINE);
		}
		if (settings.getMinimumAtRestTime() != Settings.DEFAULT_MINIMUM_AT_REST_TIME) {
			sb.append(TAB2).append("settings.setMinimumAtRestTime(").append(settings.getMinimumAtRestTime()).append(");").append(NEW_LINE);
		}
		if (settings.getVelocityConstraintSolverIterations() != Settings.DEFAULT_SOLVER_ITERATIONS) {
			sb.append(TAB2).append("settings.setVelocityConstraintSolverIterations(").append(settings.getVelocityConstraintSolverIterations()).append(");").append(NEW_LINE);
		}
		if (settings.getPositionConstraintSolverIterations() != Settings.DEFAULT_SOLVER_ITERATIONS) {
			sb.append(TAB2).append("settings.setPositionConstraintSolverIterations(").append(settings.getPositionConstraintSolverIterations()).append(");").append(NEW_LINE);
		}
		if (settings.getMaximumWarmStartDistance() != Settings.DEFAULT_MAXIMUM_WARM_START_DISTANCE) {
			sb.append(TAB2).append("settings.setMaximumWarmStartDistance(").append(settings.getMaximumWarmStartDistance()).append(");").append(NEW_LINE);
		}
		if (settings.getLinearTolerance() != Settings.DEFAULT_LINEAR_TOLERANCE) {
			sb.append(TAB2).append("settings.setLinearTolerance(").append(settings.getLinearTolerance()).append(");").append(NEW_LINE);
		}
		if (settings.getAngularTolerance() != Settings.DEFAULT_ANGULAR_TOLERANCE) {
			sb.append(TAB2).append("settings.setAngularTolerance(Math.toRadians(").append(Math.toDegrees(settings.getAngularTolerance())).append("));").append(NEW_LINE);
		}
		if (settings.getMaximumLinearCorrection() != Settings.DEFAULT_MAXIMUM_LINEAR_CORRECTION) {
			sb.append(TAB2).append("settings.setMaximumLinearCorrection(").append(settings.getMaximumLinearCorrection()).append(");").append(NEW_LINE);
		}
		if (settings.getMaximumAngularCorrection() != Settings.DEFAULT_MAXIMUM_ANGULAR_CORRECTION) {
			sb.append(TAB2).append("settings.setMaximumAngularCorrection(Math.toRadians(").append(Math.toDegrees(settings.getMaximumAngularCorrection())).append("));").append(NEW_LINE);
		}
		if (settings.getBaumgarte() != Settings.DEFAULT_BAUMGARTE) {
			sb.append(TAB2).append("settings.setBaumgarte(").append(settings.getBaumgarte()).append(");").append(NEW_LINE);
		}
		if (settings.getContinuousDetectionMode() != ContinuousDetectionMode.ALL) {
			sb.append(TAB2).append("settings.setContinuousDetectionMode(Settings.ContinuousDetectionMode.").append(settings.getContinuousDetectionMode()).append(");").append(NEW_LINE);
		}
		return sb.toString();
	}
	
	/**
	 * Exports the given mass.
	 * <p>
	 * Exports in the format:
	 * <pre>
	 * new Mass(...)
	 * </pre>
	 * @param mass the mass
	 * @return String
	 */
	private static final String export(Mass mass) {
		StringBuilder sb = new StringBuilder();
		// create a temporary mass so we can set the
		// mass type and get the correct mass and inertia values
		Mass temp = new Mass(mass);
		temp.setType(MassType.NORMAL);
		sb.append("new Mass(new Vector2(").append(temp.getCenter().x).append(", ").append(temp.getCenter().y).append("), ").append(temp.getMass()).append(", ").append(temp.getInertia()).append(")");
		return sb.toString();
	}
	
	/**
	 * Exports the given vector.
	 * <p>
	 * Exports in the format:
	 * <pre>
	 * new Vector2(v.x, v.y)
	 * </pre>
	 * @param v the vector
	 * @return String
	 */
	private static final String export(Vector2 v) {
		StringBuilder sb = new StringBuilder();
		sb.append("new Vector2(").append(v.x).append(", ").append(v.y).append(")");
		return sb.toString();
	}
	
	/**
	 * Exports the given convex shape.
	 * @param c the convex shape
	 * @param tabs the tabs string for formatting
	 * @return String
	 */
	private static final String export(Convex c, String tabs) {
		StringBuilder sb = new StringBuilder();
		
		if (c instanceof Circle) {
			Circle circle = (Circle)c;
			sb.append(tabs).append("Convex c = Geometry.createCircle(").append(circle.getRadius()).append(");").append(NEW_LINE);
			// translate only if the center is not (0, 0)
			if (!circle.getCenter().isZero()) {
				sb.append(tabs).append("c.translate(").append(export(circle.getCenter())).append(");").append(NEW_LINE);
			}
		} else if (c instanceof Rectangle) {
			Rectangle rectangle = (Rectangle)c;
			sb.append(tabs).append("Convex c = Geometry.createRectangle(").append(rectangle.getWidth()).append(", ").append(rectangle.getHeight()).append(");").append(NEW_LINE);
			// rotate only if the rotation is greater than zero
			if (Math.abs(rectangle.getRotationAngle()) > Epsilon.E) {
				sb.append(tabs).append("c.rotate(Math.toRadians(").append(Math.toDegrees(rectangle.getRotationAngle())).append("));").append(NEW_LINE);
			}
			// translate only if the center is not (0, 0)
			if (!rectangle.getCenter().isZero()) {
				sb.append(tabs).append("c.translate(").append(export(rectangle.getCenter())).append(");").append(NEW_LINE);
			}
		} else if (c instanceof Triangle) {
			Triangle triangle = (Triangle)c;
			sb.append(tabs).append("Convex c = Geometry.createTriangle(").append(export(triangle.getVertices()[0])).append(", ").append(export(triangle.getVertices()[1])).append(", ").append(export(triangle.getVertices()[2])).append(");").append(NEW_LINE);
			// transformations are maintained by the vertices
		} else if (c instanceof Polygon) {
			Polygon polygon = (Polygon)c;
			sb.append(tabs).append("Convex c = Geometry.createPolygon(");
			int vSize = polygon.getVertices().length;
			for (int i = 0; i < vSize; i++) {
				Vector2 v = polygon.getVertices()[i];
				if (i != 0) sb.append(", ");
				sb.append(export(v));
			}
			sb.append(");").append(NEW_LINE);
			// transformations are maintained by the vertices
		} else if (c instanceof Segment) {
			Segment segment = (Segment)c;
			sb.append(tabs).append("Convex c = Geometry.createSegment(").append(export(segment.getVertices()[0])).append(", ").append(export(segment.getVertices()[1])).append(");").append(NEW_LINE);
			// transformations are maintained by the vertices
		} else if (c instanceof Capsule) {
			Capsule capsule = (Capsule)c;
			sb.append(tabs).append("Convex c = Geometry.createCapsule(").append(capsule.getLength()).append(", ").append(capsule.getCapRadius() * 2.0).append(");").append(NEW_LINE);
			// rotate only if the rotation is greater than zero
			if (Math.abs(capsule.getRotationAngle()) > Epsilon.E) {
				sb.append(tabs).append("c.rotate(Math.toRadians(").append(Math.toDegrees(capsule.getRotationAngle())).append("));").append(NEW_LINE);
			}
			// translate only if the center is not (0, 0)
			if (!capsule.getCenter().isZero()) {
				sb.append(tabs).append("c.translate(").append(export(capsule.getCenter())).append(");").append(NEW_LINE);
			}
		} else if (c instanceof Ellipse) {
			Ellipse ellipse = (Ellipse)c;
			sb.append(tabs).append("Convex c = Geometry.createEllipse(").append(ellipse.getHalfWidth() * 2.0).append(", ").append(ellipse.getHalfHeight() * 2.0).append(");").append(NEW_LINE);
			// rotate only if the rotation is greater than zero
			if (Math.abs(ellipse.getRotationAngle()) > Epsilon.E) {
				sb.append(tabs).append("c.rotate(Math.toRadians(").append(Math.toDegrees(ellipse.getRotationAngle())).append("));").append(NEW_LINE);
			}
			// translate only if the center is not (0, 0)
			if (!ellipse.getCenter().isZero()) {
				sb.append(tabs).append("c.translate(").append(export(ellipse.getCenter())).append(");").append(NEW_LINE);
			}
		} else if (c instanceof HalfEllipse) {
			HalfEllipse halfEllipse = (HalfEllipse)c;
			
			double width = halfEllipse.getHalfWidth() * 2.0;
			double height = halfEllipse.getHeight();
			double originalY = (4.0 * height) / (3.0 * Math.PI);
			
			sb.append(tabs).append("Convex c = Geometry.createHalfEllipse(").append(width).append(", ").append(height).append(");").append(NEW_LINE);
			// rotate only if the rotation is greater than zero
			if (Math.abs(halfEllipse.getRotationAngle()) > Epsilon.E) {
				sb.append(tabs).append("c.rotate(Math.toRadians(").append(Math.toDegrees(halfEllipse.getRotationAngle())).append("));").append(NEW_LINE);
			}
			// translate only if the center is not (0, 0)
			if (halfEllipse.getCenter().y != originalY) {
				sb.append(tabs).append("c.translate(").append(export(halfEllipse.getCenter())).append(");").append(NEW_LINE);
			}
		} else if (c instanceof Slice) {
			Slice slice = (Slice)c;
			
			double theta = slice.getTheta();
			double radius = slice.getSliceRadius();
			double originalX = 2.0 * radius * Math.sin(theta * 0.5) / (1.5 * theta);
			
			sb.append(tabs).append("Convex c = Geometry.createSlice(").append(radius).append(", Math.toRadians(").append(Math.toDegrees(theta)).append("));").append(NEW_LINE);
			// rotate only if the rotation is greater than zero
			if (Math.abs(slice.getRotationAngle()) > Epsilon.E) {
				sb.append(tabs).append("c.rotate(Math.toRadians(").append(Math.toDegrees(slice.getRotationAngle())).append("));").append(NEW_LINE);
			}
			// translate only if the center is not (0, 0)
			if (slice.getCenter().x != originalX) {
				sb.append(tabs).append("c.translate(").append(export(slice.getCenter())).append(");").append(NEW_LINE);
			}
		} else {
			throw new UnsupportedOperationException(MessageFormat.format("Unknown/Unsupported class {0}", c.getClass().getName()));
		}
		
		return sb.toString();
	}
	
	/**
	 * Exports the given filter.
	 * @param f the filter
	 * @param tabs the tabs string for formatting
	 * @return String
	 */
	private static final String export(Filter f, String tabs) {
		StringBuilder sb = new StringBuilder();
		
		if (f == Filter.DEFAULT_FILTER) {
			// output nothing
		} else if (f instanceof CategoryFilter) {
			CategoryFilter cf = (CategoryFilter)f;
			sb.append(tabs).append("bf.setFilter(new CategoryFilter(").append(cf.getCategory()).append(", ").append(cf.getMask()).append("));").append(NEW_LINE);
		} else {
			throw new UnsupportedOperationException("The class " + f.getClass().getName() + " is not known.");
		}
		
		return sb.toString();
	}
}
