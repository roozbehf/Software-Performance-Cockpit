package org.sopeco.plugin.std.parametervariation.bool;


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

public class BooleanVariationTest {

	DynamicValueAssignment dva = null;
	BooleanVariation bv = null;
	
	@Before
	public void setUp() throws Exception {
		ScenarioDefinitionBuilder builder = new ScenarioDefinitionBuilder("test");
		builder.createNewNamespace("initialization");
		ParameterDefinition pdef = builder.createParameter("initParameter", ParameterType.BOOLEAN, ParameterRole.INPUT);
		Map<String, String> config = new HashMap<String, String>();
		builder.createMeasurementSpecification("testSpecification");
		builder.createExperimentSeriesDefinition("ES");
		dva = builder.createDynamicValueAssignment(AssignmentType.Experiment, BooleanVariationExtension.NAME, pdef, config);
		bv = new BooleanVariation(null);
		bv.initialize(dva);
	}

	@Test
	public void testGet() {
		assertTrue(!bv.get(0).getValueAsBoolean());
		assertTrue(bv.get(1).getValueAsBoolean());
	}

	@Test
	public void testSize() {
		assertEquals(2, bv.size());
	}

	@Test
	public void testIterator() {
		Iterator<ParameterValue<?>> iterator = bv.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			System.out.println(iterator.next().getValueAsBoolean());
			i++;
		}
		
		assertEquals(i, bv.size());
	}

	@Test
	public void testReset() {
		testIterator();
		bv.reset();
		testIterator();
	}

	@Test
	public void testCanVary() {
		assertTrue(bv.canVary(dva));
	}

}
