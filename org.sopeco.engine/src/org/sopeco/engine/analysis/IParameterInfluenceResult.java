/**
 * Copyright (c) 2013 SAP
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the SAP nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL SAP BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.sopeco.engine.analysis;

import java.util.List;

import org.sopeco.persistence.entities.definition.ParameterDefinition;

/**
 * Interface that encapsulates all result objects that describe parameter
 * influences.
 * 
 * @author Dennis Westermann
 * 
 */
public interface IParameterInfluenceResult extends IAnalysisResult {

	/**
	 * Returns a list of all ParameterInfluence-Objects describing the influence
	 * of the independent parameters on the dependent parameter.
	 * 
	 * @return list of IParameterInfluenceDescriptor
	 */
	List<IParameterInfluenceDescriptor> getAllParameterInfluenceDescriptors();

	/**
	 * Returns the ParameterInfluenceDescriptor-Object of the specified
	 * parameter.
	 * 
	 * @param parameter
	 *            parameter for which the influence descriptor should be
	 *            returned
	 * @return a descriptor that describes the influence of the given
	 *         independent parameter on the dependent parameter of the analysis
	 */
	IParameterInfluenceDescriptor getParameterInfluenceDescriptorByParam(ParameterDefinition parameter);
}
