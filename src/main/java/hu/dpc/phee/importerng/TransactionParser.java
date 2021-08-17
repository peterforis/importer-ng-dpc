package hu.dpc.phee.importerng;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import hu.dpc.phee.importerng.db.model.Event;
import hu.dpc.phee.importerng.db.model.ProcessDefinition;
import hu.dpc.phee.importerng.db.repository.EventRepository;
import hu.dpc.phee.importerng.db.repository.ProcessDefinitionRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionParser {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    EventRepository eventRepository;

    @Autowired
    ProcessDefinitionRepository processDefinitionRepository;

    private List<Chainr> chainrs;

    private List<String> specs;

    @PostConstruct
    public void setup() {
        specs = new ArrayList<>();
        chainrs = new ArrayList<>();
        loadSpecs();
        loadParsers();
    }

    public void loadParsers() {
        for (String spec : specs) {
            String eventParserPath = "/parsers/" + spec;
            List<Object> chainrSpecJSON = JsonUtils.classpathToList(eventParserPath);
            LOG.info("Loaded parser from {}", eventParserPath);
            chainrs.add(Chainr.fromSpec(chainrSpecJSON));
        }
    }

    public void loadSpecs() {
        File directoryPath = new File("src/main/resources/parsers");
        File[] filesList = directoryPath.listFiles();
        if (filesList.length == 0) {
            LOG.error("No spec files found under /resources/parsers/");
            return;
        }
        for (File file : filesList) {
            specs.add(file.getName());
        }
    }

    public boolean parseTransaction(String transaction, Chainr chainr) {
        Object transformedOutput = chainr.transform(JsonUtils.jsonToObject(transaction));
        if (transformedOutput == null) {
            LOG.warn("Parsers did not parse transaction, transaction was: {}", transaction);
            return false;
        }

        // TODO create caching for performance
        JSONObject json = new JSONObject(JsonUtils.toJsonString(transformedOutput));
        Long processDefinitionKey = json.getLong("processDefinitionKey");
        Optional<ProcessDefinition> processDefinition = processDefinitionRepository.findByProcessDefinitionKey(processDefinitionKey);
        if (processDefinition.isEmpty()) {
            LOG.warn("Could not find a processDefinition with processDefinitionKey {}, transaction was {}", processDefinitionKey, transaction);
            return false;
        }

        saveEvent(json);
        return true;
    }

    private void saveEvent(JSONObject json) {

        Long key = json.getLong("key");

        Event event = new Event();
        event.setKey(key);
        event.setProcessDefinitionKey(json.getLong("processdefinitionkey"));
        event.setVersion(json.getInt("version"));
        event.setTimeStamp(json.getLong("timestamp"));
        event.setEventText(json.getString("eventtext"));

        eventRepository.save(event);

        LOG.debug("Saved event with key: {}", key);
    }

    private void saveProcessDefinition(JSONObject json) {

        int id = json.getInt("id");

        ProcessDefinition processDefinition = new ProcessDefinition();
        processDefinition.setId(id);
        processDefinition.setProcessDefinitionKey(json.getLong("processdefinitionkey"));
        processDefinition.setVersion(json.getInt("version"));
        processDefinition.setBpmnProcessId(json.getString("bpmnProcessId"));
        processDefinition.setResourceName(json.getString("resourceName"));

        processDefinitionRepository.save(processDefinition);

        LOG.debug("Saved processDefinition with id: {}", id);
    }

    public List<Chainr> getChainrs() {
        return chainrs;
    }

    public void setChainrs(List<Chainr> chainrs) {
        this.chainrs = chainrs;
    }
}
