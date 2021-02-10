package org.dyn4j.samples.framework.input;

import java.util.List;

public interface InputHandler {
	public void install();
	public void uninstall();
	public boolean isEnabled();
	public void setEnabled(boolean flag);
	public boolean isActive();
	
	public List<InputHandler> getDependentBehaviors();
	public boolean isDependentBehaviorActive();
	public boolean isDependentBehaviorsAdditive();
	public void setDependentBehaviorsAdditive(boolean flag);
}
