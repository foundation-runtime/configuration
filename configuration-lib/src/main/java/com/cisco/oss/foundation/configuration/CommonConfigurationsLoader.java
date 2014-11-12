/*
 * Copyright 2014 Cisco Systems, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.cisco.oss.foundation.configuration;

import com.cisco.oss.foundation.configuration.xml.jaxb.*;
import com.cisco.oss.foundation.logging.ApplicationStateInterface;
import com.cisco.oss.foundation.logging.FoundationLevel;
import org.apache.commons.configuration.*;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * Create a common configuration object based on the List of resource created by
 * Spring. This class us built by injection container it relies on a previously
 * loaded bean called: configResourcesLoader.<br>
 * <br>
 * This loader is in charge of building the configuration layers for the
 * application in respect of the commons configuration "Configuration"
 * interface.<br>
 * It is injected with the resources list built by the resources loader and
 * looping on all resources creating the actual Composite Configuration object
 * that holds all the layers.<br>
 * The layer that is first put in the composite configuration is the strongest
 * so the order of the configuration layers are:<br>
 * 1. customer configuration.<br>
 * 2. deployment configuration.<br>
 * 3. factory/default configuration.<br>
 * <p/>
 * there are 2 ways in which you can obtain the configuration object create by
 * Infra:<br>
 * <br>
 * 1. Via Spring injection.<br>
 * 2. Via <code>ConfigurationFactory</code> API.<br>
 * <p/>
 * For information on <code>ConfigurationFactory</code> please refer in it's
 * javadoc.
 * <p/>
 * <p>
 * Spring injection: In order to use the spring injection you need the
 * following:<br>
 * 1. Obtain a spring xml file which has a definition to your bean that will
 * contain the commons configuration object.<br>
 * 2. Add a reference the the infra bean represented by "configuration" bean id.
 * <br>
 * 3. Load the spring file to start the injection container.<br>
 * <p/>
 * Following is an example of a typical spring file:
 * <p>
 * <xmp> <?xml version="1.0" encoding="UTF-8"?> <beans
 * xmlns="http://www.springframework.org/schema/beans"
 * xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation=
 * "http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd"
 * default-lazy-init="false">
 * <p/>
 * <import resource="classpath:/META-INF/configurationContext.xml" />
 * <p/>
 * <bean id="sampleBean" class="com.nds.cab.sample.SampleBean"> <property
 * name="configuration" ref="configuration"/> </bean> </xmp>
 * <p/>
 * In order for the above to work you will need to add the
 * <code>org.apache.commons.configuration.Configuration</code> class as a class
 * member in your SampleBean. Another thing you must do is have: 1. A default
 * constructor with out any arguments. 2. A setter method for the configuration
 * class member.
 * <p/>
 * now you can use the injected configuration member as created in this
 * <code>CommonconfigurationLoader</code> class.
 * </p>
 * 
 * @author Joel Gurfinkel
 * @author Yair Ogen
 * @see com.cisco.oss.foundation.configuration.ConfigResourcesLoader
 * @see com.cisco.oss.foundation.configuration.ConfigurationFactory
 * @see org.apache.commons.configuration.Configuration
 */
public class CommonConfigurationsLoader implements FactoryBean<Configuration>, InitializingBean {

	private static final int NUM_OF_BACKUP_FILES = 50;

	private static final Logger LOGGER = LoggerFactory.getLogger(CommonConfigurationsLoader.class);
	private static final Logger AUDITOR = LoggerFactory.getLogger("audit." + CommonConfigurationsLoader.class);

	private static CompositeConfiguration configuration = null;

	private static boolean printedToLog = false;

	private List<Resource> resourcesList;

	// private Set<String> factoryKeySet = new HashSet<String>();

	// private DBAPI dbapi;

	private static final SimpleDateFormat ROLLING_TIME_STAMP = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

	// TODO: would it be better to create a class to wrap key and value
	// together?
	private final Map<String, String> descriptionMap = new HashMap<String, String>();

	// primitive dynamic reload
	private final Set<String> enablesDynamicSupportSet = new HashSet<String>();

	private boolean delimiterParsingDisabled = false;

	private String defaultListDelimiter = ",";

    private ApplicationStateInterface applicationState;

    public void setApplicationState(ApplicationStateInterface applicationState) {
        this.applicationState = applicationState;
    }

    @Override
	public void afterPropertiesSet() throws Exception {
		if (configuration == null) {
			configuration = new FoundationCompositeConfiguration();
			configuration.setDelimiterParsingDisabled(delimiterParsingDisabled);
			AbstractConfiguration.setDefaultListDelimiter(defaultListDelimiter.charAt(0));
			boolean centralConfigEnabled = Boolean.valueOf(System.getenv(CcpConstants.CCP_ENABLED));
			if (centralConfigEnabled) {

                if(applicationState != null) {
                    applicationState.setState(FoundationLevel.INFO, "central configuration IS enabled!");
                }
				// LOGGER.info("central configuration IS enabled!");
				CentralConfigurationUtil.INSTANCE.loadCentralConfiguration(configuration, descriptionMap, enablesDynamicSupportSet);

				// for backward compatibility also load regular config files
				// with lower priority
				loadNonCentralConfiguration(configuration, false);

				createFallBackFile(configuration);

				LOGGER.info("central configuration dynamic reload IS enabled!");
				DynamicConfigurationSupport dynamicConfigurationSupport = new DynamicConfigurationSupport();
				dynamicConfigurationSupport.start();

				// register first time for service discovery support
//				try {
//					String result = CentralConfigurationUtil.INSTANCE.registerServerWithCCP();
//					if (result != null) {
//						String[] splits = result.split(";");
//						if (splits.length == 2) {
//							int levelInstance = Integer.parseInt(splits[0]);
//							int leasePeriod = Integer.parseInt(splits[1]);
//							new ServiceDiscoveryPresenceAwarness(levelInstance, leasePeriod).start();
//						} else {
//							LOGGER.warn("Got illegal response from CCP server. Reponse is: " + result);
//						}
//					}
//				} catch (Exception e) {
//					AUDITOR.error("problem with CCP service discoery: " + e, e);
//				}

			} else {
                if(applicationState != null) {
                    applicationState.setState(FoundationLevel.INFO, "central configuration IS NOT enabled!");
                }
				// LOGGER.info("central configuration IS NOT enabled!");
				loadNonCentralConfiguration(configuration, true);

				validateConfiguration();

				reportOrphanParameters();

			}
			printPropertiesLoaded(configuration);
		}

	}

