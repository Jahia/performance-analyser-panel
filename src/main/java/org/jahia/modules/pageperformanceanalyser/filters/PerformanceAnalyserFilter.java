package org.jahia.modules.pageperformanceanalyser.filters;

import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PerformanceAnalyserFilter extends AbstractFilter {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceAnalyserFilter.class);


    @Override
    public String prepare(RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        if (renderContext.getRequest().getParameter("perfAnalyse") != null) {
            Map<String, Map<String, Object>> responseTimeStack = (Map<String, Map<String, Object>>) renderContext.getRequest().getAttribute("responseTimeStack");
            if (responseTimeStack == null) {
                responseTimeStack = new HashMap<>();
                renderContext.getRequest().setAttribute("responseTimeStack", responseTimeStack);
                renderContext.getRequest().setAttribute("depth", "0");
            }
            String depth = String.valueOf(Integer.parseInt((String) renderContext.getRequest().getAttribute("depth")) + 1);
            renderContext.getRequest().setAttribute("depth", depth);

            HashMap<String, Object> objectRenderChainMap = new HashMap<String, Object>();
            objectRenderChainMap.put("date", new Date());
            objectRenderChainMap.put("depth", depth);
            responseTimeStack.put(resource.getPath(), objectRenderChainMap);
            logger.info("PREPARE (" + depth + ") - starting counter for: " + resource.getPath());


        }
        return null;

    }
}
