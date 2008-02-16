package org.seasar.javelin.bottleneckeye.views;

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * 
 * @author eriguchi
 */
class TableContentProvider implements IStructuredContentProvider
{
    /**
     * {@inheritDoc}
     */
    public Object[] getElements(Object element)
    {
        if (element instanceof Object[])
        {
            return (Object[])element;
        }

        if (element instanceof Collection)
        {
            return ((Collection<?>)element).toArray();
        }

        return new Object[0];
    }

    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        // Do Nothing.
    }

    /**
     *  {@inheritDoc}
     */
    public void inputChanged(Viewer viewer, Object oldObject, Object newObject)
    {
        // Do Nothing.
    }
}
