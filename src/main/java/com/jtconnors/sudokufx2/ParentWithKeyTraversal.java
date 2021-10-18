
package com.jtconnors.sudokufx2;

import javafx.scene.Parent;

public class ParentWithKeyTraversal extends Parent {

    protected ParentWithKeyTraversal upNode;
    protected ParentWithKeyTraversal downNode;
    protected ParentWithKeyTraversal leftNode;
    protected ParentWithKeyTraversal rightNode;
    protected FunctionPtr action;
    protected boolean focusHintDisplayed = false;

    /*
     * The getKeyXXXNode methods, during key traversal, must skip over
     * SpaceNode instances if they are non-editable.
     */
    public ParentWithKeyTraversal getKeyLeftNode() {
        ParentWithKeyTraversal currentNode = this;
        while (true) {
            if (currentNode.leftNode instanceof SpaceNode) {
                if (!((SpaceNode)currentNode.leftNode).getSpace().isEditable()) {
                    currentNode = currentNode.leftNode;
                } else {
                    return currentNode.leftNode;
                }
            } else {
                return currentNode.leftNode;
            }
        }
    }

    public ParentWithKeyTraversal getKeyRightNode() {
        ParentWithKeyTraversal currentNode = this;
        while (true) {
            if (currentNode.rightNode instanceof SpaceNode) {
                if (!((SpaceNode)currentNode.rightNode).getSpace().isEditable()) {
                    currentNode = currentNode.rightNode;
                } else {
                    return currentNode.rightNode;
                }
            } else {
                return currentNode.rightNode;
            }
        }
    }

    public ParentWithKeyTraversal getKeyUpNode() {
        ParentWithKeyTraversal currentNode = this;
        while (true) {
            if (currentNode.upNode instanceof SpaceNode) {
                if (!((SpaceNode)currentNode.upNode).getSpace().isEditable()) {
                    currentNode = currentNode.upNode;
                } else {
                    return currentNode.upNode;
                }
            } else {
                return currentNode.upNode;
            }
        }
    }

    public ParentWithKeyTraversal getKeyDownNode() {
        ParentWithKeyTraversal currentNode = this;
        while (true) {
            if (currentNode.downNode instanceof SpaceNode) {
                if (!((SpaceNode)currentNode.downNode).getSpace().isEditable()) {
                    currentNode = currentNode.downNode;
                } else {
                    return currentNode.downNode;
                }
            } else {
                return currentNode.downNode;
            }
        }
    }
    
    public void setKeyLeftNode(ParentWithKeyTraversal leftNode) {
        this.leftNode = leftNode;
    }

    public void setKeyRightNode(ParentWithKeyTraversal rightNode) {
        this.rightNode = rightNode;
    }

    public void setKeyUpNode(ParentWithKeyTraversal upNode) {
        this.upNode = upNode;
    }

    public void setKeyDownNode(ParentWithKeyTraversal downNode) {
        this.downNode = downNode;
    }

    public void setAction(final FunctionPtr F) {
        action = F;
    }

    public void invokeAction() {
        if (action != null) {
            action.invoke();
        }
    }

    public boolean hasFocusHint() {
        return focusHintDisplayed;
    }

    /*
     * The following method will be overriden by subclasses but
     * must have super.showFocusHint() as its first line.
     */
    public void showFocusHint() {
        focusHintDisplayed = true;
    }

    /*
     * The following method will be overriden by subclasses but
     * must have super.unShowFocusHint() as its first line.
     */
    public void unShowFocusHint() {
        focusHintDisplayed = false;
    }
}