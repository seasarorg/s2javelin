package org.seasar.javelin.bottleneckeye.views;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * TableViewerのカラムをソートするクラス。
 * @author smg
 */
public abstract class ColumnViewerSorter extends ViewerComparator
{
    /** 昇順にソートする。 */
    public static final int   ASC        = 1;

    /** ソートを行わない。 */
    public static final int   NONE       = 0;

    /** 降順にソートする。 */
    public static final int   DESC       = -1;

    /** 現在のソート順序。 */
    private int               direction_ = 0;

    /** ソート対象のカラム。 */
    private TableViewerColumn column_;

    /**  カラムを保持するビューワ。 */
    private ColumnViewer      viewer_;

    /**
     * コンストラクタ。ビューワとカラムを保持する。
     * @param viewer ビューワ
     * @param column カラム
     */
    public ColumnViewerSorter(ColumnViewer viewer, TableViewerColumn column)
    {
        this.column_ = column;
        this.viewer_ = viewer;
        this.column_.getColumn().addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                int direction = calcDirection();
                setSorter(ColumnViewerSorter.this, direction);
            }

            private int calcDirection()
            {
                if (ColumnViewerSorter.this.viewer_.getComparator() == null)
                {
                    return ASC;
                }

                if (ColumnViewerSorter.this.viewer_.getComparator() != ColumnViewerSorter.this)
                {
                    return ASC;
                }

                int direction = ColumnViewerSorter.this.direction_;
                if (direction == ASC)
                {
                    return DESC;
                }
                else if (direction == DESC)
                {
                    return NONE;
                }

                return ASC;
            }
        });
    }

    /**
     * ソート順序を設定する。
     * @param sorter ソートクラス
     * @param direction ソート順
     */
    public void setSorter(ColumnViewerSorter sorter, int direction)
    {
        if (direction == NONE)
        {
            this.column_.getColumn().getParent().setSortColumn(null);
            this.column_.getColumn().getParent().setSortDirection(SWT.NONE);
            this.viewer_.setComparator(null);
        }
        else
        {
            this.column_.getColumn().getParent().setSortColumn(this.column_.getColumn());
            sorter.direction_ = direction;

            if (direction == ASC)
            {
                this.column_.getColumn().getParent().setSortDirection(SWT.UP);
            }
            else
            {
                this.column_.getColumn().getParent().setSortDirection(SWT.DOWN);
            }

            if (this.viewer_.getComparator() == sorter)
            {
                this.viewer_.refresh();
            }
            else
            {
                this.viewer_.setComparator(sorter);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(Viewer viewer, Object e1, Object e2)
    {
        return this.direction_ * doCompare(e1, e2);
    }

    /**
     * 比較を行う。
     * @param e1 要素1
     * @param e2 要素2
     * @return e1の方が大きい場合は1、同じ場合は0、e1の方が小さい場合は-1。
     */
    protected abstract int doCompare(Object e1, Object e2);
}
