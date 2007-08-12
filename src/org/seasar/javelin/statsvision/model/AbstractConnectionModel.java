package org.seasar.javelin.statsvision.model;


public abstract class AbstractConnectionModel
{
	  private ComponentModel source, target;

	  // このコネクションの根元をsourceに接続
	  public void attachSource() {
	    // このコネクションが既に接続されている場合は無視
	    if (!source.getModelSourceConnections().contains(this))
	      source.addSourceConnection(this);
	  }

	  // このコネクションの先端をtargetに接続
	  public void attachTarget() {
	    if (!target.getModelTargetConnections().contains(this))
	      target.addTargetConnection(this);
	  }

	  // このコネクションの根元をsourceから取り外す
	  public void detachSource() {
	    source.removeSourceConnection(this);
	  }

	  // このコネクションの先端をtargetから取り外す
	  public void detachTarget() {
	    target.removeTargetConnection(this);
	  }

	  public ComponentModel getSource() {
	    return source;
	  }

	  public ComponentModel getTarget() {
	    return target;
	  }

	  public void setSource(ComponentModel model) {
	    source = model;
	  }

	  public void setTarget(ComponentModel model) {
	    target = model;
	  }
}
