package org.dyn4j.samples.framework.input;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import org.dyn4j.geometry.Vector2;
import org.dyn4j.samples.framework.Camera;

public abstract class AbstractMouseInputHandler extends AbstractInputHandler implements InputHandler {
	
	protected final Component component;
	protected final Camera camera;
	protected final int button;
	
	private final MouseAdapter mouseAdapter;
	
	private Point dragCurrent;
	private Point dragStart;
	
	private double wheelRotation;
	
	public AbstractMouseInputHandler(Component component, Camera camera, int button) {
		this.component = component;
		this.camera = camera;
		this.button = button;
		this.mouseAdapter = new CustomMouseAdapter();
	}

	protected Vector2 toWorldCoordinates(Point p) {
		return this.camera.toWorldCoordinates(this.component.getWidth(), this.component.getHeight(), p);
	}
	
	/**
	 * A custom mouse adapter to track mouse drag events.
	 * @author William Bittle
	 * @version 3.2.1
	 * @since 3.2.0
	 */
	private final class CustomMouseAdapter extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			if (e.isConsumed()) return;
			
			if (e.getButton() == button) {
				// store the mouse click postion for use later
				dragCurrent = new Point(e.getX(), e.getY());
				dragStart = dragCurrent;
				if (isEnabled() && !isDependentBehaviorActive()) {
					onMousePressed(dragStart);
				}
			}
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			if (e.isConsumed()) return;
			
			dragCurrent = new Point(e.getX(), e.getY());
			if (isEnabled() && !isDependentBehaviorActive() && dragStart != null) {
				onMouseDrag(dragStart, dragCurrent);
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isConsumed()) return;
			
			if (e.getButton() == button) {
				dragCurrent = null;
				dragStart = null;
				if (isEnabled() && !isDependentBehaviorActive()) {
					onMouseRelease();
				}
			}
		}
		
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			if (e.isConsumed()) return;
			
			wheelRotation = e.getWheelRotation();
			if (isEnabled() && !isDependentBehaviorActive()) {
				onMouseWheel(wheelRotation);
			}
			wheelRotation = 0.0;
		}
	}

	@Override
	public void install() {
		this.component.addMouseListener(this.mouseAdapter);
		this.component.addMouseMotionListener(this.mouseAdapter);
		this.component.addMouseWheelListener(this.mouseAdapter);
	}
	
	@Override
	public void uninstall() {
		this.component.removeMouseListener(this.mouseAdapter);
		this.component.removeMouseMotionListener(this.mouseAdapter);
		this.component.removeMouseWheelListener(this.mouseAdapter);
	}
	
	@Override
	public void setEnabled(boolean flag) {
		super.setEnabled(flag);
		if (!flag) {
			this.dragCurrent = null;
			this.dragStart = null;
			this.wheelRotation = 0.0;
		}
	}
	
	protected void onMousePressed(Point point) {
		
	}
	
	protected void onMouseDrag(Point start, Point current) {
		
	}
	
	protected void onMouseRelease() {
		
	}
	
	protected void onMouseWheel(double rotation) {
		
	}
}
