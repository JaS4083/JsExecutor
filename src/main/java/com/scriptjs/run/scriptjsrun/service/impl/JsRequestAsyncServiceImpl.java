package com.scriptjs.run.scriptjsrun.service.impl;

import com.scriptjs.run.scriptjsrun.model.ScriptModel;
import com.scriptjs.run.scriptjsrun.model.status.ScriptStatus;
import com.scriptjs.run.scriptjsrun.service.JsRequestAsyncService;
import com.scriptjs.run.scriptjsrun.local.impl.LocalScriptModelStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class JsRequestAsyncServiceImpl implements JsRequestAsyncService {
    private final LocalScriptModelStorage localScriptModelStorage;

    @Async
    @Override
    public void processAsyncScript(ScriptModel model) {
        log.warn("Thread name: " + Thread.currentThread().getName());
        String scriptId = model.getScriptId();
        //adding script POJO to modelMap
        localScriptModelStorage.addToScriptModelToMap(model);
        //saving current thread to threadInfoMap

        CompletableFuture<String> result = null;
        try(Context context = Context.create()) {
            localScriptModelStorage.getContextMap().put(scriptId, context);
            result = CompletableFuture.completedFuture(context.eval("js", model.getScriptBody()).toString());
        }catch (PolyglotException exception){
            localScriptModelStorage.addExceptionToModel(scriptId, exception);
            log.warn("Exception when executing script: " + scriptId, exception);
        }

        String scriptResult;
        if(result != null) {
            CompletableFuture.anyOf(result);
            try {
                scriptResult = result.get();
                log.info("The value of script run is - {})", scriptResult);
                localScriptModelStorage.addResultToModel(scriptId, scriptResult);
            } catch (InterruptedException | ExecutionException exception) {
                localScriptModelStorage.addExceptionToModel(scriptId, exception);
                localScriptModelStorage.markScriptModelStatus(scriptId, ScriptStatus.FINISHED_WITH_ERROR);
            }
        }

        localScriptModelStorage.markScriptModelStatus(scriptId, ScriptStatus.FINISHED);
    }

    @Override
    public ScriptModel getScriptStatusById(String scriptId){
        return localScriptModelStorage.getScriptById(scriptId);
    }

    @Override
    public void forceToCloseContext(String scriptId) {
        localScriptModelStorage.markScriptModelStatus(scriptId, ScriptStatus.FINISHED_WITH_ERROR);
        localScriptModelStorage.getContextMap().get(scriptId).close(true);
    }

    @Override
    public List<ScriptModel> getAllModelStorage() {
        return localScriptModelStorage.getAllModelStorage();
    }
}
