import com.bjpowernode.crm.workbench.mapper.ActivityMapper;
import com.bjpowernode.crm.workbench.pojo.Activity;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class test {


    public static void main(String[] args) {
        ApplicationContext ioc = new ClassPathXmlApplicationContext("applicationContext.xml");
        ActivityMapper mapper = ioc.getBean(ActivityMapper.class);
        String[] ids = new String[]{"1de9eeac3ba34ce597bb7d3fc10a1b40","f7cb23de956e405e8ba098d11e7cf5af"};
        List<Activity> activities = mapper.selectByIds(ids);
        System.out.println(activities);
    }
}
