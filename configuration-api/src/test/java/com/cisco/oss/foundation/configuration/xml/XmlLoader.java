/*
 * Copyright 2015 Cisco Systems, Inc.
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

package com.cisco.oss.foundation.configuration.xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * A test class used to load test XML files as resources.
 * 
 * @author dventura
 *
 */
public class XmlLoader {

    /**
     * Loads the contents of the file at 'path' and returns the contents as String
     *
     * @param path the resource path.
     * @return The contents of the file as String
     * @throws XmlLoaderException
     */
    public String loadXmlFromResource(String path) throws XmlLoaderException {
        BufferedReader br = null;
        try {
            StringBuffer sb = new StringBuffer();
            br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(path)));
            String line = null;

            do{
                line = br.readLine();
                if(line != null) {
                    sb.append(line + "\n");
                }
            } while(line != null);

            br.close();
            br = null;

            return(sb.toString());
        } catch (IOException e) {
            throw new XmlLoaderException(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {}
            }
        }
    }
}
