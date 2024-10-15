package Fridge_Chef.team.recipe.dump;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dump")
@RequiredArgsConstructor
public class DumpController {

    private final DumpService dumpService;

//    @PostMapping("/")
//    public void dump() {
//
//        dumpService.insertAll();
//    }
}
