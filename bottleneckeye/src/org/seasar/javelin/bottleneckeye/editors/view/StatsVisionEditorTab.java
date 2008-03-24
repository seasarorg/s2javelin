package org.seasar.javelin.bottleneckeye.editors.view;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PrintFigureOperation;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.print.PrintGraphicalViewerOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.seasar.javelin.bottleneckeye.StatsVisionPlugin;
import org.seasar.javelin.bottleneckeye.communicate.Common;
import org.seasar.javelin.bottleneckeye.communicate.Telegram;
import org.seasar.javelin.bottleneckeye.communicate.TelegramSender;
import org.seasar.javelin.bottleneckeye.editors.EditorTabInterface;
import org.seasar.javelin.bottleneckeye.editors.MultiPageEditor;
import org.seasar.javelin.bottleneckeye.model.ContentsModel;
import org.seasar.javelin.bottleneckeye.model.persistence.PersistenceModel;
import org.seasar.javelin.bottleneckeye.model.persistence.Settings;
import org.seasar.javelin.bottleneckeye.model.persistence.View;
import org.seasar.javelin.bottleneckeye.util.ModelConverter;

/**
 * �N���X�}��\������^�u�B
 *
 * @author Sakamoto
 */
public class StatsVisionEditorTab implements EditorTabInterface
{
    /** �N���X�}��\������G�f�B�^ */
    private AbstractStatsVisionEditor<?> editor_;

    /** �ʐM���[�h */
    private String                       mode_;

    /** EditorPart */
    private MultiPageEditor              editorPart_;

    /**
     * �N���X�}��\������^�u�𐶐�����C���X�^���X���쐬����B
     */
    public StatsVisionEditorTab()
    {
        this.editor_ = null;
    }

    /**
     * �N���X�}��\������r���[��Ԃ��B
     *
     * @return �N���X�}��\������r���[
     */
    public StatsVisionEditor getStatsVisionEditor()
    {
        return this.editor_;
    }

    /**
     * �^�u�̒��g���쐬����i�R���|�W�b�g�j�B
     *
     * @param container �e�R���|�W�b�g
     * @param editorPart �^�u�𐶐�����G�f�B�^
     * @return ��ʃC���X�^���X
     */
    public Composite createComposite(Composite container, MultiPageEditorPart editorPart)
    {
        return null;
    }

    /**
     * �^�u�̒��g���쐬����i�G�f�B�^�j�B
     *
     * @param container �e�R���|�W�b�g
     * @param editorPart �^�u�𐶐�����G�f�B�^
     * @return ��ʃC���X�^���X
     */
    public IEditorPart createEditor(Composite container, MultiPageEditor editorPart)
    {
        this.editorPart_ = editorPart;

        if (MultiPageEditor.MODE_JMX.equalsIgnoreCase(this.mode_))
        {
            this.editor_ = new JmxStatsVisionEditor();
        }
        else
        {
            this.mode_ = MultiPageEditor.MODE_TCP;
            this.editor_ = new TcpStatsVisionEditor();
        }
        return this.editor_;
    }

    /**
     * �^�u�̖��O��Ԃ��B
     *
     * @return �^�u��
     */
    public String getName()
    {
        return "View";
    }

    /**
     * ���M����Ă����d������������B
     *
     * @param telegram �d��
     * @return ���̃I�u�W�F�N�g�ɓd�����񂳂Ȃ��ꍇ�� <code>false</code>
     */
    public boolean receiveTelegram(Telegram telegram)
    {
        boolean isProcess = false;
        int telegramKind = telegram.getObjHeader().getByteTelegramKind();
        int requestKind = telegram.getObjHeader().getByteRequestKind();
        switch (telegramKind)
        {
        case Common.BYTE_TELEGRAM_KIND_ALERT:
            // �ʒm��M�������s��
            this.editor_.listeningGraphicalViewer(telegram);
            isProcess = true;
            break;
        case Common.BYTE_TELEGRAM_KIND_GET:
            if (Common.BYTE_REQUEST_KIND_RESPONSE == requestKind)
            {
                // ������M�������s��
                this.editor_.addResponseTelegram(telegram);
                System.out.println("�d����M[" + requestKind + "]");
                isProcess = true;
            }
            break;
        default:
            break;
        }
        return isProcess;
    }

