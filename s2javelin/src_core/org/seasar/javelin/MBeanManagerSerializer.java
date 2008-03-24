package org.seasar.javelin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.seasar.javelin.bean.Component;

public class MBeanManagerSerializer {

	public static Map<String, Component> deserialize() {
		S2JavelinConfig config = new S2JavelinConfig();
		if (!config.isSetSerializeFile())
		{
			return new HashMap<String, Component>();
		}
		
		String serializeFile = config.getSerializeFile();
		File file = new File(serializeFile);

		// �t�@�C�������݂��Ȃ��ꍇ�̓f�V���A���C�Y���X�L�b�v����B
		if (!file.exists())
		{
			return new HashMap<String, Component>();
		}
		
		try
		{
			FileInputStream inFile = new FileInputStream(serializeFile);
			ObjectInputStream inObject = new ObjectInputStream(inFile);
			Map<String, Component> map = 
				(Map<String, Component>) inObject.readObject();
			inObject.close();
			inFile.close();
			return map;
		} catch (Exception e) {
			SystemLogger.getInstance().warn(
					"MBeanManager��deserialize�Ɏ��s���܂����Bdeserialize�����ɋN�����܂��B"
					+ "deserialize��[" + serializeFile + "]", e);
		}
		
		return new HashMap<String, Component>();
	}
	
	public static void serialize(Map<String, Component> map) {
		S2JavelinConfig config = new S2JavelinConfig();
		if (config.isSetSerializeFile()) {
			String serializeFile = config.getSerializeFile();
			
			try {
				FileOutputStream outFile = new FileOutputStream(serializeFile);
				ObjectOutputStream outObject = new ObjectOutputStream(outFile);
				outObject.writeObject(map);
				outObject.close();
				outFile.close();
			} catch (Exception e) {
				SystemLogger.getInstance().warn(
						"MBeanManager��serialize�Ɏ��s���܂����B"
						+ "serialize��[" + serializeFile + "]", e);
			}
		}
	}
}
