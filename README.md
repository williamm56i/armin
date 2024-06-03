# Armin Batch
Batch架構專案，定義批次流程與業務邏輯實作，並提供接口以立即執行作業或Runtime調整批次流程

### 技術框架
* Java version 18
* Spring boot 3.3.0
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