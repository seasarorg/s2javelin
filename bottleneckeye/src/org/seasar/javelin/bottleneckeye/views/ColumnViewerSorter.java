package org.seasar.javelin.bottleneckeye.views;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * TableViewer�̃J�������\�[�g����N���X�B
 * @author smg
 */
public abstract class ColumnViewerSorter extends ViewerComparator
{
    /** �����Ƀ\�[�g����B */
    public static final int   ASC        = 1;

    /** �\�[�g���s��Ȃ��B */
    public static final int   NONE       = 0;

    /** �~���Ƀ\�[�g����B */
    public static final int   DESC       = -1;

    /** ���݂̃\�[�g�����B */
    private int               direction_ = 0;

    /** �\�[�g�Ώۂ̃J�����B */
    private TableViewerColumn column_;

    /**  �J������ێ�����r���[���B */
    private ColumnViewer      viewer_;

    /**
     * �R���X�g���N�^�B�r���[���ƃJ������ێ�����B
     * @param viewer �r���[��
     * @param column �J����
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
     * �\�[�g������ݒ肷��B
     * @param sorter �\�[�g�N���X
     * @param direction �\�[�g��
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
     * ��r���s���B
     * @param e1 �v�f1
     * @param e2 �v�f2
     * @return e1�̕����傫���ꍇ��1�A�����ꍇ��0�Ae1�̕����������ꍇ��-1�B
     */
    protected abstract int doCompare(Object e1, Object e2);
}
