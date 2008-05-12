package org.seasar.javelin.bottleneckeye.communicate;

/**
 * ��{�I�ȋ��ʋ@�\��񋟂���
 */
public class Common 
{

    /** �d����ʁi��Ԓʒm�j */
	public static final byte BYTE_TELEGRAM_KIND_ALERT = 0;

	/** �d����ʁi��Ԏ擾�j */
	public static final byte BYTE_TELEGRAM_KIND_GET = 1;
	
	/** �d����ʁi���Z�b�g�j */
	public static final byte BYTE_TELEGRAM_KIND_RESET = 2;

	/** �d����ʁi���\�[�X�ʒm�j */
    public static final byte BYTE_TELEGRAM_KIND_RESOURCENOTIFY = 3;

    /** �d����ʁi�ݒ�ύX�j */
    public static final byte BYTE_TELEGRAM_KIND_CONFIGCHANGE = 4;

    /** �d����ʁi�@�\�Ăяo���j */
    public static final byte BYTE_TELEGRAM_KIND_FUNCTIONCALL = 5;
    
    /** �d�����(JVN���O�_�E�����[�h) */
	public static final byte BYTE_TELEGRAM_KIND_JVN_FILE = 6;

    /** �d�����(�T�[�o�v���p�e�B�ݒ�擾) */
	public static final byte BYTE_TELEGRAM_KIND_GET_PROPERTY = 7;

    /** �d�����(�T�[�o�v���p�e�B�ݒ�X�V) */
	public static final byte BYTE_TELEGRAM_KIND_UPDATE_PROPERTY = 8;
    
    /**
     * �d�����(JVN���O�ꗗ�擾)
     */
    public static final byte BYTE_TELEGRAM_KIND_JVN_FILE_LIST = 9;
    
    /** �v��������ʁi�ʒm�j */
	public static final byte BYTE_REQUEST_KIND_NOTIFY = 0;

	/** �v��������ʁi�v���j */
	public static final byte BYTE_REQUEST_KIND_REQUEST = 1;

	/** �v��������ʁi�����j */
	public static final byte BYTE_REQUEST_KIND_RESPONSE = 2;

    /** �I�u�W�F�N�g���i���\�[�X�ʒm�ł̃��\�[�X�l�j */
	public static final String OBJECTNAME_RESOURCE = "resources";

    /** �I�u�W�F�N�g���i�@�\�Ăяo���ł̐ڑ��Ǘ��j */
	public static final String OBJECTNAME_CONNECTIONMANAGER = "connectionManager";

	/** �I�u�W�F�N�g���i�@�\�Ăяo���ł̕\������j */
	public static final String OBJECTNAME_VIEWOPERATION = "viewOperation";

    /** �I�u�W�F�N�g���iJVN�t�@�C���j */
    public static final String OBJECTNAME_JVN_FILE = "jvnFile";

    /** ���ږ��i���\�[�X�l�ł̎擾�����j */
    public static final String ITEMNAME_ACQUIREDTIME = "acquiredTime";

    /** ���ږ��i���\�[�X�l�ł�CPU���ԁj */
    public static final String ITEMNAME_CPUTIME = "cpuTime";

    /** ���ږ��i���\�[�X�l�ł�Java�ғ����ԁj */
    public static final String ITEMNAME_JAVAUPTIME = "javaUpTime";

    /** ���ږ��i���\�[�X�l�ł̃v���Z�b�T���j */
    public static final String ITEMNAME_PROCESSORCOUNT = "processorCount";

    /** ���ږ��i���\�[�X�l�ł̃q�[�v�������R�~�b�g�e�ʁj */
    public static final String ITEMNAME_HEAPMEMORYCOMMITTED = "heapMemoryCommitted";

    /** ���ږ��i���\�[�X�l�ł̃q�[�v�������g�p�ʁj */
    public static final String ITEMNAME_HEAPMEMORYUSED = "heapMemoryUsed";

    /** ���ږ��i���\�[�X�l�ł̃q�[�v�������ő�j */
    public static final String ITEMNAME_HEAPMEMORYMAX = "heapMemoryMax";

    /** ���ږ��i���\�[�X�l�ł̃q�[�v�ȊO�̃������R�~�b�g�e�ʁj */
    public static final String ITEMNAME_NONHEAPMEMORYCOMMITTED = "nonHeapMemoryCommitted";