	private void reportOrphanParameters() {

		StringBuilder orphansStr = new StringBuilder("The following configuration parameters were found with no backing definition in any component/library:\n");
		List<String> suspectedOrphans = new ArrayList<String>();

		try {
			Iterator<String> keys = configuration.getKeys();

			while (keys.hasNext()) {
				String key = (String) keys.next();
				if (!CentralConfigurationUtil.parameterMap.containsKey(key)) {
					suspectedOrphans.add(key);
				}
			}

			Iterator<String> suspecteOrphaneItr = suspectedOrphans.iterator();

			while (suspecteOrphaneItr.hasNext()) {

				String suspectedOrphan = suspecteOrphaneItr.next();
				Set<Entry<String, Parameter>> paramEntries = CentralConfigurationUtil.parameterMap.entrySet();

				for (Entry<String, Parameter> paramEntry : paramEntries) {
					final String key = paramEntry.getKey();
					Parameter parameter = paramEntry.getValue();

					if (suspectedOrphan.startsWith(key)) {

						// TODO: consider changing above if and pulling out this
						// if
						if (parameter.isIsArray() || parameter.getType().equals(ParameterKind.STRUCTURE)) {

							if (parameter.isIsArray() && !parameter.getType().equals(ParameterKind.STRUCTURE)) {

								String stripedKey = suspectedOrphan.substring(0, suspectedOrphan.lastIndexOf("."));
								if (stripedKey.equals(key)) {
									suspecteOrphaneItr.remove();
									break;
								}

							} else { // parameter is a STRUCTURE and / or an
										// Array
								String[] splittedOrphan = suspectedOrphan.split("\\.");
								String base = "";
								int baseIndex = 0;

								if (key.contains(".")) {
									String[] split = key.split("\\.");
									int numberOfDots = split.length - 1;
									baseIndex += numberOfDots;
									for (int i = 0; i < split.length; i++) {
										base += split[i] + ".";
									}
									base = base.substring(0, base.lastIndexOf('.'));
								} else {
									base = splittedOrphan[baseIndex];
								}

								if (key.equals(base)) {

									boolean found = false;
									int index = baseIndex + 2;

									if (parameter.isIsArray()) {

										List<StructureMemberDefinition> structureMemberDefinitions = parameter.getStructureDefinition().getStructureMemberDefinitions();

										for (StructureMemberDefinition structureMemberDefinition : structureMemberDefinitions) {

											String name = structureMemberDefinition.getName();
											int numOfTokens = 0;
											if (name.contains(".")) {
												numOfTokens = name.split("\\.").length - 1;
											}

											// TODO: what if the suspected
											// orphan
											// has more tokens?
											if (splittedOrphan.length > (index + numOfTokens)) {
												String orphanSubset = splittedOrphan[index];
												if (numOfTokens > 0) {
													for (int i = 1; i <= numOfTokens; i++) {
														orphanSubset += "." + splittedOrphan[index + i];
													}
												}
												if (name.equals(orphanSubset)) {
													suspecteOrphaneItr.remove();
													found = true;
													break;
												}
											}

											if (structureMemberDefinition.isIgnoreName() && structureMemberDefinition.isIsArray() && splittedOrphan.length > (baseIndex + 3) && structureMemberDefinition.getStructureDefinition() != null
													&& structureMemberDefinition.getStructureDefinition().getStructureMemberDefinitions().size() > 0) {

												List<StructureMemberDefinition> inner = structureMemberDefinition.getStructureDefinition().getStructureMemberDefinitions();

												for (StructureMemberDefinition innerStructureMemberDefinition : inner) {

													StructureDefinition innerStructureDefinition = innerStructureMemberDefinition.getStructureDefinition();

													// we assume the
													// innerStructureMemberDefinition
													// name has no '.'
													if (innerStructureMemberDefinition.isIgnoreName() && innerStructureMemberDefinition.isIsArray() && splittedOrphan.length > (baseIndex + 4) && innerStructureDefinition != null && innerStructureDefinition.getStructureMemberDefinitions().size() > 0) {

														List<StructureMemberDefinition> innerStructureMemberDefinitionList = innerStructureDefinition.getStructureMemberDefinitions();

														for (StructureMemberDefinition structureMemberDefinition2 : innerStructureMemberDefinitionList) {
															if (structureMemberDefinition2.getName().equals(splittedOrphan[baseIndex + 4])) {
																suspecteOrphaneItr.remove();
																found = true;
																break;
															}

														}

													} else if (innerStructureMemberDefinition.getName().equals(splittedOrphan[baseIndex + 3])) {
														suspecteOrphaneItr.remove();
														found = true;
														break;
													}
												}
											}
											// else {
											// if (splittedOrphan.length > index
											// &&
											// name.equals(splittedOrphan[index]))
											// {
											// suspecteOrphaneItr.remove();
											// found = true;
											// break;
											// }
											// }
										}

										if (found)
											break;
									} else {// parameter is a structure that is
											// not
											// an array

										List<StructureMemberDefinition> structureMemberDefinitions = parameter.getStructureDefinition().getStructureMemberDefinitions();
										for (StructureMemberDefinition structureMemberDefinition : structureMemberDefinitions) {
											String name = structureMemberDefinition.getName();
                                            boolean ignoreName = structureMemberDefinition.isIgnoreName();
											if (structureMemberDefinition.isIsArray()) {

												if (structureMemberDefinition.getType().equals(ParameterKind.STRUCTURE)) {

													List<StructureMemberDefinition> innerStructureMemberDefinitions = structureMemberDefinition.getStructureDefinition().getStructureMemberDefinitions();
													for (StructureMemberDefinition innerStructureMemberDefinition : innerStructureMemberDefinitions) {

														if (splittedOrphan.length > index && innerStructureMemberDefinition.getName().equals(splittedOrphan[index])) {
															suspecteOrphaneItr.remove();
															found = true;
															break;

                                                        }else  if (splittedOrphan.length > (index+1) && !ignoreName && innerStructureMemberDefinition.getName().equals(splittedOrphan[index+1])) {
                                                            suspecteOrphaneItr.remove();
                                                            found = true;
                                                            break;

														} else {

															if (innerStructureMemberDefinition.isIgnoreName() && innerStructureMemberDefinition.isIsArray() && splittedOrphan.length > (baseIndex + 3) && innerStructureMemberDefinition.getStructureDefinition() != null
																	&& innerStructureMemberDefinition.getStructureDefinition().getStructureMemberDefinitions().size() > 0) {

																List<StructureMemberDefinition> innerStructureMemberDefinitionList = innerStructureMemberDefinition.getStructureDefinition().getStructureMemberDefinitions();

																for (StructureMemberDefinition innerInnerStructureMemberDefinition : innerStructureMemberDefinitionList) {
																	StructureDefinition innerStructureDefinition = innerInnerStructureMemberDefinition.getStructureDefinition();

																	if (innerInnerStructureMemberDefinition.isIgnoreName() && innerInnerStructureMemberDefinition.isIsArray() && splittedOrphan.length > (baseIndex + 4) && innerInnerStructureMemberDefinition != null
																			&& innerStructureDefinition.getStructureMemberDefinitions().size() > 0) {
																		List<StructureMemberDefinition> innerInnerStructureMemberDefinitionList = innerStructureDefinition.getStructureMemberDefinitions();

																		for (StructureMemberDefinition structureMemberDefinition2 : innerInnerStructureMemberDefinitionList) {

																			if (structureMemberDefinition2.getName().equals(splittedOrphan[baseIndex + 4])) {
																				suspecteOrphaneItr.remove();
																				found = true;
																				break;
																			}

																		}

																	} else if (innerInnerStructureMemberDefinition.getName().equals(splittedOrphan[baseIndex + 3])) {
																		suspecteOrphaneItr.remove();
																		found = true;
																		break;
																	}
																}
															} else {
																if (splittedOrphan.length > index && innerStructureMemberDefinition.getName().equals(splittedOrphan[index])) {
																	suspecteOrphaneItr.remove();
																	found = true;
																	break;
																}
															}
														}
													}

												} else {

													int numOfTokens = 0;
													if (name.contains(".")) {
														if (name.contains(".")) {
															numOfTokens = name.split("\\.").length;
														}
													}

													if (splittedOrphan.length > baseIndex + numOfTokens) {

														String orphanSubset = splittedOrphan[baseIndex + 1];
														if (numOfTokens > 0) {

															orphanSubset = "";

															for (int i = 0; i < numOfTokens; i++) {
																orphanSubset += splittedOrphan[baseIndex + 1 + i] + ".";
															}

															orphanSubset = orphanSubset.substring(0, orphanSubset.lastIndexOf('.'));
														}
														if (name.equals(orphanSubset)) {
															suspecteOrphaneItr.remove();
															found = true;
															break;
														}
													}

												}

											} else {
												if (name.contains(".")) {

													int numOfTokens = 0;
													if (name.contains(".")) {
														if (name.contains(".")) {
															numOfTokens = name.split("\\.").length;
														}
													}

													if (splittedOrphan.length > baseIndex + numOfTokens) {

														String orphanSubset = "";
														for (int i = 0; i < numOfTokens; i++) {
															orphanSubset += splittedOrphan[baseIndex + 1 + i] + ".";
														}
														orphanSubset = orphanSubset.substring(0, orphanSubset.lastIndexOf('.'));
														if (name.equals(orphanSubset)) {
															suspecteOrphaneItr.remove();
															found = true;
															break;
														}
													}

												} else if (name.equals(splittedOrphan[baseIndex + 1])) {
													// TODO: handle members that
													// have
													// '.'
													suspecteOrphaneItr.remove();
													found = true;
													break;
												}
											}
										}
										if (found)
											break;
									}
								}
							}
						}
					}
				}
			}

		} catch (Exception e) {
			suspectedOrphans = new ArrayList<String>();
			LOGGER.debug("couldn't run the orhpan configuration validation test. error is: " + e);
		}

		for (String suspectedOrphan : suspectedOrphans) {
			orphansStr.append(suspectedOrphan).append("\n");
		}

		if (suspectedOrphans.size() > 0) {
			// if (configuration.getBoolean("configuration.rejectInvalidConfiguration",
			// false)) {
			// throw new IllegalArgumentException(orphansStr.toString());
			// } else {
			LOGGER.debug(orphansStr.toString());
			// }

		}

	}

