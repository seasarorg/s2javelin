package org.seasar.javelin.bottleneckeye.editors.profiler;

import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.AVERAGE_CPU_TIME;
import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.AVERAGE_TIME;
import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.AVERAGE_USER_TIME;
import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.CALL_TIME;
import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.CLASS_NAME;
import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.MAX_CPU_TIME;
import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.MAX_TIME;
import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.MAX_USER_TIME;
import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.METHOD_NAME;
import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.MIN_CPU_TIME;
import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.MIN_TIME;
import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.MIN_USER_TIME;
import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.THROWABLE_TIME;
import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.TOTAL_CPU_TIME;
import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.TOTAL_TIME;
import static org.seasar.javelin.bottleneckeye.editors.profiler.ProfilerTab.TOTAL_USER_TIME;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

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

    /** �w�b�_������B */
    private static String[]     headers__;

    static
    {
        headers__ = new String[]{CLASS_NAME, METHOD_NAME, TOTAL_TIME, AVERAGE_TIME, MAX_TIME, MIN_TIME,
         TOTAL_CPU_TIME, AVERAGE_CPU_TIME, MAX_CPU_TIME, MIN_CPU_TIME, TOTAL_USER_TIME,
         AVERAGE_USER_TIME, MAX_USER_TIME, MIN_USER_TIME, CALL_TIME, THROWABLE_TIME};
        HEADER = StringUtils.join(headers__, "\t") + NEWLINE;
    }

    /**
     * �R���X�g���N�^�BTableViewer��ݒ肷��B
     */
    public ProfilerCopyAction()
    {
        // �������Ȃ��B
    }

    /**
     * {@inheritDoc}<br>
     * �e�[�u���̓��e���N���b�v�{�[�h�ɃR�s�[����B<br>
     * �N���b�v�{�[�h�ɂ́A�e�v�f���^�u��������A����Ɋe�s�����s�Ō������������񂪃R�s�[�����B<br>
     * �Ȃ��A�e�[�u���\���p������́AColumnLabelProvider��p���āA<br>
     * InvocationModel����擾����B
     */
    @Override
    public void runWithEvent(Event event)
    {

        if (event.widget instanceof Table == false)
        {
            return;
        }
        Table table = (Table)event.widget;
        StringBuilder builder = new StringBuilder();
        builder.append(HEADER);

        TableItem[] items = table.getSelection();
        for (TableItem item : items)
        {
            builder.append("\"");
            builder.append(item.getText(0));
            builder.append("\"");
            for (int index = 1; index < headers__.length; index++)
            {
                builder.append("\t\"");
                builder.append(item.getText(index));
                builder.append("\"");
            }
            builder.append(NEWLINE);
        }
        String content = new String(builder);

        TextTransfer textTransfer = TextTransfer.getInstance();
        getClipboard().setContents(new Object[]{content}, new Transfer[]{textTransfer});
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
