     _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/
                                                                2008/03/24
   _/                       S2Javelin �ȈՃ}�j���A��                       _/
 
 _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/


�P.�T�v

    S2Javelin�Ƃ́A���\���̉�͋@�\����ѓ��샍�O�o�͋@�\��g�ݍ��ރv���_�N�g
    �ł��BS2Javelin�́ASeasar2��AOP�@�\��p���āA�A�v���P�[�V�����̃\�[�X�R�[�h
    �ɕύX�������鎖�����A�������������܂��B

    ���\���́ABottleneckEye�ɂ���ăN���X�}�`����\�`���ŎQ�Ƃ��鎖���ł��܂��B
    ���샍�O�́AArrowVision�ɂ���ăV�[�P���X�}�`���ŕ\�����邱�Ƃ��ł��܂��B

    ���P�jBottleneckEye�̓G�X�G���W�[������Ђ����J���Ă���t���[�̉�̓c�[���ł��B
          �ڂ����́u�R�D�g�p���@(BottleneckEye)�v�����Q�Ɖ������B
    ���Q�jArrowVision�̓G�X�G���W�[������Ђ����J���Ă���t���[�̉�̓c�[���ł��B
          �ڂ����́u�S�D�g�p���@(ArrowVision)�v����сu�T�DArrowVision�ɂ��āv
          �����Q�Ɖ������B


�Q�DS2Javelin�C���X�g�[�����@

    S2Javelin�C���X�g�[���ɕK�v�Ȑݒ�́A
  
        -------------------------
        (1) S2Javelin�̔z�u
        (2) ���C�u�����̔z�u
        (3) JVM�N���I�v�V�����̕ύX
        (4) Javelin�ݒ�t�@�C���̕ύX(javelin.properties)
        (5) dicon�̐ݒ�(S2JavelinInterceptor)
        (6) web.xml�̐ݒ�(S2JavelinFilter)
        (7) �K�p�̊m�F
        -------------------------

    ��9�i�K�ł��B

    (1) S2Javelin�̔z�u

        S2Javelin-V1.1.0.zip��W�J���Ăł����A�ȉ��̃f�B���N�g���K�w���A
        �C���X�g�[����ɔz�u���܂��B

        .
        ��  readme.txt(�{�t�@�C��)
        ��  SEASAR-LICENSE.TXT
        ��
        ����conf
        ��      javelin.properties
        ��
        ����data
        ��      deleteme.txt
        ��
        ����lib
        ��      s2javelin.jar
        ��      s2javelin_core.jar
        ��
        ����logs
        ��      deleteme.txt
        ��
        ����traces
                deleteme.txt

        �ȉ��̐����ł́AC:\Javelin�z���ɔz�u�������Ƃ�O��Ƃ��܂��B

    (2) ���C�u�����̔z�u

        s2javelin.jar�A�����s2javelin_core.jar���A��͑ΏۃA�v���P�[�V�����̃N
        ���X�p�X�ɒǉ����܂��B
        Web�A�v���P�[�V�����̏ꍇ�́AWEB-INF/lib�f�B���N�g����s2javelin_core.jar
        �����s2javelin.jar���R�s�[���Ă��������B

    (3) JVM�N���I�v�V�����̕ύX
        JVM�N���I�v�V�����ɁA�ȉ���ǉ����܂��B
        
        ========================================================================
        -Djavelin.property=C:/Javelin/conf/javelin.properties
        ========================================================================

    (4) Javelin�ݒ�t�@�C���̕ύX(javelin.properties)
        �O�̍��ڂŎw�肵���Ajavelin.properties���̐ݒ���A���ɍ��킹�ĕύX����
        ���B�ڂ����́Ajavelin.properties�t�@�C���̃R�����g���Q�Ƃ��������B

    (5) dicon�̐ݒ�(S2JavelinInterceptor)
        S2Javelin�́AInterceptor��K�p�������\�b�h���Ď��ΏۂƂ���̂ŁAdicon��
        Interceptor�̒�`��ǉ����܂��B
        ��Seasar�̃o�[�W�����ɂ��A�A�X�y�N�g�̓K�p���@���ς�邽�߁A
          �e�o�[�W�����ɍ��킹���ݒ���s���Ă��������B

        �ȍ~�AS2JSF Example 1.1.2�ɑ΂��āAS2Javelin��K�p����ۂ̎菇�������܂��B

        src/main/resources/examples/jsf/dicon/allaop.dicon�ɑ΂��āA�ȉ��̕ύX��
        �����܂��B
        ========================================================================
            (��)
            <include path="aop.dicon"/>
            <include path="dao.dicon"/>
            <include path="j2ee.dicon"/>
            <include path="javelin.dicon"/>
            ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^(�ǉ�)

        <component
            name="actionInterceptorChain"
            class="org.seasar.framework.aop.interceptors.InterceptorChain"
        >
            <initMethod name="add"><arg>s2JavelinInterceptor</arg></initMethod>
            ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^(�ǉ�)
            <initMethod name="add"><arg>aop.traceInterceptor</arg></initMethod>
            <initMethod name="add"><arg>actionThrowsInterceptor</arg></initMethod>
        </component>

        <component
            name="logicInterceptorChain"
            class="org.seasar.framework.aop.interceptors.InterceptorChain"
        >
            <initMethod name="add"><arg>s2JavelinInterceptor</arg></initMethod>
            ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^(�ǉ�)
            <initMethod name="add"><arg>aop.traceThrowsInterceptor</arg></initMethod>
            <initMethod name="add"><arg>aop.traceInterceptor</arg></initMethod>
            <initMethod name="add"><arg>j2ee.requiredTx</arg></initMethod>
        </component>

        <component
            name="daoInterceptorChain"
            class="org.seasar.framework.aop.interceptors.InterceptorChain"
        >
            <initMethod name="add"><arg>s2JavelinInterceptor</arg></initMethod>
            ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^(�ǉ�)
            <initMethod name="add"><arg>aop.traceInterceptor</arg></initMethod>
            <initMethod name="add"><arg>dao.interceptor</arg></initMethod>
        </component>

        ========================================================================

        ���̒ǋL�ɂ���āAAction�ALogic�ADao�̑S�R���|�[�l���g�N���X�ɑ΂��ă��O
        ���o�͂����悤�ɂȂ�܂��B

    (6) web.xml�̐ݒ�(S2JavelinFilter)
        
        web.xml��filter��`�����ɁA�ȉ��̓��e���L�q���܂��B
        ========================================================================
        <!-- S2JavelinFilter -->
        <filter>
            <filter-name>s2javelinfilter</filter-name>
            <filter-class>org.seasar.javelin.S2JavelinFilter</filter-class>
        </filter>
        ========================================================================

        web.xml��filter-mapping��`�����ɁA�ȉ��̓��e���L�q���܂��B
        ��encodingfilter����ɒ�`����K�v������܂��B
        ========================================================================
        <!-- S2JavelinFilter -->
        <filter-mapping>
            <filter-name>s2JavelinFilter</filter-name>
            <url-pattern>*.html</url-pattern>
        </filter-mapping>
        ========================================================================

    (7) �K�p�̊m�F
        �ȏ�̐ݒ���s������A�A�v���P�[�V�����E�T�[�o���N�����āAWeb�A�v���P�[
        �V����������ɓ��삷�鎖���m�F���Ă��������B

        �Ď��ΏۂƂ������������s�����l�ɃA�v���P�[�V�����𑀍삷��ƁA�A�v���P
        �[�V�����̕W���o�͂Ɉȉ��̂悤�ȓ��e���o�͂���܂��B
        ���p�����[�^�̏ڍׂ́Ajavelin.properties�ɐݒ肵�����e�Ɉˑ����܂��B
        
        >>>> Properties related with S2JavelinFilter
            javelin.intervalMax             : 500
            javelin.throwableMax            : 500
            javelin.javelinFileDir          : C:/Javelin/logs
            javelin.acceptPort              : 18000
            javelin.javelinEnable           : false
            javelin.stringLimitLength       : 1024
            javelin.error.log               : C:/Javelin/logs/javelin.log
            javelin.record.jmx              : true
        <<<<

        >>>> Properties related with S2JavelinInterceptor
            javelin.javelinEnable   : false
            javelin.intervalMax     : 500
            javelin.throwableMax    : 500
            javelin.recordThreshold : 0
            javelin.alarmThreshold  : 0
            javelin.domain          : default
            javelin.httpPort        : 0
        <<<<    


�R�D�g�p���@(BottleneckEye)
    BottleneckEye�́AEclipse�v���O�C���ł��B
    Eclipse��plugins�f�B���N�g���ɔz�u���Ă��������B

    (1) �Ώۂ�Seasar�A�v���P�[�V�������N�������삳����B

    (2) BottleneckEye�t�@�C��(�g���q".beye"�̃t�@�C��)���쐬����B
        �uFile�v���uNew�v���uBottleneck Eye�v���uFile�v��I�����A
        �t�@�C��������͂���B
    
    (3) S2Javelin�ɐڑ�����ݒ���s���B
        �uSettings�v�^�u��I�����A�ȉ��̓��e����͂��܂��B

        Host   : Seasar�A�v���P�[�V�����̂���}�V����IP
        Port   : javelin.acceptPort�Ŏw�肵���|�[�g�ԍ�
        Domain : �ʐM��JMX���g�p���邳���ɕK�v�ȃh���C�����w�肵�܂��B
                 (JMX�̃T�|�[�g�͔p�~�\��ł��B�l�͕ύX���Ȃ��ŉ������B)
        Warning: �N���X�}��ŁA�����ŕ\�����郁�\�b�h��臒l���w�肵�܂��B
        Alarm  : �N���X�}��ŁA�Ԏ��ŕ\�����郁�\�b�h��臒l���w�肵�܂��B
        Mode   : �ʐM�����ɁATCP/JMX�̂�������g�p���邩�w�肵�܂��B
                 (JMX�̃T�|�[�g�͔p�~�\��ł��BTCP���g�p���ĉ������B)
        Style  : �N���X�Ԃ̃����[�V�����V�b�v�̐����`��A���S���Y�����w�肵�܂��B
                 (�ȉ����A�ł��K�������̂�I�����ĉ������B)
                 NORMAL    - �ŒZ�̒����ŕ`�悵�܂��B
                 SHORTEST  - �N���X�����؂�܂���B
                 FAN       - �������d�Ȃ����ۂɏd�Ȃ��������܂��B
                 MANHATTAN - ���p�ɋȂ�����ŕ`�悵�܂��B

    (4) BottleneckEye�t�@�C�����J���AS2Javelin�ɐڑ�����B
        �t�@�C�����J���Ɠ����ɐڑ����J�n���܂��B
        �˖��ڑ��̏ꍇ�͔w�i�̓O���[�ł��B�ڑ��ɐ��������ꍇ�A���F�ɂȂ�܂��B


�S�D�g�p���@(ArrowVision)
    ArrowVision�́AEclipse�v���O�C���ł��B
    �}�̂�W�J���Ăł����f�B���N�g����Eclipse��plugins�f�B���N�g���ɔz�u���Ă��������B

    (1) �Ώۂ�Seasar�A�v���P�[�V�������N�������삳����B
        �ˁu�Q�D�C���X�g�[���v�́uJavelin�ݒ�t�@�C���̕ύX�v�Ŏw�肵���ꏊ�ɁA
          ���O�t�@�C������������܂��B

    (2) (1)�Ő������ꂽ���O�t�@�C�����A���̂܂�ArrowVision�̉�ʏ�Ƀh���b�O���h
        ���b�v���Ă��������B
        �˓���V�[�P���X���\������܂��B


�T�DArrowVision�ɂ���

    S2Javelin�̃��O�t�@�C�����r�W���A���ɕ\�����邽�߂ɂ́uArrowVision�v�Ƃ����\
    ���p�c�[�����K�v�ɂȂ�܂��B

    ArrowVision�́A�G�X�G���W�[������Ђ̃z�[���y�[�W����A�����Ń_�E�����[�h��
    �邱�Ƃ��ł��܂��B

    �uArrowVision�v
        http://www.smg.co.jp/service/products/arrow_vision/index.html


�ȏ�


This product includes software developed by the 
Seasar Project (http://www.seasar.org/).