	private void validateConfiguration() {

		try {
			List<String> requiredErrors = new ArrayList<String>();
			List<String> typeErrors = new ArrayList<String>();
			List<String> rangeErrors = new ArrayList<String>();

			Map<String, Parameter> parameterMap = CentralConfigurationUtil.parameterMap;
			for (Parameter parameter : parameterMap.values()) {

				String name = parameter.getName();
				boolean required = parameter.isRequired();
				ParameterKind type = parameter.getType();
				ParameterRange range = parameter.getRange();
				EnabledBy enabledBy = parameter.getEnabledBy();

				if (parameter.isIsArray()) {

					if (ParameterKind.STRUCTURE.equals(parameter.getType())) {

						List<StructureMemberDefinition> structureMemberDefinitions = parameter.getStructureDefinition().getStructureMemberDefinitions();
						validateStructureArray(requiredErrors, typeErrors, rangeErrors, name, structureMemberDefinitions, enabledBy);

					} else {// primitive array

						validatePrimitveArray(requiredErrors, typeErrors, rangeErrors, name, required, type, range, enabledBy);

					}
				} else {// not an array

					// handle primitive
					if (!ParameterKind.STRUCTURE.equals(parameter.getType())) {

						validateSingleParameter(requiredErrors, typeErrors, rangeErrors, name, required, type, range, enabledBy);

					} else {// handle single structure

						StructureDefinition structureDefinition = parameter.getStructureDefinition();
						if (structureDefinition != null) {

							List<StructureMemberDefinition> structureMemberDefinitions = structureDefinition.getStructureMemberDefinitions();
							if (structureMemberDefinitions != null) {
								for (StructureMemberDefinition structureMemberDefinition : structureMemberDefinitions) {

									boolean isArray = structureMemberDefinition.isIsArray();
									if (isArray) {

										boolean ignoreName = structureMemberDefinition.isIgnoreName();
										String tempName = name;
										if (!ignoreName) {
											tempName = tempName + "." + structureMemberDefinition.getName();
										}

										if (structureMemberDefinition.getStructureDefinition() != null) {
											List<StructureMemberDefinition> innerStructureMemberDefinitions = structureMemberDefinition.getStructureDefinition().getStructureMemberDefinitions();
											validateStructureArray(requiredErrors, typeErrors, rangeErrors, tempName, innerStructureMemberDefinitions, enabledBy);
										} else {
											validatePrimitveArray(requiredErrors, typeErrors, rangeErrors, tempName, structureMemberDefinition.isRequired(), structureMemberDefinition.getType(), range, enabledBy);
										}
									} else {
										String tempName = name + "." + structureMemberDefinition.getName();
										required = structureMemberDefinition.isRequired();
										type = structureMemberDefinition.getType();
										range = structureMemberDefinition.getRange();

										validateSingleParameter(requiredErrors, typeErrors, rangeErrors, tempName, required, type, range, enabledBy);
									}
								}
							}
						}

					}
				}
			}

			if (!requiredErrors.isEmpty() || !typeErrors.isEmpty() || !rangeErrors.isEmpty()) {

				String errorMessage = buildErrorMultiLineMessage(requiredErrors, typeErrors, rangeErrors);

				if (configuration.getBoolean("configuration.rejectInvalidConfiguration", false)) {
					throw new IllegalArgumentException(errorMessage);
				} else {
					LOGGER.error(errorMessage);
				}
			}
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error("can't run configuraiton validation", e);
		}

	}

