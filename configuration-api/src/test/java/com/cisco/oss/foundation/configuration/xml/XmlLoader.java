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
