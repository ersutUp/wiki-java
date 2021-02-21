package top.ersut.valid.servlet_component_scan;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class PrintListener implements ServletContextListener {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("{} contextInitialized",this.getClass().getName());

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("{} contextDestroyed",this.getClass().getName());

    }


}