	private void validatePrimitveArray(List<String> requiredErrors, List<String> typeErrors, List<String> rangeErrors, String name, boolean required, ParameterKind type, ParameterRange range, EnabledBy enabledBy) {
		Configuration subset = configuration.subset(name);

		// we assume that the primitive array is unique, hence
		// anythink after the base key name is the index
		@SuppressWarnings("unchecked")
		Iterator<String> keys = subset.getKeys();
		boolean found = false;

        if (keys.hasNext()) {
            while (keys.hasNext()) {
                found = true;
                String index = (String) keys.next();
                validateSingleParameter(requiredErrors, typeErrors, rangeErrors, name + "." + index, required, type, range, enabledBy);
            }
        }else if (enabledBy != null) {
            String enabledByName = enabledBy.getParameterName();
            Operator operator = enabledBy.getOperator();
            if (Operator.E.equals(operator)) {
                List<PrimitiveValue> primitiveValues = enabledBy.getValue().getPrimitiveValues();
                if (primitiveValues != null) {
                    String enabledByValue = primitiveValues.get(0).getValue();
                    String enabledByValue2 = configuration.getString(enabledByName, null);
                    if (enabledByValue != null && enabledByValue2 != null && !enabledByValue.equals(enabledByValue2)) {
                        found = true;
                    }
                }
            }
        }

		if (required && !found) {
			requiredErrors.add("[Array] " + name);
		}
	}

