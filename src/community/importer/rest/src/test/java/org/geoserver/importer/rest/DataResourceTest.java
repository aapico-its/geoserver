/* Copyright (c) 2001 - 2013 OpenPlans - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.importer.rest;

import java.io.File;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.geoserver.importer.Directory;
import org.geoserver.importer.ImporterTestSupport;

import com.mockrunner.mock.web.MockHttpServletResponse;

public class DataResourceTest extends ImporterTestSupport {

    @Override
    protected void setUpInternal() throws Exception {
        super.setUpInternal();
    
        File dir = unpack("shape/archsites_epsg_prj.zip");
        unpack("shape/bugsites_esri_prj.tar.gz", dir);
        importer.createContext(new Directory(dir));
    }

    public void testGet() throws Exception {
        JSONObject json = (JSONObject) getAsJSON("/rest/imports/0/data");
        assertEquals("directory", json.getString("type"));
        assertEquals(2, json.getJSONArray("files").size());
    }

    public void testGetFiles() throws Exception {
        JSONObject json = (JSONObject) getAsJSON("/rest/imports/0/data/files");
        assertEquals(2, json.getJSONArray("files").size());
    }

    public void testGetFile() throws Exception {
        JSONObject json = (JSONObject) getAsJSON("/rest/imports/0/data/files/archsites.shp");
        assertEquals("archsites.shp", json.getString("file"));
        assertEquals("archsites.prj", json.getString("prj"));
    }

    public void testDelete() throws Exception {
        MockHttpServletResponse response = 
            getAsServletResponse("/rest/imports/0/data/files/archsites.shp");
        assertEquals(200, response.getStatusCode());

        response = deleteAsServletResponse("/rest/imports/0/data/files/archsites.shp");
        assertEquals(204, response.getStatusCode());
        
        response = getAsServletResponse("/rest/imports/0/data/files/archsites.shp");
        assertEquals(404, response.getStatusCode());

        JSONArray arr = ((JSONObject)getAsJSON("/rest/imports/0/data/files")).getJSONArray("files");
        assertEquals(1, arr.size());
    }

}
