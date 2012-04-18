package org.sopeco.persistence.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.sopeco.persistence.dataset.DataSetAggregated;

@Entity
public class ExperimentSeriesRun {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Lob
	private DataSetAggregated resultDataSet;
	
	@ManyToOne
	private ExperimentSeries experimentSeries;

	public Long getId() {
		return id;
	}
	
	public ExperimentSeries getExperimentSeries() {
		return experimentSeries;
	}

	public void setExperimentSeries(ExperimentSeries experimentSeries) {
		this.experimentSeries = experimentSeries;
	}

	public DataSetAggregated getResultDataSet() {
		return resultDataSet;
	}
	
	public void setResultDataSet(DataSetAggregated resultDataSet){
		this.resultDataSet = resultDataSet; 
	}

}