	private void validateStructureArray(List<String> requiredErrors, List<String> typeErrors, List<String> rangeErrors, String baseName, List<StructureMemberDefinition> structureMemberDefinitions, EnabledBy enabledBy) {

		// build 2 lists - primitives and arrays
		List<StructureMemberDefinition> primitives = new ArrayList<StructureMemberDefinition>();
		List<StructureMemberDefinition> arrays = new ArrayList<StructureMemberDefinition>();
		for (StructureMemberDefinition structureMemberDefinition : structureMemberDefinitions) {
			boolean isArray = structureMemberDefinition.isIsArray();
			if (isArray) {
				arrays.add(structureMemberDefinition);
			} else {
				primitives.add(structureMemberDefinition);
			}
		}

		// loop over primitives
		Set<String> names = new HashSet<String>();
		for (StructureMemberDefinition structureMemberDefinition : primitives) {
			names.addAll(calculateNameWithIndex(baseName, structureMemberDefinition.getName()));
		}

		// for each name found me may have a primitive definition that is
		// missing
		// loop again to supplement it

		Map<String, StructureMemberDefinition> nameToPrimitiveDefinition = new HashMap<String, StructureMemberDefinition>();

		for (String name : names) {

			for (StructureMemberDefinition primitive : primitives) {
				String key = name + "." + primitive.getName();
				nameToPrimitiveDefinition.put(key, primitive);
			}
			// for (StructureMemberDefinition primitive : primitives) {
			//
			// // skip out own name
			// String primitiveName = primitive.getName();
			// if (!key.endsWith(primitiveName)) {
			// int dotIndex = key.lastIndexOf('.');
			// String lastKey = key.substring(dotIndex + 1);
			// String newName = key.replace(lastKey, primitiveName);
			// nameToPrimitiveDefinition.put(newName, primitive);
			// }
			// // else {
			// // key = name + "." + primitive.getName();
			// // nameToPrimitiveDefinition.put(key, primitive);
			// // }
			// }
		}

		// now that we add names that may be required but missing, we can loop
		// on updated names and validate
		for (Entry<String, StructureMemberDefinition> entry : nameToPrimitiveDefinition.entrySet()) {

			StructureMemberDefinition structureMemberDefinition = entry.getValue();
			boolean required = structureMemberDefinition.isRequired();
			ParameterKind type = structureMemberDefinition.getType();
			ParameterRange range = structureMemberDefinition.getRange();

			validateSingleParameter(requiredErrors, typeErrors, rangeErrors, entry.getKey(), required, type, range, enabledBy, structureMemberDefinition.getDefaultValue());
		}

		for (StructureMemberDefinition structureMemberDefinition : arrays) {

			names = calculateNameWithIndex(baseName, structureMemberDefinition.getName());

			for (String name : names) {

				boolean ignoreName = structureMemberDefinition.isIgnoreName();
				if (!ignoreName) {
					name = name + "." + structureMemberDefinition.getName();
				}

				StructureDefinition structureDefinition = structureMemberDefinition.getStructureDefinition();

				// inner structure array
				if (structureDefinition != null) {

					List<StructureMemberDefinition> innerStructureMemberDefinitions = structureDefinition.getStructureMemberDefinitions();
					validateStructureArray(requiredErrors, typeErrors, rangeErrors, name, innerStructureMemberDefinitions, enabledBy);

				} else { // inner primitive array

					validatePrimitveArray(requiredErrors, typeErrors, rangeErrors, name, structureMemberDefinition.isRequired(), structureMemberDefinition.getType(), structureMemberDefinition.getRange(), enabledBy);
				}

			}

		}

	}

	private Set<String> calculateNameWithIndex(final String baseName, final String name) {
		Set<String> names = new HashSet<String>();

		String origBaseName = baseName;

		Configuration subset = configuration.subset(baseName);

		@SuppressWarnings("unchecked")
		Iterator<String> keys = subset.getKeys();

		boolean found = false;

		while (keys.hasNext()) {
			String key = (String) keys.next();
			if (key.contains("." + name)) {
				String tempBaseName = baseName + "." + key;
				tempBaseName = tempBaseName.substring(0, tempBaseName.lastIndexOf("." + name));
				names.add(tempBaseName);
				found = true;
			}
		}

		return names;
	}

	private void validateSingleParameter(List<String> requiredErrors, List<String> typeErrors, List<String> rangeErrors, String name, boolean required, ParameterKind type, ParameterRange range, EnabledBy enabledBy) {
		validateSingleParameter(requiredErrors, typeErrors, rangeErrors, name, required, type, range, enabledBy, null);
	}

	private void validateSingleParameter(List<String> requiredErrors, List<String> typeErrors, List<String> rangeErrors, String name, boolean required, ParameterKind type, ParameterRange range, EnabledBy enabledBy, ParameterValue defaultValue) {
		String value = configuration.getString(name, null);

		// validate existing when required
		boolean defValueExists = defaultValue != null && defaultValue.getPrimitiveValues() != null && !defaultValue.getPrimitiveValues().isEmpty();
		if(value == null && defValueExists){
			value = defaultValue.getPrimitiveValues().get(0).getValue();
			configuration.setProperty(name, value);
		}
		if (value == null ) {

			if (required) {
				if (enabledBy != null) {
					String enabledByName = enabledBy.getParameterName();
					Operator operator = enabledBy.getOperator();
					if (Operator.E.equals(operator)) {
						List<PrimitiveValue> primitiveValues = enabledBy.getValue().getPrimitiveValues();
						if (primitiveValues != null) {
							String enabledByValue = primitiveValues.get(0).getValue();
							String enabledByValue2 = configuration.getString(enabledByName, null);
							if (enabledByValue != null && enabledByValue2 != null && enabledByValue.equals(enabledByValue2)) {
								requiredErrors.add(name);
							}
						}
					}
				} else {
					requiredErrors.add(name);
				}

			}
		} else { // validate types and ranges
			switch (type) {
			case INTEGER:
				try {
					int intVal = Integer.parseInt(value);
					if (range != null && range.getValueRange() != null) {
						ValueRange valueRange = range.getValueRange();
						String minStr = valueRange.getMin();
						String maxStr = valueRange.getMax();
						int min = minStr != null ? Integer.parseInt(minStr) : Integer.MIN_VALUE;
						int max = maxStr != null ? Integer.parseInt(maxStr) : Integer.MAX_VALUE;
						if (intVal < min || intVal > max) {
							rangeErrors.add(name + ": expected range is ([min-max]): [" + min + "-" + max + "]");
						}
					}
				} catch (NumberFormatException e) {
					typeErrors.add(name + ": expecting Integer");
				}
				break;
			case BOOLEAN:
				String upValue = value.toUpperCase();
				if (!"FALSE".equals(upValue) && !"TRUE".equals(upValue)) {
					typeErrors.add(name + ": expecting Boolean");
				}
				break;

			case FLOAT:
				try {
					Float.parseFloat(value);
				} catch (NumberFormatException e) {
					typeErrors.add(name + ": expecting Float");
				}
				break;

			case STRING:
				if (range != null && range.getStringEna() != null) {
					List<StringEnum> stringEnumeration = range.getStringEna();
					List<String> stringEnums = new ArrayList<String>(stringEnumeration.size());
					boolean found = false;
					for (StringEnum stringEnum : stringEnumeration) {
						String strValue = stringEnum.getValue();
						stringEnums.add(strValue);
						if (strValue.equals(value)) {
							found = true;
							break;
						}
					}
					if (!found) {
						rangeErrors.add(name + ": expected range is " + stringEnums);
					}
				}
				break;

			default:
				break;
			}
		}
	}

