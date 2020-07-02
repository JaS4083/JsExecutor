package com.scriptjs.run.scriptjsrun.local.impl;

import com.scriptjs.run.scriptjsrun.local.LocalStorage;
import com.scriptjs.run.scriptjsrun.model.ScriptModel;
import com.scriptjs.run.scriptjsrun.model.status.ScriptStatus;
import com.scriptjs.run.scriptjsrun.repository.ScriptModelRepository;
import lombok.Data;
import org.graalvm.polyglot.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Data
@Component
public class LocalScriptModelStorage implements LocalStorage {
    private final Map<String, ScriptModel> modelMap = new ConcurrentHashMap<>();
    private final Map<String, Context> contextMap = new ConcurrentHashMap<>();
    private ScriptModelRepository scriptModelRepository;

    @Autowired
    public void setScriptModelRepository(ScriptModelRepository scriptModelRepository) {
        this.scriptModelRepository = scriptModelRepository;
    }

    @PostConstruct
    public void init(){
        modelMap.putAll(scriptModelRepository.findAll().stream().collect(Collectors.toMap(ScriptModel::getScriptId, x -> x)));
    }

    public ScriptModel addToScriptModelToMap(ScriptModel scriptModel){
        modelMap.put(scriptModel.getScriptId(), scriptModel);
        return scriptModel;
    }

    public ScriptModel markScriptModelStatus(String scriptId, ScriptStatus status){
        ScriptModel modelToChange = modelMap.get(scriptId);
        modelToChange.setScriptStatus(status);
        modelMap.put(scriptId, modelToChange);
        return modelToChange;
    }

    public void addExceptionToModel(String scriptId, Exception exception){
        ScriptModel modelToAddException = modelMap.get(scriptId);
        modelToAddException.getExceptionMessages().add(exception.getMessage());
    }

    public void addResultToModel(String scriptId, String result){
        ScriptModel modelToAddResult = modelMap.get(scriptId);
        modelToAddResult.setResult(result);
    }

    @PreDestroy
    public void preDestroy(){
        scriptModelRepository.deleteAll();
        scriptModelRepository.saveAll(modelMap.values());
    }


    public ScriptModel getScriptById(String scriptId){
        return modelMap.get(scriptId);
    }

    public List<ScriptModel> getAllModelStorage(){
        return new ArrayList<>(modelMap.values());
    }

}
