package org.seasar.javelin.statsvision.model;


public abstract class AbstractConnectionModel
{
	  private ComponentModel source, target;

	  // ���̃R�l�N�V�����̍�����source�ɐڑ�
	  public void attachSource() {
	    // ���̃R�l�N�V���������ɐڑ�����Ă���ꍇ�͖���
	    if (!source.getModelSourceConnections().contains(this))
	      source.addSourceConnection(this);
	  }

	  // ���̃R�l�N�V�����̐�[��target�ɐڑ�
	  public void attachTarget() {
	    if (!target.getModelTargetConnections().contains(this))
	      target.addTargetConnection(this);
	  }

	  // ���̃R�l�N�V�����̍�����source������O��
	  public void detachSource() {
	    source.removeSourceConnection(this);
	  }

	  // ���̃R�l�N�V�����̐�[��target������O��
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
