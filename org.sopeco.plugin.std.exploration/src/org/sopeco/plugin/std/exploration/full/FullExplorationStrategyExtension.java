/**
 * 
 */
package org.sopeco.plugin.std.exploration.full;

import java.util.Collections;
import java.util.Map;

import org.sopeco.engine.experimentseries.IExplorationStrategy;
import org.sopeco.engine.experimentseries.IExplorationStrategyExtension;

/**
 * The extension that provides the full exploration strategy.
 * 
 * @author Roozbeh Farahbod
 *
 */
public class FullExplorationStrategyExtension implements IExplorationStrategyExtension {

	public static final String NAME = "Full Exploration Strategy";
	
	public FullExplorationStrategyExtension() { }

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public IExplorationStrategy createExtensionArtifact() {
		return new FullExplorationStrategy(this);
	}

	@Override
	public Map<String, String> getConfigParameters() {
		return Collections.emptyMap();
	}

	
}
