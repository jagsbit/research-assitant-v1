package com.project.research_assistant_v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/research")
@CrossOrigin(origins = "*")
public class ResearchController {

    public ResearchService researchService;
    public ResearchController(ResearchService researchService){
        this.researchService=researchService;
    }

    @PostMapping("/process")
    public ResponseEntity<String> processContent(@RequestBody ResearchRequest researchRequest){
        String result=researchService.processContent(researchRequest);
        return ResponseEntity.ok(result);
    }
}