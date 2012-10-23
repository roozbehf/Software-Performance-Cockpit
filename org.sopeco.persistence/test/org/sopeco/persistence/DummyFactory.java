package org.sopeco.persistence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.sopeco.persistence.dataset.DataSetAggregated;
import org.sopeco.persistence.dataset.DataSetColumnBuilder;
import org.sopeco.persistence.dataset.DataSetModifier;
import org.sopeco.persistence.dataset.ParameterValueList;
import org.sopeco.persistence.dataset.util.ParameterType;
import org.sopeco.persistence.entities.ExperimentSeries;
import org.sopeco.persistence.entities.ExperimentSeriesRun;
import org.sopeco.persistence.entities.ScenarioInstance;
import org.sopeco.persistence.entities.definition.ExperimentSeriesDefinition;
import org.sopeco.persistence.entities.definition.ParameterDefinition;
import org.sopeco.persistence.entities.definition.ParameterRole;
import org.sopeco.persistence.entities.definition.ScenarioDefinition;
import org.sopeco.persistence.util.ScenarioDefinitionBuilder;
import org.sopeco.persistence.util.ScenarioDefinitionBuilder.AssignmentType;

public class DummyFactory {

	private static ScenarioDefinition scenarioDefinition;

	public static ScenarioInstance createDummyScenarioInstance() throws IOException {
		scenarioDefinition = loadScenarioDefinition();
		ScenarioInstance scenarioInstance = EntityFactory.createScenarioInstance(scenarioDefinition, "Dummy");

		for (ExperimentSeries es : createDummyExperimentSeries()) {
			scenarioInstance.getExperimentSeriesList().add(es);
			es.setScenarioInstance(scenarioInstance);
		}
		return scenarioInstance;
	}

	@SuppressWarnings("unchecked")
	public static ScenarioDefinition loadScenarioDefinition() {
		ScenarioDefinitionBuilder builder = new ScenarioDefinitionBuilder("Dummy");

		builder.createNewNamespace("default");
		ParameterDefinition dummyInputParam = builder.createParameter("DummyInput", ParameterType.INTEGER,
				ParameterRole.INPUT);
		ParameterDefinition dummyOutputParam = builder.createParameter("DummyOutput", ParameterType.INTEGER,
				ParameterRole.OBSERVATION);

		builder.createMeasurementSpecification("DummyMeasurementSpecification");

		builder.createExperimentSeriesDefinition("Dummy0");
		builder.createDynamicValueAssignment(AssignmentType.Experiment, "Linear Numeric Variation", dummyInputParam,
				Collections.EMPTY_MAP);
		builder.createExplorationStrategy("Full Exploration Strategy", Collections.EMPTY_MAP);
		builder.createAnalysisConfiguration("MARS", Collections.EMPTY_MAP);
		builder.addDependentParameter(dummyOutputParam);
		builder.addIndependentParameter(dummyInputParam);

		builder.createExperimentSeriesDefinition("Dummy1");
		builder.createDynamicValueAssignment(AssignmentType.Experiment, "Linear Numeric Variation", dummyInputParam,
				Collections.EMPTY_MAP);
		builder.createExplorationStrategy("Full Exploration Strategy", Collections.EMPTY_MAP);
		builder.createAnalysisConfiguration("MARS", Collections.EMPTY_MAP);
		builder.addDependentParameter(dummyOutputParam);
		builder.addIndependentParameter(dummyInputParam);

		return builder.getScenarioDefinition();
	}