	private String buildErrorMultiLineMessage(List<String> requiredErrors, List<String> typeErrors, List<String> rangeErrors) {
		StringBuilder builder = new StringBuilder();

		builder.append("Configuration errors:\n");

		if (!requiredErrors.isEmpty()) {
			builder.append("\nRequired configuration parameters without values:\n");
			for (String error : requiredErrors) {
				builder.append(error);
				builder.append("\n");
			}
		}

		if (!typeErrors.isEmpty()) {
			builder.append("\nType mismatch:\n");
			for (String error : typeErrors) {
				builder.append(error);
				builder.append("\n");
			}
		}

		if (!rangeErrors.isEmpty()) {
			builder.append("\nOut of range:\n");
			for (String error : rangeErrors) {
				builder.append(error);
				builder.append("\n");
			}
		}

		return builder.toString();
	}

	/**
	 * spring inject method.
	 *
	 * @param resourcesList
	 *            the list of all resources to load as property configuration
	 *            objects.
	 */
	public void setResourcesList(final List<Resource> resourcesList) {
		this.resourcesList = resourcesList;
	}

	/**
	 * As this is a Dynamic Proxy we expose externally the
	 * <code>org.apache.commons.configuration.Configuration</code> class.
	 * <p/>
	 * In this method we build the composite configuration based on the injected
	 * resources and return it as it will be injected in a client bean.
	 *
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	@Override
	public Configuration getObject() throws Exception { // NOPMD

		return configuration;
	}

	/**
	 * @param configuration
	 */
	private void createFallBackFile(CompositeConfiguration configuration) {

		String backupDir = CentralConfigurationUtil.getCcpBackupFileName();

		File backupDirFile = new File(backupDir);

		if (!backupDirFile.exists()) {
			backupDirFile.mkdirs();
		}

		cleanup(backupDirFile);

		File fallbackFile = new File(backupDir + CentralConfigurationUtil.CCP_BACKUP_FILE_NAME);

		try {

			// create temp file
			String tempFileName = backupDir + CentralConfigurationUtil.CCP_BACKUP_FILE_NAME + "_temp";
			File tempFile = new File(tempFileName);

			writeConfigurationToFile(configuration, tempFile);

			if (fallbackFile.exists()) {
				String fallBackFilePath = fallbackFile.getPath();
				boolean renamed = fallbackFile.renameTo(new File(fallBackFilePath + "_" + ROLLING_TIME_STAMP.format(new Date())));
				if (!renamed) {
					LOGGER.error("could not rename the file: " + fallBackFilePath);
				} else {
					fallbackFile.createNewFile();
				}
			} else {
				fallbackFile.createNewFile();
			}

			boolean renamed = tempFile.renameTo(fallbackFile);

			if (!renamed) {
				LOGGER.error("could not rename the file: " + tempFileName);
			}

		} catch (IOException e) {
            LOGGER.error("cannot fallback to property file!", e);
			throw new IllegalArgumentException("cannot fallback to property file!", e);
		}

		// Properties properties = new Properties();
		// while (keys.hasNext()) {
		// String key = keys.next();
		// if (configuration.getString(key) != null) {
		// properties.setProperty(key, configuration.getString(key));
		// }
		// }
		//
		// try {
		// properties.store(new FileOutputStream(fallbackFile), null);
		// } catch (FileNotFoundException e) {
		// throw new
		// IllegalArgumentException("cannot fallback to property file!", e);
		// } catch (IOException e) {
		// throw new
		// IllegalArgumentException("cannot fallback to property file!", e);
		// }

	}

