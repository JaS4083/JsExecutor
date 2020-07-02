# JsExecutor

This program is an example how to execute simple JS scripts using [GraalVm](https://www.graalvm.org/)

To run, GraalVm needs to be set as default VM. 

**To test in Postman:**

Post    *http://localhost:8080/scriptRun*

Body -> raw ->
 any JS script for instance:
 
 `function getReadyForRock(){
       console.log("Starting rock");
       return "SMOCK!"
   }
   getReadyForRock();`

