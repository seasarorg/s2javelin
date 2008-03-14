package org.seasar.javelin.bottleneckeye.editors.profiler;

import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.AVERAGE_TIME;
import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.CALL_TIME;
import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.CLASS_NAME;
import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.MAX_TIME;
import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.METHOD_NAME;
import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.MIN_TIME;
import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.TOTAL_USER_TIME;
import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.AVERAGE_USER_TIME;
import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.MAX_USER_TIME;
import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.MIN_USER_TIME;
import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.TOTAL_CPU_TIME;
import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.AVERAGE_CPU_TIME;
import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.MAX_CPU_TIME;
import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.MIN_CPU_TIME;
import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.THROWABLE_TIME;
import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.TOTAL_TIME;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.seasar.javelin.bottleneckeye.model.InvocationModel;

/**
 * �v���t�@�C���r���[�ŃR�s�[���s�����ۂ̃A�N�V�����B
 * @author cero-t
 *
 */
public class ProfilerCopyAction extends Action
{
    /** ���s�L�� */
    private static final String NEWLINE = "\n";

    /** �N���b�v�{�[�h�ɃR�s�[����ۂ̃w�b�_ */
    private static final String HEADER;

    /** �N���b�v�{�[�h */
    private static Clipboard    clipboard__;

    /** �R�s�[�Ώۂ�TableViewer */
    private TableViewer         viewer_;

    static
    {
        String[] headers =
                {CLASS_NAME, METHOD_NAME, TOTAL_TIME, AVERAGE_TIME, MAX_TIME, MIN_TIME,
            TOTAL_CPU_TIME, AVERAGE_CPU_TIME, MAX_CPU_TIME, MIN_CPU_TIME, TOTAL_USER_TIME,
            AVERAGE_USER_TIME, MAX_USER_TIME, MIN_USER_TIME, CALL_TIME, THROWABLE_TIME};
        HEADER = StringUtils.join(headers, "\t") + NEWLINE;
    }

    /**
     * �R���X�g���N�^�BTableViewer��ݒ肷��B
     * @param viewer TableViewer
     */
    public ProfilerCopyAction(TableViewer viewer)
    {
        this.viewer_ = viewer;
    }

    /**
     * {@inheritDoc}<br>
     * �e�[�u���̓��e���N���b�v�{�[�h�ɃR�s�[����B<br>
     * �N���b�v�{�[�h�ɂ́A�e�v�f���^�u��������A����Ɋe�s�����s�Ō������������񂪃R�s�[�����B<br>
     * �Ȃ��A�e�[�u���\���p������́AColumnLabelProvider��p���āA<br>
     * InvocationModel����擾����B
     */
    @Override
    public void run()
    {
        TextTransfer textTransfer = TextTransfer.getInstance();
        Object[] selection = ((IStructuredSelection)this.viewer_.getSelection()).toArray();

        ColumnLabelProvider[] providers = getProviders();

        StringBuilder builder = new StringBuilder();
        builder.append(HEADER);
        for (int index = 0; index < selection.length; index++)
        {
            Object object = selection[index];
            if (object instanceof InvocationModel == false)
            {
                continue;
            }

            String str = createSingleLine(providers, (InvocationModel)object);

            builder.append("\"").append(str).append("\"");
            builder.append(NEWLINE);
        }
        String content = new String(builder);

        getClipboard().setContents(new Object[]{content}, new Transfer[]{textTransfer});
    }

    /**
     * �e�[�u�����AColumnLabelProvider�̔z����擾����B
     * @return ColumnLabelProvider�̔z��
     */
    protected ColumnLabelProvider[] getProviders()
    {
        TableColumn[] columns = this.viewer_.getTable().getColumns();
        int columnSize = columns.length;

        ColumnLabelProvider[] providers = new ColumnLabelProvider[columnSize];
        for (int index = 0; index < columnSize; index++)
        {
            providers[index] = (ColumnLabelProvider)this.viewer_.getLabelProvider(index);
        }

        return providers;
    }

    /**
     * InvocationModel����A1�s�̕�������쐬����B
     * @param providers ColumnLabelProvider�̔z��
     * @param model InvocationModel
     * @return 1�s�̕�����
     */
    protected String createSingleLine(ColumnLabelProvider[] providers, InvocationModel model)
    {
        String[] labels = new String[providers.length];
        for (int columnIndex = 0; columnIndex < providers.length; columnIndex++)
        {
            labels[columnIndex] = providers[columnIndex].getText(model);
        }
        String str = StringUtils.join(labels, "\"\t\"");
        return str;
    }

    /**
     * �N���b�v�{�[�h���擾����B
     * @return �N���b�v�{�[�h
     */
    protected static Clipboard getClipboard()
    {
        if (clipboard__ == null)
        {
            clipboard__ = new Clipboard(Display.getCurrent());
        }
        return clipboard__;
    }

}
