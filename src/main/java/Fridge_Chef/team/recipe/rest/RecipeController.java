package Fridge_Chef.team.recipe.rest;

import Fridge_Chef.team.recipe.rest.request.SampleRequest;
import Fridge_Chef.team.recipe.rest.response.SampleResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RecipeController {

    @GetMapping()
    public String info(){
        return "web request success";
    }

    @PostMapping("/test")
    public SampleResponse docs(@RequestBody SampleRequest dto){
        return new SampleResponse(dto.getName(),dto.getValue());
    }

}