	private void writeConfigurationToFile(CompositeConfiguration configuration, File fallbackFile) throws IOException {
		// loop over configuration and write to file.
		@SuppressWarnings("unchecked")
		Iterator<String> keys = configuration.getKeys();

		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fallbackFile));
		while (keys.hasNext()) {
			String key = keys.next();
			if (configuration.getString(key) != null) {
				String description = descriptionMap.get(key);
				String value = configuration.getString(key);
				if (description == null) {
					int indexOfDot = key.indexOf(".");
					if (indexOfDot > 0) {
						String tempKey = key.substring(0, indexOfDot);
						description = descriptionMap.get(tempKey);
					}
				}

				if (description != null) {
					bufferedWriter.write("#" + description + "\n");
				}
				bufferedWriter.write(key + "=" + value + "\n");
			}
		}

		bufferedWriter.flush();
		bufferedWriter.close();
	}

	private void cleanup(File backupDirFile) {

		File[] files = backupDirFile.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("ccp.backup.properties");
			}
		});

		if (files != null && files.length > NUM_OF_BACKUP_FILES) {
			Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
			files[files.length - 1].delete();
		}

	}

	private void loadNonCentralConfiguration(final CompositeConfiguration configuration, boolean includeCustomerConfig) throws IOException, ConfigurationException {

		Map<String, ParameterType> baseMap = new HashMap<String, ParameterType>();
		Map<String, Parameter> childMap = new HashMap<String, Parameter>();

		for (int i = resourcesList.size() - 1; i >= 0; i--) {
			final Resource resource = resourcesList.get(i);
			Configuration config = null;
			if (resource.getFilename().endsWith(".xml")) {
				Map<String, String> properties = new HashMap<String, String>();
				CentralConfigurationUtil.INSTANCE.loadPropertiesFromXml(properties, resource.getInputStream(), baseMap, childMap, configuration);
				// factoryKeySet.addAll(properties.keySet());
				config = new MapConfiguration(properties);// NOPMD
			} else {
				URL resourceUrl = resource.getURL();
				if (resourceUrl != null && resourceUrl.getPath().endsWith("config.properties")) {
					if (includeCustomerConfig) {
						config = createPropertiesConfiguration(resourceUrl);
					}
				} else {
					config = createPropertiesConfiguration(resourceUrl);
				}

			}

			if (config != null) {
				configuration.addConfiguration(config);
				if (config instanceof PropertiesConfiguration) {
					//update cache
					Iterator<String> keys = configuration.getKeys();
					while (keys.hasNext()) {
                        String next =  keys.next();
                        configuration.getString(next);
                    }
				}
			}
		}

		// for template support go over the map and add the required parameters
		Set<Entry<String, Parameter>> childEntries = childMap.entrySet();

		for (Entry<String, Parameter> childEntry : childEntries) {

			String key = childEntry.getKey();

			Parameter parameter = childEntry.getValue();

			// remove if exists
			CentralConfigurationUtil.parameterMap.remove(parameter);

			String base = parameter.getBase();
			ParameterType parameterType = baseMap.get(base);

			if (parameterType != null) {
				StructureDefinition baseStructureDefinition = parameterType.getStructureDefinition();
				StructureDefinition childStructureDefinition = parameter.getStructureDefinition();
				if (childStructureDefinition == null) {
					parameter.setStructureDefinition((StructureDefinition) baseStructureDefinition.clone());
				} else {
					List<StructureMemberDefinition> baseStructureMemberDefinitions = baseStructureDefinition.getStructureMemberDefinitions();
					List<StructureMemberDefinition> childStructureMemberDefinitions = childStructureDefinition.getStructureMemberDefinitions();
					childStructureMemberDefinitions.addAll(baseStructureMemberDefinitions);
				}

                parameter.setRequired(parameterType.isRequired());
                parameter.setRequiresRestart(parameterType.isRequiresRestart());
                parameter.setAdvanced(parameterType.isAdvanced());
                parameter.setHidden(parameterType.isHidden());

			}



			// pull default values from parameter to structure member defaults
			if (parameter.getDefaultValue() != null && parameter.getDefaultValue().getStructureValues() != null) {
				List<StructureValue> structureValues = parameter.getDefaultValue().getStructureValues();
				for (StructureValue structureValue : structureValues) {
					if (structureValue != null) {
						List<StructureMemberValue> structureMemberValues = structureValue.getStructureMemberValues();
						for (StructureMemberValue structureMemberValue : structureMemberValues) {
							if (structureMemberValue != null && parameter.getStructureDefinition() != null) {
								String name = structureMemberValue.getName();
								List<StructureMemberDefinition> structureMemberDefinitions = parameter.getStructureDefinition().getStructureMemberDefinitions();
								for (StructureMemberDefinition structureMemberDefinition : structureMemberDefinitions) {
									if (name.equals(structureMemberDefinition.getName())) {
										ParameterValue defaultValue = structureMemberDefinition.getDefaultValue();

										ArrayList<PrimitiveValue> primitiveValues = new ArrayList<PrimitiveValue>();
										PrimitiveValue primitiveValue = new PrimitiveValue();
										primitiveValue.setValue(structureMemberValue.getValue());
										primitiveValue.setIndex(structureMemberValue.getIndex());
										primitiveValues.add(primitiveValue);

										if (defaultValue == null) {
											defaultValue = new ParameterValue();
											structureMemberDefinition.setDefaultValue(defaultValue);
											defaultValue.setPrimitiveValues(primitiveValues);
										} else {
											if (defaultValue.getPrimitiveValues() != null && defaultValue.getPrimitiveValues().size() > 0) {
												if (structureMemberDefinition.isIsArray()) {
													defaultValue.getPrimitiveValues().add(primitiveValue);
												} else {
													defaultValue.getPrimitiveValues().get(0).setValue(structureMemberValue.getValue());
												}
											} else {
												defaultValue.setPrimitiveValues(primitiveValues);
											}
										}
									}
								}
							}
						}
					}

				}

			}

			// add updated
			CentralConfigurationUtil.parameterMap.put(parameter.getName(), parameter);

			// reset the base parameter before we load the properties
			Map<String, String> properties = new HashMap<String, String>();
			parameter.setBase(null);
			CentralConfigurationUtil.INSTANCE.loadPropsFromParameter(properties, childMap, parameter, configuration);
			configuration.addConfiguration(new MapConfiguration(properties));
		}
	}

	private Configuration createPropertiesConfiguration(URL resourceUrl) throws ConfigurationException {
		Configuration config;
		config = new PropertiesConfiguration();// NOPMD
		PropertiesConfiguration propertiesConfiguration = (PropertiesConfiguration) config;
		propertiesConfiguration.setDelimiterParsingDisabled(delimiterParsingDisabled);
		propertiesConfiguration.load(resourceUrl);
		return config;
	}

	/**
	 * return the interface represented by this Dynamic proxy:
	 * <code>org.apache.commons.configuration.Configuration</code>.
	 *
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@Override
	public Class<Configuration> getObjectType() {
		return Configuration.class;
	}

	/**
	 * This class is a singleton.
	 *
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	@Override
	public boolean isSingleton() {
		return true;
	}

	private void printPropertiesLoaded(final CompositeConfiguration configuration) {

		if (printedToLog) {
			return;
		}

		printedToLog = true;

		@SuppressWarnings("unchecked")
		final Iterator<String> keys = configuration.getKeys();
		final StringBuilder logMessage = new StringBuilder("The properties loaded are:\n");
		while (keys.hasNext()) {

			final String key = keys.next();
			final Object value = configuration.getProperty(key);
			if (value != null && !(value.toString().contains("DUMMY"))) {
				logMessage.append(key).append("=").append(value).append("\n");
			}
		}
        if(applicationState != null) {
            applicationState.setState(FoundationLevel.INFO, logMessage.toString());
        }
	}

	public boolean isDelimiterParsingDisabled() {
		return delimiterParsingDisabled;
	}

	public void setDelimiterParsingDisabled(boolean delimiterParsingDisabled) {
		this.delimiterParsingDisabled = delimiterParsingDisabled;
	}

	public String getDefaultListDelimiter() {
		return defaultListDelimiter;
	}

	public void setDefaultListDelimiter(String defaultListDelimiter) {
		this.defaultListDelimiter = defaultListDelimiter;
	}

	/**
	 * run over the parameters that support dynamic reload update local cache
	 * and fallback file with the updated values if such exist.
	 *
	 * @author Yair Ogen
	 */
	private class DynamicConfigurationSupport extends Thread {

		public DynamicConfigurationSupport() {
			super();
			setName("DynamicConfigurationSupport");
			// Make this a daemon thread so it does not outlive any of the main
			// threads.
			setDaemon(true);
		}

		/**
		 * @see Thread#run()
		 */
		@Override
		public void run() {

			LOGGER.info("started re-load thread");

			while (true) {

				final long timeInterval = configuration.getLong("configuration.dynamicConfigReload.refreshDelay");
				LOGGER.trace("running re-load thread");
				try {

					CompositeConfiguration newConfig = new FoundationCompositeConfiguration();
					CentralConfigurationUtil.INSTANCE.loadCentralConfiguration(newConfig, descriptionMap, enablesDynamicSupportSet);
					boolean hasChanged = false;

					// support for primitive arrays
					for (String key : enablesDynamicSupportSet) {
						String newValue = newConfig.getString(key);
						if (configuration.getString(key) != null && !configuration.getString(key).equals(newValue)) {
							LOGGER.info("found a change in the configuration schema for key: {}. new value is: {}", key, newValue);
							hasChanged = true;
							configuration.setProperty(key, newValue);
						}
					}

//					for (String key : enablesDynamicSupportArraySet) {
//						Configuration newSubset = newConfig.subset(key);
//						Configuration oldSubset = configuration.subset(key);
//
//						@SuppressWarnings("unchecked")
//						Iterator<String> newKeysIter = newSubset.getKeys();
//
//						@SuppressWarnings("unchecked")
//						Iterator<String> oldKeysIter = oldSubset.getKeys();
//
//						while (newKeysIter.hasNext()) {
//							String k = newKeysIter.next();
//							String newValue = newSubset.getString(k);
//
//							if (newValue != null) {
//								oldSubset.setProperty(k, newValue);
//								hasChanged = true;
//							}
//						}
//
//						while (oldKeysIter.hasNext()) {
//							String k = oldKeysIter.next();
//							String newValue = newSubset.getString(k);
//							if (newValue == null) {
//								oldSubset.clearProperty(k);
//								hasChanged = true;
//							}
//						}
//
//					}

					//
					if (hasChanged) {
						FoundationCompositeConfiguration compositeConfiguration = (FoundationCompositeConfiguration)configuration;
						compositeConfiguration.clearCache();
						createFallBackFile(configuration);
						configuration.getLong("configuration.dynamicConfigReload.refreshDelay");
						FoundationConfigurationListenerRegistry.fireConfigurationChangedEvent();
					}

					Thread.sleep(timeInterval);
				} catch (Exception e) {
					LOGGER.error("Cannot update the dynamic configuration. Message is: " + e.getMessage(), e);
					try {
						// Pause for a couple of seconds before trying again to
						// prevent looping
						// at high speed and filling the logs.
						Thread.sleep(3000);
					} catch (InterruptedException e1) {
						// Don't care.
					}
				}
			}
		}
	}

	/**
	 * renew the lease so CCP knows when you're up and down.
	 *
	 * @author Yair Ogen
	 */
	private class ServiceDiscoveryPresenceAwarness extends Thread {

		private int levelInstanceId = -1;
		private int leaseExpiration = -1;

		public ServiceDiscoveryPresenceAwarness(int levelInstanceId, int leaseExpiration) {

			super();
			this.levelInstanceId = levelInstanceId;
			this.leaseExpiration = leaseExpiration;

			setName("ServiceDiscoveryPresenceAwarness");
			// Make this a daemon thread so it does not outlive any of the main
			// threads.
			setDaemon(true);
		}

		/**
		 * @see Thread#run()
		 */
		@Override
		public void run() {

			LOGGER.info("running ServiceDiscoveryPresenceAwarness thread");

			while (true) {

//				CentralConfigurationUtil.INSTANCE.renewLeaseWithCCP(levelInstanceId);

				try {
					long timeToSleep = TimeUnit.SECONDS.toMillis(leaseExpiration);
					Thread.sleep(timeToSleep - 100);
				} catch (InterruptedException e) {
					LOGGER.trace("interrupted",e);
				}
			}
		}
	}
}
