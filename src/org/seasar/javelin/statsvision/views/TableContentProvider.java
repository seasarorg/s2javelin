package org.seasar.javelin.statsvision.views;

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * 
 * @author eriguchi
 */
class TableContentProvider implements IStructuredContentProvider
{
    
    public TableContentProvider()
    {
        super();
    }
    
	/**
	 *  
	 * @param element
	 * @return
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
    public Object[] getElements(Object element)
    {
        if (element instanceof Object[])
        {
            return (Object[]) element;
        }

        if (element instanceof Collection)
        {
            return ((Collection) element).toArray();
        }

        return new Object[0];
    }
	
	/**
	 *  
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose()
	{
	}
	
	/**
	 *  
	 * @param viewer
	 * @param old_object
	 * @param new_object
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(
		Viewer viewer,
		Object old_object,
		Object new_object)
	{
	}
}
