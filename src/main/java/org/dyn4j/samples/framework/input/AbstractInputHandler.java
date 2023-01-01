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

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractInputHandler implements InputHandler {
	private boolean enabled;
	private boolean additive;
	private final List<InputHandler> dependentBehaviors;
	
	public AbstractInputHandler() {
		this.enabled = true;
		this.additive = false;
		this.dependentBehaviors = new ArrayList<InputHandler>();
	}
	
	@Override
	public boolean isEnabled() {
		return this.enabled;
	}
	
	@Override
	public void setEnabled(boolean flag) {
		this.enabled = flag;
	}
	
	public List<InputHandler> getDependentBehaviors() {
		return this.dependentBehaviors;
	}
	
	@Override
	public boolean isDependentBehaviorActive() {
		boolean result = false;
		for (InputHandler behavior : this.dependentBehaviors) {
			if (behavior.isActive()) {
				result = true;
			}
		}
		if (this.additive) return !result;
		return result;
	}
	
	@Override
	public boolean isDependentBehaviorsAdditive() {
		return this.additive;
	}
	
	@Override
	public void setDependentBehaviorsAdditive(boolean flag) {
		this.additive = flag;
	}
}
