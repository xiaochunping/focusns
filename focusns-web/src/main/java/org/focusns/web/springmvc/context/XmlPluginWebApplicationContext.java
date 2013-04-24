package org.focusns.web.springmvc.context;

/*
 * #%L
 * FocusSNS Web
 * %%
 * Copyright (C) 2011 - 2013 FocusSNS
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.focusns.common.plugin.PluginClassLoader;
import org.focusns.web.plugin.PluginListener;
import org.focusns.web.portal.config.PageConfig;
import org.focusns.web.portal.config.xml.XmlPageConfigParser;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class XmlPluginWebApplicationContext extends XmlWebApplicationContext implements PluginListener {

    @Override
    public void pluginChanged(URL[] pluginUrls) {
        //
        try {
            //
            setPluginLoader(pluginUrls);
            //
            scanPluginContext(pluginUrls);
            //
            pluginPageConfig(pluginUrls);
            //
            copyPluginResources(pluginUrls);
            //
            refresh();
            //
        } catch (Exception e) {
            getServletContext().log(e.getMessage(), e);
        }
    }

    private void setPluginLoader(URL[] pluginUrls) throws IOException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        setClassLoader(new PluginClassLoader(pluginUrls, contextClassLoader));
    }

    private void scanPluginContext(URL[] pluginUrls) {
        String[] configLocations = getConfigLocations();
        configLocations = StringUtils.addStringToArray(configLocations, "classpath*:/META-INF/spring/pluginContext.xml");
        setConfigLocations(configLocations);
    }

    private void copyPluginResources(URL[] pluginUrls) throws IOException {
        File pluginsDir = getResourceByPath("/WEB-INF/plugins").getFile();
        for (URL pluginUrl : pluginUrls) {
            File pluginFile = new File(pluginUrl.getFile());
            String pluginDirName = pluginFile.getName().substring(0, pluginFile.getName().indexOf(".jar"));
            File pluginDir = new File(pluginsDir, pluginDirName);
            //
            JarFile jarFile = new JarFile(pluginFile);
            Enumeration<JarEntry> e = jarFile.entries();
            while (e.hasMoreElements()) {
                JarEntry jarEntry = e.nextElement();
                String jarFilePath = jarEntry.getName();
                if (jarFilePath.startsWith("META-INF/widgets") && !jarEntry.isDirectory()) {
                    InputStream in = jarFile.getInputStream(jarEntry);
                    File targetFile = new File(pluginDir, jarEntry.getName());
                    if (!targetFile.exists()) {
                        targetFile.getParentFile().mkdirs();
                        targetFile.createNewFile();
                    }

                    FileOutputStream out = new FileOutputStream(targetFile);
                    FileCopyUtils.copy(in, out);
                }
            }
        }
    }

    public void pluginPageConfig(URL[] pluginUrls) throws Exception {
        //
        for (URL pluginUrl : pluginUrls) {
            File pluginFile = new File(pluginUrl.getFile());
            JarFile pluginJarFile = new JarFile(pluginFile);
            JarEntry jarEntry = pluginJarFile.getJarEntry("META-INF/focusns-plugin.xml");
            //
            if(jarEntry!=null) {
                InputStream in = pluginJarFile.getInputStream(jarEntry);
                List<PageConfig> pageConfigPlugins = XmlPageConfigParser.parse(in);
                getServletContext().setAttribute("pageConfigPlugins", pageConfigPlugins);
            }
        }
    }

}
