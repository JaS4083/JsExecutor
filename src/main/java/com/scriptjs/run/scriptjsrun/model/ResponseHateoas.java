package com.scriptjs.run.scriptjsrun.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@NoArgsConstructor
@AllArgsConstructor
public class ResponseHateoas extends RepresentationModel<ResponseHateoas> {
    private String response;
}
