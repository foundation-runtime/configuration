package com.cisco.oss.foundation.configuration;

public enum CcpConstants {
	
	// singleton implemetnation.
		INSTANCE;

		// the ccp server environment variable must much a host port pair in the
		// form of: host:port;host:port etc...
		public static final String CCP_SERVER__HOST_REG_EX = "[[a-zA-Z0-9\\-]*:\\d*[;]*]*";
		public static final String CCP_SERVER__IPV4_REG_EX = "[\\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\\b*:\\d*[;]*]*";
		public static final String CCP_SERVER__IPV6_REG_EX = "[/^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$/:\\d*[;]*]*"; 
		

		public static final String CCP_ENABLED = "_CCP_ENABLED";
		public static final String ARTIFACT_VERSION = "_ARTIFACT_VERSION";
		public static final String FQDN = "_FQDN";
		public static final String ARTIFACT_NAME = "_ARTIFACT_NAME";
		public static final String RPM_SOFTWARE_NAME = "_RPM_SOFTWARE_NAME";
		public static final String INSTALL_DIR = "_INSTALL_DIR";
		public static final String CCP_CONFIG_FILE = "/ccpConfig.xml";
		public static final String VCS_CONSOLE = "vcsconsole";
		public static final String CCP_SERVER_PORT = "_CCP_SERVER_PORT";
		public static final String CCP_SERVER = "_CCP_SERVER";
		public static final String CCP_DB_URL = "_CCP_DB_URL";
		public static final String CCP_DB_USER = "_CCP_DB_USER";
		public static final String CCP_DB_PASSWORD = "_CCP_DB_PASSWORD";
		public static final String BASE_INSTALL_PATH = "/opt/nds/installed";
		public static final String CCP_PASSWORD_ENCRYPTION = "_CCP_PASSWORD_ENCRYPTION";
		public static final String CCP_COMPONENT_CHECK_DELAY="_CCP_COMPONENT_CHECK_DELAY";
		public static final String CCP_COMPONENT_CHECK_ENABLE="_CCP_COMPONENT_CHECK_ENABLE";
		
		public static final String REDUNDANCY_ISENABLED = "_REDUNDANCY_ISENABLED";
		public static final String REDUNDANCY_PEER_HOST = "_REDUNDANCY_PEER_HOST";
		public static final String REDUNDANCY_PEER_HEARTBEAT_PORT = "_REDUNDANCY_PEER_HEARTBEATPORT";
		public static final String REDUNDACY_HOST = "_REDUNDANCY_HOST";
		public static final String REDUNDANCY_HEARTBEAT_PORT = "_REDUNDANCY_HEARTBEATPORT";
		public static final String REDUNDACY_ISPREFERRED_MASTER = "_REDUNDANCY_ISPREFFEREDMASTER";
		public static final String REDUNDACY_HEARTBEAT_INTERVAL = "_REDUNDANCY_HEARTBEATINTERVAL";
		
		public static final String MONITOR_MAX_INNER_PORT="_SERVICE_MXAGENTREGISTRY_INNERPORT";
		public static final String MONITOR_MAX_RMI_PORT="_SERVICE_MXAGENTREGISTRY_PORT";
		public static final String CCP_SERVICE_DISCOVERY_LEASE = "_CCP_SERVICE_DISCOVERY_LEASE";
		
		//USED FOR HORNETQ CCP-LOADER
		public static final String CONFIG_SOURCE = "_CONFIG_SOURCE";

}
