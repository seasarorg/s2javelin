package org.seasar.javelin.bottleneckeye;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class StatsVisionPlugin extends AbstractUIPlugin
{
    /** ���̃C���X�^���X�B */
    private static StatsVisionPlugin plugin__;

    /** �X�V�A�C�R���B */
    public static final String       IMG_REFRESH = "icons/refresh.gif";

    /** ���ڑ��̂Ƃ��̃^�C�g���A�C�R�� */
    public static final String IMG_DISCONNECT_TITLE = "icons/bottleneckeye_disconnect.gif";

    /** �ڑ����Ă���Ƃ��̃^�C�g���A�C�R�� */
    public static final String IMG_CONNECT_TITLE = "icons/bottleneckeye.gif";

    /**
     * {@inheritDoc}<br>
     * �摜�̓o�^���s���B
     */
    protected void initializeImageRegistry(ImageRegistry registry)
    {
        registerImage(registry, IMG_REFRESH, IMG_REFRESH);
        registerImage(registry, IMG_DISCONNECT_TITLE, IMG_DISCONNECT_TITLE);
        registerImage(registry, IMG_CONNECT_TITLE, IMG_CONNECT_TITLE);
    }

    /**
     * 1�̉摜�̓o�^���s���B
     * @param registry �C���[�W���W�X�g��
     * @param key �摜�̃L�[
     * @param fileName �摜�t�@�C����
     */
    private void registerImage(ImageRegistry registry, String key, String fileName)
    {
        ImageDescriptor desc = getImageDescriptor(fileName);
        registry.put(key, desc);
    }

    /**
     * �R���X�g���N�^�B
     */
    public StatsVisionPlugin()
    {
        plugin__ = this;
    }

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext context)
        throws Exception
    {
        super.start(context);
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context)
        throws Exception
    {
        super.stop(context);
        plugin__ = null;
    }

    /**
     * �C���X�^���X���擾����B
     * @return �C���X�^���X
     */
    public static StatsVisionPlugin getDefault()
    {
        return plugin__;
    }

    /**
     * Returns an image descriptor for the image file at the given
     * plug-in relative path.
     *
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path)
    {
        return AbstractUIPlugin.imageDescriptorFromPlugin("org.seasar.javelin.bottleneckeye", path);
    }
}
