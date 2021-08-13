package hu.dpc.phee.importerng.db.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ProcessDefinition {

    @Id
    private int id;
    private Long processDefinitionKey;
    private int version;
    private String bpmnProcessid;
    private String resourceName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(Long processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getBpmnProcessid() {
        return bpmnProcessid;
    }

    public void setBpmnProcessid(String bpmnProcessid) {
        this.bpmnProcessid = bpmnProcessid;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }
}
