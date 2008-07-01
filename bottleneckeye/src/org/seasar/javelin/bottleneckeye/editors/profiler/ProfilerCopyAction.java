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

    /** ヘッダ文字列。 */
    private static String[]     headers__;

    static
    {
        headers__ = new String[]{CLASS_NAME, METHOD_NAME, TOTAL_TIME, AVERAGE_TIME, MAX_TIME, MIN_TIME,
         TOTAL_CPU_TIME, AVERAGE_CPU_TIME, MAX_CPU_TIME, MIN_CPU_TIME, TOTAL_USER_TIME,
         AVERAGE_USER_TIME, MAX_USER_TIME, MIN_USER_TIME, CALL_TIME, THROWABLE_TIME};
        HEADER = StringUtils.join(headers__, "\t") + NEWLINE;
    }

    /**
     * コンストラクタ。TableViewerを設定する。
     */
    public ProfilerCopyAction()
    {
        // 何もしない。
    }

    /**
     * {@inheritDoc}<br>
     * テーブルの内容をクリップボードにコピーする。<br>
     * クリップボードには、各要素がタブ結合され、さらに各行を改行で結合した文字列がコピーされる。<br>
     * なお、テーブル表示用文字列は、ColumnLabelProviderを用いて、<br>
     * InvocationModelから取得する。
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