    /** ���ږ��i���\�[�X�l�ł̃q�[�v�ȊO�̃������g�p�ʁj */
    public static final String ITEMNAME_NONHEAPMEMORYUSED = "nonHeapMemoryUsed";

    /** ���ږ��i���\�[�X�l�ł̃q�[�v�ȊO�̃������ő�j */
    public static final String ITEMNAME_NONHEAPMEMORYMAX = "nonHeapMemoryMax";

    /** ���ږ��i���\�[�X�l�ł̉��z�������e�ʁj */
    public static final String ITEMNAME_VIRTUALMEMORYSIZE = "virtualMemorySize";

    /** ���ږ��i���\�[�X�l�ł̉��z�}�V���������e�ʁj */
    public static final String ITEMNAME_VIRTUALMACHINEMEMORYCAPACITY = "virtualMachineMemoryCapacity";

    /** ���ږ��i���\�[�X�l�ł̉��z�}�V���������󂫗e�ʁj */
    public static final String ITEMNAME_VIRTUALMACHINEMEMORYFREE = "virtualMachineMemoryFree";

    /** ���ږ��i���\�[�X�l�ł̕����������e�ʁj */
    public static final String ITEMNAME_PHYSICALMEMORYCAPACITY = "physicalMemoryCapacity";

    /** ���ږ��i���\�[�X�l�ł̕����������󂫗e�ʁj */
    public static final String ITEMNAME_PHYSICALMEMORYFREE = "physicalMemoryFree";

    /** ���ږ��i���\�[�X�l�ł̃X���b�v�̈�e�ʁj */
    public static final String ITEMNAME_SWAPSPACECAPACITY = "swapSpaceCapacity";

    /** ���ږ��i���\�[�X�l�ł̃X���b�v�̈�󂫗e�ʁj */
    public static final String ITEMNAME_SWAPSPACEFREE = "swapSpaceFree";

    /** ���ږ��i���\�[�X�l�ł́A�v���Z�X�S�̂̃l�b�g���[�N�f�[�^��M�ʁj */
    public static final String ITEMNAME_NETWORKINPUTSIZEOFPROCESS = "networkInputSizeOfProcess";

    /** ���ږ��i���\�[�X�l�ł́A�v���Z�X�S�̂̃l�b�g���[�N�f�[�^���M�ʁj */
    public static final String ITEMNAME_NETWORKOUTPUTSIZEOFPROCESS = "networkOutputSizeOfProcess";

    /** ���ږ��i���\�[�X�l�ł́A�t�@�C�����͗ʁj */
    public static final String ITEMNAME_FILEINPUTSIZEOFPROCESS = "fileInputSizeOfProcess";

    /** ���ږ��i���\�[�X�l�ł́A�t�@�C���o�͗ʁj */
    public static final String ITEMNAME_FILEOUTPUTSIZEOFPROCESS = "fileOutputSizeOfProcess";

    /** ���ږ��i���\�[�X�l�ł́A�X���b�h���j */
    public static final String ITEMNAME_THREADCOUNT = "threadCount";

    /** ���ږ��i���\�[�X�l�ł́A�g�[�^���̃K�x�[�W�R���N�V�����̎��ԁj */
    public static final String ITEMNAME_GARBAGETOTALTIME = "garbageTotalTime";

    /** ���ږ��i���\�[�X�l�ł́A�R���N�V�����̐��j */
    public static final String ITEMNAME_LISTCOUNT = "listCount";

    /** ���ږ��i���\�[�X�l�ł́A�R���N�V�����̐��j */
    public static final String ITEMNAME_QUEUECOUNT = "queueCount";

    /** ���ږ��i���\�[�X�l�ł́A�R���N�V�����̐��j */
    public static final String ITEMNAME_SETCOUNT = "setCount";

    /** ���ږ��i���\�[�X�l�ł́A�R���N�V�����̐��j */
    public static final String ITEMNAME_MAPCOUNT = "mapCount";

    /** ���ږ��iJVN�t�@�C�� JVN�t�@�C�����j */
    public static final String ITEMNAME_JVN_FILE_NAME = "jvnFileName";

    /** ���ږ��iJVN�t�@�C�� JVN�t�@�C�����e�j */
    public static final String ITEMNAME_JVN_FILE_CONTENT = "jvnFileContent";

}
