package com.scriptjs.run.scriptjsrun.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scriptjs.run.scriptjsrun.model.ScriptModel;
import com.scriptjs.run.scriptjsrun.model.status.ScriptStatus;
import com.scriptjs.run.scriptjsrun.service.JsRequestAsyncService;
import com.sun.xml.internal.ws.api.message.Header;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class JsRequestControllerTest {

    @MockBean
    private JsRequestAsyncService jsRequestAsyncService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Post /scriptRun - Accepted")
    void acceptsJsScriptAndReturnsAcceptCode() throws Exception {
        mockMvc.perform(post("/scriptRun")
                .contentType(MediaType.TEXT_PLAIN).content("{console.log('Test')}"))
                .andExpect(status().isAccepted());
    }

    @Test
    @DisplayName("Get /allScriptInfo - Returns all script_models")
    void returnsAllScriptHistoryWhenGetMethodRun() throws Exception {
        List<ScriptModel> scriptModels = Arrays.asList(
                new ScriptModel("1", "{console.log('Test1')}", ScriptStatus.FINISHED ),
                new ScriptModel("2", "{console.log('Test2')}", ScriptStatus.RUNNING ),
                new ScriptModel("3", "{console.log('Test3')}", ScriptStatus.FINISHED_WITH_ERROR)
        );

        doReturn(scriptModels).when(jsRequestAsyncService).getAllModelStorage();

        mockMvc.perform(get("/allScriptInfo"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(scriptModels.size()))
                .andExpect(jsonPath("$[0].scriptId").value("1"))
                .andExpect(jsonPath("$[0].scriptBody").value("{console.log('Test1')}"))
                .andExpect(jsonPath("$[0].scriptStatus").value("FINISHED"))
                .andExpect(jsonPath("$[2].scriptId").value("3"))
                .andExpect(jsonPath("$[2].scriptBody").value("{console.log('Test3')}"))
                .andExpect(jsonPath("$[2].scriptStatus").value("FINISHED_WITH_ERROR"));
    }

    @Test
    @DisplayName("Get /script/{scriptId} - returns correct script_mode by scriptId")
    void returnsCorrectScriptRequestWithStatus() throws Exception {
        ScriptModel model = new ScriptModel("1", "{console.log('Test1')}", ScriptStatus.FINISHED);
        doReturn(model).when(jsRequestAsyncService).getScriptStatusById("1");

        mockMvc.perform(get("/script/{scriptId}", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$.scriptId").value("1"))
                .andExpect(jsonPath("$.scriptBody").value("{console.log('Test1')}"))
                .andExpect(jsonPath("$.scriptStatus").value("FINISHED"));
    }

    @Test
    @DisplayName("Get /script/{scriptId} - returns correct script_mode by scriptId")
    void returnsProductNotFound() throws Exception {
        doReturn(null).when(jsRequestAsyncService).getScriptStatusById("1");

        mockMvc.perform(get("/script/{scriptId}", "1"))
                .andExpect(status().isNotFound());
    }



}