/*******************************************************************************
 * Copyright 2011 Universidad Politécnica de Madrid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.universAAL.ui.handler.gui.swing;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Find the resources referenced by urls;
 * @author amedrano
 *
 */
public class ResourceMapper {

	/**
	 * The folders where the resources should be allocated, whether it is in the confDir or
	 * in the resources (inside the JAR)
	 */
	static String[] resourceFolders = {"icons/", "images/", "resources/"};
	
	/**
	 * Searches for the specified url in the config directory and JAR resources
	 * @param url
	 * 			relative url of the resource to find
	 * @return
	 * 		the {@link URL} for the resource, null if not found
	 */
	static public URL search(String url) {
		
		URL resource;
		try {
			resource = new URL(url);
			if (existsURL(resource)) {
				return resource;
			}
			else {
				return null;
			}
		} catch (MalformedURLException e) {
			resource = searchFolder(url);
			if (resource != null) {
				return resource;
			}
			else {
				return searchResources(url);
			}
		}
				
	}
	
	/**
	 * Check that the resource pointed by the URL really exists
	 * @param url
	 * @return
	 */
	static private boolean existsURL(URL url) {
		URLConnection con;
		try {
			con = url.openConnection();
			con.connect();
			con.getInputStream();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * Searched for the specified url in the config directory.
	 * @param url
	 * 		the relative url of the file to look for.
	 * @return
	 * 		the {@link URL} of the file if found, null otherwise.
	 */
	static private URL searchFolder(String url) {
		int i = 0;
		URL file = null;
		while (i<resourceFolders.length &&
				file == null) {
			file = checkFolder(resourceFolders[i]+url);
			i++;
		}
		return file;
	}
	
	/**
	 * check whether the specified url exists or not
	 * @param url
	 * 		the url to test.
	 * @return
	 * 		the {@link URL} of existent file, null otherwise
	 */
	static private URL checkFolder(String url) {
		URL urlFile;
		try {
			urlFile = new URL("file://" + Renderer.getHomeDir() + url);

			File resourceFile = new File(urlFile.getFile());
			if (resourceFile.exists()) {
				return urlFile;
			}
			else {
				return null;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Searched for the specified url in the JAR.
	 * @param url
	 * 		the relative url of the resource to look for.
	 * @return
	 * 		the {@link URL} of the resource if found, null otherwise.
	 */
	static private URL searchResources(String url) {
		int i = 0;
		URL resource = null;
		while (i<resourceFolders.length &&
				resource == null) {
			resource = ResourceMapper.class.getClassLoader()
					.getResource(resourceFolders[i] + url);
			i++;
		}
		return resource;
	}
}
