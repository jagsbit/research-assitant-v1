package com.project.research_assistant_v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class ResearchService {

    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Autowired
    private ObjectMapper objectMapper;

    private final WebClient webClient;

    public ResearchService(WebClient.Builder webClientBuilder){
        this.webClient=webClientBuilder.build();
    }

    public String processContent(ResearchRequest request){

        // Build the prompt

        String prompt=buildPrompt(request);
        // Query the AI Model API

        Map<String,Object> requestBody=Map.of(
                "contents",new Object[]{
                        Map.of("parts",new Object[]{
                                Map.of("text",prompt)
                        })
                }
        );

        String response=webClient.post()
                .uri(geminiApiUrl+geminiApiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        // Parse the response


        // Return response
        return extractTextFromResponse(response);
    }
    private String buildPrompt(ResearchRequest request){
        StringBuilder prompt=new StringBuilder();
        switch (request.getOperation()){
            case "summarize":
                prompt.append("Provide a clear and concise summery of the following text in a few sentences:\n\n");
                break;
            case "suggest":
                prompt.append("Based on the following content: suggest related topics and further reading. Format the response with clear headings and bullet points:\n\n");
            default:
                throw  new IllegalArgumentException("Unknown Operation "+request.getOperation());
        }
        prompt.append(request.getContent());
        return  prompt.toString();
    }

    private String extractTextFromResponse(String response){
        try {
            GeminiResponse geminiResponse=objectMapper.readValue(response, GeminiResponse.class);
            if(geminiResponse.getCandidates()!=null && !geminiResponse.getCandidates().isEmpty()){
                GeminiResponse.Candidate firstCandidate=geminiResponse.getCandidates().get(0);
                if(firstCandidate.getContent()!=null
                        && firstCandidate.getContent().getParts()!=null
                        && !firstCandidate.getContent().getParts().isEmpty()){
                    return firstCandidate.getContent().getParts().get(0).getText();
                }
            }

            return "No content Found";
        }

        catch (Exception e){
            return "Error Parsing"+e.getMessage();
        }
    }
}
