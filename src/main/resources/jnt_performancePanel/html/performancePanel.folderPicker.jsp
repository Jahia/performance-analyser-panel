<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="ui" uri="http://www.jahia.org/tags/uiComponentsLib" %>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions" %>
<%@ taglib prefix="query" uri="http://www.jahia.org/tags/queryLib" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>
<%@ taglib prefix="s" uri="http://www.jahia.org/tags/search" %>
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>
<%--@elvariable id="vfsFactory" type="org.jahia.modules.external.vfs.factory.VFSMountPointFactory"--%>
<template:addResources type="javascript" resources="jquery.fancytree-all-deps.min.js" />
<template:addResources type="css" resources="ui.fancytree.min.css"/>
<c:url value="${url.base}${docPath}${renderContext.mainResource.node.path}" var="currentNodePath"/>

<template:addResources>
    <script type="text/javascript">
        var firstLoad = true;
        var glyphOpts = {
            map: {
                doc: "glyphicon glyphicon-folder-close",
                docOpen: "glyphicon glyphicon-folder-open",
                checkbox: "glyphicon glyphicon-unchecked",
                checkboxSelected: "glyphicon glyphicon-check",
                checkboxUnknown: "glyphicon glyphicon-share",
                dragHelper: "glyphicon glyphicon-play",
                dropMarker: "glyphicon glyphicon-arrow-right",
                error: "glyphicon glyphicon-warning-sign",
                expanderClosed: "glyphicon glyphicon-menu-right",
                expanderLazy: "glyphicon glyphicon-menu-right",  // glyphicon-plus-sign
                expanderOpen: "glyphicon glyphicon-menu-down",  // glyphicon-collapse-down
                folder: "glyphicon glyphicon-warning-sign",
                folderOpen: "glyphicon glyphicon-warning-sign",
                loading: "glyphicon glyphicon-refresh glyphicon-spin"
            }
        };
        function callTreeView(targetId, sitePath) {
            $('#windowPathPicker').modal({
                show: 'true'
            });
            if(firstLoad){
                $.ajax({
                    url: "${currentNodePath}" +".pickerPath.do",
                    context: document.body,
                    dataType: "json",
                    data: {
                        level: 2, //For the first we need 2 level the rest will be one.
                        pagePath: sitePath
                    },
                }).done(function(data) {
                        firstLoad =false;
                        $("#treeviewpath").fancytree({
                            extensions: [ "glyph"],
                            icon: function(event, data) {
                                if (data.node.noSelect) {
                                    return "glyphicon glyphicon-warning-sign";
                                }
                            },
                            activeVisible:true,
                            source: data ,
                            glyph: glyphOpts,
                            lazyLoad: function(event, data) {
                                var node = data.node;
                                data.result = {
                                    url:"${currentNodePath}" +".pickerPath.do",
                                    context: document.body,
                                    dataType: "json",
                                    data: {
                                        level: 2,
                                        pagePath: node.data.href
                                    }
                                };
                            },
                            postProcess: function(event, data) { //Important to get the Children table
                                var response = data.response;
                                if(response.children){
                                    data.result = response.children;
                                }else{
                                    data.result = []; //No error if no children
                                }
                            },
                            activate: function(event, data) {
                                var node = data.node;
                                if(data.node.noSelect){
                                    alert("You can not select this node");
                                }else{
                                    $("#" + targetId + "").val(node.data.href);
                                    $('#windowPathPicker').modal('toggle');;
                                }

                            },
                            beforeActivate: function(event, data){
                                if(data.node.data.noSelect){
                                    alert("This element can not be selected");
                                    return false;
                                }
                            }
                        });
                });
            }
        };
    </script>
</template:addResources>

<div id="windowPathPicker" class="nModal fade window-detail-nModal" role="dialog">
    <div class="nModal-dialog">

        <!-- nModal content-->
        <div class="nModal-content">
            <div class="nModal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="nModal-body">
                <div id="treeviewpath"></div>
            </div>
            <div class="nModal-footer" style="text-align: center">
                <button type="button" class="btn-small" data-dismiss="modal">Close</button>
            </div>
        </div>

    </div>
</div>