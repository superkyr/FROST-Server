/*
 * Copyright (C) 2016 Fraunhofer IOSB
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fraunhofer.iosb.ilt.sta;

import de.fraunhofer.iosb.ilt.sta.mqtt.MqttManager;
import de.fraunhofer.iosb.ilt.sta.persistence.PersistenceManagerFactory;
import de.fraunhofer.iosb.ilt.sta.settings.CoreSettings;
import java.util.Enumeration;
import java.util.Properties;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jab
 */
@WebListener
public class ContextListener implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContextListener.class);
    public static final String TAG_CORE_SETTINGS = "CoreSettings";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if (sce != null && sce.getServletContext() != null) {
            ServletContext context = sce.getServletContext();
            Properties properties = new Properties();
            Enumeration<String> names = context.getInitParameterNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                properties.put(name, context.getInitParameter(name));
            }
            CoreSettings coreSettings = new CoreSettings(
                    properties,
                    properties.getProperty(CoreSettings.TAG_SERVICE_ROOT_URL) + context.getContextPath() + "/" + properties.getProperty(CoreSettings.TAG_API_VERSION),
                    context.getAttribute(ServletContext.TEMPDIR).toString());
            context.setAttribute(TAG_CORE_SETTINGS, coreSettings);
            PersistenceManagerFactory.init(coreSettings);
            MqttManager.init(coreSettings);
            PersistenceManagerFactory.addEntityChangeListener(MqttManager.getInstance());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        MqttManager.shutdown();
    }

}