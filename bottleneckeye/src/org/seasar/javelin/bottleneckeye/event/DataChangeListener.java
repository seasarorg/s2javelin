package org.seasar.javelin.bottleneckeye.event;

/**
 * データ更新を監視するリスナ。
 * @author cero-t
 *
 */
public interface DataChangeListener
{
    /**
     * データの更新を行う。
     * @param element 要素
     */
    public void updateData(Object element);
}
