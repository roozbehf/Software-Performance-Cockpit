package org.sopeco.plugin.std.analysis;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sopeco.analysis.wrapper.AnalysisWrapper;
import org.sopeco.engine.analysis.AnovaCalculatedEffect;
import org.sopeco.engine.analysis.AnovaResult;
import org.sopeco.engine.analysis.IAnovaResult;
import org.sopeco.engine.analysis.IAnovaStrategy;
import org.sopeco.engine.registry.ISoPeCoExtension;
import org.sopeco.persistence.dataset.AbstractDataSetColumn;
import org.sopeco.persistence.dataset.DataSetAggregated;
import org.sopeco.persistence.dataset.DataSetColumnBuilder;
import org.sopeco.persistence.dataset.DataSetObservationColumn;
import org.sopeco.persistence.dataset.ParameterValueList;
import org.sopeco.persistence.dataset.SimpleDataSet;
import org.sopeco.persistence.entities.definition.AnalysisConfiguration;
import org.sopeco.persistence.entities.definition.ParameterDefinition;
import org.sopeco.plugin.std.analysis.common.AbstractAnalysisStrategy;
import org.sopeco.plugin.std.analysis.util.CSVStringGenerator;
import org.sopeco.plugin.std.analysis.util.RAdapter;

/**
 * This analysis strategy allows using the ANOVA method in R.
 * 
 * @author Dennis Westermann
 * 
 */
/**
 * @author Dennis Westermann
 * 
 */
public class AnovaStrategy extends AbstractAnalysisStrategy implements IAnovaStrategy {

	Logger logger = LoggerFactory.getLogger(AnovaStrategy.class);

	AnovaResult latestAnalysisResult;

	/**
	 * Instantiate a new MARS Analysis for R.
	 */
	public AnovaStrategy(ISoPeCoExtension<?> provider) {
		super(provider);
		loadLibraries();
	}

	@Override
	public void analyse(DataSetAggregated dataset, AnalysisConfiguration config) {

		logger.debug("Starting Anova analysis.");

		deriveDependentAndIndependentParameters(dataset, config);

		DataSetAggregated analysisDataSet = extractAnalysisDataSet(dataset);

		DataSetAggregated numericAnalysisDataSet = createNumericDataSet(analysisDataSet);

		loadDataSetInR(createValidSimpleDataSet(numericAnalysisDataSet));

		/**
		 * Example for Anova in R: <br>
		 * a <- c(1, 1, 1, 1, 2, 2, 2, 2) <br>
		 * b <- c(1, 1, 2, 2, 1, 1, 2, 2) <br>
		 * c <- a*b + 2*b + 0.1*a*b + b <br>
		 * c <- jitter(c) <br>
		 * data <- data.frame(a,b,c) <br>
		 * anova(lm(c ~ a * b, data)) <br>
		 */

		StringBuilder cmdBuilder = new StringBuilder();
		cmdBuilder.append(getId());
		cmdBuilder.append(" <- anova(lm(");
		cmdBuilder.append(dependentParameterDefintion.getFullName("_"));
		cmdBuilder.append(" ~ ");
		cmdBuilder.append(CSVStringGenerator.generateParameterString("*", independentParameterDefinitions));
		cmdBuilder.append(", ");
		cmdBuilder.append(data.getId());
		cmdBuilder.append("))");
		logger.debug("Running R Command: {}", cmdBuilder.toString());
		RAdapter.getWrapper().executeCommandString(cmdBuilder.toString());
		RAdapter.shutDown();
		extractResult();
	}

	private void extractResult() {
		latestAnalysisResult = new AnovaResult(config);

		// main effects
		for (ParameterDefinition pd : independentParameterDefinitions) {
			List<ParameterDefinition> paramList = new ArrayList<ParameterDefinition>();
			paramList.add(pd);
			latestAnalysisResult.addMainEffect(getEffect(paramList, pd.getFullName("_")));
		}

		// interaction effects
		String[] effectCodes = RAdapter.getWrapper().executeCommandStringArray("rownames(" + getId() + ")");
		RAdapter.shutDown();
		for (int i = 0; i < effectCodes.length; i++) {
			List<ParameterDefinition> paramList = new ArrayList<ParameterDefinition>();
			String[] paramNames = effectCodes[i].split(":");
			if (paramNames.length >= 2) {
				for (int j = 0; j < paramNames.length; j++) {
					ParameterDefinition pd = getIndependentParameterDefiniton(paramNames[j]);
					if (pd != null) {
						paramList.add(pd);
					} else {
						throw new IllegalStateException(
								"Parameter in ANOVA result table is not in the list of independent parameters.");
					}
				}
				latestAnalysisResult.addInteractionEffect(getEffect(paramList, effectCodes[i]));
			}
		}
	}

