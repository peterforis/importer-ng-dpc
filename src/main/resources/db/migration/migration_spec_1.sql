CREATE TABLE IF NOT EXISTS EVENT
(KEY bigint, PROCESSDEFINITIONKEY bigint, VERSION int, TIMESTAMP bigint, EVENTTEXT varchar);


CREATE TABLE IF NOT EXISTS PROCESSDEFINITION
(ID bigint, PROCESSDEFINITIONKEY varchar, VERSION int, BPMNPROCESSID varchar, RESOURCENAME varchar);