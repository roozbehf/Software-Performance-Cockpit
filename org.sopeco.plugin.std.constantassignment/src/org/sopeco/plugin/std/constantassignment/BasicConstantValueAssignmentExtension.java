/**
 * 
 */
package org.sopeco.plugin.std.constantassignment;

import java.util.Collections;
import java.util.Map;

import org.sopeco.engine.experimentseries.IConstantAssignment;
import org.sopeco.engine.experimentseries.IConstantAssignmentExtension;

/**
 * The provider of constant value assignments for basic types. 
 * 
 * @author Roozbeh Farahbod
 *
 */
public abstract class BasicConstantValueAssignmentExtension implements IConstantAssignmentExtension {

	protected String name;
	
	protected BasicConstantValueAssignmentExtension(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public IConstantAssignment createExtensionArtifact() {
		return new BasicConstantValueAssignment(this);
	}

	@Override
	public Map<String, String> getConfigParameters() {
		return Collections.emptyMap();
	}

}
