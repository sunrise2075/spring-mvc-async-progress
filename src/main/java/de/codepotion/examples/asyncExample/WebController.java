package de.codepotion.examples.asyncExample;

import de.codepotion.examples.asyncExample.jobs.ExampleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

/**
 * Created by Frenos on 18.08.2016.
 */
@Controller
public class WebController {

    private static int jobNumber;
    private final asyncService myService;
    @Qualifier("taskExecutor")
    @Autowired
    private ThreadPoolTaskExecutor myExecutor;

    @Autowired
    private SimpMessagingTemplate template;

    private ArrayList<ExampleJob> myJobList = new ArrayList<>(5);

    @Autowired
    WebController(asyncService myService) {
        this.myService = myService;
    }

    @RequestMapping("/trigger")
    @ResponseBody
    public void startWork() {
        //创建三个样例任务，把它们交给异步方法运行
        for (int i = 0; i < 3; i++) {
            System.out.println(this + "START startWork");

            ExampleJob newJob = new ExampleJob("Job-" + jobNumber, template);
            jobNumber = jobNumber + 1;
            myJobList.add(newJob);
            //这里的异步方法没有返回值
            myService.doWork(newJob);
            System.out.println(this + "END startWork");
        }
    }

    /**
     * 本方法提供给浏览器的定时任务调用，返回样例作业的列表
     * **/
    @RequestMapping(value = "/status")
    @ResponseBody
    @SubscribeMapping("initial")
    ArrayList<ExampleJob> fetchStatus() {
        return this.myJobList;
    }

    @RequestMapping(value = "/poolsize/{newSize}")
    @ResponseBody
    public void setNewPoolsize(@PathVariable("newSize") int newSize) {
        myExecutor.setCorePoolSize(newSize);
    }


}
