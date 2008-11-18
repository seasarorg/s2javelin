package org.seasar.javelin.bottleneckeye.util;

import org.seasar.javelin.bottleneckeye.editors.util.ProjectUtil;

import junit.framework.TestCase;

/**
 * ProjectUtilのテストクラス
 * @author fujii
 *
 */
public class ProjectUtilTest extends TestCase
{

    /**
     * 置換対象文字列外のものを入力した場合(半角英数字)
     * "012345abcdeABCDE!#$%&'-_~[{}]"に対して、
     * ProjectUtil.getValidFileNameを呼び出す。
     */
    public void testNormalHalfCharacter()
    {
        // 準備
        String fileName = "012345abcdeABCDE!#$%&'-_~[{}]";
        String expected = "012345abcdeABCDE!#$%&'-_~[{}]";

        // 実行
        String result = ProjectUtil.getValidFileName(fileName);

        // 結果
        assertEquals(expected, result);
    }

    /**
     * 置換対象文字列外のものを入力した場合(全角文字)
     * "あいうえおアイウエオ亜異卯絵尾"に対して、
     * ProjectUtil.getValidFileNameを呼び出す。
     */
    public void testNormalFullCharacter()
    {
        // 準備
        String fileName = "あいうえおアイウエオ亜異卯絵尾";
        String expected = "あいうえおアイウエオ亜異卯絵尾";

        // 実行
        String result = ProjectUtil.getValidFileName(fileName);

        // 結果
        assertEquals(expected, result);
    }

    /**
     * 置換対象文字列を入力した場合
     * "\/:,*?"<>|()" に対して、
     * ProjectUtil.getValidFileNameを呼び出す。
     */
    public void testReplaceFullCharacter()
    {
        // 準備
        String fileName = "\\/:,*?\"<>|()";
        String expected = "____________";

        // 実行
        String result = ProjectUtil.getValidFileName(fileName);

        // 結果
        assertEquals(expected, result);
    }

    /**
     * 置換対象文字と置換対象外文字列を入力した場合
     * "1\2/a:b,A*B?あ"い<ア>イ|亜(異)" に対して、
     * ProjectUtil.getValidFileNameを呼び出す。
     */
    public void testReplaceMergeCharacter()
    {
        // 準備
        String fileName = "1\\2/a:b,A*B?あ\"い<ア>イ|亜(異)";
        String expected = "1_2_a_b_A_B_あ_い_ア_イ_亜_異_";

        // 実行
        String result = ProjectUtil.getValidFileName(fileName);

        // 結果
        assertEquals(expected, result);
    }

}
