package trace.simpleTrace;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import rpcTest.Service;

import java.util.concurrent.CountDownLatch;

/**
 * Created by wephone on 17-12-30.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:src/test/java/trace/simpleTrace/ClientContext.xml"})
public class Client {

    @Autowired
    Service service;

    @Test
    public void start(){
        CountDownLatch countDownLatch=new CountDownLatch(1);
//        Service service= (Service) RPC.call(Service.class);
        System.out.println("RPC接收成功:"+service.remoteService(233.0,"hhh"));
        System.out.println("测试Integer类型传参与返回:"+service.IntegerMethodTest(233));
        System.out.println("测试Integer,Double类型传参与返回String对象:"+service.stringMethodIntegerArgsTest(233,666.66));
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}