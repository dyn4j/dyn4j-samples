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

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public abstract class AbstractKeyboardInputHandler extends AbstractInputHandler implements InputHandler {
	protected final Component component;
	protected final Key[] keys;
	
	private final KeyAdapter keyAdapter;
	
	public AbstractKeyboardInputHandler(Component component, Key... keys) {
		this.component = component;
		this.keys = keys;
		this.keyAdapter = new CustomKeyListener();
	}
	
	public AbstractKeyboardInputHandler(Component component, int... keys) {
		this.component = component;
		this.keyAdapter = new CustomKeyListener();
		this.keys = new Key[keys.length];
		for (int i = 0; i < keys.length; i++) {
			this.keys[i] = new Key(keys[i]);
		}
		
	}

	private boolean isKeyMatch(int key, int modifiers) {
		for (int i = 0; i < this.keys.length; i++) {
			if (this.keys[i].key == key && this.keys[i].modifiers == modifiers) 
				return true;
		}
		return false;
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
//			System.out.println(e.getKeyChar() + " " + e.getKeyCode() + " " + e.getModifiersEx());
			if (isKeyMatch(e.getKeyCode(), e.getModifiersEx())) {
				if (isEnabled() && !isDependentBehaviorActive()) {
					onKeyPressed();
				}
			}
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
			if (e.isConsumed()) return;
			
			if (isKeyMatch(e.getKeyCode(), e.getModifiersEx())) {
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