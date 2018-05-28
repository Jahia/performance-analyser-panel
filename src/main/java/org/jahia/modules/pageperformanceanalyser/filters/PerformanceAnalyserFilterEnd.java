package org.jahia.modules.pageperformanceanalyser.filters;

import org.jahia.exceptions.JahiaInitializationException;
import org.jahia.services.cache.Cache;
import org.jahia.services.cache.CacheService;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PerformanceAnalyserFilterEnd extends AbstractFilter {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceAnalyserFilterEnd.class);
    private CacheService cacheService;

    public CacheService getCacheService() {
        return cacheService;
    }

    public void setCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Override
    public void finalize(RenderContext renderContext, Resource resource, RenderChain renderChain) {
        if (renderContext.getRequest().getParameter("perfAnalyse") != null) {
            //First thing to do get the date to be able to calculate the timer before doing anything
            Long dateTime = new Date().getTime();

            HashMap<String, Long> childrenMap = (HashMap<String, Long>) renderContext.getRequest().getAttribute("childrenMap");

            if (null == childrenMap) {
                childrenMap = new HashMap<String, Long>();
            }

            Map<String, Map<String, Object>> responseTimeStack = (Map<String, Map<String, Object>>) renderContext.getRequest().getAttribute("responseTimeStack");

            //To make sure to not remove anything from the store elements
            int element = 1;
            if (renderContext.getRequest().getAttribute("NumberofElement") != null) {
                element = (Integer) renderContext.getRequest().getAttribute("NumberOfElement");
            }

            if (responseTimeStack != null) {
                Map<String, Object> objectRenderChainMap = responseTimeStack.get(resource.getPath());
                Date date = (Date) objectRenderChainMap.get("date");
                String depth = (String) objectRenderChainMap.get("depth");

                Long totalTimeSpent = (Long) renderContext.getRequest().getAttribute("totalTimeSpent");
                if (totalTimeSpent == null) {
                    totalTimeSpent = new Long(0);
                }

                String currentDepth = (String) renderContext.getRequest().getAttribute("depth");
                int currentDepthInt = Integer.parseInt(currentDepth);

                renderContext.getRequest().setAttribute("depth", String.valueOf(Integer.parseInt(depth) - 1));

                if (Integer.parseInt(depth) == Integer.parseInt(currentDepth)) {
                    long timeSpent = (dateTime - date.getTime());

                    //Remove children time if there is any
                    int depthChildren = currentDepthInt + 1;
                    if (null != childrenMap.get("childrenLevel" + depthChildren)) {
                        timeSpent -= childrenMap.get("childrenLevel" + depthChildren);
                    }

                    //Total time spent
                    totalTimeSpent += timeSpent;

                    //Total time Spent for the childrenMAp
                    long ChildrenTotalTimeSpent = timeSpent;

                    renderContext.getRequest().setAttribute("totalTimeSpent", totalTimeSpent);
                    logger.info("timespent: " + timeSpent + " - totalTimeSpent - " + totalTimeSpent + "ms");

                    //All the infos needs to show:
                    Map<String, String> infos = getInfos(resource.getNode(), timeSpent);

                    //Store element
                    try {
                        storeInCache(resource.getNode().getIdentifier(), infos);
                    } catch (RepositoryException e) {
                        logger.info("PagePerformance : Impossible to save in cache : " + resource.getPath());
                        e.printStackTrace();
                    }

                    storeInCache("TotalTimeSpent", totalTimeSpent);
                    element++;
                    renderContext.getRequest().setAttribute("NumberOfElement", element);

                    int childrenLevel = 1;
                    if (null != renderContext.getRequest().getAttribute("ChildrenLevel")) {
                        childrenLevel = (Integer) renderContext.getRequest().getAttribute("ChildrenLevel");
                    } else {
                        renderContext.getRequest().setAttribute("ChildrenLevel", currentDepthInt);
                    }
                    //If the currentDepth is under or equal to the children level we add the time or/and remove the element from the map
                    if (currentDepthInt <= childrenLevel) {
                        //if there is already a children time with the same level we add it to the map
                        if (null != childrenMap.get("childrenLevel" + currentDepth)) {
                            ChildrenTotalTimeSpent += childrenMap.get("childrenLevel" + currentDepth);
                        }
                        int levelUp = currentDepthInt + 1;
                        if (null != childrenMap.get("childrenLevel" + levelUp)) {
                            ChildrenTotalTimeSpent += childrenMap.get("childrenLevel" + levelUp);
                            childrenMap.remove("childrenLevel" + levelUp);
                        }

                    }
                    //Add the childrenLevel, if the current element is higher than the current children level
                    childrenMap.put("childrenLevel" + currentDepth, ChildrenTotalTimeSpent);
                    renderContext.getRequest().setAttribute("childrenMap", childrenMap);
                    renderContext.getRequest().setAttribute("ChildrenLevel", currentDepthInt);
                }
                logger.info("FINALIZE (" + depth + ") - stopping counter for " + resource.getPath());
            }
        }
        super.finalize(renderContext, resource, renderChain);
    }

    private void storeInCache(String key, Object entry) {
        Cache<Object, Object> cacheInstance = null;
        try {
            cacheInstance = cacheService.getCache("performanceRecordCache", true);
            if (cacheInstance.get(key) != null) {
                cacheInstance.remove(key);
            }
            cacheInstance.put(key, entry);


        } catch (JahiaInitializationException e) {
            e.printStackTrace();
        }

    }

    /**
     * To Create a Map with all the infos need. If more elements are need just add them here and change the PerformancePanelFilter and the jsp to add show them.
     *
     * @param node
     * @param timeSpent
     * @return
     */
    private Map<String, String> getInfos(JCRNodeWrapper node, long timeSpent) {
        Map<String, String> infos = new HashMap<String, String>();
        //Add all the infos need, if you want more you will have to add it manually
        try {
            infos.put("jcr:uuid", node.getIdentifier());
            infos.put("jcr:primaryType", node.getPrimaryNodeTypeName());
            infos.put("j:nodename", node.getName());
            infos.put("timeSpent", (String.valueOf(timeSpent)));
            infos.put("path", node.getPath());
        } catch (RepositoryException e) {
            e.printStackTrace();
        }

        return infos;
    }
}
