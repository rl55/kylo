/**
 * 
 */
package com.thinkbiganalytics.metadata.modeshape.security.action;

import javax.jcr.Node;
import javax.jcr.security.Privilege;

import org.modeshape.jcr.security.SimplePrincipal;

import com.thinkbiganalytics.metadata.modeshape.security.JcrAccessControlUtil;
import com.thinkbiganalytics.metadata.modeshape.support.JcrUtil;
import com.thinkbiganalytics.security.action.Action;
import com.thinkbiganalytics.security.action.config.ActionBuilder;
import com.thinkbiganalytics.security.action.config.ActionsTreeBuilder;

/**
 *
 * @author Sean Felten
 */
public class JcrActionTreeBuilder<P> extends JcrAbstractActionsBuilder implements ActionsTreeBuilder<P> {
    
    private Node actionsNode;
    private P parentBuilder;

    public JcrActionTreeBuilder(Node actionsNode, P parent) {
        this.actionsNode = actionsNode;
        this.parentBuilder = parent;
    }
    
    /* (non-Javadoc)
     * @see com.thinkbiganalytics.security.action.config.ActionsTreeBuilder#action(com.thinkbiganalytics.security.action.Action)
     */
    @Override
    public ActionsTreeBuilder<P> action(Action action) {
        Node currentNode = this.actionsNode;
        
        for (Action current : action.getHierarchy()) {
            currentNode = JcrUtil.getOrCreateNode(currentNode, current.getSystemName(), JcrAllowableAction.NODE_TYPE);
        }

        return new JcrActionBuilder<>(currentNode, this)
                      .title(action.getTitle())
                      .description(action.getDescription())
                      .add();
    }

    /* (non-Javadoc)
     * @see com.thinkbiganalytics.security.action.config.ActionsTreeBuilder#action(java.lang.String)
     */
    @Override
    public ActionBuilder<ActionsTreeBuilder<P>> action(String systemName) {
        Node actionNode = JcrUtil.getOrCreateNode(this.actionsNode, systemName, JcrAllowableAction.NODE_TYPE);
        return new JcrActionBuilder<>(actionNode, this);
    }

    /* (non-Javadoc)
     * @see com.thinkbiganalytics.security.action.config.ActionsTreeBuilder#add()
     */
    @Override
    public P add() {
        JcrAccessControlUtil.addPermissions(this.actionsNode, getManagementPrincipal(), Privilege.JCR_ALL);
        JcrAccessControlUtil.addPermissions(this.actionsNode, SimplePrincipal.EVERYONE, Privilege.JCR_READ);
        return this.parentBuilder;
    }

}
