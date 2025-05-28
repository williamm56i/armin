# Armin Batch
Batch架構專案，定義批次流程與業務邏輯實作，並提供接口以立即執行作業或Runtime調整批次流程

### 技術框架
* Java version 18
* Spring boot 3.3.12
* Spring Batch
* Maven
* Mybatis
* h2 (視專案情況替換成任何RDB
* Swagger

### 開發工具
* IntelliJ

### 版本資訊
* 0.0.1-SNAPSHOT
    * 初版

### 執行
* 打包jar
```
mvn install
```
* build image
```
docker build --tag armin:latest .
```
* run
```
docker run --name ARMIN -p 8180:8180 -d armin:latest
```

---

### Spring Batch介紹
* 參考package: batch
#### Job (package: job)
* 一支可運行的批次作業為一個job
* 建議一個job單獨一支Class
* 一個job由一或多個step組成，用以決定該job的執行任務順序
  * 或可將step先組成flow，再組入job中

```java
@Configuration
@Slf4j
public class ExampleJob extends BaseJob {

  @Autowired
  JobRepository jobRepository;
  @Autowired
  ExampleStep exampleStep;

  @Bean("example-job")
  public Job exampleJob() {
    return new JobBuilder("ExampleJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(exampleStep.example1Step())
            .next(exampleStep.example2Step())
            .build();
  }
}
```
#### Step (package: step)
* 定義一個獨立的任務步驟
  * 低耦合的step利於重複利用於不同的job中
* 建議同類型或服務相同job的step可定義於同一支Class中
* 一個step是由一個tasklet或一組reader, processor, writer所組成
```java
@Configuration
public class ExampleStep {

    @Autowired
    JobRepository jobRepository;
    @Autowired
    PlatformTransactionManager transactionManager;
    @Autowired
    Example1Tasklet example1Tasklet;
    @Autowired
    Example2Tasklet example2Tasklet;

    @Bean("example1-step")
    public Step example1Step() {
        return new StepBuilder("範例步驟1", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(example1Tasklet, transactionManager)
                .build();
    }

    @Bean("example2-step")
    public Step example2Step() {
        return new StepBuilder("範例步驟2", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(example2Tasklet, transactionManager)
                .build();
    }
}
```
#### Tasklet (package: tasklet)
* 實際任務邏輯的撰寫位置，implements Tasklet
* 正確執行完畢回傳RepeatStatus.FINISHED，其餘例外情況可自行設定return status
```java
@Configuration
@Slf4j
public class Example1Tasklet implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("example 1 tasklet");
        return RepeatStatus.FINISHED;
    }
}
```
#### 小結
* 一隻批次對應一個job，一個job包含多個step，一個step對應一個tasklet
* step執行順序是依照job中，start -> next -> next -> ...依序執行
* job和step在定義批次的流程與步驟，tasklet定義真正的業務邏輯

#### Listener (package: listener)
* 撰寫監聽器的before, after方法，implements JobExecutionListener/StepExecutionListener
```java
@Component
@Slf4j
public class BaseJobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("before {}...", jobExecution.getJobInstance().getJobName());
    }

    public void afterJob(JobExecution jobExecution) {
        log.info("after {}...", jobExecution.getJobInstance().getJobName());
        if (jobExecution.getStatus().isUnsuccessful()) {
            log.error("Batch failed, jobId: {}", jobExecution.getJobId());
        }
    }
}
```
```java
@Component
@Slf4j
public class BaseStepListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("before {}...", stepExecution.getStepName());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("after {}...", stepExecution.getStepName());
        return stepExecution.getExitStatus();
    }
}
```
* 定義job和step時可加入監聽器，用於進入/離開job或step時做判斷處理
* 一個job/step可定義多組listener

```java
@Configuration
@Slf4j
public class ExampleJob extends BaseJob {

  @Autowired
  JobRepository jobRepository;
  @Autowired
  ExampleStep exampleStep;
  @Autowired
  BaseJobListener baseJobListener;

  @Bean("example-job")
  public Job exampleJob() {
    return new JobBuilder("ExampleJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .listener(baseJobListener)
            .start(exampleStep.example1Step())
            .next(exampleStep.example2Step())
            .end()
            .build();
  }
}
```
#### 相關設定
* 定義於ArminApplication中
* @EnableBatchProcessing
* job策略
  * setIsolationLevelForCreate("ISOLATION_READ_COMMITTED")：讓每支job有獨立的session，避免當批次觸發時間相同時造成一方失敗
  * setTablePrefix("BATCH_")：定義BATCH TABLE PREFIX，當多個系統共用資料庫時另外以前綴區分對應批次控制檔（如：XXX_BATCH_)
  * 非同步機制，批次觸發後立即返回jobId，不等批次完成
