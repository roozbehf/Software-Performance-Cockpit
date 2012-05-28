/**
 * 
 */
package org.sopeco.engine.imp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sopeco.config.Configuration;
import org.sopeco.config.IConfiguration;
import org.sopeco.engine.IEngine;
import org.sopeco.engine.experiment.IExperimentController;
import org.sopeco.engine.experimentseries.IExperimentSeriesManager;
import org.sopeco.engine.registry.ExtensionRegistry;
import org.sopeco.engine.registry.IExtensionRegistry;
import org.sopeco.engine.util.EngineTools;
import org.sopeco.persistence.EntityFactory;
import org.sopeco.persistence.IPersistenceProvider;
import org.sopeco.persistence.entities.ExperimentSeries;
import org.sopeco.persistence.entities.ScenarioInstance;
import org.sopeco.persistence.entities.definition.ExperimentSeriesDefinition;
import org.sopeco.persistence.entities.definition.ScenarioDefinition;
import org.sopeco.persistence.exceptions.DataNotFoundException;

/**
 * The default implementation of SoPeCo engine.
 * 
 * @author Roozbeh Farahbod
 * @author Dennis Westermann
 * 
 */
public class EngineImp implements IEngine {

	private static final Logger logger = LoggerFactory.getLogger(EngineImp.class);

	private IConfiguration configuration = null;

	private IExtensionRegistry registry = null;

	final private IExperimentController experimentController;
	final private IExperimentSeriesManager experimentSeriesManager;
	final private IPersistenceProvider persistenceProvider;

	public EngineImp(IExperimentController experimentController, IExperimentSeriesManager experimentSeriesManager,
			IPersistenceProvider persistenceProvider) {
		super();
		this.experimentController = experimentController;
		this.experimentSeriesManager = experimentSeriesManager;
		this.persistenceProvider = persistenceProvider;
	}

	@Override
	public IConfiguration getConfiguration() {
		if (configuration == null)
			configuration = Configuration.getSingleton();
		return configuration;
	}

	@Override
	public IExtensionRegistry getExtensionRegistry() {
		if (registry == null)
			registry = ExtensionRegistry.getSingleton();
		return registry;
	}

	@Override
	public ScenarioInstance run(ScenarioDefinition scenario) {
		ScenarioInstance scenarioInstance;
		try {
			scenarioInstance = persistenceProvider.loadScenarioInstance(scenario.getScenarioName(), getConfiguration()
					.getMeasurementControllerURIAsStr());
			logger.debug("Loaded ScenarioInstance {} from database", scenarioInstance);
			logger.debug("Compare Scenario definition defined in the specification with the one loaded from database");

			checkScenarioDefinition(scenarioInstance, scenario);

		} catch (DataNotFoundException e) {
			scenarioInstance = EntityFactory.createScenarioInstance(scenario, getConfiguration()
					.getMeasurementControllerURIAsStr());
			persistenceProvider.store(scenarioInstance);
			logger.debug("Created new ScenarioInstance {}", scenarioInstance);
		}

		experimentController.initialize(EngineTools.getConstantParameterValues(scenario.getMeasurementSpecification()
				.getInitializationAssignemts()), scenario.getMeasurementEnvironmentDefinition());

		// loop over all the experiment series in the spec
		for (ExperimentSeriesDefinition esd : scenario.getMeasurementSpecification().getExperimentSeriesDefinitions()) {

			ExperimentSeries series = scenarioInstance.getExperimentSeries(esd.getName());
			if (series == null) {
				series = EntityFactory.createExperimentSeries(esd);
				scenarioInstance.getExperimentSeriesList().add(series);
				series.setScenarioInstance(scenarioInstance);

				persistenceProvider.store(series);
			}

			experimentSeriesManager.runExperimentSeries(series);
		}

		try {
			ScenarioInstance loadedScenario = persistenceProvider
					.loadScenarioInstance(scenarioInstance.getPrimaryKey());
			return loadedScenario;
		} catch (DataNotFoundException e) {
			logger.error("Cannot load the scenario from the persistnce provider. Something is seriously gone wrong.");
			throw new RuntimeException("Something went wrong");
		}
	}

	/**
	 * Checks if the given scenario instance contains a scenario definition with
	 * the same id as the given scenario definition. If yes, it checks whether
	 * the scenario definition has been changed and throws a runtime exception
	 * if this is true.
	 * 
	 * @param scenarioInstance
	 * @param scenarioDefinition
	 */
	private void checkScenarioDefinition(ScenarioInstance scenarioInstance, ScenarioDefinition scenarioDefinition) {

		if (scenarioInstance != null) {
			for (ScenarioDefinition storedDef : scenarioInstance.getScenarioDefinitions()) {
				if (storedDef.getDefinitionId().equals(scenarioDefinition.getDefinitionId())
						&& !scenarioDefinition.equals(scenarioInstance.getScenarioDefinitions())) {
					throw new RuntimeException(
							"Scenario definition has been changed! Either rename the new scenario definition id or delete the old scenario definition (with the same id) from the database!");
				}
			}
		}
	}

	@Override
	public IExperimentController getExperimentController() {
		return experimentController;
	}

	@Override
	public IExperimentSeriesManager getExperimentSeriesManager() {
		return experimentSeriesManager;
	}

	@Override
	public IPersistenceProvider getPersistenceProvider() {
		return persistenceProvider;
	}

}