	@SuppressWarnings("unchecked")
	public static ScenarioDefinition loadDifferentScenarioDefinition() {
		ScenarioDefinitionBuilder builder = new ScenarioDefinitionBuilder("Dummy2");

		builder.createNewNamespace("default");
		ParameterDefinition dummyInputParam = builder.createParameter("DummyInput", ParameterType.INTEGER,
				ParameterRole.INPUT);
		ParameterDefinition dummyOutputParam = builder.createParameter("DummyOutput", ParameterType.INTEGER,
				ParameterRole.OBSERVATION);

		builder.createMeasurementSpecification("DummyMeasurementSpecification");

		builder.createExperimentSeriesDefinition("Dummy0");
		builder.createDynamicValueAssignment(AssignmentType.Experiment, "Linear Numeric Variation", dummyInputParam,
				Collections.EMPTY_MAP);
		builder.createExplorationStrategy("Full Exploration Strategy", Collections.EMPTY_MAP);
		builder.createAnalysisConfiguration("MARS", Collections.EMPTY_MAP);
		builder.addDependentParameter(dummyOutputParam);
		builder.addIndependentParameter(dummyInputParam);

		builder.createExperimentSeriesDefinition("Dummy1");
		builder.createDynamicValueAssignment(AssignmentType.Experiment, "Linear Numeric Variation", dummyInputParam,
				Collections.EMPTY_MAP);
		builder.createExplorationStrategy("Full Exploration Strategy", Collections.EMPTY_MAP);
		builder.createAnalysisConfiguration("MARS", Collections.EMPTY_MAP);
		builder.addDependentParameter(dummyOutputParam);
		builder.addIndependentParameter(dummyInputParam);

		return builder.getScenarioDefinition();
	}

	private static List<ExperimentSeries> createDummyExperimentSeries() throws IOException {
		List<ExperimentSeries> seriesList = new LinkedList<ExperimentSeries>();
		for (ExperimentSeriesDefinition esd : scenarioDefinition.getMeasurementSpecifications().get(0)
				.getExperimentSeriesDefinitions()) {
			ExperimentSeries es = EntityFactory.createExperimentSeries(esd);

			for (ExperimentSeriesRun run : createDummyExperimentSeriesRuns(10)) {
				es.getExperimentSeriesRuns().add(run);
				run.setExperimentSeries(es);
			}

			seriesList.add(es);
		}

		return seriesList;

	}

	static Collection<? extends ExperimentSeriesRun> createDummyExperimentSeriesRuns(int numberOfRunsToCreate)
			throws IOException {
		ArrayList<ExperimentSeriesRun> runs = new ArrayList<ExperimentSeriesRun>();
		for (int i = 0; i < numberOfRunsToCreate; i++) {

			ExperimentSeriesRun run = EntityFactory.createExperimentSeriesRun();
			run.setSuccessfulResultDataSet(createDummyResultDataSet());
			runs.add(run);
		}

		return runs;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static DataSetAggregated createDummyResultDataSet() throws IOException {
		if (scenarioDefinition == null)
			scenarioDefinition = loadScenarioDefinition();

		DataSetColumnBuilder builder = new DataSetColumnBuilder();
		builder.startInputColumn(scenarioDefinition.getParameterDefinition("default.DummyInput"));
		builder.addInputValue(1);
		builder.addInputValue(2);
		builder.finishColumn();

		ParameterDefinition paramDef = scenarioDefinition.getParameterDefinition("default.DummyOutput");
		ArrayList<ParameterValueList> obsValueLists = new ArrayList<ParameterValueList>();
		builder.startObservationColumn(paramDef);
		ArrayList<Object> obsValues1 = new ArrayList<Object>();
		obsValues1.add(10);
		obsValues1.add(10);
		obsValueLists.add(new ParameterValueList<Object>(paramDef, obsValues1));

		ArrayList<Object> obsValues2 = new ArrayList<Object>();
		obsValues2.add(20);
		obsValues2.add(20);
		// builder.addObservationValues(obsValues2);
		obsValueLists.add(new ParameterValueList(paramDef, obsValues2));

		builder.addObservationValueLists(obsValueLists);
		builder.finishColumn();

		return builder.createDataSet();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void addDummyObservationColumn(DataSetAggregated dataset) {
		DataSetModifier modifier = new DataSetModifier(dataset);
		ArrayList<Object> obsValuesAdded = new ArrayList<Object>();
		obsValuesAdded.add(30);
		obsValuesAdded.add(30);
		ParameterDefinition paramDef = createDummyObservationParameterDefinition("DummyOutputAdded");
		List<ParameterValueList<?>> listOfValueLists = new ArrayList<ParameterValueList<?>>();
		listOfValueLists.add(new ParameterValueList(paramDef, obsValuesAdded));
		modifier.addObservationColumn(paramDef, listOfValueLists);

	}

	public static ParameterDefinition createDummyObservationParameterDefinition(String name) {
		ParameterDefinition paramDef = EntityFactory.createParameterDefinition(name, "INTEGER",
				ParameterRole.OBSERVATION);
		return paramDef;
	}

}