	/**
	 * Searches in the list of independent parameters for a definition with the
	 * given name.
	 * 
	 * @param fullNameSeparatedByUnderscore
	 *            e.g. org_sopeco_MyParam
	 * @return the corresponding {@link ParameterDefinition} instance or
	 *         <code>null</code> if no parameter was found
	 */
	private ParameterDefinition getIndependentParameterDefiniton(String fullNameSeparatedByUnderscore) {
		for (ParameterDefinition pd : independentParameterDefinitions) {
			if (pd.getFullName("_").equalsIgnoreCase(fullNameSeparatedByUnderscore)) {
				return pd;
			}
		}

		return null;
	}

	/**
	 * Creates the effect instance by reading and interpreting the ANOVA result
	 * table from R.
	 * 
	 * @param indepParams
	 *            a single parameter for main effects and multiple parameters
	 *            for interaction effects
	 * @param effectCode
	 *            String representation of a parameter or a set of parameters
	 *            (in case of an interaction effect) in the ANOVA table provided
	 *            by R
	 * @return the effect of the given indep parameters on the dependent
	 *         parameter calculated by ANOVA
	 */
	private AnovaCalculatedEffect getEffect(List<ParameterDefinition> indepParams, String effectCode) {

		AnovaCalculatedEffect ace = new AnovaCalculatedEffect(indepParams, dependentParameterDefintion);

		ace.setDegreesOfFreedom(getValueAsDouble(effectCode, "Df").intValue());

		ace.setSumOfSquares(getValueAsDouble(effectCode, "Sum Sq"));

		ace.setMeanSquare(getValueAsDouble(effectCode, "Mean Sq"));

		ace.setfValue(getValueAsDouble(effectCode, "F value"));

		ace.setpValue(getValueAsDouble(effectCode, "Pr(>F)"));

		return ace;
	}

	private Double getValueAsDouble(String rowName, String colName) {
		StringBuilder cmdBuilder = new StringBuilder();
		cmdBuilder.append("as.double(");
		cmdBuilder.append(getId());
		cmdBuilder.append("[\"");
		cmdBuilder.append(rowName);
		cmdBuilder.append("\",]$\"");
		cmdBuilder.append(colName);
		cmdBuilder.append("\")");
		double result = RAdapter.getWrapper().executeCommandDouble(cmdBuilder.toString());
		RAdapter.shutDown();
		return result;
	}

	@Override
	public IAnovaResult getAnovaResult() {

		return latestAnalysisResult;
	}

	/**
	 * Ensures that the observation parameter that is the dependent parameter
	 * contains at least 2 values. This is the minimum number required by the
	 * ANOVA implementation. If the dataset contains less than 2 values the
	 * existing value is duplicated.
	 * 
	 * @param dataSet
	 *            the dataset passed to the analysis strategy
	 * @return a {@link SimpleDataSet} that contains at least 2 values for the
	 *         dependent parameter
	 */
	private SimpleDataSet createValidSimpleDataSet(DataSetAggregated dataSet) {

		DataSetColumnBuilder builder = new DataSetColumnBuilder();
		for (AbstractDataSetColumn<?> column : dataSet.getColumns()) {
			if (column.getParameter().equals(dependentParameterDefintion)
					&& column instanceof DataSetObservationColumn<?>) {
				DataSetObservationColumn<?> depParamObsColumn = (DataSetObservationColumn<?>) column;
				for (ParameterValueList<?> pvl : depParamObsColumn.getValueLists()) {
					if (pvl.getSize() == 1) {
						pvl.addValue(pvl.getValues().get(0));
					}
				}
				builder.addColumn(depParamObsColumn);
			} else {
				builder.addColumn(column);
			}

		}

		return builder.createDataSet().convertToSimpleDataSet();
	}

}
