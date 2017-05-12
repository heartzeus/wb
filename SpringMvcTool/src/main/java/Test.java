import com.tuhanbao.autotool.mvc.ClassCreatorUtil;
import com.tuhanbao.util.config.ConfigManager;
import com.tuhanbao.web.controller.helper.MyPropertyPlaceholderConfigurer;

//TODO ,  主键序列，分页，redis, 地区，枚举，常量（配置文件）
//
//update 
public class Test {
	
	public static void main(String args[]) {
	    MyPropertyPlaceholderConfigurer.start();
		ClassCreatorUtil.startCreateProject(ConfigManager.getConfig("solid"));
	}
	
}
