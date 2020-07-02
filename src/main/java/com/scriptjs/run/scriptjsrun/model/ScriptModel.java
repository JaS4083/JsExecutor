package com.scriptjs.run.scriptjsrun.model;

import com.scriptjs.run.scriptjsrun.model.status.ScriptStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.hateoas.RepresentationModel;

import java.util.ArrayList;
import java.util.List;

@Data
@Document
@NoArgsConstructor
public class ScriptModel extends RepresentationModel<ScriptModel> {
    @Id
    @NonNull private String scriptId;
    @NonNull private String scriptBody;
    @NonNull private ScriptStatus scriptStatus;

    private String result;
    private List<String> exceptionMessages;

    public ScriptModel(@NonNull String scriptId, @NonNull String scriptBody, @NonNull ScriptStatus scriptStatus) {
        this.scriptId = scriptId;
        this.scriptBody = scriptBody;
        this.scriptStatus = scriptStatus;
        this.exceptionMessages = new ArrayList<>();
    }
}