```java
@SpringBootApplication
@OpenAPIDefinition
@EnableBatchProcessing
public class ArminApplication {

	@Autowired
	DataSource dataSource;

	public static void main(String[] args) {
		SpringApplication.run(ArminApplication.class, args);
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
		transactionManager.setGlobalRollbackOnParticipationFailure(false);
		transactionManager.setDefaultTimeout(300);
		return transactionManager;
	}

	@Bean
	public JobRepository getJobRepository() throws Exception {
		JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
		jobRepositoryFactoryBean.setDataSource(dataSource);
		jobRepositoryFactoryBean.setTransactionManager(transactionManager());
		jobRepositoryFactoryBean.setIsolationLevelForCreate("ISOLATION_READ_COMMITTED");
		jobRepositoryFactoryBean.setTablePrefix("BATCH_");
		jobRepositoryFactoryBean.afterPropertiesSet();
		return jobRepositoryFactoryBean.getObject();
	}

	@Bean("AsyncJobLauncher")
	public JobLauncher jobLauncher() throws Exception {
		TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
		jobLauncher.setJobRepository(getJobRepository());
		jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
		jobLauncher.afterPropertiesSet();
		return jobLauncher;
	}
}
```

#### 動態載入Job執行流程
* 欲解決維運問題：批次執行流程以程式碼定義於job中，若因業務需要，需不執行其中某個step或要調整step執行順序，則需調整程式碼後安排公司上線程序，曠日費時
* 做法概念：將job執行流程定義於資料庫中，專案啟動時載入當下資料表設定之流程生效之
* 實作方法：定義BaseJob，內含setFlow方法，至資料表(BATCH_JOB_FLOW_CONTROL)中取的當前job的欲執行的step名稱，逐名稱以getBean方式取得實例組成flow後回傳
* Job改寫：所有job需繼承BaseJob，並將流程串接寫法改以呼叫setFlow取得flow後組入job，完成動態載入
```java
public class BaseJob {

    @Autowired
    BatchJobFlowControlDao batchJobFlowControlDao;
    @Autowired
    ApplicationContext applicationContext;

    public Flow setFlow(String jobName, String flowName) {
        FlowBuilder<Flow> flow = new FlowBuilder<>(flowName);
        List<BatchJobFlowControl> stepList = batchJobFlowControlDao.selectByJobName(jobName);
        boolean isFirst = true;
        for (BatchJobFlowControl step: stepList) {
            Step stepObj  = (Step) applicationContext.getBean(step.getStepName());
            if (isFirst) {
                flow.start(stepObj);
                isFirst = false;
            } else {
                flow.next(stepObj);
            }
        }
        return flow.build();
    }
}
```
```
-- selectByJobName
select * from BATCH_JOB_FLOW_CONTROL where JOB_NAME = #{jobName} and IS_EXECUTABLE = 'Y'
order by STEP_ORDER asc
```
```
@Bean("example-job")
public Job exampleJob() {
    return new JobBuilder("ExampleJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .listener(baseJobListener)
            .start(setFlow("ExampleJob", "ExampleFlow"))
            .end()
            .build();
}
```
#### Runtime更新Job執行流程
* 欲解決維運問題：雖已實現動態載入執行流程，但流程仍在專案啟動後就已決定，調整資料表後的流程需要重啟專案才會生效，正式環境不具備能隨時重啟的條件
* 作法概念：調整資料表流程後，移除專案中的Job bean再重新建立，並取消JobRegistry中的註冊；待下次執行批次時重新觸發載入流程，達到Runtime更新的目的
* 實作方法：reloadJobFlow方法實作上述邏輯並提供API
```java
@Service
@Slf4j
public class BatchServiceImpl implements BatchService {
    // 上略
  @Override
  public String reloadJobFlow(String beanName) {
    BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();
    BeanDefinition beanDefinition = beanDefinitionRegistry.getBeanDefinition(beanName);
    beanDefinitionRegistry.removeBeanDefinition(beanName);
    log.info("remove {} bean definition completed!", beanName);
    String jobName = batchJobTriggerConfigDao.selectJobNameByBean(beanName);
    if (jobRepository.getJobNames().contains(jobName)) {
      jobRegistry.unregister(jobName);
    }
    beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);
    log.info("{} is reloaded", beanName);
    return beanName + " flow is reloaded!";
  }
  // 下略
}
```

### Report模組（報表）
* 解決通點：手刻worksheet純屬苦工，欲提供一個僅需傳入資料即能快速生成報表的模組，讓開發人員能專注於資料邏輯處理
* 報表包含以下區塊，可依需求組裝/移除：
  * 報表標題（title）
  * 報表產生條件資訊區（header）
  * 主要資料區（column, content）
  * 頁尾資訊（footer）
* 模組元件：
  * utils.ReportGenerator：實際刻worksheet的苦力，將個區塊拼入worksheet
  * service.report.Report：定義取得各區塊資料以及生成的抽象方法，供實際業務邏輯報表繼承後實作，以及定義產生Excel、刪除暫存實體檔之實作方法，使用到ReportGenerator
  * ReportJob：定義執行ReportTasklet的批次（詳細定義方式見上方Spring Batch介紹），依照參數的reportName以getBean方法取得欲執行的報表並以Report承接之，再以多型執行生成方法開始生成報表
* 開發人員實作：
  * service.report.XXXReport：業務邏輯報表，開發人員接收需求後建立專屬XXXReport並繼承Report