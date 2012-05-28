package org.sopeco.plugin.std.parametervariation.linear;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.sopeco.persistence.dataset.ParameterValue;
import org.sopeco.persistence.dataset.util.ParameterType;
import org.sopeco.persistence.entities.definition.DynamicValueAssignment;
import org.sopeco.persistence.entities.definition.ParameterDefinition;
import org.sopeco.persistence.entities.definition.ParameterRole;
import org.sopeco.persistence.util.ScenarioDefinitionBuilder;
import org.sopeco.persistence.util.ScenarioDefinitionBuilder.AssignmentType;

public class LinearNumericVariationTest {

	static final double MIN = 4;
	static final double MAX = 28.99999999999;
	static final double STEP = 12.5;
	
	DynamicValueAssignment dva = null;
	LinearNumericVariation lnv = null;
	
	@Before
	public void setUp() throws Exception {
		ScenarioDefinitionBuilder builder = new ScenarioDefinitionBuilder("test", "testDef");
		builder.createNewNamespace("initialization");
		ParameterDefinition pdef = builder.createParameter("initParameter", ParameterType.DOUBLE, ParameterRole.INPUT);
		Map<String, String> config = new HashMap<String, String>();
		config.put("min", String.valueOf(MIN));
		config.put("max", String.valueOf(MAX));
		config.put("step", String.valueOf(STEP));
		
		builder.createExperimentSeriesDefinition("ES");
		dva = builder.createDynamicValueAssignment(AssignmentType.Experiment, LinearNumericVariationExtension.NAME, pdef, config);
		lnv = new LinearNumericVariation(null);
		lnv.initialize(dva);
	}

	@Test
	public void testGet() {
		assertEquals(MIN, lnv.get(0).getValueAsDouble(), 0.0001);
		assertEquals(MIN + STEP, lnv.get(1).getValueAsDouble(), 0.0001);
	}

	@Test
	public void testSize() {
		assertEquals(2, lnv.size());
	}

	@Test
	public void testIterator() {
		Iterator<ParameterValue<?>> iterator = lnv.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			System.out.println(iterator.next().getValueAsDouble());
			i++;
		}
		
		assertEquals(i, lnv.size());
	}

	@Test
	public void testReset() {
		testIterator();
		lnv.reset();
		testIterator();
	}

	@Test
	public void testCanVary() {
		assertTrue(lnv.canVary(dva));
	}

}
