DROP TABLE IF EXISTS EVENT;
CREATE TABLE EVENT
(KEY bigint, PROCESSDEFINITIONKEY bigint, VERSION int, TIMESTAMP bigint, EVENTTEXT varchar);


DROP TABLE IF EXISTS PROCESSDEFINITION;
CREATE TABLE PROCESSDEFINITION
(ID bigint, PROCESSDEFINITIONKEY varchar, VERSION int, BPMNPROCESSID varchar, RESOURCENAME varchar);