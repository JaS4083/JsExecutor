package com.scriptjs.run.scriptjsrun.local;

import com.scriptjs.run.scriptjsrun.model.ScriptModel;
import com.scriptjs.run.scriptjsrun.model.status.ScriptStatus;

import java.util.List;

public interface LocalStorage {
    ScriptModel addToScriptModelToMap(ScriptModel scriptModel);
    ScriptModel markScriptModelStatus(String scriptId, ScriptStatus status);
    void addExceptionToModel(String scriptId, Exception exception);
    void addResultToModel(String scriptId, String result);
    List<ScriptModel> getAllModelStorage();
}
