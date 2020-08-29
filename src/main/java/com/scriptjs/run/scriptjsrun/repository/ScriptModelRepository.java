package com.scriptjs.run.scriptjsrun.repository;

import com.scriptjs.run.scriptjsrun.model.ScriptModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ScriptModelRepository extends MongoRepository<ScriptModel, String> {}