    /**
     * {@inheritDoc}
     */
    public void setTelegramSender(TelegramSender telegramSender)
    {
        // Do Nothing
    }

    /**
     * {@inheritDoc}
     */
    public void onCopy()
    {
        ScalableRootEditPart part =
                (ScalableRootEditPart)(this.editor_.getGraphicalViewer().getRootEditPart());

        FileDialog dialog = new FileDialog(this.editor_.getEditorSite().getShell(), SWT.SAVE);
        dialog.setFilterExtensions(new String[]{"bmp"});
        dialog.setFileName(this.editorPart_.getTitle());
        if (dialog.open() == null)
        {
            return;
        }

        IFigure layer = part.getLayer(LayerConstants.PRINTABLE_LAYERS);

        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int w = layer.getSize().width;
            int h = layer.getSize().height;
            Image image = new Image(Display.getDefault(), w, h);
            GC gc = new GC(image);
            SWTGraphics graphics = new SWTGraphics(gc);
            layer.paint(graphics);
            graphics.dispose();
            gc.dispose();

            ImageLoader imageLoader = new ImageLoader();
            imageLoader.data = new ImageData[]{image.getImageData()};
            imageLoader.save(out, SWT.IMAGE_BMP);
            byte[] buffer = out.toByteArray();
            out.close();

            String filename = dialog.getFilterPath() + "/" + dialog.getFileName();

            if (!filename.endsWith(".bmp"))
            {
                filename = filename + ".bmp";
            }

            FileOutputStream fos = new FileOutputStream(filename);
            fos.write(buffer);
            fos.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void onPrint()
    {
        PrintDialog dialog = new PrintDialog(this.editor_.getSite().getShell(), SWT.NULL);
        PrinterData data = dialog.open();
        if (data != null)
        {
            Printer printer = new Printer(data);
            PrintGraphicalViewerOperation op =
                    new PrintGraphicalViewerOperation(printer, this.editor_.getGraphicalViewer());
            op.setPrintMode(PrintFigureOperation.FIT_PAGE);
            op.run("StatsVision - " + this.editorPart_.getTitle());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void onReset()
    {
        this.editor_.reset();
    }

    /**
     * {@inheritDoc}
     */
    public void onStart()
    {
        this.editor_.start();
    }

    /**
     * {@inheritDoc}
     */
    public void onStop()
    {
        this.editor_.stop();
    }

    /**
     * {@inheritDoc}
     */
    public void onReload()
    {
        this.editor_.reload();
    }

    /**
     * {@inheritDoc}
     */
    public void connected()
    {
        Display.getDefault().asyncExec(new Runnable() {
            public void run()
            {
                StatsVisionEditorTab.this.editorPart_.setTitleImage(StatsVisionPlugin.IMG_CONNECT_TITLE);
                StatsVisionEditorTab.this.editor_.setBackground(AbstractStatsVisionEditor.CONNECTED_BACKCOLOR);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void disconnected()
    {
        Display.getDefault().asyncExec(new Runnable() {
            public void run()
            {
                StatsVisionEditorTab.this.editorPart_.setTitleImage(StatsVisionPlugin.IMG_DISCONNECT_TITLE);
                StatsVisionEditorTab.this.editor_.setBackground(AbstractStatsVisionEditor.DISCONNECTED_BACKCOLOR);
            }
        });
    }

    public void onSave(PersistenceModel persistence)
    {
        Settings settings = new Settings();
        persistence.setSettings(settings);

        settings.setHostName(this.editor_.getHostName());
        settings.setPortNum(this.editor_.getPortNum());
        settings.setDomain(this.editor_.getDomain());
        settings.setWarningThreshold(this.editor_.getWarningThreshold());
        settings.setAlarmThreshold(this.editor_.getAlarmThreshold());
        settings.setMode(this.editor_.getMode());
        settings.setLineStyle(this.editor_.getLineStyle());

        View view = ModelConverter.toView(this.editor_.getComponentMap().values());
        persistence.setView(view);

        this.editor_.setDirty(false);
    }

    public void onLoad(PersistenceModel persistence)
    {
        ContentsModel contents = ModelConverter.toContentsModel(persistence.getView());
        if (contents != null)
        {
            this.editor_.loadContent(contents);
            this.editor_.layoutModel();
        }
        this.editor_.getViewer().setContents(this.editor_.rootModel);
    }
}
