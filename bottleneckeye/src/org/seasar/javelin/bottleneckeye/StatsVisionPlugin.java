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
    /** このインスタンス。 */
    private static StatsVisionPlugin plugin__;

    /** 更新アイコン。 */
    public static final String       IMG_REFRESH = "icons/refresh.gif";

    /** 未接続のときのタイトルアイコン */
    public static final String IMG_DISCONNECT_TITLE = "icons/bottleneckeye_disconnect.gif";

    /** 接続しているときのタイトルアイコン */
    public static final String IMG_CONNECT_TITLE = "icons/bottleneckeye.gif";

    /**
     * {@inheritDoc}<br>
     * 画像の登録を行う。
     */
    protected void initializeImageRegistry(ImageRegistry registry)
    {
        registerImage(registry, IMG_REFRESH, IMG_REFRESH);
        registerImage(registry, IMG_DISCONNECT_TITLE, IMG_DISCONNECT_TITLE);
        registerImage(registry, IMG_CONNECT_TITLE, IMG_CONNECT_TITLE);
    }

    /**
     * 1つの画像の登録を行う。
     * @param registry イメージレジストリ
     * @param key 画像のキー
     * @param fileName 画像ファイル名
     */
    private void registerImage(ImageRegistry registry, String key, String fileName)
    {
        ImageDescriptor desc = getImageDescriptor(fileName);
        registry.put(key, desc);
    }

    /**
     * コンストラクタ。
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
     * インスタンスを取得する。
     * @return インスタンス
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
