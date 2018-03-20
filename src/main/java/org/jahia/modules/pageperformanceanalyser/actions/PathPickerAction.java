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
import java.util.List;
import java.util.Map;

public class PathPickerAction extends Action {

    /* the logger for the class. */
    private static Logger logger = LoggerFactory.getLogger(PathPickerAction.class);

    @Override
    public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource, JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
        logger.info("doExecute: begins the PathPickerAction action.");
        try {
            return new ActionResult(HttpServletResponse.SC_OK, null, new JSONObject(getSitePathJson(renderContext.getSite())));
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
    protected String getSitePathJson(JCRNodeWrapper node) throws RepositoryException {
        StringBuilder jsonBuilder = new StringBuilder("{");
        jsonBuilder.append("text:'").append(node.getDisplayableName().replaceAll("'", "")).append("',");
        jsonBuilder.append("href:'").append(node.getPath()).append("',");
        /* getting the folder child nodes */
        List<JCRNodeWrapper> childNodeList = JCRContentUtils.getChildrenOfType(node, "jnt:page");
        jsonBuilder.append("tags: ['").append(childNodeList.size()).append("'],");
        if(childNodeList.size() > 0){
            jsonBuilder.append("nodes:[");
            for(int index = 0; index < childNodeList.size(); index++){
                if(index > 0) jsonBuilder.append(",");
                jsonBuilder.append(getSitePathJson(childNodeList.get(index)));
            }
            jsonBuilder.append("]");
        }
        jsonBuilder.append("}");

        return jsonBuilder.toString();
    }

}
