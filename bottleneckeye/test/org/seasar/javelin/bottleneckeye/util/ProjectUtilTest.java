package org.seasar.javelin.bottleneckeye.util;

import org.seasar.javelin.bottleneckeye.editors.util.ProjectUtil;

import junit.framework.TestCase;

/**
 * ProjectUtil�̃e�X�g�N���X
 * @author fujii
 *
 */
public class ProjectUtilTest extends TestCase
{

    /**
     * �u���Ώە�����O�̂��̂���͂����ꍇ(���p�p����)
     * "012345abcdeABCDE!#$%&'-_~[{}]"�ɑ΂��āA
     * ProjectUtil.getValidFileName���Ăяo���B
     */
    public void testNormalHalfCharacter()
    {
        // ����
        String fileName = "012345abcdeABCDE!#$%&'-_~[{}]";
        String expected = "012345abcdeABCDE!#$%&'-_~[{}]";

        // ���s
        String result = ProjectUtil.getValidFileName(fileName);

        // ����
        assertEquals(expected, result);
    }

    /**
     * �u���Ώە�����O�̂��̂���͂����ꍇ(�S�p����)
     * "�����������A�C�E�G�I���ىK�G��"�ɑ΂��āA
     * ProjectUtil.getValidFileName���Ăяo���B
     */
    public void testNormalFullCharacter()
    {
        // ����
        String fileName = "�����������A�C�E�G�I���ىK�G��";
        String expected = "�����������A�C�E�G�I���ىK�G��";

        // ���s
        String result = ProjectUtil.getValidFileName(fileName);

        // ����
        assertEquals(expected, result);
    }

    /**
     * �u���Ώە��������͂����ꍇ
     * "\/:,*?"<>|()" �ɑ΂��āA
     * ProjectUtil.getValidFileName���Ăяo���B
     */
    public void testReplaceFullCharacter()
    {
        // ����
        String fileName = "\\/:,*?\"<>|()";
        String expected = "____________";

        // ���s
        String result = ProjectUtil.getValidFileName(fileName);

        // ����
        assertEquals(expected, result);
    }

    /**
     * �u���Ώە����ƒu���ΏۊO���������͂����ꍇ
     * "1\2/a:b,A*B?��"��<�A>�C|��(��)" �ɑ΂��āA
     * ProjectUtil.getValidFileName���Ăяo���B
     */
    public void testReplaceMergeCharacter()
    {
        // ����
        String fileName = "1\\2/a:b,A*B?��\"��<�A>�C|��(��)";
        String expected = "1_2_a_b_A_B_��_��_�A_�C_��_��_";

        // ���s
        String result = ProjectUtil.getValidFileName(fileName);

        // ����
        assertEquals(expected, result);
    }

}
