package com.scriptjs.run.scriptjsrun.service;

import com.scriptjs.run.scriptjsrun.model.ScriptModel;

import java.util.List;

public interface JsRequestAsyncService {
    void processAsyncScript(ScriptModel model);
    ScriptModel getScriptStatusById(String scriptId);
    void forceToCloseContext(String scriptId);
    List<ScriptModel> getAllModelStorage();
}
