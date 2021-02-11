package org.dyn4j.samples.framework.input;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public abstract class AbstractKeyboardInputHandler extends AbstractInputHandler implements InputHandler {
	protected final Component component;
	protected final int key;
	
	private final KeyAdapter keyAdapter;
	
	public AbstractKeyboardInputHandler(Component component, int key) {
		this.component = component;
		this.key = key;
		this.keyAdapter = new CustomKeyListener();
	}

	/**
	 * Custom key adapter to listen for key events.
	 * @author William Bittle
	 * @version 3.2.1
	 * @since 3.2.0
	 */
	private class CustomKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.isConsumed()) return;
			
			if (e.getKeyCode() == key) {
				if (isEnabled() && !isDependentBehaviorActive()) {
					onKeyPressed();
				}
			}
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
			if (e.isConsumed()) return;
			
			if (e.getKeyCode() == key) {
				if (isEnabled() && !isDependentBehaviorActive()) {
					onKeyReleased();
				}
			}
		}
	}
	
	@Override
	public void install() {
		this.component.addKeyListener(this.keyAdapter);
	}
	
	@Override
	public void uninstall() {
		this.component.removeKeyListener(this.keyAdapter);
	}
	
	protected void onKeyPressed() {
		
	}
	
	protected void onKeyReleased() {
		
	}
}