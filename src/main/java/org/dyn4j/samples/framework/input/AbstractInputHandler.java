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
