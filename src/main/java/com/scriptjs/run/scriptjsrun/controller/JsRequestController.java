package com.scriptjs.run.scriptjsrun.controller;

import com.scriptjs.run.scriptjsrun.model.ResponseHateoas;
import com.scriptjs.run.scriptjsrun.model.ScriptModel;
import com.scriptjs.run.scriptjsrun.model.status.ScriptStatus;
import com.scriptjs.run.scriptjsrun.service.JsRequestAsyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
@RequestMapping(value = "/")
@RequiredArgsConstructor
public class JsRequestController {
    private final JsRequestAsyncService jsRequestAsyncService;

    @PostMapping(value="/scriptRun", consumes = "text/plain")
    public ResponseEntity<?> returnJsResponse(@RequestBody String payload){

        if(payload == null || payload.isEmpty()){
            return new ResponseEntity<>("No script was found", HttpStatus.BAD_REQUEST);
        }
        String scriptId = UUID.randomUUID().toString();
        ScriptModel model = new ScriptModel(scriptId, payload, ScriptStatus.RUNNING);

            jsRequestAsyncService.processAsyncScript(model);

            return new ResponseEntity<>(new ResponseHateoas("Script has been accepted")
                    .add(linkTo(methodOn(JsRequestController.class).getScriptStatusById(scriptId)).withSelfRel()), HttpStatus.ACCEPTED);
    }

    @GetMapping(value = "/allScriptInfo")
    public ResponseEntity<List<ScriptModel>> getAllScriptHistory() {
        List<ScriptModel> scriptList = jsRequestAsyncService.getAllModelStorage();
        scriptList.forEach(x -> {
          if(x.getScriptStatus().equals(ScriptStatus.RUNNING)) {
              x.add(linkTo(methodOn(JsRequestController.class).shutDownThread(x.getScriptId())).withSelfRel());
          } else { x.removeLinks(); }
        });
        return new ResponseEntity<>(jsRequestAsyncService.getAllModelStorage(), HttpStatus.OK);
    }

    @GetMapping(value = "/script/{scriptId}")
    public ResponseEntity<ScriptModel> getScriptStatusById(@PathVariable("scriptId") String scriptId) {
        ScriptModel scriptModel = jsRequestAsyncService.getScriptStatusById(scriptId);
       if (scriptModel.getScriptStatus().equals(ScriptStatus.RUNNING)){
           return new ResponseEntity<>(scriptModel
                   .add(linkTo(methodOn(JsRequestController.class).shutDownThread(scriptId)).withSelfRel()),
                   HttpStatus.OK);
       }
        return new ResponseEntity<>(scriptModel.removeLinks(), HttpStatus.OK);
    }

    @PatchMapping(value = "/shutDownThread/{scriptId}")
    public ResponseEntity<?> shutDownThread(@PathVariable("scriptId") String scriptId){
        jsRequestAsyncService.forceToCloseContext(scriptId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
