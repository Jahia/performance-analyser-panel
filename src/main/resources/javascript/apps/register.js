
window.jahia.i18n.loadNamespaces('page-performance-analyzer');

window.jahia.uiExtender.registry.add('adminRoute', 'performanceanalyzer', {
    targets: ['administration-sites:10'],
    label: 'page-performance-analyzer:panel-title',
    isSelectable: true,
    requiredPermission: 'siteAdminUsers',
    requireModuleInstalledOnSite: 'page-performance-analyzer',
    iframeUrl: window.contextJsParameters.contextPath + '/cms/editframe/default/$lang/sites/$site-key.performanceanalyzer.html'
});