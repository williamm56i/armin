INSERT INTO SYS_CODE (SYS_GROUP, SYS_NAME, SYS_VALUE, MEMO, CREATE_ID, CREATE_DATE, UPDATE_ID, UPDATE_DATE)
VALUES ('SYS', 'GIT_TOKEN', '', '2024/8/29 expire', 'SYS', NOW(), null, null);

INSERT INTO BATCH_JOB_FLOW_CONTROL (JOB_NAME, STEP_NAME, STEP_ORDER, IS_EXECUTABLE)
VALUES ('ExampleJob', 'example1-step', 1, 'Y');
INSERT INTO BATCH_JOB_FLOW_CONTROL (JOB_NAME, STEP_NAME, STEP_ORDER, IS_EXECUTABLE)
VALUES ('ExampleJob', 'example2-step', 2, 'Y');

INSERT INTO BATCH_JOB_TRIGGER_CONFIG(JOB_NAME, JOB_DESC, BEAN_NAME, TRIGGER_NAME, CRON_TRIGGER, ENABLE, CREATE_ID, CREATE_DATE, UPDATE_ID, UPDATE_DATE)
VALUES ('ExampleJob', '範例作業', 'example-job', 'example-job-trigger', '0 * * * * ?', 'Y', 'SYS', NOW(), null, null);

INSERT INTO SYS_USER (ACCOUNT, USER_NAME, EMAIL, CREATE_ID, CREATE_DATE, UPDATE_ID, UPDATE_DATE)
VALUES ('B0132', 'Tony', 'tony@gmail.com', 'SYS', NOW(), null, null);
INSERT INTO SYS_USER (ACCOUNT, USER_NAME, EMAIL, CREATE_ID, CREATE_DATE, UPDATE_ID, UPDATE_DATE)
VALUES ('A9090', 'Liz', 'liz@gmail.com', 'SYS', NOW(), null, null);
INSERT INTO SYS_USER (ACCOUNT, USER_NAME, EMAIL, CREATE_ID, CREATE_DATE, UPDATE_ID, UPDATE_DATE)
VALUES ('A8703', 'Nick', 'nick@gmail.com', 'SYS', NOW(), null, null);
INSERT INTO SYS_USER (ACCOUNT, USER_NAME, EMAIL, CREATE_ID, CREATE_DATE, UPDATE_ID, UPDATE_DATE)
VALUES ('A8672', 'Ruby', 'ruby@gmail.com', 'SYS', NOW(), null, null);