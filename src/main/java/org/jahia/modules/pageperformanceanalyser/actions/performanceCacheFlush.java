package org.jahia.modules.pageperformanceanalyser.actions;

import org.apache.jackrabbit.commons.JcrUtils;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.services.cache.Cache;
import org.jahia.services.cache.CacheHelper;
import org.jahia.services.cache.CacheService;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.*;
import org.jahia.taglibs.jcr.node.JCRTagUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public class performanceCacheFlush extends Action {
    private CacheService cacheService;


    public CacheService getCacheService() {
        return cacheService;
    }

    public void setCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }
    @Override
    public ActionResult doExecute(HttpServletRequest httpServletRequest, RenderContext renderContext, Resource resource, JCRSessionWrapper jcrSessionWrapper, Map<String, List<String>> map, URLResolver urlResolver) throws Exception {

        String path = getParameter(map,"path"); //Path of the page
        JCRNodeWrapper page = jcrSessionWrapper.getNode(path);
        String pageName = page.getDisplayableName();

        Cache<Object, Object> cacheInstance = null;

        cacheInstance = cacheService.getCache("performanceRecordCache", true);

        cacheInstance.flush();

        cacheInstance.put("PageNameCachePerf",pageName);
        cacheInstance.put("PagePathCachePerf", path);

        //We need to flush the page to go through the process. What if someone go the page at the same time?
        CacheHelper.flushOutputCachesForPath(path,true);

        /* Try to flush the template cache doesnt work. With an option to activate it or not.
        Template template = RenderService.getInstance().resolveTemplate(new org.jahia.services.render.Resource(
                page, "html", null, org.jahia.services.render.Resource.CONFIGURATION_PAGE), renderContext);
        CacheHelper.flushOutputCachesForPath(jcrSessionWrapper.getNodeByIdentifier(template.getNode()).getPath(),true);
        JCRNodeWrapper templateNode = jcrSessionWrapper.getNodeByIdentifier(template.getNode());
        CacheHelper.flushOutputCachesForPath(templateNode.getPath(),true);
        */

        return ActionResult.OK_JSON;
    }
}
