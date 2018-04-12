package org.jahia.modules.pageperformanceanalyser.actions;

import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.services.content.JCRContentUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class PathPickerAction extends Action {

    /* the logger for the class. */
    private static Logger logger = LoggerFactory.getLogger(PathPickerAction.class);

    @Override
    public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource, JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
        logger.info("doExecute: begins the PathPickerAction action.");
        try {
            return new ActionResult(HttpServletResponse.SC_OK, null, new JSONObject(getSitePathJson(renderContext.getSite(),renderContext, new AtomicBoolean(true))));
        }catch (Exception ex) {
            logger.error("doExecute(), Error,", ex);
            return new ActionResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * getSitePathJson
     * <p>The method returns a json with the tree under a specific node.</p>
     *
     *
     * @param node @JCRNodeWrapper
     * @return jsonString @String
     * @throws RepositoryException
     */
    protected String getSitePathJson(JCRNodeWrapper node, RenderContext renderContext, AtomicBoolean displayable) throws RepositoryException {

        if(JCRContentUtils.isADisplayableNode(node,renderContext)) {
            displayable.set(true);
        }
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
        jsonBuilder.append("text:'").append(node.getDisplayableName().replaceAll("'", "")).append("',");
        jsonBuilder.append("href:'").append(node.getPath()).append("',");
        if(!JCRContentUtils.isADisplayableNode(node,renderContext)) {
            jsonBuilder.append("selectable: false,");
            jsonBuilder.append("color: \"#A9A9A9\",");
        }
        List<JCRNodeWrapper> childNodeList = new ArrayList<JCRNodeWrapper>();

        /* getting the folder child nodes */
        childNodeList = JCRContentUtils.getChildrenOfType(node, "nt:base");

        if(childNodeList.size() > 0){
            boolean hasChildren = false;
            for(int index = 0; index < childNodeList.size(); index++){
                AtomicBoolean isChildDisplayed = new AtomicBoolean(false);
                // We only push the child to the jsonBuilder is possesses a content template
                String temp = getSitePathJson(childNodeList.get(index), renderContext,isChildDisplayed);
                if (isChildDisplayed.get()){
                    // if the child is displayed, then the parent must be displayed too
                    displayable.set(true);

                    if (!hasChildren){
                        jsonBuilder.append("nodes:[");
                    }
                    if(index > 0 && hasChildren && !temp.isEmpty()) jsonBuilder.append(",");
                    jsonBuilder.append(temp);
                    hasChildren = true;

                }
            }
            if (hasChildren){
                jsonBuilder.append("]");
            }
        }

        jsonBuilder.append("}");

        return jsonBuilder.toString();
    }

}
