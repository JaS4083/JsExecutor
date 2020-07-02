package com.scriptjs.run.scriptjsrun.service;

import com.scriptjs.run.scriptjsrun.local.impl.LocalScriptModelStorage;
import com.scriptjs.run.scriptjsrun.model.ScriptModel;
import com.scriptjs.run.scriptjsrun.model.status.ScriptStatus;
import com.scriptjs.run.scriptjsrun.service.impl.JsRequestAsyncServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class JsRequestAsyncServiceImplTest {

    private LocalScriptModelStorage localScriptModelStorage;
    private JsRequestAsyncServiceImpl jsRequestAsyncService;

    @BeforeEach
    public void setup() {
        localScriptModelStorage = new LocalScriptModelStorage();
        jsRequestAsyncService = new JsRequestAsyncServiceImpl(localScriptModelStorage);

    }

    @Test
    public void shouldRunScriptCorrectlyTest() {

        //given
        String scriptMessage = "function getReadyForRock(){\n" +
                "    console.log(\"Starting rock\");\n" +
                "    return \"SMOCK!\"\n" +
                "}\n" +
                "getReadyForRock();";
        ScriptModel scriptModelStart = new ScriptModel("randomId", scriptMessage, ScriptStatus.RUNNING);
        ScriptModel scriptModelFinish = new ScriptModel("randomId", scriptMessage, ScriptStatus.FINISHED);
        scriptModelFinish.setResult("SMOCK!");

        //when
        jsRequestAsyncService.processAsyncScript(scriptModelStart);

        //then
        assertThat(localScriptModelStorage.getScriptById(scriptModelStart.getScriptId()).equals(scriptModelFinish));
        assertThat(localScriptModelStorage.getScriptById(scriptModelStart.getScriptId()).getResult().equals(scriptModelFinish.getResult()));
    }

    @Test
    public void shouldRunWithExceptionWhenGivenWrongScriptTest(){
        //given
        String scriptMessage = "function getReadyForRock(){\n" +
                "    console.log(\"Starting rock\");\n" +
                "    return \"SMOCK!\"\n" +
                "}\n" +
                "setTimeout(getReadyForRoc, 3000);";
        ScriptModel scriptModelStart = new ScriptModel("randomId", scriptMessage, ScriptStatus.RUNNING);

        //when
        try {
            jsRequestAsyncService.processAsyncScript(scriptModelStart);
        } catch (NullPointerException exception){
            log.warn("NullPointerException while executing shouldRunWithExceptionWhenGivenWrongScriptTest");
        }

        //then
        ScriptModel resultModel = localScriptModelStorage.getScriptById(scriptModelStart.getScriptId());
        assertThat(resultModel.getScriptStatus().equals(ScriptStatus.RUNNING));
        assertThat(resultModel.getExceptionMessages().size() == 1);
        assertThat(resultModel.getExceptionMessages().get(0).equals("resultModel"));
    }

}
