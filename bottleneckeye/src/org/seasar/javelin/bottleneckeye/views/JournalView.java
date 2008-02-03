package org.seasar.javelin.bottleneckeye.views;

import java.util.List;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;
import org.seasar.javelin.bottleneckeye.event.DataChangeListener;
import org.seasar.javelin.bottleneckeye.model.InvocationModel;
import org.seasar.javelin.bottleneckeye.model.MainCtrl;
import org.seasar.javelin.bottleneckeye.util.NormalDateFormatter;

/**
 * �W���[�i����\������r���[�B
 * 
 * @author cero-t
 */
public class JournalView extends ViewPart implements DataChangeListener
{

    /** ���̃r���[�̒��Ŏg�p����r���[�A�i�e�[�u���⃊�X�g�j */
    private Control          viewer_;

    /** ���̃r���[�ŕێ�����C�x���g�̍ő吔�B����𒴂���ƁA�ł��Â��C�x���g����폜�����B */
    private static final int JOURNAL_MAX = 1000;

    private MainCtrl         main_;

    /**
     * �r���[�𐶐�������B
     * �܂��R���e�L�X�g���j���[�A�c�[���o�[�A
     * �_�u���N���b�N���̃A�N�V�����Ȃǂ�����������B
     * 
     * @param parent �e�̃E�B�W�b�g
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl(Composite parent)
    {
        this.viewer_ = createComposite(parent);
        if (this.viewer_ != null)
        {
            hookContextMenu();
        }
        init();
    }

    /**
     * �R���e�L�X�g���j���[�����������A�}�E�X����ŕ\�������悤�ɂ���B
     * ���j���[�̍��ڂ́AfillContextMenu�ɂ���Đ�������B
     *
     */
    private void hookContextMenu()
    {
        //���j���[�}�l�[�W��������������B
        MenuManager menuMgr = new MenuManager("#PopupMenu");

        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager)
            {}
        });

        Menu menu = menuMgr.createContextMenu(this.viewer_);
        this.viewer_.setMenu(menu);
    }

    /**
     * �r���[�ŗp����e�[�u���𐶐�����B
     *  
     * @param parent �e�̃E�B�W�b�g
     * @return �e�[�u��
     * @see jp.co.smg.efv.views.AbstractView#createComposite(org.eclipse.swt.widgets.Composite)
     */
    public Control createComposite(Composite parent)
    {
        this.main_ = MainCtrl.getInstance();

        //�e�[�u���̃r���[�A�𐶐�����B
        tbv = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI
        /*| SWT.VIRTUAL*/);

        //�R���e���c�v���o�C�_���Z�b�g����B
        tbv.setContentProvider(new TableContentProvider());

        //���x���v���o�C�_���Z�b�g����B
        tbv.setLabelProvider(getLabelProvider());

        //�e�[�u���w�b�_������������B
        initTableHeader(tbv);

        //�e�[�u���ɕ\������ΏۂƂȂ�v�f���擾���A�r���[�A�ɃZ�b�g����B
        Object inputElement = getInputElement(this.main_);
        tbv.setInput(inputElement);

        return this.tbv.getControl();
    }

    private TableViewer         tbv;

    static final private String COLMUN_TIME     = "Time";

    static final private String COLMUN_NODE     = "Node";

    static final private String COLMUN_NAME     = "Name";

    static final private String COLMUN_DURATION = "Duration";

    public JournalView()
    {}

    protected void init()
    {
        MainCtrl.getInstance().addDataChangeListeners(this);
    }

    public void updateData(final Object element)
    {
        Display.getDefault().asyncExec(new Runnable() {
            public void run()
            {
                addInputElement(element);
            }
        });
    }

    public void addInputElement(Object element)
    {
        this.tbv.add(element);
        Table table = this.tbv.getTable();
        int itemCount = table.getItemCount();

        if (itemCount > JOURNAL_MAX)
        {
            table.remove(0);
            itemCount--;
        }

        int row = itemCount - 1;

        table.showItem(table.getItem(row));
    }

    /**
     * �e�[�u���ŕ\��������e���擾����B ���̃r���[�̏ꍇ�́A�A�N�V�����̃��X�g��Ԃ��B
     * 
     * 
     */
    protected Object getInputElement(MainCtrl main)
    {
        List list = main.getInvocationList();
        return list;
    }

    public void addEvent()
    {

    }

    /**
     * ���x���v���o�C�_��Ԃ��B
     */
    protected IBaseLabelProvider getLabelProvider()
    {
        return new ActionListLabelProvider();
    }

    /**
     * 
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     */
    public void setFocus()
    {}

    /**
     * �e�[�u���w�b�_������������B
     * 
     */
    protected void initTableHeader(TableViewer tbv)
    {
        TableColumn column = new TableColumn(tbv.getTable(), SWT.LEFT);

        column = new TableColumn(tbv.getTable(), SWT.LEFT);
        column.setText(COLMUN_TIME);
        column.setWidth(140);

        column = new TableColumn(tbv.getTable(), SWT.LEFT);
        column.setText(COLMUN_NODE);
        column.setWidth(100);

        column = new TableColumn(tbv.getTable(), SWT.LEFT);
        column.setText(COLMUN_NAME);
        column.setWidth(100);

        column = new TableColumn(tbv.getTable(), SWT.LEFT);
        column.setText(COLMUN_DURATION);
        column.setWidth(70);

        tbv.getTable().setHeaderVisible(true);
        this.tbv = tbv;
    }

    class ActionListLabelProvider implements ITableLabelProvider, ITableColorProvider
    {
        /**
         * element��\���s�́Acolumn_index��ڂ̃e�L�X�g��Ԃ��B
         * 
         * @param element
         * @param column_index
         * @return
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object,
         *      int)
         */
        public String getColumnText(Object element, int column_index)
        {
            String ret;

            InvocationModel invocation = (InvocationModel)element;

            // TODO �s�ԍ��Ƀn�[�h�R�[�h���������Ƃ������߁A���P�������B�B
            switch (column_index)
            {
            case 0:
                ret = "-";
                break;
            case 1:
                long time = invocation.getDate().getTime();
                ret = NormalDateFormatter.format(time);
                break;
            case 2:
                ret = invocation.getClassName();
                break;
            case 3:
                ret = invocation.getMethodName();
                break;
            case 4:
                ret = String.valueOf(invocation.getAverage());
                break;
            default:
                ret = "";
            }

            if (ret == null)
            {
                return "";
            }

            return ret;
        }

        public Image getColumnImage(Object element, int column_index)
        {
            return null;
        }

        public Color getBackground(Object element, int columnIndex)
        {
            // TODO TANIMOTO 臒l�̐ݒ�͌�������K�v����
            if (element == null || ((InvocationModel)element).getAverage() < 50)
            {
                return null;
            }

            return new Color(null, 255, 0, 0);
        }

        public Color getForeground(Object element, int columnIndex)
        {
            // TODO �����������ꂽ���\�b�h�E�X�^�u
            return null;
        }

        public void addListener(ILabelProviderListener ilabelproviderlistener)
        {}

        public void dispose()
        {}

        public boolean isLabelProperty(Object obj, String s)
        {
            return false;
        }

        public void removeListener(ILabelProviderListener ilabelproviderlistener)
        {}
    }
}