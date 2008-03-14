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
 * プロファイラビューでコピーを行った際のアクション。
 * @author cero-t
 *
 */
public class ProfilerCopyAction extends Action
{
    /** 改行記号 */
    private static final String NEWLINE = "\n";

    /** クリップボードにコピーする際のヘッダ */
    private static final String HEADER;

    /** クリップボード */
    private static Clipboard    clipboard__;

    /** コピー対象のTableViewer */
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
     * コンストラクタ。TableViewerを設定する。
     * @param viewer TableViewer
     */
    public ProfilerCopyAction(TableViewer viewer)
    {
        this.viewer_ = viewer;
    }

    /**
     * {@inheritDoc}<br>
     * テーブルの内容をクリップボードにコピーする。<br>
     * クリップボードには、各要素がタブ結合され、さらに各行を改行で結合した文字列がコピーされる。<br>
     * なお、テーブル表示用文字列は、ColumnLabelProviderを用いて、<br>
     * InvocationModelから取得する。
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
     * テーブルより、ColumnLabelProviderの配列を取得する。
     * @return ColumnLabelProviderの配列
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
     * InvocationModelから、1行の文字列を作成する。
     * @param providers ColumnLabelProviderの配列
     * @param model InvocationModel
     * @return 1行の文字列
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
     * クリップボードを取得する。
     * @return クリップボード
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
